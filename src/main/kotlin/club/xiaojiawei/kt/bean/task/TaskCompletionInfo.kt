package club.xiaojiawei.kt.bean.task

/**
 * 任务完成信息，用于批量通知任务完成状态
 * @author 肖嘉威
 * @date 2025/8/12 15:19
 */
data class TaskCompletionInfo(
    val taskId: String,
    val taskName: String,
    val success: Boolean,
    /**
     * 子任务结果列表
     * Key: 子任务ID，Value: 子任务结果
     */
    val subTaskResults: Map<String, SubTaskResult<out SubTaskData?>> = emptyMap(),
    /**
     * 任务错误信息（如果失败）
     */
    val error: String? = null
) {
    /**
     * 获取成功的子任务数量
     */
    fun getSuccessfulSubTaskCount(): Int = subTaskResults.values.count { it.success }

    /**
     * 获取失败的子任务数量
     */
    fun getFailedSubTaskCount(): Int = subTaskResults.values.count { !it.success }

    /**
     * 检查是否所有子任务都成功
     */
    fun areAllSubTasksSuccessful(): Boolean = subTaskResults.values.all { it.success }

    /**
     * 获取失败的子任务ID列表
     */
    fun getFailedSubTaskIds(): List<String> = 
        subTaskResults.filter { !it.value.success }.keys.toList()
}
