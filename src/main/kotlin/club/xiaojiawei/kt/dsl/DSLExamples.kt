package club.xiaojiawei.kt.dsl.examples

import club.xiaojiawei.kt.dsl.*
import javafx.application.Application
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.layout.Priority
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.stage.Stage

/**
 * DSL框架使用示例
 * @author 肖嘉威
 */

internal class DSLExamples : Application() {

    override fun start(primaryStage: Stage) {
        primaryStage.scene = Scene(createMainView(), 800.0, 600.0)
        primaryStage.title = "JavaFX Kotlin DSL 示例"
        primaryStage.show()
    }

    // 示例1：布局约束使用
    private fun constraintsExample() = anchorPane {
        size(600.0, 400.0)

        // 使用anchor方法添加带约束的节点
        anchor(button("顶部按钮") {
            prefWidth(100.0)
        }) {
            top(10.0)
            left(10.0)
            right(10.0)
        }

        // 使用anchorVBox添加VBox
        anchorVBox({
            spacing(10.0)
            addLabel("标签1")
            addLabel("标签2")
            addButton("按钮")
        }) {
            all(20.0)
        }
    }

    // 示例2：属性绑定使用
    private fun bindingExample() = vbox {
        spacing(10.0)
        padding(20.0)

        val textField1 = textField {
            promptText("输入文本")
        }
        add(textField1)

        val textField2 = textField()
        add(textField2)

        // 绑定文本
        bindings {
            textField2.textProperty() bind textField1.textProperty()
        }

        val slider1 = slider {
            range(0.0, 100.0)
            value(50.0)
        }
        add(slider1)

        val progressBar1 = progressBar()
        add(progressBar1)

        // 绑定进度
        bindings {
            progressBar1.progressProperty() bind slider1.valueProperty().divide(100.0)
        }
    }

    // 示例3：动画使用
    private fun animationExample() = vbox {
        spacing(10.0)
        padding(20.0)
        alignCenter()

        val box = hbox {
            size(100.0, 100.0)
            background("#3498db")
        }
        add(box)

        addButton("淡入") {
            onClick {
                box.fadeIn(500.0) {
                    onFinished {
                        println("淡入完成")
                    }
                }
            }
        }

        addButton("淡出") {
            onClick {
                box.fadeOut(500.0)
            }
        }

        addButton("缩放") {
            onClick {
                box.scaleAnimation {
                    duration(300.0)
                    from(1.0)
                    to(1.5)
                    autoReverse()
                    cycleCount(2)
                }.play()
            }
        }

        addButton("旋转") {
            onClick {
                box.rotateAnimation {
                    duration(500.0)
                    to(360.0)
                }.play()
            }
        }

        addButton("震动") {
            onClick {
                box.shake()
            }
        }

        addButton("脉冲") {
            onClick {
                box.pulse()
            }
        }

        addButton("组合动画") {
            onClick {
                parallelAnimation {
                    add(box.fadeAnimation {
                        duration(500.0)
                        to(0.5)
                    })
                    add(box.scaleAnimation {
                        duration(500.0)
                        to(1.5)
                    })
                }.play()
            }
        }
    }

    // 示例4：事件处理使用
    private fun eventExample() = vbox {
        spacing(10.0)
        padding(20.0)

        val label1 = label("点击按钮测试事件")
        add(label1)

        addButton("单击测试") {
            onClick { event ->
                label1.text = "单击在: ${event.x}, ${event.y}"
            }
        }

        addButton("悬停测试") {
            onHover(
                onEnter = {
                    label1.text = "鼠标进入"
                    this.opacity = 0.7
                },
                onExit = {
                    label1.text = "鼠标离开"
                    this.opacity = 1.0
                }
            )
        }

        addButton("多事件测试") {
            events {
                onMouseClicked = { label1.text = "点击" }
                onMousePressed = { label1.text = "按下" }
                onMouseReleased = { label1.text = "释放" }
            }
        }

        addButton("防抖测试") {
            var count = 0
            onClickDebounced(1000) {
                count++
                label1.text = "防抖点击次数: $count"
            }
        }

        addButton("右键菜单") {
            mouseHandlers {
                onClick = { label1.text = "左键单击" }
                onDoubleClick = { label1.text = "左键双击" }
                onRightClick = { label1.text = "右键点击" }
            }
        }
    }

