package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.controls.ico.FailIco
import club.xiaojiawei.controls.ico.OKIco
import club.xiaojiawei.kt.dsl.FontWeight
import club.xiaojiawei.kt.dsl.styled
import club.xiaojiawei.kt.dsl.titledPane
import club.xiaojiawei.kt.dsl.vbox
import club.xiaojiawei.kt.ext.runUI
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:21
 */

class TaskProgressView<T : TaskBuilder> : VBox() {
    private val taskContainers = mutableMapOf<String, VBox>()
    private val taskProgressBars = mutableMapOf<String, ProgressBar>()
    private val taskLabels = mutableMapOf<String, Label>()
    private val taskStatuses = mutableMapOf<String, Label>()
    private val taskControlButtons = mutableMapOf<String, HBox>()
    private val subTaskViews = mutableMapOf<String, MutableMap<String, VBox>>()
    private val subTaskContainers = mutableMapOf<String, VBox>()

    // 状态显示标签
    private var runningCountLabel: Label? = null
    private var pendingCountLabel: Label? = null
    private var completedCountLabel: Label? = null
    private var failedCountLabel: Label? = null
    private var totalCountLabel: Label? = null
    private val taskPane = VBox(10.0).apply {
        padding = Insets(12.0)
//        styleClass.addAll("bg-ui")
        styled {
            backgroundColor("#f7f8fa")
        }
    }

    // 任务控制回调
    var onPauseTask: ((String) -> Unit)? = null
    var onResumeTask: ((String) -> Unit)? = null
    var onRetryTask: ((String) -> Unit)? = null
    var onCancelTask: ((String) -> Unit)? = null
    var onDeleteTask: ((String) -> Unit)? = null

    // 全局控制回调
    var onPauseAll: (() -> Unit)? = null
    var onResumeAll: (() -> Unit)? = null
    var onRetryAll: (() -> Unit)? = null
    var onCancelAll: (() -> Unit)? = null
    var onDeleteAll: (() -> Unit)? = null

    init {
        styleClass.addAll("task-progress-view", "radius-ui")
        styled {
            backgroundColor("#ffffff")
            backgroundRadius(8.0)
        }
        createGlobalControlPanel()
        maxWidth = 700.0
    }

    private fun createGlobalControlPanel() {
        val globalControlPanel = HBox(14.0).apply {
            alignment = Pos.CENTER_LEFT
            padding = Insets(14.0, 16.0, 12.0, 16.0)
//            styleClass.addAll("bg-ui")
            styled {
                backgroundColor("#ffffff")
                borderColor("#edf0f4")
                borderWidth(0.0)
                custom("-fx-border-width", "0 0 1 0")
            }
        }

        val titleLabel = Label("任务管理器").applyTitleStyle()

        // 添加状态统计显示
        val statisticsContainer = HBox(8.0).apply {
            alignment = Pos.CENTER_LEFT
        }

        val runningLabel = Label("运行中: 0").applyStatisticStyle("#24a148")

        val pendingLabel = Label("等待中: 0").applyStatisticStyle("#c77700")

        val completedLabel = Label("已完成: 0").applyStatisticStyle("#2773d9")

        val failedLabel = Label("已失败: 0").applyStatisticStyle("#d64545")

        val totalLabel = Label("总计: 0").applyStatisticStyle("#5f6875")

        statisticsContainer.children.addAll(runningLabel, pendingLabel, completedLabel, failedLabel, totalLabel)

        // 存储标签引用以便更新
        this.runningCountLabel = runningLabel
        this.pendingCountLabel = pendingLabel
        this.completedCountLabel = completedLabel
        this.failedCountLabel = failedLabel
        this.totalCountLabel = totalLabel

        val buttonContainer = HBox(6.0).apply {
            alignment = Pos.BOTTOM_RIGHT
        }

        val pauseAllBtn = Button("暂停所有").applyActionStyle("btn-ui-warn")
        pauseAllBtn.setOnAction { onPauseAll?.invoke() }

        val resumeAllBtn = Button("继续所有").applyActionStyle("btn-ui-success")
        resumeAllBtn.setOnAction { onResumeAll?.invoke() }

        val cancelAllBtn = Button("取消所有").applyActionStyle("btn-ui-error")
        cancelAllBtn.setOnAction { onCancelAll?.invoke() }

        val deleteAllBtn = Button("删除所有").applyActionStyle("btn-ui-normal")
        deleteAllBtn.setOnAction { onDeleteAll?.invoke() }

        buttonContainer.children.addAll(pauseAllBtn, resumeAllBtn, cancelAllBtn, deleteAllBtn)

        // 布局：标题 - 统计信息 - 按钮
        val leftContainer = VBox(6.0)
        leftContainer.children.addAll(titleLabel, statisticsContainer)

        HBox.setHgrow(leftContainer, Priority.ALWAYS)
        globalControlPanel.children.addAll(leftContainer, buttonContainer)

        children.addAll(globalControlPanel, ScrollPane(taskPane).apply {
            maxHeight = 800.0
            isFitToWidth = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            styleClass.addAll("edge-to-edge")
            styled {
                background("#f7f8fa")
                backgroundColor("#f7f8fa")
                backgroundInsets(0.0)
                padding(0.0)
            }
        })
    }

