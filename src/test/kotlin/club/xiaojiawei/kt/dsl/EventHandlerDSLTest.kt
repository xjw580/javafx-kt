package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.addEventFilter as addDslEventFilter
import club.xiaojiawei.kt.dsl.addEventHandler as addDslEventHandler
import club.xiaojiawei.kt.test.onFxThread
import javafx.event.Event
import javafx.event.EventHandler
import javafx.event.EventType
import javafx.scene.Group
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyCodeCombination
import javafx.scene.input.KeyCombination
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.shape.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame

class EventHandlerDSLTest {

    @Test
    fun eventsDispatchMouseAndKeyboardEventsLikeNativeHandlers() = onFxThread {
        val actual = Rectangle(20.0, 20.0)
        val expected = Rectangle(20.0, 20.0)
        val actualCalls = mutableListOf<String>()
        val expectedCalls = mutableListOf<String>()
        actual.events {
            onMouseClicked = { actualCalls += "clicked:${it.clickCount}" }
            onMousePressed = { actualCalls += "pressed" }
            onMouseReleased = { actualCalls += "released" }
            onMouseEntered = { actualCalls += "entered" }
            onMouseExited = { actualCalls += "exited" }
            onMouseMoved = { actualCalls += "moved" }
            onMouseDragged = { actualCalls += "dragged" }
            onKeyPressed = { actualCalls += "key-pressed:${it.code}" }
            onKeyReleased = { actualCalls += "key-released:${it.code}" }
            onKeyTyped = { actualCalls += "typed:${it.character}" }
        }
        expected.onMouseClicked = EventHandler { expectedCalls += "clicked:${it.clickCount}" }
        expected.onMousePressed = EventHandler { expectedCalls += "pressed" }
        expected.onMouseReleased = EventHandler { expectedCalls += "released" }
        expected.onMouseEntered = EventHandler { expectedCalls += "entered" }
        expected.onMouseExited = EventHandler { expectedCalls += "exited" }
        expected.onMouseMoved = EventHandler { expectedCalls += "moved" }
        expected.onMouseDragged = EventHandler { expectedCalls += "dragged" }
        expected.onKeyPressed = EventHandler { expectedCalls += "key-pressed:${it.code}" }
        expected.onKeyReleased = EventHandler { expectedCalls += "key-released:${it.code}" }
        expected.onKeyTyped = EventHandler { expectedCalls += "typed:${it.character}" }

        listOf(
            MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED,
            MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED, MouseEvent.MOUSE_MOVED, MouseEvent.MOUSE_DRAGGED,
        ).forEach { type ->
            Event.fireEvent(expected, mouseEvent(type))
            Event.fireEvent(actual, mouseEvent(type))
        }
        listOf(KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED).forEach { type ->
            Event.fireEvent(expected, KeyEvent(type, "a", "a", KeyCode.A, false, false, false, false))
            Event.fireEvent(actual, KeyEvent(type, "a", "a", KeyCode.A, false, false, false, false))
        }

        assertEquals(10, expectedCalls.size)
        assertEquals(expectedCalls, actualCalls)
    }

