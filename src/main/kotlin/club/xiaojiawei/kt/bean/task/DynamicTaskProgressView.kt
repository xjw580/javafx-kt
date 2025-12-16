package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.controls.ico.FailIco
import club.xiaojiawei.controls.ico.OKIco
import club.xiaojiawei.kt.ext.runUI
import javafx.scene.control.*
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:21
 */

class DynamicTaskProgressView<T : TaskBuilder> : VBox() {
    private val taskContainers = mutableMapOf<String, VBox>()
    private val taskProgressBars = mutableMapOf<String, ProgressBar>()
    private val taskLabels = mutableMapOf<String, Label>()
    private val taskStatuses = mutableMapOf<String, Label>()
    private val taskControlButtons = mutableMapOf<String, HBox>()
    private val subTaskViews = mutableMapOf<String, MutableMap<String, VBox>>()

    // 状态显示标签
    private var runningCountLabel: Label? = null
    private var pendingCountLabel: Label? = null
    private var completedCountLabel: Label? = null
    private var failedCountLabel: Label? = null
    private var totalCountLabel: Label? = null
    private val taskPane = VBox()

    // 任务控制回调
    var onPauseTask: ((String) -> Unit)? = null
    var onResumeTask: ((String) -> Unit)? = null
    var onCancelTask: ((String) -> Unit)? = null
    var onDeleteTask: ((String) -> Unit)? = null

    // 全局控制回调
    var onPauseAll: (() -> Unit)? = null
    var onResumeAll: (() -> Unit)? = null
    var onCancelAll: (() -> Unit)? = null
    var onDeleteAll: (() -> Unit)? = null

    init {
        createGlobalControlPanel()
        maxWidth = 650.0
    }

    private fun createGlobalControlPanel() {
        val globalControlPanel = HBox(10.0).apply {
            style =
                "-fx-padding: 10; -fx-background-color: #e8e8e8; -fx-border-color: #cccccc; -fx-border-width: 0 0 1 0;"
        }

        val titleLabel = Label("任务管理器")
        titleLabel.style = "-fx-font-weight: bold; -fx-font-size: 16px;"

        // 添加状态统计显示
        val statisticsContainer = HBox(15.0)
        statisticsContainer.style = "-fx-alignment: center-left;"

        val runningLabel = Label("运行中: 0")
        runningLabel.style = "-fx-font-size: 12px; -fx-text-fill: #4CAF50; -fx-font-weight: bold;"

        val pendingLabel = Label("等待中: 0")
        pendingLabel.style = "-fx-font-size: 12px; -fx-text-fill: #FF9800; -fx-font-weight: bold;"

        val completedLabel = Label("已完成: 0")
        completedLabel.style = "-fx-font-size: 12px; -fx-text-fill: #2196F3; -fx-font-weight: bold;"

        val failedLabel = Label("已完成: 0")
        failedLabel.style = "-fx-font-size: 12px; -fx-text-fill: #fa7a7a; -fx-font-weight: bold;"

        val totalLabel = Label("总计: 0")
        totalLabel.style = "-fx-font-size: 12px; -fx-text-fill: #666666; -fx-font-weight: bold;"

        statisticsContainer.children.addAll(runningLabel, pendingLabel, completedLabel, failedLabel, totalLabel)

        // 存储标签引用以便更新
        this.runningCountLabel = runningLabel
        this.pendingCountLabel = pendingLabel
        this.completedCountLabel = completedLabel
        this.failedCountLabel = failedLabel
        this.totalCountLabel = totalLabel

        val buttonContainer = HBox(5.0)
        buttonContainer.style = "-fx-alignment: center-right;"

        val pauseAllBtn = Button("暂停所有")
        pauseAllBtn.style =
            "-fx-font-size: 11px; -fx-padding: 5 10 5 10; -fx-background-color: #FF9800; -fx-text-fill: white;"
        pauseAllBtn.setOnAction { onPauseAll?.invoke() }

        val resumeAllBtn = Button("继续所有")
        resumeAllBtn.style =
            "-fx-font-size: 11px; -fx-padding: 5 10 5 10; -fx-background-color: #4CAF50; -fx-text-fill: white;"
        resumeAllBtn.setOnAction { onResumeAll?.invoke() }

        val cancelAllBtn = Button("取消所有")
        cancelAllBtn.style =
            "-fx-font-size: 11px; -fx-padding: 5 10 5 10; -fx-background-color: #f44336; -fx-text-fill: white;"
        cancelAllBtn.setOnAction { onCancelAll?.invoke() }

        val deleteAllBtn = Button("删除所有")
        deleteAllBtn.style =
            "-fx-font-size: 11px; -fx-padding: 5 10 5 10; -fx-background-color: #9C27B0; -fx-text-fill: white;"
        deleteAllBtn.setOnAction { onDeleteAll?.invoke() }

        buttonContainer.children.addAll(pauseAllBtn, resumeAllBtn, cancelAllBtn, deleteAllBtn)

        // 布局：标题 - 统计信息 - 按钮
        val leftContainer = VBox(5.0)
        leftContainer.children.addAll(titleLabel, statisticsContainer)

        HBox.setHgrow(leftContainer, Priority.ALWAYS)
        globalControlPanel.children.addAll(leftContainer, buttonContainer)

        children.addAll(globalControlPanel, ScrollPane(taskPane).apply {
            maxHeight = 800.0
            isFitToWidth = true
        })
    }

