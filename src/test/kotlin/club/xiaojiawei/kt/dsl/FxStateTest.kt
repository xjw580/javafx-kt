package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.ext.getValue
import club.xiaojiawei.kt.ext.setValue
import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.util.Duration

/**
 * 响应式观察测试 — 直接使用 JavaFX Property
 * @author 肖嘉威
 */
fun main() {
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
                    configure {
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
