package club.xiaojiawei.kt.bean.task

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:17
 */

data class TaskContext(
    val results: MutableMap<String, TaskResult> = mutableMapOf(),
    var currentTaskId: String = ""
)


// 子任务执行上下文，包含暂停检查功能和数据传递
data class TaskExecutionContext(
    val taskContext: TaskContext,
    val parentTask: CompositeTask,
    val previousResults: Map<String, SubTaskResult<out SubTaskData?>> = emptyMap() // 前面子任务的结果
) {
    // 子任务可以通过这个方法检查是否应该暂停
    suspend fun checkPauseAndWait() {
        while (checkPause()) {
            delay(100) // 等待100ms后再检查
        }

        // 检查是否被取消
        if (isCancelled()) {
            throw CancellationException("任务已被取消")
        }
    }

    fun checkPause(): Boolean {
        return isPaused() && !isCancelled()
    }

    fun isCancelled(): Boolean {
        return parentTask.job?.isCancelled == true
    }

    fun isPaused(): Boolean {
        return parentTask.isPaused()
    }

    // 新增：检查是否应该停止执行（暂停或取消）
    fun shouldStop(): Boolean {
        return isPaused() || isCancelled()
    }

    // 泛型安全的获取前面子任务的结果
    inline fun <reified T> getPreviousResult(subTaskId: String): T? {
        return previousResults[subTaskId]?.getDataAs<T>()
    }

    // 获取最近一个子任务的结果
    inline fun <reified T> getLastResult(): T? {
        return previousResults.values.lastOrNull()?.getDataAs<T>()
    }

    // 获取第一个成功的指定类型的结果
    inline fun <reified T> getFirstResultOfType(): T? {
        return previousResults.values.firstOrNull { it.success && it.hasDataType<T>() }?.getDataAs<T>()
    }

    // 获取所有指定类型的结果
    inline fun <reified T> getAllResultsOfType(): List<T> {
        return previousResults.values.mapNotNull { result ->
            if (result.success) result.getDataAs<T>() else null
        }
    }
}