    fun addTask(taskList: List<CompositeTask>) {
        runUI {
            // 批量收集新增的容器，减少UI更新次数
            val newContainers = mutableListOf<VBox>()

            for (task in taskList) {
                // 如果任务已存在，跳过继续处理下一个（修复：使用 continue 而不是 return）
                if (taskContainers.containsKey(task.id)) continue

                val container = VBox(8.0)
                container.style =
                    "-fx-border-color: #cccccc; -fx-border-width: 1; -fx-padding: 10; -fx-background-color: #fafafa;"

                // 任务头部（标题和控制按钮）
                val headerBox = HBox(10.0)
                headerBox.style = "-fx-alignment: center-left;"

                val label = Label("任务: ${task.name}")
                label.style = "-fx-font-weight: bold; -fx-font-size: 14px;"

                val controlBox = HBox(5.0)
                controlBox.style = "-fx-alignment: center-right;"

                val pauseBtn = Button("暂停")
                pauseBtn.isDisable = true
                pauseBtn.style = "-fx-font-size: 11px; -fx-padding: 3 8 3 8;"
                pauseBtn.setOnAction { onPauseTask?.invoke(task.id) }

                val resumeBtn = Button("继续")
                resumeBtn.style = "-fx-font-size: 11px; -fx-padding: 3 8 3 8;"
                resumeBtn.setOnAction { onResumeTask?.invoke(task.id) }
                resumeBtn.isDisable = true // 初始状态禁用

                val cancelBtn = Button("取消")
                cancelBtn.style =
                    "-fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-color: #f44336; -fx-text-fill: white;"
                cancelBtn.setOnAction { onCancelTask?.invoke(task.id) }

                val deleteBtn = Button("删除")
                deleteBtn.style =
                    "-fx-font-size: 11px; -fx-padding: 3 8 3 8; -fx-background-color: #9C27B0; -fx-text-fill: white;"
                deleteBtn.setOnAction { onDeleteTask?.invoke(task.id) }

                controlBox.children.addAll(pauseBtn, resumeBtn, cancelBtn, deleteBtn)

                // 使用HBox的grow属性让label占据剩余空间
                HBox.setHgrow(label, Priority.ALWAYS)
                headerBox.children.addAll(label, controlBox)

                val progressBar = ProgressBar(0.0)
                progressBar.prefWidthProperty().bind(container.widthProperty())
                progressBar.prefHeight = 11.0
                progressBar.style = "-fx-accent: #80ce80!important;"

                val statusLabel = Label("等待中")
                statusLabel.style = "-fx-font-size: 12px; -fx-text-fill: #666666;"
                statusLabel.contentDisplay = ContentDisplay.RIGHT

                container.children.addAll(headerBox, progressBar, statusLabel)

                taskContainers[task.id] = container
                taskProgressBars[task.id] = progressBar
                taskLabels[task.id] = label
                taskStatuses[task.id] = statusLabel
                taskControlButtons[task.id] = controlBox
                subTaskViews[task.id] = mutableMapOf()

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
                it.text = "${progress.status.name}: ${progress.message}"
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
    private var taskManager: DynamicTaskManager<T>? = null

    fun setTaskManager(taskManager: DynamicTaskManager<T>) {
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

    private fun updateSubTaskProgress(taskId: String, subTaskId: String, progress: SubTaskProgress) {
        val taskSubViews = subTaskViews[taskId] ?: return

        if (subTaskId !in taskSubViews) {
            val subContainer = VBox(3.0)
            subContainer.style =
                "-fx-padding: 0 0 0 20; -fx-background-color: #f0f0f0; -fx-border-radius: 3; -fx-padding: 5;"

            // 直接显示子任务的名称
            val subLabel = Label("  └ ${progress.subTaskName}")
            subLabel.style = "-fx-font-size: 12px;"

            val subProgressBar = ProgressBar(0.0)
            subProgressBar.prefHeight = 9.0
            subProgressBar.prefWidthProperty().bind(subContainer.widthProperty())
            subProgressBar.style = "-fx-accent: #4f9fff!important;"

            val subStatus = Label("等待中")
            subStatus.style = "-fx-font-size: 10px; -fx-text-fill: #888888;"

            subContainer.children.addAll(subLabel, subProgressBar, subStatus)
            taskSubViews[subTaskId] = subContainer
            taskContainers[taskId]?.children?.add(subContainer)
        }

        val subContainer = taskSubViews[subTaskId]
        if (subContainer != null && subContainer.children.size >= 3) {
            (subContainer.children[1] as ProgressBar).progress = progress.progress
            (subContainer.children[2] as Label).text = "${progress.status.name}: ${progress.message}"
        }
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
        }
    }
}