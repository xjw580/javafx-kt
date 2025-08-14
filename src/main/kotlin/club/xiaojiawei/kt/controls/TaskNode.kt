package club.xiaojiawei.kt.controls

import club.xiaojiawei.controls.ico.AbstractIco
import club.xiaojiawei.kt.bean.task.TaskBuilder
import club.xiaojiawei.kt.bean.task.TaskController
import club.xiaojiawei.kt.bean.task.TaskStatistics
import club.xiaojiawei.kt.utils.runUI
import javafx.animation.*
import javafx.beans.property.ObjectProperty
import javafx.beans.property.SimpleIntegerProperty
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

    private val taskCount = SimpleIntegerProperty()

    private val statisticsCallback: (TaskStatistics) -> Unit = { statistics ->
        runUI {
            taskCount.set(statistics.running + statistics.pending)
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

        taskCount.addListener { _, oldValue, newValue ->
            runUI {
                taskCountLabel.text = newValue.toInt().toString()
                taskCountLabel.translateX = 4.0 - (taskCountLabel.text.length - 1) * 3
                if (newValue.toInt() > 0) {
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

    private fun playAddTaskTransition() {
        runUI {
            taskCount.set(taskCount.get() + 1)
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

                parallelTransition.onFinished = EventHandler {
                    icoNode.cacheHint = CacheHint.DEFAULT
                    icoNode.isCache = false
                }
                parallelTransition.play()
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

}