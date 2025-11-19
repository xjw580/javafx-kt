package club.xiaojiawei.kt.dsl

import javafx.event.Event
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.input.*

/**
 * 事件处理DSL
 * @author 肖嘉威
 */

@FXMarker
class EventHandlers {

    // 鼠标事件
    var onMouseClicked: ((MouseEvent) -> Unit)? = null
    var onMousePressed: ((MouseEvent) -> Unit)? = null
    var onMouseReleased: ((MouseEvent) -> Unit)? = null
    var onMouseEntered: ((MouseEvent) -> Unit)? = null
    var onMouseExited: ((MouseEvent) -> Unit)? = null
    var onMouseMoved: ((MouseEvent) -> Unit)? = null
    var onMouseDragged: ((MouseEvent) -> Unit)? = null

    // 键盘事件
    var onKeyPressed: ((KeyEvent) -> Unit)? = null
    var onKeyReleased: ((KeyEvent) -> Unit)? = null
    var onKeyTyped: ((KeyEvent) -> Unit)? = null

    // 拖拽事件
    var onDragDetected: ((MouseEvent) -> Unit)? = null
    var onDragOver: ((DragEvent) -> Unit)? = null
    var onDragEntered: ((DragEvent) -> Unit)? = null
    var onDragExited: ((DragEvent) -> Unit)? = null
    var onDragDropped: ((DragEvent) -> Unit)? = null
    var onDragDone: ((DragEvent) -> Unit)? = null

    // 滚动事件
    var onScroll: ((ScrollEvent) -> Unit)? = null
    var onScrollStarted: ((ScrollEvent) -> Unit)? = null
    var onScrollFinished: ((ScrollEvent) -> Unit)? = null

    // 触摸事件
    var onTouchPressed: ((TouchEvent) -> Unit)? = null
    var onTouchReleased: ((TouchEvent) -> Unit)? = null
    var onTouchMoved: ((TouchEvent) -> Unit)? = null

    // 缩放和旋转事件
    var onZoom: ((ZoomEvent) -> Unit)? = null
    var onRotate: ((RotateEvent) -> Unit)? = null

    fun applyTo(node: Node) {
        onMouseClicked?.let { node.onMouseClicked = EventHandler(it) }
        onMousePressed?.let { node.onMousePressed = EventHandler(it) }
        onMouseReleased?.let { node.onMouseReleased = EventHandler(it) }
        onMouseEntered?.let { node.onMouseEntered = EventHandler(it) }
        onMouseExited?.let { node.onMouseExited = EventHandler(it) }
        onMouseMoved?.let { node.onMouseMoved = EventHandler(it) }
        onMouseDragged?.let { node.onMouseDragged = EventHandler(it) }

        onKeyPressed?.let { node.onKeyPressed = EventHandler(it) }
        onKeyReleased?.let { node.onKeyReleased = EventHandler(it) }
        onKeyTyped?.let { node.onKeyTyped = EventHandler(it) }

        onDragDetected?.let { node.onDragDetected = EventHandler(it) }
        onDragOver?.let { node.onDragOver = EventHandler(it) }
        onDragEntered?.let { node.onDragEntered = EventHandler(it) }
        onDragExited?.let { node.onDragExited = EventHandler(it) }
        onDragDropped?.let { node.onDragDropped = EventHandler(it) }
        onDragDone?.let { node.onDragDone = EventHandler(it) }

        onScroll?.let { node.onScroll = EventHandler(it) }
        onScrollStarted?.let { node.onScrollStarted = EventHandler(it) }
        onScrollFinished?.let { node.onScrollFinished = EventHandler(it) }

        onTouchPressed?.let { node.onTouchPressed = EventHandler(it) }
        onTouchReleased?.let { node.onTouchReleased = EventHandler(it) }
        onTouchMoved?.let { node.onTouchMoved = EventHandler(it) }

        onZoom?.let { node.onZoom = EventHandler(it) }
        onRotate?.let { node.onRotate = EventHandler(it) }
    }
}

// Node扩展函数
fun Node.events(block: EventHandlers.() -> Unit) {
    EventHandlers().apply(block).applyTo(this)
}

// 便捷的单个事件设置
fun Node.onClick(handler: (MouseEvent) -> Unit) {
    onMouseClicked = EventHandler(handler)
}

fun Node.onPress(handler: (MouseEvent) -> Unit) {
    onMousePressed = EventHandler(handler)
}

fun Node.onRelease(handler: (MouseEvent) -> Unit) {
    onMouseReleased = EventHandler(handler)
}

