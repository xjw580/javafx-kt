package club.xiaojiawei.kt.dsl

import javafx.animation.Animation
import javafx.animation.KeyFrame
import javafx.animation.Timeline
import javafx.util.Duration

/**
 * FxState 响应式状态测试
 * @author 肖嘉威
 */
fun main() {
    launchApp {
        title("FxState 响应式测试")
        size(500.0, 400.0)
        root {
            val secondsState = state(0)
            var seconds by secondsState

            val clickState = intState(0)
            var clickCount by clickState

            val nameState = state("世界")
            var name by nameState

            // 定时器：每秒递增
            val timer = Timeline(KeyFrame(Duration.seconds(1.0), { seconds++ }))
            timer.cycleCount = Animation.INDEFINITE
            timer.play()

            vbox {
                spacing(15.0)
                padding(20.0)
                alignCenter()

                // 示例1：单 State 观察
                addLabel {
                    observe(secondsState) { "⏱ 已运行 $it 秒" }
                    fontSize(20.0)
                }

                // 示例2：多 State 观察
                addLabel {
                    observes(secondsState, clickState) { "⏱ $seconds 秒 | 🖱 点击 $clickCount 次" }
                    fontSize(16.0)
                }

                // 示例3：按钮点击修改 State
                addButton {
                    text("点击我")
                    onAction { clickCount++ }
                }

                // 示例4：输入框修改 State + 观察
                addTextField {
                    text("世界")
                    configure {
                        textProperty().addListener { _, _, newVal ->
                            name = newVal
                        }
                    }
                }

                addLabel {
                    observe(nameState) { "你好，$it！" }
                    fontSize(18.0)
                }

                // 示例5：多 State 组合观察
                addLabel {
                    observes(secondsState, clickState, nameState) {
                        "📊 $name 已运行 $seconds 秒，点击了 $clickCount 次"
                    }
                    fontSize(14.0)
                }

                // 示例6：使用泛型 FxState<T> + 单参数 observe
                val colorState = state("红色")
                var color by colorState

                addButton {
                    text("切换颜色")
                    onAction {
                        color = if (color == "红色") "蓝色" else "红色"
                    }
                }

                addLabel {
                    observe(colorState) { "当前颜色: $it" }
                    fontSize(16.0)
                }
            }
        }
    }
}
