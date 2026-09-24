package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.EventHandler
import javafx.geometry.NodeOrientation
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox

// 基础 Node 构建器
@FXMarker
abstract class NodeBuilder<T : Node> : DslBuilder<T>() {
    // --- 布局约束 & 元数据 ---
    fun userData(d: Any) = settings { userData = d }
    fun hgrow(p: Priority) = settings { HBox.setHgrow(this, p) }
    fun hgrowAlways() = hgrow(Priority.ALWAYS)
    fun hgrowSometimes() = hgrow(Priority.SOMETIMES)
    fun hgrowNever() = hgrow(Priority.NEVER)
    fun vgrow(p: Priority) = settings { VBox.setVgrow(this, p) }
    fun vgrowAlways() = vgrow(Priority.ALWAYS)
    fun vgrowSometimes() = vgrow(Priority.SOMETIMES)
    fun vgrowNever() = vgrow(Priority.NEVER)
    fun pickOnBounds(b: Boolean = true) = settings { isPickOnBounds = b }
    fun nodeOrientation(nodeOrientation: NodeOrientation) = settings { this.nodeOrientation = nodeOrientation }

    // --- 样式系统 (StyleColor & StyleSize) ---
    fun styleMain(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.MAIN, s)
    fun styleNormal(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.NORMAL, s)
    fun styleSuccess(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.SUCCESS, s)
    fun styleWarn(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.WARN, s)
    fun styleError(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.ERROR, s)
    fun styleRadius() = settings { styleClass.add("radius-ui") }
    fun styleRadiusBig() = settings { styleClass.add("radius-ui-big") }
    fun styleBg() = settings { styleClass.add("bg-ui") }
    fun styleBgHover() = settings { styleClass.add("bg-hover-ui") }
    fun styled(block: StyleBuilder.() -> Unit) = settings { this.styled(block) }
    fun styled(styleBuilder: StyleBuilder) = settings { this.styled(styleBuilder) }

    // --- 基础 Node 属性 ---
    fun id(v: String) = settings { id = v }
    fun styleClass(vararg c: String) = settings { styleClass.addAll(c) }
    fun style(v: String) = settings { style = v }
    fun visible(v: Boolean = true) = settings { isVisible = v }
    fun managed(v: Boolean = true) = settings { isManaged = v }
    fun disable(v: Boolean = true) = settings { isDisable = v }
    fun opacity(v: Double) = settings { opacity = v }
    fun rotate(a: Double) = settings { rotate = a }
    fun scale(x: Double, y: Double = x) = settings { scaleX = x; scaleY = y }
    fun translate(x: Double, y: Double) = settings { translateX = x; translateY = y }
    fun mouseTransparent(mouseTransparent: Boolean) = settings { isMouseTransparent = mouseTransparent }
    fun accessibleText(text: String) = settings { accessibleText = text }

    // --- 光标控制 ---
    fun cursor(c: Cursor = Cursor.DEFAULT) = settings { cursor = c }
    fun cursorHand() = cursor(Cursor.HAND)
    fun cursorCrosshair() = cursor(Cursor.CROSSHAIR)
    fun cursorWait() = cursor(Cursor.WAIT)
    fun cursorMove() = cursor(Cursor.MOVE)

    // --- 属性 ---
    fun bindDisable(observable: ObservableValue<Boolean>) = settings { disableProperty().bind(observable) }
    fun bindVisible(observable: ObservableValue<Boolean>) = settings { visibleProperty().bind(observable) }
    fun bindManaged(observable: ObservableValue<Boolean>) = settings { managedProperty().bind(observable) }
    fun bindVisibleAndManaged(observable: ObservableValue<Boolean>) = settings {
        visibleProperty().bind(observable)
        managedProperty().bind(observable)
    }

    // --- 监听 ---
    fun addVisibleListener(changeListener: ChangeListener<Boolean>) = settings { visibleProperty().addListener(changeListener) }
    fun removeVisibleListener(changeListener: ChangeListener<Boolean>) = settings { visibleProperty().removeListener(changeListener) }

    // --- 鼠标 & 拖拽事件 ---
    fun onMouseClicked(h: EventHandler<MouseEvent>? = null) = settings { onMouseClicked = h }
    fun onMouseEntered(h: EventHandler<MouseEvent>? = null) = settings { onMouseEntered = h }
    fun onMouseExited(h: EventHandler<MouseEvent>? = null) = settings { onMouseExited = h }
    fun onMouseReleased(h: EventHandler<MouseEvent>? = null) = settings { onMouseReleased = h }
    fun onMousePressed(h: EventHandler<MouseEvent>? = null) = settings { onMousePressed = h }
    fun onMouseDragged(h: EventHandler<MouseEvent>? = null) = settings { onMouseDragged = h }
    fun onDragDetected(h: EventHandler<MouseEvent>? = null) = settings { onDragDetected = h }
    fun onDragDone(h: EventHandler<DragEvent>? = null) = settings { onDragDone = h }
    fun onDragOver(h: EventHandler<DragEvent>? = null) = settings { onDragOver = h }
    fun onDragExited(h: EventHandler<DragEvent>? = null) = settings { onDragExited = h }
    fun onDragDropped(h: EventHandler<DragEvent>? = null) = settings { onDragDropped = h }
    fun onScroll(h: EventHandler<ScrollEvent>? = null) = settings { onScroll = h }

    fun onMouseClicked(h: (MouseEvent) -> Unit = {}) = settings { onMouseClicked = EventHandler(h) }
    fun onMouseEntered(h: (MouseEvent) -> Unit = {}) = settings { onMouseEntered = EventHandler(h) }
    fun onMouseExited(h: (MouseEvent) -> Unit = {}) = settings { onMouseExited = EventHandler(h) }
    fun onMouseReleased(h: (MouseEvent) -> Unit = {}) = settings { onMouseReleased = EventHandler(h) }
    fun onMousePressed(h: (MouseEvent) -> Unit = {}) = settings { onMousePressed = EventHandler(h) }
    fun onMouseDragged(h: (MouseEvent) -> Unit = {}) = settings { onMouseDragged = EventHandler(h) }
    fun onDragDetected(h: (MouseEvent) -> Unit = {}) = settings { onDragDetected = EventHandler(h) }
    fun onDragDone(h: (DragEvent) -> Unit = {}) = settings { onDragDone = EventHandler(h) }
    fun onDragOver(h: (DragEvent) -> Unit = {}) = settings { onDragOver = EventHandler(h) }
    fun onDragExited(h: (DragEvent) -> Unit = {}) = settings { onDragExited = EventHandler(h) }
    fun onDragDropped(h: (DragEvent) -> Unit = {}) = settings { onDragDropped = EventHandler(h) }
    fun onScroll(h: (ScrollEvent) -> Unit = {}) = settings { onScroll = EventHandler(h) }

}