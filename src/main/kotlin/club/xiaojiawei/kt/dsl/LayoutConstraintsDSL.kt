package club.xiaojiawei.kt.dsl

import javafx.geometry.HPos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.*

/**
 * 布局约束DSL
 * @author 肖嘉威
 */

@FXMarker
class AnchorConstraints(private val node: Node) {
    fun top(value: Double) {
        AnchorPane.setTopAnchor(node, value)
    }

    fun bottom(value: Double) {
        AnchorPane.setBottomAnchor(node, value)
    }

    fun left(value: Double) {
        AnchorPane.setLeftAnchor(node, value)
    }

    fun right(value: Double) {
        AnchorPane.setRightAnchor(node, value)
    }

    fun all(value: Double) {
        top(value)
        bottom(value)
        left(value)
        right(value)
    }

    fun horizontal(value: Double) {
        left(value)
        right(value)
    }

    fun vertical(value: Double) {
        top(value)
        bottom(value)
    }
}

@FXMarker
class GridConstraints(private val node: Node) {
    fun columnIndex(index: Int) {
        GridPane.setColumnIndex(node, index)
    }

    fun rowIndex(index: Int) {
        GridPane.setRowIndex(node, index)
    }

    fun columnSpan(span: Int) {
        GridPane.setColumnSpan(node, span)
    }

    fun rowSpan(span: Int) {
        GridPane.setRowSpan(node, span)
    }

    fun hgrow(priority: Priority) {
        GridPane.setHgrow(node, priority)
    }

    fun vgrow(priority: Priority) {
        GridPane.setVgrow(node, priority)
    }

    fun halignment(alignment: HPos) {
        GridPane.setHalignment(node, alignment)
    }

    fun valignment(alignment: VPos) {
        GridPane.setValignment(node, alignment)
    }

    fun margin(value: Double) {
        GridPane.setMargin(node, javafx.geometry.Insets(value))
    }

    fun margin(top: Double, right: Double, bottom: Double, left: Double) {
        GridPane.setMargin(node, javafx.geometry.Insets(top, right, bottom, left))
    }

    fun fillWidth(fill: Boolean) {
        GridPane.setFillWidth(node, fill)
    }

    fun fillHeight(fill: Boolean) {
        GridPane.setFillHeight(node, fill)
    }
}

@FXMarker
class BoxConstraints(private val node: Node) {
    fun margin(value: Double) {
        HBox.setMargin(node, javafx.geometry.Insets(value))
        VBox.setMargin(node, javafx.geometry.Insets(value))
    }

    fun margin(top: Double, right: Double, bottom: Double, left: Double) {
        HBox.setMargin(node, javafx.geometry.Insets(top, right, bottom, left))
        VBox.setMargin(node, javafx.geometry.Insets(top, right, bottom, left))
    }

    fun hgrow(priority: Priority) {
        HBox.setHgrow(node, priority)
    }

    fun vgrow(priority: Priority) {
        VBox.setVgrow(node, priority)
    }
}

// 扩展函数，让Node可以直接设置约束
inline fun Node.anchorConstraints(block: AnchorConstraints.() -> Unit) {
    AnchorConstraints(this).block()
}

inline fun Node.gridConstraints(block: GridConstraints.() -> Unit) {
    GridConstraints(this).block()
}

inline fun Node.boxConstraints(block: BoxConstraints.() -> Unit) {
    BoxConstraints(this).block()
}

// 为PaneBaseBuilder添加约束支持
fun <T : Node> PaneBaseBuilder<*>.addWithAnchor(
    node: T,
    block: AnchorConstraints.() -> Unit
): T {
    add(node)
    node.anchorConstraints(block)
    return node
}

fun <T : Node> PaneBaseBuilder<*>.addWithGrid(
    node: T,
    block: GridConstraints.() -> Unit
): T {
    add(node)
    node.gridConstraints(block)
    return node
}

fun <T : Node> PaneBaseBuilder<*>.addWithBox(
    node: T,
    block: BoxConstraints.() -> Unit
): T {
    add(node)
    node.boxConstraints(block)
    return node
}
