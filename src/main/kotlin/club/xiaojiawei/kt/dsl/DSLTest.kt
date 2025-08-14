package club.xiaojiawei.kt.dsl

import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.layout.StackPane

/**
 * @author 肖嘉威
 * @date 2025/8/12 16:01
 */

// 方案一：为StackPane添加扩展函数
//fun StackPane.addChildren(vararg nodes: Node) {
//    children.addAll(nodes)
//}
//
//// 方案二：更DSL化的方式，支持lambda构建
//fun StackPane.addChildren(builder: MutableList<Node>.() -> Unit) {
//    val nodes = mutableListOf<Node>()
//    nodes.builder()
//    children.addAll(nodes)
//}
//
//// 方案三：单个添加的扩展函数
//fun StackPane.addChild(node: Node) {
//    children.add(node)
//}
//
//// 你的原始stackPane函数
//fun stackPane(config: StackPane.() -> Unit): StackPane {
//    return StackPane().apply(config)
//}
//
//// 为了演示，添加一些辅助函数
//fun button(text: String, config: Button.() -> Unit = {}): Button {
//    return Button(text).apply(config)
//}
//
//fun label(text: String, config: Label.() -> Unit = {}): Label {
//    return Label(text).apply(config)
//}
//
//fun main() {
//    // 使用方案一：直接传入多个Node
//    val stack1 = stackPane {
//        alignment = Pos.CENTER
//        addChildren(
//            button("按钮1"),
//            label("标签1"),
//            button("按钮2")
//        )
//    }
//
//    // 使用方案二：使用lambda构建器
//    val stack2 = stackPane {
//        alignment = Pos.CENTER
//        addChildren {
//            add(button("按钮1") {
//                prefWidth = 100.0
//            })
//            add(label("标签1"))
//            add(button("按钮2"))
//        }
//    }
//
//    // 使用方案三：逐个添加
//    val stack3 = stackPane {
//        alignment = Pos.CENTER
//        addChild(button("按钮1"))
//        addChild(label("标签1"))
//        addChild(button("按钮2"))
//    }
//
//    // 更进一步的DSL风格（方案四）
//    val stack4 = stackPane {
//        alignment = Pos.CENTER
//
//        // 直接使用操作符重载
//        this += button("按钮1")
//        this += label("标签1")
//        this += button("按钮2")
//    }
//}
//
//// 方案四：操作符重载，让语法更简洁
//operator fun StackPane.plusAssign(node: Node) {
//    children.add(node)
//}
//
//// 方案五：最DSL化的方式 - 创建一个专门的构建器
//class StackPaneBuilder {
//    private val stackPane = StackPane()
//    private val nodes = mutableListOf<Node>()
//
//    var alignment: Pos
//        get() = stackPane.alignment
//        set(value) { stackPane.alignment = value }
//
//    fun button(text: String, config: Button.() -> Unit = {}):Button {
//        val btn = Button(text).apply(config)
//        nodes.add(btn)
//        return btn
//    }
//
//    fun label(text: String, config: Label.() -> Unit = {}):Label {
//        val label = Label(text).apply(config)
//        nodes.add(label)
//        return label
//    }
//
//    // 添加任意Node
//    fun node(node: Node): Node {
//        nodes.add(node)
//        return node
//    }
//
//    internal fun build(): StackPane {
//        stackPane.children.addAll(nodes)
//        return stackPane
//    }
//}
//
//fun stackPaneDsl(config: StackPaneBuilder.() -> Unit): StackPane {
//    return StackPaneBuilder().apply(config).build()
//}
//
//// 使用最DSL化的方式
//fun dslExample() {
//    val stack = stackPaneDsl {
//        alignment = Pos.CENTER
//
//        button("按钮1") {
//            prefWidth = 100.0
//            onAction = EventHandler {
//
//            }
//        }
//
//        label("标签文本")
//
//        button("按钮2") {
//            prefHeight = 50.0
//        }
//    }
//}