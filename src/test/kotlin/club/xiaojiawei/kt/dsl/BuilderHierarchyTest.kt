package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.FilterComboBox
import javafx.application.Platform
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ColorPicker
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.DatePicker
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import java.time.LocalDate
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BuilderHierarchyTest {

    @Test
    fun buttonActionSupportsEventLambdaHandlerAndClearing() = runOnJavaFxThread {
        var received: ActionEvent? = null
        val armed: Property<Any> = SimpleObjectProperty()
        val armedChanges = mutableListOf<Boolean>()
        val armedListener = ChangeListener { _, _, value -> armedChanges.add(value) }
        val callback: (ActionEvent) -> Unit = { received = it }
        val button = button {
            style()
            onAction(callback)
            byBindArmed(armed)
            addArmedListener(armedListener)
        }
        assertEquals(false, armed.value)
        button.arm()
        assertEquals(true, armed.value)
        button.disarm()
        assertEquals(false, armed.value)
        assertEquals(listOf(true, false), armedChanges)
        button.config { removeArmedListener(armedListener) }
        button.arm()
        button.disarm()
        assertEquals(listOf(true, false), armedChanges)
        button.fire()
        assertSame(button, received?.source)

        var handlerCalls = 0
        val handler = EventHandler<ActionEvent> { handlerCalls++ }
        assertSame(button, button.config { onAction(handler) })
        assertSame(handler, button.onAction)
        button.fire()
        assertEquals(1, handlerCalls)

        button.config { onAction(null) }
        assertNull(button.onAction)
        button.fire()
        assertEquals(1, handlerCalls)
    }

    @Test
    fun checkBoxPreservesActionOverloadsAndThreeStateCycle() = runOnJavaFxThread {
        var lambdaCalls = 0
        val callback: () -> Unit = { lambdaCalls++ }
        val check = checkBox {
            style()
            allowIndeterminate()
            indeterminate(false)
            selected(false)
            onAction(callback)
        }
        check.fire()
        assertTrue(check.isIndeterminate)
        assertFalse(check.isSelected)
        check.fire()
        assertFalse(check.isIndeterminate)
        assertTrue(check.isSelected)
        check.fire()
        assertFalse(check.isIndeterminate)
        assertFalse(check.isSelected)
        assertEquals(3, lambdaCalls)

        var received: ActionEvent? = null
        val handler = EventHandler<ActionEvent> { received = it }
        check.config { onAction(handler) }
        assertSame(handler, check.onAction)
        check.fire()
        assertSame(check, received?.source)
        assertEquals(3, lambdaCalls)
    }

    @Test
    fun radioButtonPreservesActionOverloadsAndGroupExclusion() = runOnJavaFxThread {
        val group = ToggleGroup()
        var lambdaCalls = 0
        val callback: () -> Unit = { lambdaCalls++ }
        val first = radioButton("first", group) {
            style()
            selected()
            onAction(callback)
        }
        var received: ActionEvent? = null
        val handler = EventHandler<ActionEvent> { received = it }
        val second = radioButton {
            style()
            toggleGroup(group)
            onAction(handler)
        }
        assertSame(first, group.selectedToggle)
        second.fire()
        assertSame(second, group.selectedToggle)
        assertFalse(first.isSelected)
        assertSame(second, received?.source)
        assertSame(handler, second.onAction)
        first.fire()
        assertEquals(1, lambdaCalls)
        assertSame(first, group.selectedToggle)
        assertFalse(second.isSelected)

        val changes = mutableListOf<Boolean>()
        val listener = ChangeListener { _, _, selected -> changes.add(selected) }
        val toggleBuilder = object : ToggleButtonBaseBuilder<ToggleButton>() {
            override fun buildInstance(): ToggleButton = ToggleButton()
        }.apply {
            style()
            toggleGroup(group)
            selected(false)
            addSelectedListener(listener)
        }
        val toggle = toggleBuilder.build()
        toggle.fire()
        assertSame(toggle, group.selectedToggle)
        assertFalse(first.isSelected)
        assertEquals(listOf(true), changes)
        toggleBuilder.delayMode()
        toggleBuilder.removeSelectedListener(listener)
        toggleBuilder.config(toggle)
        first.fire()
        assertFalse(toggle.isSelected)
        assertEquals(listOf(true), changes)
    }

    @Test
    fun textInputsPreserveImmediateDelayedAndExistingInstanceConfiguration() = runOnJavaFxThread {
        val fieldBuilder = textFieldBuilder {
            style()
            text("immediate")
            promptText("field prompt")
            editable(false)
            prefColumnCount(count = 12)
        }
        val immediate = fieldBuilder.instance()
        assertEquals("immediate", immediate.text)
        assertEquals(12, immediate.prefColumnCount)
        assertSame(immediate, fieldBuilder.build())
        assertSame(immediate, immediate.config {
            text("configured")
            editable()
            prefColumnCount(count = 18)
        })
        assertEquals("configured", immediate.text)
        assertTrue(immediate.isEditable)
        assertEquals(18, immediate.prefColumnCount)
        assertEquals("field prompt", immediate.promptText)

        val areaBuilder = textAreaBuilder {
            delayMode()
            style()
            text("delayed")
            promptText("area prompt")
            editable(false)
            prefColumnCount(columns = 24)
            prefRowCount(rows = 4)
            wrapText()
        }
        assertEquals("", areaBuilder.instance().text)
        val first = areaBuilder.build()
        val second = areaBuilder.build()
        assertNotSame(first, second)
        for (area in listOf(first, second)) {
            assertEquals("delayed", area.text)
            assertEquals("area prompt", area.promptText)
            assertFalse(area.isEditable)
            assertEquals(24, area.prefColumnCount)
            assertEquals(4, area.prefRowCount)
            assertTrue(area.isWrapText)
        }
        first.config {
            text("existing")
            prefColumnCount(columns = 30)
        }
        assertEquals("existing", first.text)
        assertEquals(30, first.prefColumnCount)
        assertEquals("delayed", second.text)
    }

    @Test
    fun shapesPreserveNamedFillAndReplaceDashArraysAcrossDelayedBuilds() = runOnJavaFxThread {
        val circle = CircleBuilder().apply {
            style()
            radius(6.0)
            fill(color = Color.CORAL)
            stroke(Color.BLACK)
            strokeWidth(2.0)
        }.build()
        assertEquals(6.0, circle.radius)
        assertEquals(Color.CORAL, circle.fill)
        assertEquals(Color.BLACK, circle.stroke)
        assertEquals(2.0, circle.strokeWidth)
        val caption = text("outlined") {
            style()
            stroke(Color.RED)
            strokeWidth(1.5)
            strokeDashArray(2.0, 3.0)
        }
        assertEquals(Color.RED, caption.stroke)
        assertEquals(1.5, caption.strokeWidth)
        assertEquals(listOf(2.0, 3.0), caption.strokeDashArray.toList())
        val paint: Paint = Color.DARKBLUE
        val builder = polygonBuilder {
            delayMode()
            style()
            points(0.0, 0.0, 10.0, 0.0, 5.0, 8.0)
            fill(paint = paint)
            strokeDashArray(1.0, 2.0)
            strokeDashArray(3.0, 4.0)
        }
        val first = builder.build()
        val second = builder.build()
        assertNotSame(first, second)
        for (polygon in listOf(first, second)) {
            assertEquals(paint, polygon.fill)
            assertEquals(listOf(3.0, 4.0), polygon.strokeDashArray.toList())
            assertEquals(listOf(0.0, 0.0, 10.0, 0.0, 5.0, 8.0), polygon.points.toList())
        }
        first.config { strokeDashArray() }
        assertTrue(first.strokeDashArray.isEmpty())
        assertEquals(listOf(3.0, 4.0), second.strokeDashArray.toList())
        builder.strokeDashArray()
        assertTrue(builder.build().strokeDashArray.isEmpty())
    }

    @Test
    fun comboBoxPreservesItemAppendReplacementAndConverterCallbacks() = runOnJavaFxThread {
        val combo = comboBox {
            style()
            items(1, 2)
            items(listOf(3, 4))
            visibleRowCount(7)
            converter(fromString = { it?.removePrefix("#")?.toInt() }, toString = { it?.let { "#$it" } })
        }
        assertEquals(listOf(1, 2, 3, 4), combo.items.toList())
        assertEquals(7, combo.visibleRowCount)
        assertEquals("#12", combo.converter.toString(12))
        assertEquals(12, combo.converter.fromString("#12"))
        assertNull(combo.converter.toString(null))
        assertNull(combo.converter.fromString(null))

        val replacement = FXCollections.observableArrayList(8, 9)
        combo.config { items(replacement) }
        assertSame(replacement, combo.items)
        replacement.add(10)
        combo.config { items(listOf(11)) }
        assertEquals(listOf(8, 9, 10, 11), replacement.toList())
    }

    @Test
    fun filterComboBoxBuilderRetainsConcreteTypeOptionsAndComboStyles() = runOnJavaFxThread {
        val builder = FilterComboBoxBuilder<String>().apply {
            delayMode()
            items("Alpha", "Beta")
            ignoreCase(false)
            visibleRowCount(5)
            style(StyleColor.SUCCESS, StyleSize.SMALL)
        }
        val first: FilterComboBox<String> = builder.build()
        val second: FilterComboBox<String> = builder.build()
        assertNotSame(first, second)
        for (combo in listOf(first, second)) {
            assertFalse(combo.isIgnoreCase)
            assertEquals(listOf("Alpha", "Beta"), combo.items.toList())
            assertEquals(5, combo.visibleRowCount)
            assertTrue(
                combo.styleClass.containsAll(
                    listOf(
                        "combo-box-ui",
                        "combo-box-ui-success",
                        "combo-box-ui-small"
                    )
                )
            )
        }
    }

    @Test
    fun controlsInheritTooltipMenuVisibilityAndParentStylesheets() = runOnJavaFxThread {
        val visibility = SimpleBooleanProperty(true)
        val builders: List<ControlBuilder<out Control>> = listOf(
            LabelBuilder(), ButtonBuilder(), CheckBoxBuilder(), RadioButtonBuilder(),
            TextFieldBuilder(), TextAreaBuilder(), ComboBoxBuilder<String>(),
            FilterComboBoxBuilder<String>(), ListViewBuilder<String>(), TableViewBuilder<String>(),
            ProgressBarBuilder(), SliderBuilder(), SeparatorBuilder(), PaginationBuilder(),
            ScrollPaneBuilder(), SplitPaneBuilder(), TitledPaneBuilder(),
        )
        val controls = builders.map { builder ->
            val menu = ContextMenu()
            val originalStylesheetCount = builder.instance().stylesheets.size
            builder.apply {
                delayMode()
                style()
                toolTip("inherited tooltip")
                contextMenu(menu)
                bindVisible(visibility)
                stylesheet { styleClass("inherited") { opacity(0.8) } }
            }
            builder.build().also {
                assertEquals("inherited tooltip", it.tooltip.text)
                assertSame(menu, it.contextMenu)
                assertTrue(it.isVisible)
                assertTrue(it.visibleProperty().isBound)
                assertEquals(originalStylesheetCount + 1, it.stylesheets.size)
                assertTrue(it.stylesheets.last().startsWith("data:text/css"))
            }
        }
        visibility.set(false)
        controls.forEach { assertFalse(it.isVisible) }
    }

    @Test
    fun comboBoxBaseSupportsDateAndColorValuesWithoutComboSpecificStyles() = runOnJavaFxThread {
        val initialDate = LocalDate.of(2026, 1, 2)
        val dateSource = ReadOnlyObjectWrapper(initialDate)
        val dateChanges = mutableListOf<LocalDate>()
        val listener = ChangeListener { _, _, value -> dateChanges.add(value) }
        val dateBuilder = object : ComboBoxBaseBuilder<DatePicker, LocalDate>() {
            override fun buildInstance(): DatePicker = DatePicker()
        }.apply {
            style(StyleColor.SUCCESS, StyleSize.SMALL)
            bindValue(dateSource.readOnlyProperty)
            addValueListener(listener)
            promptText("Choose date")
            editable(false)
        }
        val datePicker = dateBuilder.build()
        assertEquals(initialDate, datePicker.value)
        assertTrue(datePicker.valueProperty().isBound)
        assertEquals("Choose date", datePicker.promptText)
        assertFalse(datePicker.isEditable)
        val changedDate = initialDate.plusDays(1)
        dateSource.set(changedDate)
        assertEquals(changedDate, datePicker.value)
        assertEquals(listOf(changedDate), dateChanges)
        dateBuilder.delayMode()
        dateBuilder.removeValueListener(listener)
        dateBuilder.config(datePicker)
        dateSource.set(initialDate)
        assertEquals(initialDate, datePicker.value)
        assertEquals(listOf(changedDate), dateChanges)

        val colorModel: Property<Color> = SimpleObjectProperty(Color.CORAL)
        val mirroredColor: Property<Any> = SimpleObjectProperty()
        val colorPicker = object : ComboBoxBaseBuilder<ColorPicker, Color>() {
            override fun buildInstance(): ColorPicker = ColorPicker()
        }.apply {
            style(StyleColor.SUCCESS, StyleSize.SMALL)
            bindBidirectionalValue(colorModel)
            byBindValue(mirroredColor)
        }.build()
        assertEquals(Color.CORAL, colorPicker.value)
        colorPicker.value = Color.BLUE
        assertEquals(Color.BLUE, colorModel.value)
        assertEquals(Color.BLUE, mirroredColor.value)
        colorModel.value = Color.GREEN
        assertEquals(Color.GREEN, colorPicker.value)
        assertEquals(Color.GREEN, mirroredColor.value)
        for (picker in listOf(datePicker, colorPicker)) {
            assertTrue(picker.styleClass.none { it.startsWith("combo-box-ui") })
        }
    }

    private fun <T> runOnJavaFxThread(action: () -> T): T {
        if (Platform.isFxApplicationThread()) return action()
        val started = CountDownLatch(1)
        try {
            Platform.startup { started.countDown() }
        } catch (_: IllegalStateException) {
            started.countDown()
        }
        assertTrue(started.await(10, TimeUnit.SECONDS))
        val future = FutureTask(action)
        Platform.runLater(future)
        return future.get(10, TimeUnit.SECONDS)
    }
}