    fun addTask(taskList: List<CompositeTask>) {
        runUI {
            // 批量收集新增的容器，减少UI更新次数
            val newContainers = mutableListOf<VBox>()

            for (task in taskList) {
                // 如果任务已存在，跳过继续处理下一个（修复：使用 continue 而不是 return）
                if (taskContainers.containsKey(task.id)) continue

                val container = VBox(9.0).applyTaskCardStyle()

                // 任务头部（标题和控制按钮）
                val headerBox = HBox(10.0).apply {
                    alignment = Pos.CENTER_LEFT
                }

                val label = Label(task.name).applyTaskTitleStyle()

                val controlBox = HBox(5.0).apply {
                    alignment = Pos.CENTER_RIGHT
                }

                val pauseBtn = Button("暂停").applyActionStyle("btn-ui-warn", compact = true)
                pauseBtn.isDisable = true
                pauseBtn.setOnAction { onPauseTask?.invoke(task.id) }

                val resumeBtn = Button("继续").applyActionStyle("btn-ui-success", compact = true)
                resumeBtn.setOnAction {
                    if (resumeBtn.text == "继续") {
                        onResumeTask?.invoke(task.id)
                    } else {
                        onRetryTask?.invoke(task.id)
                    }
                }
                resumeBtn.isDisable = true // 初始状态禁用

                val cancelBtn = Button("取消").applyActionStyle("btn-ui-error", compact = true)
                cancelBtn.setOnAction { onCancelTask?.invoke(task.id) }

                val deleteBtn = Button("删除").applyActionStyle("btn-ui-normal", compact = true)
                deleteBtn.setOnAction { onDeleteTask?.invoke(task.id) }

                controlBox.children.addAll(pauseBtn, resumeBtn, cancelBtn, deleteBtn)

                // 使用HBox的grow属性让label占据剩余空间
                HBox.setHgrow(label, Priority.ALWAYS)
                headerBox.children.addAll(label, controlBox)

                val progressBar = ProgressBar(0.0)
                progressBar.prefWidthProperty().bind(container.widthProperty())
                progressBar.prefHeight = 6.0
                progressBar.styleClass.add("progress-bar-ui")
                progressBar.styled {
                    accentColor("#38b86b")
                }

                val statusLabel = Label("等待中")
                statusLabel.applyStatusStyle()
                statusLabel.contentDisplay = ContentDisplay.RIGHT

                val subTaskContainer = vbox {
                    spacing(6.0)
                }
                val subTaskPane = titledPane("子任务") {
                    content(subTaskContainer)
                    expanded(false)
                }.apply {
                    styleClass.add("radius-ui")
                }

                container.children.addAll(headerBox, progressBar, statusLabel, subTaskPane)

                taskContainers[task.id] = container
                taskProgressBars[task.id] = progressBar
                taskLabels[task.id] = label
                taskStatuses[task.id] = statusLabel
                taskControlButtons[task.id] = controlBox
                subTaskViews[task.id] = mutableMapOf()
                subTaskContainers[task.id] = subTaskContainer

                newContainers.add(container)
            }

            // 批量添加到 UI，减少布局计算次数
            if (newContainers.isNotEmpty()) {
                taskPane.children.addAll(0, newContainers)
            }
        }
    }

