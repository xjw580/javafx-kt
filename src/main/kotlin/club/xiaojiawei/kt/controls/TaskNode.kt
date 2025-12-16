package club.xiaojiawei.kt.controls

import club.xiaojiawei.controls.ico.AbstractIco
import club.xiaojiawei.kt.bean.task.TaskBuilder
import club.xiaojiawei.kt.bean.task.TaskController
import club.xiaojiawei.kt.bean.task.TaskStatistics
import club.xiaojiawei.kt.ext.runUI
import javafx.animation.*
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.scene.CacheHint
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.Tooltip
import javafx.scene.effect.DropShadow
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.stage.Popup
import javafx.util.Duration
import java.util.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author 肖嘉威
 * @date 2025/6/26 14:25
 */
open class TaskNode<T : TaskBuilder> : StackPane() {

    val icoProperty: ObjectProperty<Node>

    var ico: Node
        get() = this.icoProperty.get()
        set(value) {
            this.icoProperty.set(value)
        }

    // 使用原子变量确保线程安全
    private val taskCountAtomic = AtomicInteger(0)
    // 动画节流控制
    private val isAnimating = AtomicBoolean(false)
    private var pendingAnimationCount = AtomicInteger(0)
    private var currentAnimation: Transition? = null

    private val statisticsCallback: (TaskStatistics) -> Unit = { statistics ->
        val newCount = statistics.running + statistics.pending
        val oldCount = taskCountAtomic.getAndSet(newCount)
        if (oldCount != newCount) {
            runUI {
                updateTaskCountUI(newCount)
            }
        }
    }

    var taskController: TaskController<T>? = null
        set(value) {
            field?.getTaskManager()?.removeStatisticsCallback(statisticsCallback)
            value?.getTaskManager()?.addStatisticsCallback(statisticsCallback)
            field = value
        }

    val taskCountLabel = Label("0").apply {
        style = "-fx-text-fill: white;-fx-font-weight:bold;-fx-font-size:10"
    }

    val taskCountPane = AnchorPane(
        Circle().apply {
            fill = Color.RED
            radius = 8.0
            centerX = radius
            centerY = radius
        },
        StackPane(taskCountLabel)
    ).apply {
        isPickOnBounds = false
        isVisible = false
        isManaged = false
    }

    private val tooltipProperty: ObjectProperty<Tooltip> = SimpleObjectProperty()

    var tooltip: Tooltip?
        get() = tooltipProperty.get()
        set(value) {
            tooltipProperty.set(value)
        }

    private var tooltipShowTransition: Transition? = null
    private var tooltipHideTransition: Transition? = null

    init {
        val icoBtn = Button().apply {
            styleClass.addAll("btn-ui")
            style = "-fx-padding:7;"
            onAction = EventHandler {
                showPopup()
            }
            icoProperty = graphicProperty()
        }
        children.addAll(
            StackPane().apply {
                padding = Insets(10.0)
                children.add(icoBtn)
            },
            taskCountPane
        )
        onMouseEntered = EventHandler {
            tooltip?.let { tooltip ->
                val bounds = this.localToScreen(this.boundsInLocal)
                tooltipShowTransition = PauseTransition(tooltip.showDelay).apply {
                    onFinished = EventHandler {
                        tooltip.show(this@TaskNode, bounds.maxX - 10.0, bounds.maxY - 10.0)
                        tooltipHideTransition = PauseTransition(tooltip.showDuration).apply {
                            onFinished = EventHandler {
                                tooltip.hide()
                            }
                            play()
                        }
                    }
                    play()
                }
            }
        }
        onMouseExited = EventHandler {
            tooltipShowTransition?.pause()
            tooltipShowTransition = null
            tooltipHideTransition?.pause()
            tooltipHideTransition = null
            tooltip?.hide()
        }
    }

    /**
     * 更新任务数量UI显示
     */
    private fun updateTaskCountUI(count: Int) {
        taskCountLabel.text = count.toString()
        taskCountLabel.translateX = 4.0 - (taskCountLabel.text.length - 1) * 3
        if (count > 0) {
            taskCountPane.isVisible = true
            taskCountPane.isManaged = true
            val icoNode = icoProperty.get()
            if (icoNode is AbstractIco) {
                icoNode.color = "green"
            }
        } else {
            taskCountPane.isVisible = false
            taskCountPane.isManaged = false
            val icoNode = icoProperty.get()
            if (icoNode is AbstractIco) {
                icoNode.color = "black"
            }
        }
    }

