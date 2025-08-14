package club.xiaojiawei.kt.bean.task

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:12
 */

data class TaskProgress(
    val taskId: String,
    val status: TaskStatus,
    val progress: Double = 0.0,
    val message: String = "",
    val subTaskProgresses: Map<String, SubTaskProgress> = emptyMap()
)

data class SubTaskProgress(
    val taskId: String,
    val subTaskId: String,
    val subTaskName: String,
    val status: TaskStatus,
    val progress: Double = 0.0,
    val message: String = ""
)
