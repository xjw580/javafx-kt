package club.xiaojiawei.kt.bean.task

import java.util.UUID

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:22
 */
class TaskController<T : TaskBuilder>(taskBuilderProvider: (() -> T)) {
    private val taskManager = DynamicTaskManager<T>(taskBuilderProvider)
    private val progressView = DynamicTaskProgressView<T>()

    init {
        // 连接任务管理器和UI
        taskManager.addProgressCallback { progress ->
            progressView.updateProgress(progress)
        }

        taskManager.addTaskAddedCallback { task ->
            progressView.addTask(task)
        }

        // 让UI能够访问任务管理器来检查任务属性
        progressView.setTaskManager(taskManager)

        // 设置状态监听
        taskManager.addStatisticsCallback { statistics ->
            progressView.updateStatistics(statistics)
        }

        taskManager.addRunningCountCallback { count ->
            progressView.updateRunningCount(count)
        }

        taskManager.addPendingCountCallback { count ->
            progressView.updatePendingCount(count)
        }

        // 设置UI控制回调
        progressView.onPauseTask = { taskId ->
            taskManager.pauseTask(taskId)
            println("用户暂停任务: $taskId")
        }

        progressView.onResumeTask = { taskId ->
            taskManager.resumeTask(taskId)
            println("用户恢复任务: $taskId")
        }

        progressView.onCancelTask = { taskId ->
            taskManager.cancelTask(taskId)
            println("用户取消任务: $taskId")
        }

        progressView.onDeleteTask = { taskId ->
            taskManager.deleteTask(taskId)
            progressView.removeTask(taskId)
            println("用户删除任务: $taskId")
        }

        // 设置全局控制回调
        progressView.onPauseAll = {
            taskManager.pauseAllTasks()
            println("用户暂停所有任务")
        }

        progressView.onResumeAll = {
            taskManager.resumeAllTasks()
            println("用户恢复所有任务")
        }

        progressView.onCancelAll = {
            taskManager.cancelAllTasks()
            println("用户取消所有任务")
        }

        progressView.onDeleteAll = {
            taskManager.deleteAllTasks()
            progressView.clearTasks()
            println("用户删除所有任务")
        }
    }

    fun addTask(name: String, configure: TaskBuilder.() -> Unit) {
        taskManager.addTask(UUID.randomUUID().toString(), name, configure)
    }

    fun addTask(id: String, name: String, configure: TaskBuilder.() -> Unit) {
        taskManager.addTask(id, name, configure)
    }

    fun deleteTask(taskId: String) {
        taskManager.deleteTask(taskId)
    }

    fun getProgressView(): DynamicTaskProgressView<T> = progressView

    fun getTaskManager(): DynamicTaskManager<T> = taskManager
}
