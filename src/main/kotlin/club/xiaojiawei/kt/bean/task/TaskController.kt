package club.xiaojiawei.kt.bean.task

import java.util.*

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

        progressView.onRetryTask = { taskId ->
            taskManager.retryTask(taskId)
            println("用户重试任务: $taskId")
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

        progressView.onRetryAll = {
            taskManager.retryAllTasks()
            println("用户重试所有任务")
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

    /**
     * 添加批量完成回调监听器
     * 当多个任务在短时间内完成时，会批量通知，减少UI线程切换次数
     * @param callback 批量完成回调，参数为完成的任务列表
     */
    fun addBatchCompletionCallback(callback: (List<TaskCompletionInfo>) -> Unit) {
        taskManager.addBatchCompletionCallback(callback)
    }

    /**
     * 移除批量完成回调监听器
     */
    fun removeBatchCompletionCallback(callback: (List<TaskCompletionInfo>) -> Unit) {
        taskManager.removeBatchCompletionCallback(callback)
    }
}