    @Test
    fun convenienceHandlersMatchNativeDispatchAndPreserveOmittedHandlers() = onFxThread {
        val actual = Rectangle()
        val expected = Rectangle()
        val actualCalls = mutableListOf<String>()
        val expectedCalls = mutableListOf<String>()
        val existingExit = EventHandler<MouseEvent> { actualCalls += "exit" }
        actual.onMouseExited = existingExit
        actual.onClick { actualCalls += "click" }
        actual.onPress { actualCalls += "press" }
        actual.onRelease { actualCalls += "release" }
        actual.onHover(onEnter = { actualCalls += "enter" })
        actual.onKey(
            onPress = { actualCalls += "key-press" },
            onRelease = { actualCalls += "key-release" },
            onType = { actualCalls += "key-type" },
        )
        actual.events {}
        expected.onMouseClicked = EventHandler { expectedCalls += "click" }
        expected.onMousePressed = EventHandler { expectedCalls += "press" }
        expected.onMouseReleased = EventHandler { expectedCalls += "release" }
        expected.onMouseEntered = EventHandler { expectedCalls += "enter" }
        expected.onMouseExited = EventHandler { expectedCalls += "exit" }
        expected.onKeyPressed = EventHandler { expectedCalls += "key-press" }
        expected.onKeyReleased = EventHandler { expectedCalls += "key-release" }
        expected.onKeyTyped = EventHandler { expectedCalls += "key-type" }

        listOf(
            MouseEvent.MOUSE_CLICKED, MouseEvent.MOUSE_PRESSED, MouseEvent.MOUSE_RELEASED,
            MouseEvent.MOUSE_ENTERED, MouseEvent.MOUSE_EXITED
        ).forEach { type ->
            Event.fireEvent(expected, mouseEvent(type))
            Event.fireEvent(actual, mouseEvent(type))
        }
        listOf(KeyEvent.KEY_PRESSED, KeyEvent.KEY_RELEASED, KeyEvent.KEY_TYPED).forEach { type ->
            Event.fireEvent(expected, KeyEvent(type, "a", "a", KeyCode.A, false, false, false, false))
            Event.fireEvent(actual, KeyEvent(type, "a", "a", KeyCode.A, false, false, false, false))
        }
        assertSame<EventHandler<in MouseEvent>>(existingExit, actual.onMouseExited)
        assertEquals(8, expectedCalls.size)
        assertEquals(expectedCalls, actualCalls)
    }

    @Test
    fun mouseHandlersRouteSingleDoubleAndSecondaryClicksLikeNativeHandler() = onFxThread {
        val actual = Rectangle()
        val expected = Rectangle()
        val actualCalls = mutableListOf<String>()
        val expectedCalls = mutableListOf<String>()
        actual.mouseHandlers {
            onClick = { actualCalls += "single" }
            onDoubleClick = { actualCalls += "double" }
            onRightClick = { actualCalls += "secondary" }
        }
        expected.onMouseClicked = EventHandler {
            if (it.button == MouseButton.SECONDARY) expectedCalls += "secondary"
            if (it.button == MouseButton.PRIMARY && it.clickCount == 1) expectedCalls += "single"
            if (it.button == MouseButton.PRIMARY && it.clickCount == 2) expectedCalls += "double"
        }

        listOf(
            MouseButton.PRIMARY to 1, MouseButton.PRIMARY to 2,
            MouseButton.PRIMARY to 3, MouseButton.SECONDARY to 1, MouseButton.MIDDLE to 1,
        ).forEach { (button, count) ->
            Event.fireEvent(expected, mouseEvent(button = button, count = count))
            Event.fireEvent(actual, mouseEvent(button = button, count = count))
        }
        assertEquals(listOf("single", "double", "secondary"), expectedCalls)
        assertEquals(expectedCalls, actualCalls)
    }

    @Test
    fun shortcutsMatchNativeKeyCombinationsIncludingUnmatchedModifiers() = onFxThread {
        val actual = Rectangle()
        val expected = Rectangle()
        val actualCalls = mutableListOf<String>()
        val expectedCalls = mutableListOf<String>()
        val customCombination = KeyCodeCombination(KeyCode.D, KeyCombination.CONTROL_DOWN, KeyCombination.SHIFT_DOWN)
        actual.shortcuts {
            ctrlKey(KeyCode.A) { actualCalls += "obsolete" }
            ctrlKey(KeyCode.A) { actualCalls += "control" }
            shiftKey(KeyCode.B) { actualCalls += "shift" }
            altKey(KeyCode.C) { actualCalls += "alt" }
            on(customCombination) { actualCalls += "custom" }
            on(KeyCode.ENTER) { actualCalls += "enter" }
        }
        expected.onKeyPressed = EventHandler { event ->
            if (KeyCodeCombination(KeyCode.A, KeyCombination.CONTROL_DOWN).match(event)) expectedCalls += "control"
            if (KeyCodeCombination(KeyCode.B, KeyCombination.SHIFT_DOWN).match(event)) expectedCalls += "shift"
            if (KeyCodeCombination(KeyCode.C, KeyCombination.ALT_DOWN).match(event)) expectedCalls += "alt"
            if (customCombination.match(event)) expectedCalls += "custom"
            if (KeyCodeCombination(KeyCode.ENTER).match(event)) expectedCalls += "enter"
        }
        val events = listOf(
            keyEvent(KeyCode.A, control = true), keyEvent(KeyCode.B, shift = true),
            keyEvent(KeyCode.C, alt = true), keyEvent(KeyCode.D, shift = true, control = true),
            keyEvent(KeyCode.ENTER), keyEvent(KeyCode.A), keyEvent(KeyCode.A, shift = true, control = true),
        )
        events.forEach { event ->
            Event.fireEvent(expected, event.copyFor(expected, expected))
            Event.fireEvent(actual, event.copyFor(actual, actual))
        }
        assertEquals(listOf("control", "shift", "alt", "custom", "enter"), expectedCalls)
        assertEquals(expectedCalls, actualCalls)
    }