    fun updateProgress(progress: TaskProgress) {
        runUI {
            taskProgressBars[progress.taskId]?.progress = progress.progress
            taskStatuses[progress.taskId]?.let {
                it.text = "${progress.status.comment}: ${progress.message}"
                if (progress.status == TaskStatus.COMPLETED) {
                    if (it.graphic !is OKIco) {
                        it.graphic = OKIco()
                    }
                } else if (progress.status == TaskStatus.FAILED) {
                    if (it.graphic !is FailIco) {
                        it.graphic = FailIco()
                    }
                } else {
                    it.graphic = null
                }
            }


            // 根据任务状态更新控制按钮
            updateControlButtons(progress.taskId, progress.status)

            // 更新子任务进度
            progress.subTaskProgresses.forEach { (subTaskId, subProgress) ->
                updateSubTaskProgress(progress.taskId, subTaskId, subProgress)
            }
        }
    }

    private fun updateControlButtons(taskId: String, status: TaskStatus) {
        taskControlButtons[taskId]?.let { controlBox ->
            val pauseBtn = controlBox.children[0] as Button
            val resumeBtn = controlBox.children[1] as Button
            val cancelBtn = controlBox.children[2] as Button
            val deleteBtn = controlBox.children[3] as Button

            // 获取任务以检查是否支持暂停
            val task = getTaskById(taskId)
            val supportsPause = task?.supportsPause() ?: false

            when (status) {
                TaskStatus.PENDING -> {
                    pauseBtn.isDisable = !supportsPause
                    pauseBtn.text = "暂停"
                    resumeBtn.isDisable = true
                    resumeBtn.text = "继续"
                    cancelBtn.isDisable = false
                    deleteBtn.isDisable = false
                }

                TaskStatus.RUNNING -> {
                    pauseBtn.isDisable = !supportsPause
                    pauseBtn.text = "暂停"
                    resumeBtn.isDisable = true
                    resumeBtn.text = "继续"
                    cancelBtn.isDisable = false
                    deleteBtn.isDisable = false
                }

                TaskStatus.PAUSED -> {
                    pauseBtn.isDisable = true
                    pauseBtn.text = "暂停"
                    resumeBtn.isDisable = false
                    resumeBtn.text = "继续"
                    cancelBtn.isDisable = false
                    deleteBtn.isDisable = false
                }

                TaskStatus.COMPLETED -> {
                    pauseBtn.isDisable = true
                    resumeBtn.isDisable = true
                    cancelBtn.isDisable = true
                    deleteBtn.isDisable = false
                }

                TaskStatus.FAILED -> {
                    pauseBtn.isDisable = true
                    resumeBtn.isDisable = false
                    resumeBtn.text = "重试"
                    cancelBtn.isDisable = true
                    deleteBtn.isDisable = false
                }

                TaskStatus.CANCELLED -> {
                    pauseBtn.isDisable = true
                    resumeBtn.isDisable = false
                    resumeBtn.text = "重试"
                    cancelBtn.isDisable = true
                    deleteBtn.isDisable = false
                }
            }
        }
    }

    // 添加方法来获取任务信息（需要从TaskManager获取）
    private var taskManager: TaskManager<T>? = null

    fun setTaskManager(taskManager: TaskManager<T>) {
        this.taskManager = taskManager
    }

    private fun getTaskById(taskId: String): CompositeTask? {
        return taskManager?.getAllTasks()?.find { it.id == taskId }
    }

    // 更新统计信息显示
    fun updateStatistics(statistics: TaskStatistics) {
        runUI {
            runningCountLabel?.text = "运行中: ${statistics.running}"
            pendingCountLabel?.text = "等待中: ${statistics.pending}"
            completedCountLabel?.text = "已完成: ${statistics.completed}"
            failedCountLabel?.text = "已失败: ${statistics.failed}"
            totalCountLabel?.text = "总计: ${statistics.total}"
        }
    }

    // 单独更新运行中任务数量
    fun updateRunningCount(count: Int) {
        runUI {
            runningCountLabel?.text = "运行中: $count"
        }
    }

    // 单独更新等待中任务数量
    fun updatePendingCount(count: Int) {
        runUI {
            pendingCountLabel?.text = "等待中: $count"
        }
    }

    // 更新子任务ui
    private fun updateSubTaskProgress(taskId: String, subTaskId: String, progress: SubTaskProgress) {
        val taskSubViews = subTaskViews[taskId] ?: return

        if (subTaskId !in taskSubViews) {
            val subContainer = VBox(4.0).applySubTaskCardStyle()

            // 直接显示子任务的名称
            val subLabel = Label(progress.subTaskName).applySubTaskTitleStyle()

            val subProgressBar = ProgressBar(0.0)
            subProgressBar.prefHeight = 4.0
            subProgressBar.prefWidthProperty().bind(subContainer.widthProperty())
            subProgressBar.styleClass.add("progress-bar-ui")
            subProgressBar.styled {
                accentColor("#4f8fff")
            }

            val subStatus = Label("等待中")
            subStatus.applySubStatusStyle()

            subContainer.children.addAll(subLabel, subProgressBar, subStatus)
            taskSubViews[subTaskId] = subContainer
            subTaskContainers[taskId]?.children?.add(subContainer)
        }

        val subContainer = taskSubViews[subTaskId]
        if (subContainer != null && subContainer.children.size >= 3) {
            (subContainer.children[1] as ProgressBar).progress = progress.progress
            (subContainer.children[2] as Label).text = "${progress.status.comment}: ${progress.message}"
        }
    }

