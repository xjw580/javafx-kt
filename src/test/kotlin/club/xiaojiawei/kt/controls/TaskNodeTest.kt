package club.xiaojiawei.kt.controls

import club.xiaojiawei.controls.ico.UpdateIco
import club.xiaojiawei.kt.bean.task.*
import club.xiaojiawei.kt.dsl.launchApp
import club.xiaojiawei.kt.dsl.stackPane
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import java.util.*
import kotlin.test.Test

/**
 * @author 肖嘉威
 * @date 2026/6/10 9:05
 */
class TaskNodeTest {

    class TestTaskBuilder : TaskBuilder() {

        @JvmOverloads
        fun test(
            id: String = UUID.randomUUID().toString(),
            name: String,
            completeCallback: ((SubTaskResult<TestSubTaskData?>) -> Unit)? = null
        ) {
            subTasks.add(TestSubTask(id, name, completeCallback))
        }
    }

    data class TestSubTaskData(val msg: String) : SubTaskData

    class TestSubTask(
        override val id: String,
        override val name: String,
        override val completeCallback: ((SubTaskResult<TestSubTaskData?>) -> Unit)?,
    ) : SubTask<TestSubTaskData?> {
        override val resourceLock: String? = null
        override val resourcePermit: Int = 0
        override val supportsPause: Boolean = true

        override suspend fun execute(
            context: TaskExecutionContext,
            progressCallback: (SubTaskProgress) -> Unit
        ): SubTaskResult<TestSubTaskData?> {
            progressCallback(
                SubTaskProgress(
                    context.taskContext.currentTaskId,
                    id,
                    name,
                    TaskStatus.RUNNING,
                    0.0,
                    "开始测试"
                )
            )
            return try {
                for (i in 1..100) {
                    context.checkPauseAndWait()
                    if (i > 50) {
                        10 / 0
                    }
                    delay(150)
                    progressCallback(
                        SubTaskProgress(
                            context.taskContext.currentTaskId,
                            id,
                            name,
                            TaskStatus.RUNNING,
                            i / 100.0,
                            "测试进度:${i}%"
                        )
                    )
                }
                SubTaskResult(true, null, null, System.currentTimeMillis())
            } catch (e: CancellationException) {
                SubTaskResult(false, null, "测试" + e.message, System.currentTimeMillis())
            } catch (e: Exception) {
                SubTaskResult(false, null, e.message, System.currentTimeMillis())
            }
        }
    }

    @Test
    fun testAddTask() {
        launchApp {
            root {
                stackPane {
                    padding(20.0)
                    +TaskNode<TestTaskBuilder>().apply {
                        ico = UpdateIco()
                        taskController = TaskController { TestTaskBuilder() }
                        addTask("test add task") {
                            test(name = "test")
                        }
                    }
                }
            }
            style()
        }
    }

}