    @Test
    fun eventFiltersAndHandlersFollowNativeCaptureBubbleAndConsumption() = onFxThread {
        val actual = Rectangle()
        val expected = Rectangle()
        val actualParent = Group(actual)
        val expectedParent = Group(expected)
        val actualCalls = mutableListOf<String>()
        val expectedCalls = mutableListOf<String>()
        actualParent.addDslEventFilter(MouseEvent.MOUSE_CLICKED) { actualCalls += "parent-filter" }
        actual.addDslEventFilter(MouseEvent.MOUSE_CLICKED) {
            actualCalls += "child-filter"
            if (it.button == MouseButton.SECONDARY) it.consume()
        }
        actual.addDslEventHandler(MouseEvent.MOUSE_CLICKED) { actualCalls += "child-handler" }
        actualParent.addDslEventHandler(MouseEvent.MOUSE_CLICKED) { actualCalls += "parent-handler" }
        expectedParent.addEventFilter(MouseEvent.MOUSE_CLICKED, EventHandler { expectedCalls += "parent-filter" })
        expected.addEventFilter(MouseEvent.MOUSE_CLICKED, EventHandler {
            expectedCalls += "child-filter"
            if (it.button == MouseButton.SECONDARY) it.consume()
        })
        expected.addEventHandler(MouseEvent.MOUSE_CLICKED, EventHandler { expectedCalls += "child-handler" })
        expectedParent.addEventHandler(MouseEvent.MOUSE_CLICKED, EventHandler { expectedCalls += "parent-handler" })

        listOf(MouseButton.PRIMARY, MouseButton.SECONDARY).forEach { button ->
            Event.fireEvent(expected, mouseEvent(button = button))
            Event.fireEvent(actual, mouseEvent(button = button))
        }
        assertEquals(
            listOf(
                "parent-filter", "child-filter", "child-handler", "parent-handler",
                "parent-filter", "child-filter"
            ), expectedCalls
        )
        assertEquals(expectedCalls, actualCalls)
    }

    @Test
    fun dragStartHandlerReceivesNativeDragDetectedEvent() = onFxThread {
        val actual = Rectangle()
        val expected = Rectangle()
        val actualCalls = mutableListOf<Double>()
        val expectedCalls = mutableListOf<Double>()
        actual.dragHandlers { onDragStart = { actualCalls += it.x } }
        expected.onDragDetected = EventHandler { expectedCalls += it.x }

        Event.fireEvent(expected, mouseEvent(MouseEvent.DRAG_DETECTED))
        Event.fireEvent(actual, mouseEvent(MouseEvent.DRAG_DETECTED))

        assertEquals(listOf(4.0), expectedCalls)
        assertEquals(expectedCalls, actualCalls)
    }

    private fun mouseEvent(
        type: EventType<MouseEvent> = MouseEvent.MOUSE_CLICKED,
        button: MouseButton = MouseButton.PRIMARY,
        count: Int = 1,
    ): MouseEvent = MouseEvent(
        type, 4.0, 6.0, 4.0, 6.0, button, count,
        false, false, false, false, false, false, false, false, false, true, null
    )

    private fun keyEvent(
        code: KeyCode,
        shift: Boolean = false,
        control: Boolean = false,
        alt: Boolean = false,
    ): KeyEvent = KeyEvent(KeyEvent.KEY_PRESSED, "", "", code, shift, control, alt, false)
}
