package club.xiaojiawei.kt.bean.task

import club.xiaojiawei.kt.test.onFxThread as runOnJavaFxThread
import javafx.scene.control.ScrollPane
import javafx.scene.control.TitledPane
import javafx.scene.layout.VBox
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs

class TaskProgressViewTest {

    @Test
    fun subTasksAreWrappedInCollapsedTitledPane() {
        val task = CompositeTask(
            id = "task-id",
            name = "测试任务",
            subTasks = emptyList(),
            completeCallback = null,
        )
        val subTaskProgress = SubTaskProgress(
            taskId = task.id,
            subTaskId = "sub-task-id",
            subTaskName = "测试子任务",
            status = TaskStatus.RUNNING,
            message = "执行中",
        )

        val subTaskPane = runOnJavaFxThread {
            val view = TaskProgressView<TaskBuilder>()
            view.addTask(listOf(task))
            view.updateProgress(
                TaskProgress(
                    taskId = task.id,
                    status = TaskStatus.RUNNING,
                    subTaskProgresses = mapOf(subTaskProgress.subTaskId to subTaskProgress),
                )
            )
            val taskPane = (view.children[1] as ScrollPane).content as VBox
            val taskContainer = taskPane.children.single() as VBox
            assertIs<TitledPane>(taskContainer.children.last())
        }

        assertEquals("子任务", subTaskPane.text)
        assertFalse(subTaskPane.isExpanded)
        assertEquals(1, (subTaskPane.content as VBox).children.size)
    }

}
