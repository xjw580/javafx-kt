package club.xiaojiawei.kt.bean.task

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:13
 */

interface SubTaskData

data class SubTaskResult<T : SubTaskData?>(
    val success: Boolean,
    val data: T? = null,
    val error: String? = null,
    val executionTime: Long = 0L
) {
    // 泛型安全的数据获取方法
    inline fun <reified T> getDataAs(): T? {
        return data as? T
    }

    // 检查数据类型
    inline fun <reified T> hasDataType(): Boolean {
        return data is T
    }
}

data class TaskResult(
    val success: Boolean,
    val data: Map<String, Any> = emptyMap(),
    val error: String? = null,
    val subTaskResults: Map<String, SubTaskResult<out SubTaskData?>> = emptyMap()
) {
    // 泛型安全的数据获取方法
    inline fun <reified T> getDataAs(key: String): T? {
        return data[key] as? T
    }

    // 获取子任务结果的数据
    inline fun <reified T> getSubTaskDataAs(subTaskId: String): T? {
        return subTaskResults[subTaskId]?.getDataAs<T>()
    }
}