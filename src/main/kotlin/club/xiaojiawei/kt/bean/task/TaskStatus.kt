package club.xiaojiawei.kt.bean.task

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:12
 */
enum class TaskStatus(val comment: String) { PENDING("等待中"), RUNNING("运行中"), COMPLETED("已完成"), FAILED("失败"), CANCELLED("被取消"), PAUSED("暂停") }