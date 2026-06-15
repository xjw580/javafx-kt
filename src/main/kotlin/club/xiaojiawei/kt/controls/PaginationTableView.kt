package club.xiaojiawei.kt.controls

import club.xiaojiawei.kt.dsl.pagination
import club.xiaojiawei.kt.dsl.tableView
import club.xiaojiawei.kt.dsl.label
import club.xiaojiawei.kt.dsl.VBoxBuilder
import club.xiaojiawei.kt.dsl.config
import club.xiaojiawei.kt.ext.runUI
import javafx.beans.property.ReadOnlyBooleanProperty
import javafx.beans.property.ReadOnlyBooleanWrapper
import javafx.beans.property.ReadOnlyIntegerProperty
import javafx.beans.property.ReadOnlyIntegerWrapper
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.collections.FXCollections
import javafx.scene.control.Label
import javafx.scene.control.Pagination
import javafx.scene.control.TableView
import javafx.scene.layout.VBox
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlin.math.ceil
import kotlin.math.max

data class PageRequest(
    val pageIndex: Int,
    val pageSize: Int
)

data class PageResult<T>(
    val items: List<T>,
    val totalItems: Int
)

open class PaginationTableView<T> : VBox() {

    private val emptyPlaceholder = label("暂无数据")
    private val loadingPlaceholder = label("加载中...")

    val tableView: TableView<T> = tableView<T> {
        settings {
            placeholder = emptyPlaceholder
        }
        vgrowAlways()
    }

    val pagination: Pagination = pagination {
        pageCount(1)
        currentPageIndex(0)
    }

    private val loadingWrapper = ReadOnlyBooleanWrapper(false)
    val loadingProperty: ReadOnlyBooleanProperty = loadingWrapper.readOnlyProperty

    private val errorWrapper = ReadOnlyObjectWrapper<Throwable?>(null)
    val errorProperty: ReadOnlyObjectProperty<Throwable?> = errorWrapper.readOnlyProperty

    private val totalItemsWrapper = ReadOnlyIntegerWrapper(0)
    val totalItemsProperty: ReadOnlyIntegerProperty = totalItemsWrapper.readOnlyProperty

    var pageSize: Int = 20
        set(value) {
            require(value > 0) { "pageSize must be greater than 0" }
            field = value
            updatePageCount()
        }

    var pageLoader: (suspend (PageRequest) -> PageResult<T>)? = null

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private var loadJob: Job? = null
    private var loadVersion: Long = 0

    init {
        val builder = VBoxBuilder().apply {
            delayMode()
            spacing(8.0)
            +tableView
            +pagination
        }
        config {
            spacing(8.0)
            +tableView
            +pagination
        }
        pagination.currentPageIndexProperty().addListener { _, _, newValue ->
            loadPage(newValue.toInt())
        }
    }

    fun reload(pageIndex: Int = pagination.currentPageIndex) {
        require(pageIndex >= 0) { "pageIndex must be greater than or equal to 0" }
        if (pagination.currentPageIndex != pageIndex) {
            pagination.currentPageIndex = pageIndex
        } else {
            loadPage(pageIndex)
        }
    }

    fun refresh() {
        reload(0)
    }

    fun close() {
        loadVersion++
        loadJob?.cancel()
        scope.cancel()
    }

    private fun loadPage(pageIndex: Int) {
        val loader = pageLoader ?: throw IllegalStateException("pageLoader is not set")
        val request = PageRequest(pageIndex, pageSize)
        val version = ++loadVersion

        loadJob?.cancel()
        loadingWrapper.set(true)
        errorWrapper.set(null)
        pagination.isDisable = true
        tableView.placeholder = loadingPlaceholder

        loadJob = scope.launch {
            try {
                val result = loader(request)
                runUI {
                    if (version == loadVersion) {
                        applyResult(result)
                    }
                }
            } catch (_: CancellationException) {
            } catch (throwable: Throwable) {
                runUI {
                    if (version == loadVersion) {
                        applyError(throwable)
                    }
                }
            } finally {
                runUI {
                    if (version == loadVersion) {
                        finishLoading()
                    }
                }
            }
        }
    }

    private fun applyResult(result: PageResult<T>) {
        require(result.totalItems >= 0) { "totalItems must be greater than or equal to 0" }
        tableView.items = FXCollections.observableArrayList(result.items)
        totalItemsWrapper.set(result.totalItems)
        tableView.placeholder = emptyPlaceholder
        updatePageCount()
    }

    private fun applyError(throwable: Throwable) {
        errorWrapper.set(throwable)
        tableView.placeholder = Label(throwable.message ?: "加载失败")
    }

    private fun finishLoading() {
        loadingWrapper.set(false)
        pagination.isDisable = false
        if (errorWrapper.get() == null && tableView.items.isEmpty()) {
            tableView.placeholder = emptyPlaceholder
        }
    }

    private fun updatePageCount() {
        pagination.pageCount = max(1, ceil(totalItemsWrapper.get().toDouble() / pageSize).toInt())
    }
}