    private fun Label.applyTitleStyle(): Label {
        styled {
            fontSize(15.0)
            fontWeight(FontWeight.BOLD)
            textFill("#1f2937")
        }
        return this
    }

    private fun Label.applyStatisticStyle(textColor: String): Label {
        styleClass.addAll("label-ui", "radius-ui")
        styled {
            fontSize(12.0)
            fontWeight(FontWeight.BOLD)
            textFill(textColor)
            padding(3.0, 8.0, 3.0, 8.0)
            backgroundColor("#f8fafc")
            backgroundRadius(12.0)
            borderColor("#e5eaf0")
            borderRadius(12.0)
            borderWidth(1.0)
        }
        return this
    }

    private fun Label.applyTaskTitleStyle(): Label {
        styleClass.addAll("label-ui", "label-ui-small", "label-ui-normal")
        styled {
            fontSize(13.0)
            fontWeight(FontWeight.BOLD)
            textFill("#1f2937")
        }
        return this
    }

    private fun Label.applyStatusStyle(): Label {
        styleClass.addAll("label-ui", "label-ui-small", "radius-ui")
        styled {
            fontSize(12.0)
            textFill("#687385")
        }
        return this
    }

    private fun Label.applySubTaskTitleStyle(): Label {
        styleClass.addAll("label-ui", "label-ui-tiny", "label-ui-normal")
        styled {
            fontSize(12.0)
            fontWeight(FontWeight.BOLD)
            textFill("#3f4a5a")
        }
        return this
    }

    private fun Label.applySubStatusStyle(): Label {
        styleClass.addAll("label-ui", "label-ui-tiny", "radius-ui")
        styled {
            fontSize(10.0)
            textFill("#7d8794")
        }
        return this
    }

    private fun Button.applyActionStyle(variant: String, compact: Boolean = false): Button {
        styleClass.addAll("btn-ui", "btn-ui-small", variant)
        styled {
            fontSize(11.0)
            if (compact) {
                padding(3.0, 8.0, 3.0, 8.0)
            } else {
                padding(5.0, 10.0, 5.0, 10.0)
            }
        }
        return this
    }

    private fun VBox.applyTaskCardStyle(): VBox {
        padding = Insets(12.0)
        styleClass.addAll("radius-ui")
        styled {
            backgroundColor("#ffffff")
            backgroundRadius(6.0)
            borderColor("#e6ebf1")
            borderRadius(6.0)
            borderWidth(1.0)
            effect("dropshadow(gaussian, rgba(15, 23, 42, 0.08), 8, 0, 0, 2)")
        }
        return this
    }

    private fun VBox.applySubTaskCardStyle(): VBox {
        padding = Insets(8.0, 10.0, 8.0, 14.0)
        setMargin(this, Insets(2.0, 0.0, 0.0, 12.0))
        styleClass.addAll("radius-ui")
        styled {
            backgroundColor("#f8fafc")
            backgroundRadius(6.0)
            borderColor("#edf1f5")
            borderRadius(6.0)
            borderWidth(1.0)
        }
        return this
    }

    fun removeTask(taskId: String) {
        runUI {
            taskContainers[taskId]?.let { container ->
                taskPane.children.remove(container)
                taskContainers.remove(taskId)
                taskProgressBars.remove(taskId)
                taskLabels.remove(taskId)
                taskStatuses.remove(taskId)
                taskControlButtons.remove(taskId)
                subTaskViews.remove(taskId)
                subTaskContainers.remove(taskId)
            }
        }
    }

    fun clearTasks() {
        runUI {
            // 保留全局控制面板，只清除任务
            taskPane.children.clear()

            taskContainers.clear()
            taskProgressBars.clear()
            taskLabels.clear()
            taskStatuses.clear()
            taskControlButtons.clear()
            subTaskViews.clear()
            subTaskContainers.clear()
        }
    }
}
