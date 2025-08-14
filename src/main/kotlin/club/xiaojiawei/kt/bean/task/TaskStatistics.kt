package club.xiaojiawei.kt.bean.task

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:19
 */

data class TaskStatistics(
    val pending: Int,
    val running: Int,
    val completed: Int,
    val failed: Int,
    val cancelled: Int,
    val paused: Int,
    val total: Int
) {
    val active: Int get() = pending + running + paused // 活跃任务数（未完成的）
    val finished: Int get() = completed + failed + cancelled // 已结束的任务数
}