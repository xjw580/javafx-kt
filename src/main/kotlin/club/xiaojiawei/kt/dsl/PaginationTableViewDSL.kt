package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import club.xiaojiawei.kt.controls.PageRequest
import club.xiaojiawei.kt.controls.PageResult
import club.xiaojiawei.kt.controls.PaginationTableView

@FXMarker
class PaginationTableViewBuilder<T> : RegionBaseBuilder<PaginationTableView<T>>() {

    override fun buildInstance(): PaginationTableView<T> = PaginationTableView()

    fun pageSize(pageSize: Int) = settings {
        this.pageSize = pageSize
    }

    fun loader(loader: suspend (PageRequest) -> PageResult<T>) = settings {
        pageLoader = loader
    }

    fun table(config: TableViewBuilder<T>.() -> Unit) = settings {
        tableView.config(config)
    }

    fun pagination(config: PaginationBuilder.() -> Unit) = settings {
        pagination.config(config)
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            pagination.config {
                style()
            }
            tableView.config {
                style()
            }
        }
    }
}

inline fun <T> paginationTableView(config: PaginationTableViewBuilder<T>.() -> Unit): PaginationTableView<T> {
    return paginationTableViewBuilder(config).build()
}

inline fun <T> paginationTableViewBuilder(
    config: PaginationTableViewBuilder<T>.() -> Unit
): PaginationTableViewBuilder<T> {
    return PaginationTableViewBuilder<T>().apply(config)
}

inline fun <T> PaginationTableView<T>.config(
    config: PaginationTableViewBuilder<T>.() -> Unit
): PaginationTableView<T> {
    PaginationTableViewBuilder<T>().apply {
        delayMode()
        config()
    }.config(this)
    return this
}

inline fun <T> PaneBaseBuilder<*>.addPaginationTableView(
    config: PaginationTableViewBuilder<T>.() -> Unit = {}
) {
    add(PaginationTableViewBuilder<T>().apply {
        setMode(this@addPaginationTableView.buildMode)
        config()
    })
}