fun Node.onHover(
    onEnter: ((MouseEvent) -> Unit)? = null,
    onExit: ((MouseEvent) -> Unit)? = null
) {
    onEnter?.let { onMouseEntered = EventHandler(it) }
    onExit?.let { onMouseExited = EventHandler(it) }
}

fun Node.onKey(
    onPress: ((KeyEvent) -> Unit)? = null,
    onRelease: ((KeyEvent) -> Unit)? = null,
    onType: ((KeyEvent) -> Unit)? = null
) {
    onPress?.let { onKeyPressed = EventHandler(it) }
    onRelease?.let { onKeyReleased = EventHandler(it) }
    onType?.let { onKeyTyped = EventHandler(it) }
}

// 组合事件处理
@FXMarker
class MouseHandlers {
    var onClick: ((MouseEvent) -> Unit)? = null
    var onDoubleClick: ((MouseEvent) -> Unit)? = null
    var onRightClick: ((MouseEvent) -> Unit)? = null

    fun applyTo(node: Node) {
        node.onMouseClicked = EventHandler { event ->
            when {
                event.button == MouseButton.PRIMARY && event.clickCount == 1 -> onClick?.invoke(event)
                event.button == MouseButton.PRIMARY && event.clickCount == 2 -> onDoubleClick?.invoke(event)
                event.button == MouseButton.SECONDARY -> onRightClick?.invoke(event)
            }
        }
    }
}

fun Node.mouseHandlers(block: MouseHandlers.() -> Unit) {
    MouseHandlers().apply(block).applyTo(this)
}

// 拖拽辅助
@FXMarker
class DragHandlers {
    var onDragStart: ((MouseEvent) -> Unit)? = null
    var onDragOver: ((DragEvent) -> Unit)? = null
    var onDrop: ((DragEvent) -> Unit)? = null
    var onDragEnd: ((DragEvent) -> Unit)? = null

    fun applyTo(node: Node) {
        onDragStart?.let { 
            node.onDragDetected = EventHandler(it)
        }
        onDragOver?.let { 
            node.onDragOver = EventHandler { event ->
                event.acceptTransferModes(*TransferMode.ANY)
                it(event)
            }
        }
        onDrop?.let { node.onDragDropped = EventHandler(it) }
        onDragEnd?.let { node.onDragDone = EventHandler(it) }
    }
}

fun Node.dragHandlers(block: DragHandlers.() -> Unit) {
    DragHandlers().apply(block).applyTo(this)
}

// 键盘快捷键
@FXMarker
class KeyboardShortcuts {
    
    private val shortcuts = mutableMapOf<KeyCombination, () -> Unit>()

    fun on(keyCombination: KeyCombination, action: () -> Unit) {
        shortcuts[keyCombination] = action
    }

    fun on(key: KeyCode, vararg modifiers: KeyCombination.Modifier, action: () -> Unit) {
        val combination = KeyCodeCombination(key, *modifiers)
        shortcuts[combination] = action
    }

    fun ctrlKey(key: KeyCode, action: () -> Unit) {
        on(key, KeyCombination.CONTROL_DOWN, action = action)
    }

    fun shiftKey(key: KeyCode, action: () -> Unit) {
        on(key, KeyCombination.SHIFT_DOWN, action = action)
    }

    fun altKey(key: KeyCode, action: () -> Unit) {
        on(key, KeyCombination.ALT_DOWN, action = action)
    }

    fun applyTo(node: Node) {
        node.onKeyPressed = EventHandler { event ->
            shortcuts.entries.find { it.key.match(event) }?.value?.invoke()
        }
    }
}

fun Node.shortcuts(block: KeyboardShortcuts.() -> Unit) {
    KeyboardShortcuts().apply(block).applyTo(this)
}

// 事件过滤器
fun <T : Event> Node.addEventFilter(
    eventType: javafx.event.EventType<T>,
    handler: (T) -> Unit
) {
    addEventFilter(eventType, EventHandler(handler))
}

fun <T : Event> Node.addEventHandler(
    eventType: javafx.event.EventType<T>,
    handler: (T) -> Unit
) {
    addEventHandler(eventType, EventHandler(handler))
}

// 事件消费
fun Event.consume() {
    this.consume()
}

// 防抖动
class Debouncer(private val delayMs: Long) {
    private var lastTime = 0L

    fun execute(action: () -> Unit) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastTime >= delayMs) {
            action()
            lastTime = currentTime
        }
    }
}

fun Node.onClickDebounced(delayMs: Long = 500, handler: (MouseEvent) -> Unit) {
    val debouncer = Debouncer(delayMs)
    onClick { event ->
        debouncer.execute { handler(event) }
    }
}
