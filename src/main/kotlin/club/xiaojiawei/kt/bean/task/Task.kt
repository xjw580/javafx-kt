package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.kt.bean.ResourceLockManager
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.sync.withPermit
import kotlin.math.max

interface SubTask<T : SubTaskData?> {
    val id: String
    val name: String
    val resourceLock: String?
    val resourcePermit: Int
    val supportsPause: Boolean // 新增：是否支持暂停
    val completeCallback: ((SubTaskResult<T>) -> Unit)?

    suspend fun executeAndCallback(
        context: TaskExecutionContext,
        progressCallback: (SubTaskProgress) -> Unit
    ): SubTaskResult<T> {
        val res = execute(context, progressCallback)
        completeCallback?.invoke(res)
        return res
    }

    suspend fun execute(context: TaskExecutionContext, progressCallback: (SubTaskProgress) -> Unit): SubTaskResult<T>
}

data class CompositeTask(
    val id: String,
    val name: String,
    val subTasks: List<SubTask<out SubTaskData?>>,
    val dependencies: List<String> = emptyList(),
    val priority: Int = 0, // 优先级，数字越大优先级越高
    var completeCallback: ((Boolean) -> Unit)?
) {
    @Volatile
    var status: TaskStatus = TaskStatus.PENDING
        private set

    @Volatile
    var progress: Double = 0.0
        private set

    @Volatile
    var job: Job? = null
        private set

    var currentSubTask: SubTask<out SubTaskData?>? = null

    fun isPaused(): Boolean = status == TaskStatus.PAUSED

    // 检查任务是否支持暂停（所有子任务都支持暂停才支持）
    fun supportsPause(): Boolean = currentSubTask?.supportsPause ?: false

    suspend fun execute(context: TaskContext, progressCallback: (TaskProgress) -> Unit): TaskResult {
        currentSubTask = null
        context.currentTaskId = id
        status = TaskStatus.RUNNING
        progressCallback(TaskProgress(id, TaskStatus.RUNNING, 0.0, "开始执行任务"))

        val subTaskResults = mutableMapOf<String, SubTaskResult<out SubTaskData?>>()
        val subTaskProgresses = mutableMapOf<String, SubTaskProgress>()

        try {
            subTasks.forEachIndexed { index, subTask ->
                currentSubTask = subTask
                // 检查是否被取消
                if (job?.isCancelled == true) {
                    status = TaskStatus.CANCELLED
                    progressCallback(TaskProgress(id, TaskStatus.CANCELLED, progress, "任务已取消"))
                    return TaskResult(false, emptyMap(), "任务已取消", subTaskResults)
                }

                val subTaskProgressCallback: (SubTaskProgress) -> Unit = { subProgress ->
                    // 只有当任务未暂停时才更新进度
                    if (status != TaskStatus.PAUSED && status != TaskStatus.CANCELLED) {
                        subTaskProgresses[subTask.id] = subProgress
                        progress = (index + max(subProgress.progress, 0.0)) / subTasks.size
                        progressCallback(
                            TaskProgress(
                                id, TaskStatus.RUNNING, progress,
                                "执行子任务: ${subTask.name}", subTaskProgresses.toMap()
                            )
                        )
                    }
                }

                // 创建子任务执行上下文，传递前面子任务的结果
                val taskExecutionContext = TaskExecutionContext(context, this, subTaskResults.toMap())
                val resourceLock = subTask.resourceLock
                val result = if (resourceLock != null) {
                    val lock = ResourceLockManager.getLock(resourceLock, subTask.resourcePermit)
                    lock.withPermit {
                        subTask.executeAndCallback(taskExecutionContext, subTaskProgressCallback)
                    }
                } else {
                    subTask.executeAndCallback(taskExecutionContext, subTaskProgressCallback)
                }

                subTaskResults[subTask.id] = result

                if (!result.success) {
                    subTaskProgresses[subTask.id] = SubTaskProgress(
                        id,
                        subTask.id,
                        subTaskProgresses[subTask.id]?.subTaskName ?: "",
                        TaskStatus.FAILED,
                        0.0
                    )
                    status = TaskStatus.FAILED
                    progressCallback(
                        TaskProgress(
                            id, TaskStatus.FAILED, progress,
                            "子任务失败: ${subTask.name}", subTaskProgresses.toMap()
                        )
                    )
                    return TaskResult(false, emptyMap(), result.error, subTaskResults)
                }
            }
            currentSubTask = null

            status = TaskStatus.COMPLETED
            progress = 1.0
            progressCallback(TaskProgress(id, TaskStatus.COMPLETED, 1.0, "任务完成", subTaskProgresses.toMap()))
            return TaskResult(true, emptyMap(), null, subTaskResults)

        } catch (_: CancellationException) {
            status = TaskStatus.CANCELLED
            progressCallback(TaskProgress(id, TaskStatus.CANCELLED, progress, "任务已取消"))
            return TaskResult(false, emptyMap(), "任务已取消", subTaskResults)
        } catch (e: Exception) {
            status = TaskStatus.FAILED
            progressCallback(TaskProgress(id, TaskStatus.FAILED, progress, "任务执行异常: ${e.message}"))
            return TaskResult(false, emptyMap(), e.message, subTaskResults)
        }
    }

    fun setJob(job: Job) {
        this.job = job
    }

    fun cancel() {
        job?.cancel()
        status = TaskStatus.CANCELLED
    }

    fun pause() {
        if ((status == TaskStatus.RUNNING || status == TaskStatus.PENDING) && supportsPause()) {
            status = TaskStatus.PAUSED
        }
    }

    fun resume() {
        if (status == TaskStatus.PAUSED) {
            status = TaskStatus.RUNNING
        } else if (status == TaskStatus.FAILED) {
            status = TaskStatus.PENDING
        }
    }
}
