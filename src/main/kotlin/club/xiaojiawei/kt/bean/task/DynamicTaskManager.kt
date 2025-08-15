package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.kt.config.log
import club.xiaojiawei.kt.utils.runUI
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import java.io.Closeable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicBoolean

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:20
 */

class DynamicTaskManager<T : TaskBuilder>(
    var taskBuilderProvider: (() -> T),
    private val scope: CoroutineScope = GlobalScope
) : Closeable {
    private val tasks = ConcurrentHashMap<String, CompositeTask>()
    private val taskQueue = Channel<CompositeTask>(Channel.UNLIMITED)
    val context = TaskContext() // 改为 public 以便外部访问
    private val isRunning = AtomicBoolean(false)
    private val progressCallbacks = mutableListOf<(TaskProgress) -> Unit>()

    // 状态监听回调
    private val statisticsCallbacks = mutableListOf<(TaskStatistics) -> Unit>()
    private val runningCountCallbacks = mutableListOf<(Int) -> Unit>()
    private val pendingCountCallbacks = mutableListOf<(Int) -> Unit>()

    // 当前统计数据
    @Volatile
    private var currentStatistics = TaskStatistics(0, 0, 0, 0, 0, 0, 0)

    private var processingJob: Job? = null

    init {
        startTaskProcessor()
        startTimer(100)
    }

    fun startTimer(mills: Long) {
        scope.launch {
            while (isActive) {
                val progresses = latestProgress.toList()
                latestProgress.clear()
                runUI {
                    for (progress in progresses) {
                        progressCallbacks.forEach { it(progress) }
                        updateStatistics()
                    }
                }
                delay(mills)
            }
        }
    }

    fun stopTimer() {
        scope.cancel()
    }

    fun addProgressCallback(callback: (TaskProgress) -> Unit) {
        progressCallbacks.add(callback)
    }

    // 添加状态统计监听
    fun addStatisticsCallback(callback: (TaskStatistics) -> Unit) {
        statisticsCallbacks.add(callback)
        // 立即发送当前状态
        callback(currentStatistics)
    }

    // 添加运行中任务数量监听
    fun addRunningCountCallback(callback: (Int) -> Unit) {
        runningCountCallbacks.add(callback)
        // 立即发送当前数量
        callback(currentStatistics.running)
    }

    // 添加等待中任务数量监听
    fun addPendingCountCallback(callback: (Int) -> Unit) {
        pendingCountCallbacks.add(callback)
        // 立即发送当前数量
        callback(currentStatistics.pending)
    }

    // 移除监听器
    fun removeStatisticsCallback(callback: (TaskStatistics) -> Unit) {
        statisticsCallbacks.remove(callback)
    }

    fun removeRunningCountCallback(callback: (Int) -> Unit) {
        runningCountCallbacks.remove(callback)
    }

    fun removePendingCountCallback(callback: (Int) -> Unit) {
        pendingCountCallbacks.remove(callback)
    }

    fun CoroutineScope.startTimer(intervalMs: Long, action: suspend () -> Unit): Job {
        return launch {
            while (isActive) { // 协程活着就一直执行
                action()
                delay(intervalMs)
            }
        }
    }

    private var latestProgress = Collections.synchronizedList(mutableListOf<TaskProgress>())

    private fun notifyProgress(progress: TaskProgress) {
        latestProgress.add(progress)
    }

    // 更新并通知统计信息
    // 更新并通知统计信息
    private fun updateStatistics() {
        val newStatistics = calculateStatistics()
        val oldStatistics = currentStatistics
        currentStatistics = newStatistics

        // 通知统计变化
        if (newStatistics != oldStatistics) {
            statisticsCallbacks.forEach { it(newStatistics) }
        }

        // 通知运行中任务数量变化
        if (newStatistics.running != oldStatistics.running) {
            runningCountCallbacks.forEach { it(newStatistics.running) }
        }

        // 通知等待中任务数量变化
        if (newStatistics.pending != oldStatistics.pending) {
            pendingCountCallbacks.forEach { it(newStatistics.pending) }
        }
    }

    // 计算当前统计信息
    private fun calculateStatistics(): TaskStatistics {
        val taskList = tasks.values.toList()
        return TaskStatistics(
            pending = taskList.count { it.status == TaskStatus.PENDING },
            running = taskList.count { it.status == TaskStatus.RUNNING },
            completed = taskList.count { it.status == TaskStatus.COMPLETED },
            failed = taskList.count { it.status == TaskStatus.FAILED },
            cancelled = taskList.count { it.status == TaskStatus.CANCELLED },
            paused = taskList.count { it.status == TaskStatus.PAUSED },
            total = taskList.size
        )
    }

    // 获取当前统计信息
    fun getCurrentStatistics(): TaskStatistics = currentStatistics

    // 添加新任务
    @Synchronized
    fun addTask(taskList: List<CompositeTask>) {
        for (task in taskList) {
            tasks[task.id] = task
            // 立即通知UI显示任务

            scope.launch {
                taskQueue.send(task)
            }
            println("任务已添加到队列: ${task.name} (支持暂停: ${task.supportsPause()})")
        }

        notifyTaskAdded(taskList)
        // 更新统计信息
        updateStatistics()
    }

    class TaskParam<T>(
        val id: String,
        val name: String,
        val configure: T.() -> Unit
    )

    fun addTasks(taskList: List<TaskParam<T>>) {
        addTask(taskList.map { task(it.id, it.name, taskBuilderProvider(), it.configure) })
    }

    // 使用DSL添加任务
    fun addTask(id: String, name: String, configure: T.() -> Unit) {
        val task = task(id, name, taskBuilderProvider(), configure)
        addTask(listOf(task))
    }

    fun deleteTask(taskId: String) {
        tasks[taskId]?.let { task ->
            task.cancel()                  // 取消任务执行
            tasks.remove(taskId)           // 从任务列表移除
            context.results.remove(taskId) // 从结果缓存移除
            notifyProgress(TaskProgress(taskId, TaskStatus.CANCELLED, task.progress, "任务已删除"))
            // 更新统计信息
            updateStatistics()
        }
    }

    // 任务添加回调
    private val taskAddedCallbacks = mutableListOf<(List<CompositeTask>) -> Unit>()

    fun addTaskAddedCallback(callback: (List<CompositeTask>) -> Unit) {
        taskAddedCallbacks.add(callback)
    }

    private fun notifyTaskAdded(task: List<CompositeTask>) {
        taskAddedCallbacks.forEach { it(task) }
    }

    // 取消任务
    fun cancelTask(taskId: String) {
        tasks[taskId]?.let { task ->
            task.cancel()
            notifyProgress(TaskProgress(taskId, TaskStatus.CANCELLED, task.progress, "任务已取消"))
        }
    }

    // 暂停任务
    fun pauseTask(taskId: String) {
        tasks[taskId]?.let { task ->
            if (task.supportsPause()) {
                task.pause()
                notifyProgress(TaskProgress(taskId, task.status, task.progress, "用户暂停任务"))
            } else {
                println("任务 $taskId 不支持暂停")
            }
        }
    }

    // 恢复任务
    fun resumeTask(taskId: String) {
        tasks[taskId]?.let { task ->
            if (task.status == TaskStatus.PAUSED) {
                task.resume()
                // 立即通知UI更新状态
                notifyProgress(TaskProgress(taskId, task.status, task.progress, "用户恢复任务"))
            } else if (task.status == TaskStatus.FAILED) {
                task.resume()
                // 立即通知UI更新状态
                notifyProgress(TaskProgress(taskId, task.status, task.progress, "用户重试任务"))
                scope.launch {
                    taskQueue.send(task)
                }
            }
        }
    }

    // 获取任务状态
    fun getTaskStatus(taskId: String): TaskStatus? {
        return tasks[taskId]?.status
    }

    // 获取所有任务
    fun getAllTasks(): List<CompositeTask> {
        return tasks.values.toList()
    }

    // 启动任务处理器
    private fun startTaskProcessor() {
        processingJob = scope.launch {
            isRunning.set(true)

            while (isRunning.get()) {
                try {
                    val task = taskQueue.receive()

                    // 检查任务是否被暂停
                    if (task.status == TaskStatus.PAUSED) {
                        // 暂停的任务重新入队等待
                        delay(1000)
                        taskQueue.send(task)
                        continue
                    }

                    // 检查依赖是否满足
                    if (!areDependenciesSatisfied(task)) {
                        // 依赖未满足，重新加入队列稍后处理
                        delay(1000)
                        taskQueue.send(task)
                        continue
                    }

                    if (task.status == TaskStatus.CANCELLED) continue

                    // 执行任务
                    val job = launch {
                        val result = task.execute(context) { progress ->
                            notifyProgress(progress)
                        }
                        context.results[task.id] = result
                        task.completeCallback?.invoke(result.success)
                        println("任务完成: ${task.name}, 结果: ${if (result.success) "成功" else "失败"}")
                    }

                    task.setJob(job)

                } catch (e: Exception) {
                    log.error(e) { "处理任务时发生错误" }
                }
            }
        }
    }

    private fun areDependenciesSatisfied(task: CompositeTask): Boolean {
        return task.dependencies.all { depId ->
            context.results[depId]?.success == true
        }
    }

    // 停止任务管理器
    fun shutdown() {
        isRunning.set(false)
        processingJob?.cancel()
        tasks.values.forEach { it.cancel() }
        taskQueue.close()
    }

    // 获取队列中等待的任务数量
    fun getPendingTasksCount(): Int {
        return tasks.values.count { it.status == TaskStatus.PENDING }
    }

    // 获取正在运行的任务数量
    fun getRunningTasksCount(): Int {
        return tasks.values.count { it.status == TaskStatus.RUNNING }
    }

    fun getNormalTasksCount(): Int {
        return tasks.values.count { it.status == TaskStatus.RUNNING || it.status == TaskStatus.PENDING }
    }

    // 全局控制方法
    fun pauseAllTasks() {
        tasks.values.forEach { task ->
            if ((task.status == TaskStatus.RUNNING || task.status == TaskStatus.PENDING) && task.supportsPause()) {
                pauseTask(task.id)
            }
        }
    }

    fun resumeAllTasks() {
        tasks.values.forEach { task ->
            if (task.status == TaskStatus.PAUSED || task.status == TaskStatus.FAILED) {
                resumeTask(task.id)
            }
        }
    }

    fun cancelAllTasks() {
        tasks.values.forEach { task ->
            if (task.status != TaskStatus.COMPLETED && task.status != TaskStatus.CANCELLED) {
                cancelTask(task.id)
            }
        }
    }

    fun deleteAllTasks() {
        val taskIds = tasks.keys.toList()
        taskIds.forEach { taskId ->
            deleteTask(taskId)
        }
    }

    override fun close() {
        stopTimer()
    }
}