    // 示例5：快捷键使用
    private fun shortcutExample() = vbox {
        spacing(10.0)
        padding(20.0)

        val label1 = label("按快捷键测试")
        add(label1)

        settings {
            shortcuts {
                ctrlKey(javafx.scene.input.KeyCode.S) {
                    label1.text = "Ctrl+S 保存"
                }
                ctrlKey(javafx.scene.input.KeyCode.O) {
                    label1.text = "Ctrl+O 打开"
                }
                shiftKey(javafx.scene.input.KeyCode.DELETE) {
                    label1.text = "Shift+Delete 删除"
                }
            }

        }

    }

    // 示例6：样式DSL使用
    private fun styleExample() = vbox {
        spacing(10.0)
        padding(20.0)

        addButton("渐变按钮") {
            styled {
                backgroundColor("linear-gradient(to right, #667eea, #764ba2)")
                textFill("white")
                fontSize(16.0)
                padding(10.0, 20.0, 10.0, 20.0)
                backgroundRadius(5.0)
                cursor(Cursor.HAND)
            }
        }

        addLabel("阴影文字") {
            styled {
                fontSize(24.0)
                fontWeight(FontWeight.BOLD)
                textFill("#2c3e50")
                dropShadow(5.0, 2.0, 2.0, "rgba(0,0,0,0.3)")
            }
        }

        val box = hbox {
            size(200.0, 100.0)
            styled {
                backgroundColor("#ecf0f1")
                borderColor("#3498db")
                borderWidth(2.0)
                borderRadius(10.0)
                padding(20.0)
            }
        }
        add(box)
    }

    // 示例7：其他Pane使用
    private fun morePanesExample() = vbox {
        spacing(10.0)
        padding(20.0)

        // FlowPane示例
        val flow = flowPane {
            gap(10.0)
            prefWrapLength(300.0)
            repeat(10) {
                addButton("按钮$it")
            }
        }

        add(flow)

        // TilePane示例
        val tile = tilePane {
            gap(5.0)
            prefColumns(4)
            repeat(12) {
                addLabel("项目$it") {
                    styled {
                        backgroundColor("#3498db")
                        textFill("white")
                        padding(10.0)
                    }
                }
            }
        }
        add(tile)

        // ScrollPane示例
        val scroll = scrollPane {
            fitToWidth()
            content(vbox {
                repeat(20) {
                    addLabel("滚动内容 $it")
                }
            })
        }
        add(scroll)

        // SplitPane示例
        val split = splitPane {
            orientation(javafx.geometry.Orientation.HORIZONTAL)
            dividerPositions(0.3, 0.7)
            +label("左侧面板")
            +label("中间面板")
            +label("右侧面板")
        }
        add(split)
    }

    // 主视图 - 集成所有示例
    private fun createMainView() = borderPane {
        size(800.0, 600.0)

        // 顶部标题
        topNode(hbox {
            padding(10.0)
            background("#2c3e50")
            alignCenter()
            addLabel("JavaFX Kotlin DSL 框架示例") {
                styled {
                    textFill("white")
                    fontSize(20.0)
                    fontWeight(FontWeight.BOLD)
                }
            }
        })

        val contentPane = StackPane()
        // 左侧导航
        leftNode(vbox {
            spacing(5.0)
            padding(10.0)
            prefWidth(150.0)
            background("#34495e")

            val demos = listOf(
                "布局约束" to ::constraintsExample,
                "属性绑定" to ::bindingExample,
                "动画效果" to ::animationExample,
                "事件处理" to ::eventExample,
                "快捷键" to ::shortcutExample,
                "样式DSL" to ::styleExample,
                "其他面板" to ::morePanesExample
            )



            demos.forEach { (name, builder) ->
                addButton(name) {
                    prefWidth(Double.MAX_VALUE)
                    styled {
                        backgroundColor("#95a5a6")
                        textFill("white")
                    }
                    onClick {
                        contentPane.children.clear()
                        contentPane.children.add(builder())
                    }
                }
            }
        })

        // 中间内容区
        centerNode(stackPane {
            padding(20.0)
            addLabel("选择左侧菜单查看示例") {
                styled {
                    fontSize(18.0)
                    textFill("#7f8c8d")
                }
            }
            add(contentPane)
        })

        // 底部状态栏
        bottomNode(hbox {
            padding(5.0)
            background("#ecf0f1")
            alignCenter()
            addLabel("JavaFX Kotlin DSL - 现代化的UI开发框架")
        })
    }
}

internal fun main() {
    Application.launch(DSLExamples::class.java)
}
