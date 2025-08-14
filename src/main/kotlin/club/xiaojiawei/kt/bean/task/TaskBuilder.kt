package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.kt.utils.runUI

/**
 * @author 肖嘉威
 * @date 2025/8/12 15:20
 */

open class TaskBuilder {
    var id: String = ""
    var name: String = ""
    var priority: Int = 0
    var dependencies: MutableList<String> = mutableListOf()
    protected val subTasks: MutableList<SubTask<out SubTaskData?>> = mutableListOf()
    protected var completeCallback: ((Boolean) -> Unit)? = null

    /**
     * 依赖任务
     * @param taskIds 任务id，只有当这些任务执行完，此任务才会开始执行
     */
    fun dependsOn(taskIds: List<String>) {
        dependencies.addAll(taskIds)
    }

    /**
     * 设置完成回调，成功与否都会触发回调
     */
    fun onComplete(callback: ((Boolean) -> Unit)?) {
        completeCallback = callback
    }

    /**
     * 设置完成回调，成功与否都会在ui线程中触发回调
     */
    fun onCompleteByUI(callback: ((Boolean) -> Unit)?) {
        completeCallback = callback?.let {
            {
                runUI {
                    callback(it)
                }
            }
        }
    }

    internal fun build(): CompositeTask {
        return CompositeTask(id, name, subTasks.toList(), dependencies.toList(), priority, completeCallback)
    }
}

fun <T : TaskBuilder> task(id: String, name: String, t: T, configure: T.() -> Unit): CompositeTask {
    val builder = t.apply {
        this.id = id
        this.name = name
    }
    builder.configure()
    return builder.build()
}