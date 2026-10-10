package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.test.onFxThread
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.value.ChangeListener
import javafx.geometry.Insets
import javafx.scene.control.Label
import javafx.scene.control.Slider
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class BaseConfigurationDSLTest {
    @Test
    fun regionConfigurationMatchesNativeSettersAndPreservesExistingState() = onFxThread {
        val metadata = Any()
        val actual = Region().apply { userData = metadata; styleClass.add("existing") }
        val expected = Region().apply {
            userData = metadata
            styleClass.add("existing")
            id = "configured"
            setMinSize(10.0, 20.0)
            setPrefSize(100.0, 80.0)
            setMaxSize(200.0, 160.0)
            padding = Insets(1.0, 2.0, 3.0, 4.0)
            stylesheets.add("theme.css")
        }

        assertSame(actual, actual.configRegion(regionConfig {
            id("configured")
            minSize(10.0, 20.0)
            size(100.0, 80.0)
            maxSize(200.0, 160.0)
            padding(1.0, 2.0, 3.0, 4.0)
            stylesheets("theme.css")
        }))

        assertEquals(expected.id, actual.id)
        assertEquals(expected.minWidth, actual.minWidth)
        assertEquals(expected.minHeight, actual.minHeight)
        assertEquals(expected.prefWidth, actual.prefWidth)
        assertEquals(expected.prefHeight, actual.prefHeight)
        assertEquals(expected.maxWidth, actual.maxWidth)
        assertEquals(expected.maxHeight, actual.maxHeight)
        assertEquals(expected.padding, actual.padding)
        assertEquals(expected.stylesheets, actual.stylesheets)
        assertEquals(expected.styleClass, actual.styleClass)
        assertSame(metadata, actual.userData)
    }

    @Test
    fun paneConfigurationAppendsChildrenToTheExistingParentInOrder() = onFxThread {
        val child = Label("existing")
        val actual = Pane(child)
        val children = actual.children
        val expected = Pane(Label("existing"), Label("first"), Label("second")).apply {
            background = Background(BackgroundFill(Color.CORAL, null, null))
        }
        var providerCalls = 0

        assertSame(actual, actual.configPane(paneConfig {
            background(Color.CORAL)
            addLabel("first")
            add {
                providerCalls++
                Label("second")
            }
        }))

        assertEquals(expected.background, actual.background)
        assertEquals(expected.children.map { (it as Label).text }, actual.children.map { (it as Label).text })
        assertSame(children, actual.children)
        assertSame(child, actual.children.first())
        assertEquals(1, providerCalls)
        actual.children.forEach { assertSame(actual, it.parent) }
    }

    @Test
    fun baseConfigurationPreservesSubtypeAndExistingSpecificConfigCalls() = onFxThread {
        val actual: VBox = VBox().configRegion { padding(4.0) }
            .configPane { addLabel("body") }
            .config { spacing(8.0); fillWidth(false) }
        val expected = VBox(8.0, Label("body")).apply {
            padding = Insets(4.0)
            isFillWidth = false
        }
        assertEquals(expected.padding, actual.padding)
        assertEquals(expected.spacing, actual.spacing)
        assertEquals(expected.isFillWidth, actual.isFillWidth)
        assertEquals((expected.children.single() as Label).text, (actual.children.single() as Label).text)

        val slider: Slider = Slider().configRegion { prefWidth(160.0) }.config { range(2.0, 8.0); value(5.0) }
        val nativeSlider = Slider(2.0, 8.0, 5.0).apply { prefWidth = 160.0 }
        assertEquals(nativeSlider.prefWidth, slider.prefWidth)
        assertEquals(nativeSlider.min, slider.min)
        assertEquals(nativeSlider.max, slider.max)
        assertEquals(nativeSlider.value, slider.value)
    }

    @Test
    fun bindingsAndListenerRemovalApplyToTheConfiguredInstance() = onFxThread {
        val actual = Region()
        val expected = Region()
        val source = SimpleDoubleProperty(100.0)
        val actualChanges = mutableListOf<Double>()
        val expectedChanges = mutableListOf<Double>()
        val actualListener = ChangeListener<Number> { _, _, value -> actualChanges.add(value.toDouble()) }
        val expectedListener = ChangeListener<Number> { _, _, value -> expectedChanges.add(value.toDouble()) }
        expected.prefWidthProperty().bind(source)
        expected.prefWidthProperty().addListener(expectedListener)
        actual.configRegion {
            bindPrefWidth(source)
            addPrefWidthListener(actualListener)
        }

        source.set(140.0)
        assertTrue(actual.prefWidthProperty().isBound)
        assertEquals(expected.prefWidth, actual.prefWidth)
        assertEquals(listOf(140.0), actualChanges)
        assertEquals(expectedChanges, actualChanges)

        expected.prefWidthProperty().removeListener(expectedListener)
        actual.configRegion { removePrefWidthListener(actualListener) }
        source.set(180.0)
        assertEquals(expected.prefWidth, actual.prefWidth)
        assertEquals(listOf(140.0), actualChanges)
        assertEquals(expectedChanges, actualChanges)
    }

    @Test
    fun reusableConfigurationCreatesIndependentChildrenForEachTarget() = onFxThread {
        val configuration = paneConfig { addLabel("child"); padding(3.0) }
        val first = Pane().configPane(configuration)
        val second = Pane().configPane(configuration)
        val expected = Pane(Label("child")).apply { padding = Insets(3.0) }

        for (pane in listOf(first, second)) {
            assertEquals(expected.padding, pane.padding)
            assertEquals((expected.children.single() as Label).text, (pane.children.single() as Label).text)
            assertSame(pane, pane.children.single().parent)
        }
        (first.children.single() as Label).text = "changed"
        assertEquals("child", (second.children.single() as Label).text)
    }

    @Test
    fun emptyConfigurationRetainsBindingsAndChildren() = onFxThread {
        val source = SimpleDoubleProperty(42.0)
        val child = Label("keep")
        val pane = Pane(child).apply { prefWidthProperty().bind(source) }
        assertSame(pane, pane.configPane {})
        assertSame(pane, pane.configRegion {})
        assertSame(child, pane.children.single())
        assertTrue(pane.prefWidthProperty().isBound)
        source.set(84.0)
        assertEquals(84.0, pane.prefWidth)
    }
}
