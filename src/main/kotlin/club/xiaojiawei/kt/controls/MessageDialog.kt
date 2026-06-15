package club.xiaojiawei.kt.controls

import club.xiaojiawei.JavaFXUI
import club.xiaojiawei.kt.annotations.FXMarker
import club.xiaojiawei.kt.dsl.*
import club.xiaojiawei.kt.ext.runUI
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.Label
import javafx.scene.control.ScrollPane
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.StackPane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle

/**
 * 消息模态框
 * @author 肖嘉威
 */
class MessageDialog(val baseParent: Parent) {

    private val stage = Stage().apply {
        initStyle(StageStyle.TRANSPARENT)
        initModality(Modality.WINDOW_MODAL)
        initOwner(baseParent.scene.window)
        isResizable = false
    }

    private var rootPane: StackPane? = null
    private var topPane: StackPane? = null
    private var contentNode: Node? = null
    private var isClosing = false

    var maskClosable: Boolean = false
    var escClosable: Boolean = true
    var onClosed: (() -> Unit)? = null

    fun init(content: Node) {
        this.contentNode = content
        stage.config {
            scene {
                JavaFXUI.addjavafxUIStylesheet(instance())
                root {
                    buildRootPane(content)
                }
                fill(Color.TRANSPARENT)
            }
            initStyle(StageStyle.TRANSPARENT)
            initModality(Modality.WINDOW_MODAL)
            initOwner(baseParent.scene.window)

        }
        addClosingListeners()
    }

    private fun buildRootPane(content: Node): Pane = stackPane {
        rootPane = instance()
        styled {
            backgroundColor("#00000011")
        }
        styleClass("fixed-label-color")

        +scrollPane {
            content {
                stackPane {
                    topPane = instance()
                    +Group(
                        stackPane {
                            +content
                            styled {
                                effect("dropshadow(gaussian, rgba(128, 128, 128, 0.67), 10, 0, 0, 3)")
                                backgroundColor("white")
                            }
                        }
                    )
                }
            }
            fitToWidth(true)
            fitToHeight(true)
            styled {
                background("transparent")
                backgroundColor("transparent")
                backgroundInsets(0.0)
                padding(0.0)
            }
        }
    }

    private fun addClosingListeners() {
        stage.scene.addEventFilter(KeyEvent.KEY_RELEASED) { event ->
            if (!isClosing && escClosable && event.code == KeyCode.ESCAPE) {
                close()
            }
        }
        stage.scene.addEventFilter(MouseEvent.MOUSE_CLICKED) { event ->
            if (!isClosing && maskClosable && event.target == topPane) {
                close()
                event.consume()
            }
        }
    }

    private fun initSize() {
        val scene = baseParent.scene
        val window = scene.window
        stage.width = scene.width
        stage.height = scene.height
        // 修正坐标计算，确保与父窗口完全重合
        stage.x = window.x + scene.x
        stage.y = window.y + scene.y
    }

    fun show(shownRunnable: (() -> Unit)? = null) {
        runUI {
            if (!baseParent.scene.window.isShowing) return@runUI

            initSize()
            stage.show()

            parallelTransition {
                +fadeTransition {
                    node(rootPane)
                    from(0.0)
                    to(1.0)
                    duration(200.0)
                }
                +translateTransition {
                    node(topPane)
                    from(0.0, 25.0)
                    to(0.0, 0.0)
                    duration(200.0)
                }
                onFinished {
                    shownRunnable?.invoke()
                }
            }.play()
        }
    }

    fun close() {
        if (isClosing) return
        isClosing = true

        val duration = 150.0

        parallelTransition {
            +fadeTransition {
                node(rootPane)
                from(1.0)
                to(0.0)
                duration(duration)
            }
            +translateTransition {
                node(topPane)
                from(0.0, 0.0)
                to(0.0, -25.0)
                duration(duration)
            }
            onFinished {
                stage.close()
                onClosed?.invoke()
            }
        }.apply {
            play()
        }

    }
}

@FXMarker
class MessageDialogBuilder(val baseParent: Parent) : DslBuilder<MessageDialog>() {
    private var headingText: String? = null
    private var contentObj: Any? = null
    private val buttonBuilders = mutableListOf<ButtonBuilder>()

    override fun buildInstance(): MessageDialog = MessageDialog(baseParent)

    fun heading(text: String) {
        this.headingText = text
    }

    fun content(text: String) {
        this.contentObj = text
    }

    fun content(node: Node) {
        this.contentObj = node
    }

    fun maskClosable(value: Boolean) = settings {
        this.maskClosable = value
    }

    fun escClosable(value: Boolean) = settings {
        this.escClosable = value
    }

    fun onClosed(handler: () -> Unit) = settings {
        this.onClosed = handler
    }

    /**
     * 深度自定义按钮，继承自 DslBuilder<Button>
     */
    fun button(text: String = "", block: ButtonBuilder.() -> Unit) {
        buttonBuilders.add(ButtonBuilder().apply {
            text(text)
            styleClass("btn-ui")
            block()
        })
    }

    fun okButton(text: String = "确认", action: () -> Unit = {}) {
        button(text) {
            styleClass("btn-ui-success")
            onAction { action() }
        }
    }

    fun cancelButton(text: String = "取消", action: () -> Unit = {}) {
        button(text) {
            onAction { action() }
        }
    }

    override fun build(): MessageDialog {
        val messageBox = super.build()

        val vBox = VBox().apply {
            prefWidth = 350.0.coerceAtMost(baseParent.scene.width - 10)
            maxHeight = baseParent.scene.height - 10
            spacing = 20.0
            padding = Insets(20.0)
        }

        headingText?.let {
            vBox.children.add(HBox().apply {
                alignment = Pos.CENTER_LEFT
                children.add(Label(it).apply {
                    style = "-fx-font-weight: bold; -fx-font-size: 14; -fx-wrap-text: true; -fx-text-fill: black"
                })
            })
        }

        contentObj?.let {
            when (it) {
                is String -> {
                    val maxWidth = vBox.prefWidth - 40
                    val scrollPane = ScrollPane().apply {
                        styleClass.add("edge-to-edge")
                        style = "-fx-background: white; -fx-hbar-policy: NEVER; -fx-padding: 0 0 0 5"
                        this.maxWidth = maxWidth
                        maxHeight = 200.0
                        content = Text(it).apply {
                            wrappingWidth = maxWidth - 15
                            style = "-fx-font-size: 14;"
                        }
                    }
                    vBox.children.add(scrollPane)
                }

                is Node -> vBox.children.add(it)
            }
        }

        if (buttonBuilders.isNotEmpty()) {
            vBox.children.add(HBox().apply {
                style = "-fx-spacing: 15; -fx-alignment: CENTER_RIGHT"
                buttonBuilders.forEach { builder ->
                    val btn = builder.build()
                    val onAction = btn.onAction
                    btn.setOnAction {
                        messageBox.close()
                        onAction?.handle(it)
                    }
                    children.add(btn)
                }
            })
        }

        messageBox.init(vBox)
        return messageBox
    }
}

inline fun messageDialog(baseParent: Parent, block: MessageDialogBuilder.() -> Unit): MessageDialog {
    return MessageDialogBuilder(baseParent).apply(block).build()
}