    fun addTask(name: String, configures: List<T.() -> Unit>) =
        addTask(UUID.randomUUID().toString(), name, { configures.forEach { it() } })

    fun addTask(id: String, name: String, configures: List<T.() -> Unit>) =
        addTask(id, name, { configures.forEach { it() } })

    fun addTask(name: String, configure: T.() -> Unit) = addTask(UUID.randomUUID().toString(), name, configure)

    fun addTask(id: String, name: String, configure: T.() -> Unit) {
        playAddTaskTransition()
        taskController?.getTaskManager()?.addTask(id, name, configure)
    }

    /**
     * 播放添加任务动画 - 带节流机制
     * 避免大量任务同时添加时动画卡顿
     */
    private fun playAddTaskTransition() {
        // 如果正在播放动画，累计待处理动画数量，但不立即播放新动画
        if (!isAnimating.compareAndSet(false, true)) {
            pendingAnimationCount.incrementAndGet()
            return
        }

        runUI {
            val icoNode = icoProperty.get()
            if (icoNode is AbstractIco) {
                icoNode.isCache = true
                icoNode.cacheHint = CacheHint.SCALE_AND_ROTATE

                val transitionTime = Duration.millis(500.0)
                val rotateTransition = RotateTransition(transitionTime, icoNode).apply {
                    fromAngle = 0.0
                    toAngle = 360.0
                    cycleCount = 1
                    interpolator = Interpolator.EASE_BOTH
                }
                val scaleTransition = ScaleTransition(transitionTime, icoNode).apply {
                    fromX = 3.0
                    fromY = 3.0
                    toX = 1.0
                    toY = 1.0
                }

                val parallelTransition = ParallelTransition(
                    rotateTransition,
                    scaleTransition,
                )

                currentAnimation = parallelTransition

                parallelTransition.onFinished = EventHandler {
                    icoNode.cacheHint = CacheHint.DEFAULT
                    icoNode.isCache = false
                    currentAnimation = null
                    isAnimating.set(false)

                    // 如果有待处理的动画请求，播放一次合并动画
                    val pending = pendingAnimationCount.getAndSet(0)
                    if (pending > 0) {
                        playAddTaskTransition()
                    }
                }
                parallelTransition.play()
            } else {
                isAnimating.set(false)
            }
        }
    }

    private val popup: Popup by lazy {
        Popup().apply {
            isAutoHide = true
            isAutoFix = true
            content.add(StackPane(getPopupPane()).apply {
                style = "-fx-effect: default-common-effect"
            })
        }
    }

    protected fun getPopupPane(): Pane? = taskController?.getProgressView()

    private fun showPopup() {
        if (getPopupPane() == null) return
        popup.show(scene.window)
        updatePopupPos()
    }

    private fun updatePopupPos() {
        val popupPane = getPopupPane() ?: return
        val localBounds = this.localToScreen(this.boundsInLocal)
        var popupOffsetX = 0.0
        var popupOffsetY = 0.0
        if (effect is DropShadow) {
            val shadow = effect as DropShadow
            popupOffsetX = -shadow.offsetX + shadow.radius
            popupOffsetY = -shadow.offsetY + shadow.radius
        }
        popup.x = localBounds.maxX - popupOffsetX - popupPane.width
        popup.y = localBounds.maxY - popupOffsetY
    }

    /**
     * 添加批量完成回调监听器
     * 当多个任务在短时间内完成时，会批量通知，减少UI线程切换次数
     * 回调会在UI线程中执行
     * @param callback 批量完成回调，参数为完成的任务列表
     */
    fun addBatchCompletionCallback(callback: (List<club.xiaojiawei.kt.bean.task.TaskCompletionInfo>) -> Unit) {
        taskController?.addBatchCompletionCallback(callback)
    }

    /**
     * 移除批量完成回调监听器
     */
    fun removeBatchCompletionCallback(callback: (List<club.xiaojiawei.kt.bean.task.TaskCompletionInfo>) -> Unit) {
        taskController?.removeBatchCompletionCallback(callback)
    }

}