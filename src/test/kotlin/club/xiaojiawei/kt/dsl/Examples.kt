package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.controls.PageResult
import club.xiaojiawei.kt.ext.getValue
import club.xiaojiawei.kt.ext.setValue
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.layout.StackPane
import javafx.util.Duration

/**
 * 测试案例
 * @author 肖嘉威
 * @date 2026/3/27 15:04
 */
fun main() {
//    example1()
//    example2()
//    example3()
    example4()
}

data class UserRow(
    val id: Int,
    val name: String,
    val role: String
)

class TestPane : StackPane() {
    init {
        config {
            styled {
                backgroundColor("pink")
            }
            addVBox {
                spacing(10.0)
                addLabel { +"test1" }
                addLabel { +"test2" }
                addLabel { +"test3" }
            }
        }
    }
}

/**
 * 普通示例
 */
fun example1() {
    launchApp {
        title("Demo")
        size(800.0, 600.0)
        scene {
            root {
                vbox {
                    spacing(12.0)
                    padding(16.0)
                    addLabel {
                        +"hello" // 相当于text="hello"
                    }
                    addButton {
                        style() // 应用默认样式
                        +"click"
                        onAction {
                            println("click")
                        }
                    }
                    addVBox { }
                    +TestPane()
                }
            }
        }
    }
}

/**
 * config测试
 */
fun example2() {
    val view = vbox {
        spacing(8.0)
    }
    view.config {
        padding(20.0)
        addTextField {
            promptText("输入内容")
        }
    }
    launchApp {
        title("Demo")
        size(800.0, 600.0)
        scene {
            root {
                view
            }
        }
    }
}

/**
 * 响应式观察测试
 */
fun example3() {
    launchApp {
        title("Property 响应式测试")
        size(500.0, 400.0)
        root {
            val secondsProperty = SimpleIntegerProperty(0)
            var seconds by secondsProperty

            val clickProperty = SimpleIntegerProperty(0)
            var clickCount by clickProperty

            val nameProperty = SimpleStringProperty("世界")
            var name by nameProperty

            // 定时器：每秒递增
            val timer = Timeline(KeyFrame(Duration.seconds(1.0), { seconds++ }))
            timer.cycleCount = Animation.INDEFINITE
            timer.play()

            vbox {
                spacing(15.0)
                padding(20.0)
                alignCenter()

                // 示例1：单 Property 观察，block 接收当前值
                addLabel {
                    observe(secondsProperty) { "⏱ 已运行 $it 秒" }
                    fontSize(20.0)
                }

                // 示例2：多 Property 观察
                addLabel {
                    observes(secondsProperty, clickProperty) { "⏱ $seconds 秒 | 🖱 点击 $clickCount 次" }
                    fontSize(16.0)
                }

                // 示例3：按钮点击修改 Property
                addButton {
                    text("点击我")
                    onAction { clickCount++ }
                }

                // 示例4：输入框修改 Property + 观察
                addTextField {
                    text("世界")
                    settings {
                        textProperty().addListener { _, _, newVal ->
                            name = newVal
                        }
                    }
                }

                addLabel {
                    observe(nameProperty) { "你好，$it！" }
                    fontSize(18.0)
                }

                // 示例5：多 Property 组合观察
                addLabel {
                    observes(secondsProperty, clickProperty, nameProperty) {
                        "📊 $name 已运行 $seconds 秒，点击了 $clickCount 次"
                    }
                    fontSize(14.0)
                }

                // 示例6：颜色切换
                val colorProperty = SimpleStringProperty("红色")
                var color by colorProperty

                addButton {
                    text("切换颜色")
                    onAction {
                        color = if (color == "红色") "蓝色" else "红色"
                    }
                }

                addLabel {
                    observe(colorProperty) { "当前颜色: $it" }
                    fontSize(16.0)
                }
            }
        }
    }
}

/**
 * 分页表格示例
 */
fun example4() {
    val users = (1..86).map {
        UserRow(
            id = it,
            name = "用户$it",
            role = if (it % 2 == 0) "管理员" else "普通用户"
        )
    }

    launchApp {
        title("分页表格示例")
        size(800.0, 600.0)
        scene {
            root {
                vbox {
                    spacing(12.0)
                    padding(16.0)

                    addPaginationTableView<UserRow> {
                        pageSize(10)
                        vgrowAlways()
                        style()

                        table {
                            items()
                            settings {
                                columns.setAll(
                                    TableColumn<UserRow, String>("ID").apply {
                                        setCellValueFactory { SimpleStringProperty(it.value.id.toString()) }
                                    },
                                    TableColumn<UserRow, String>("姓名").apply {
                                        setCellValueFactory { SimpleStringProperty(it.value.name) }
                                    },
                                    TableColumn<UserRow, String>("角色").apply {
                                        setCellValueFactory { SimpleStringProperty(it.value.role) }
                                    }
                                )
                            }
                        }

                        pagination {
                            maxPageIndicatorCount(7)
                        }

                        loader { request ->
                            val fromIndex = request.pageIndex * request.pageSize
                            val toIndex = (fromIndex + request.pageSize).coerceAtMost(users.size)
                            val pageItems = if (fromIndex >= users.size) {
                                emptyList()
                            } else {
                                users.subList(fromIndex, toIndex)
                            }
                            PageResult(pageItems, users.size)
                        }

                        settings {
                            refresh()
                        }
                    }
                }
            }
        }
    }
}
