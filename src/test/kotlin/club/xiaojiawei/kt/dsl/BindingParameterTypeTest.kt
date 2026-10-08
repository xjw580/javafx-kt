package club.xiaojiawei.kt.dsl

import javafx.application.Platform
import javafx.beans.property.Property
import javafx.beans.property.ReadOnlyObjectWrapper
import javafx.beans.property.ReadOnlyStringWrapper
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ObservableValue
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class BindingParameterTypeTest {

    @Test
    fun textBindingsAcceptReadOnlySources() = runOnJavaFxThread {
        val source = ReadOnlyStringWrapper("before")
        val observable: ObservableValue<out String> = source.readOnlyProperty
        val properties = listOf(
            label { bindText(observable) }.textProperty(),
            text { bindText(observable) }.textProperty(),
            textField { bindText(observable) }.textProperty(),
            textArea { bindText(observable) }.textProperty(),
            labelSeparator { bindText(observable) }.textProperty(),
        )
        properties.forEach {
            assertEquals("before", it.value)
            assertTrue(it.isBound)
        }
        source.set("after")
        properties.forEach { assertEquals("after", it.value) }
    }

    @Test
    fun reverseTextBindingsAcceptPropertiesOfSupertypes() = runOnJavaFxThread {
        val targets = List<Property<Any>>(4) { SimpleObjectProperty<Any>() }
        val properties = listOf(
            label("label") { byBindText(targets[0]) }.textProperty(),
            text("text") { byBindText(targets[1]) }.textProperty(),
            textField("field") { byBindText(targets[2]) }.textProperty(),
            textArea("area") { byBindText(targets[3]) }.textProperty(),
        )
        properties.forEachIndexed { index, property ->
            assertEquals(property.value, targets[index].value)
            property.value = "updated"
            assertEquals("updated", targets[index].value)
            assertTrue(targets[index].isBound)
        }
    }

    @Test
    fun bidirectionalTextBindingsAcceptGenericPropertiesAndPreserveInitialDirection() = runOnJavaFxThread {
        val target: Property<String> = SimpleObjectProperty("model")
        val properties = listOf(
            label("view") { bindBidirectionalText(target) }.textProperty(),
            text("view") { bindBidirectionalText(target) }.textProperty(),
            textField("view") { bindBidirectionalText(target) }.textProperty(),
            textArea("view") { bindBidirectionalText(target) }.textProperty(),
            labelSeparator("view") { bindBidirectionalText(target) }.textProperty(),
        )
        properties.forEach { assertEquals("model", it.value) }
        properties.first().value = "edited"
        assertEquals("edited", target.value)
        properties.forEach { assertEquals("edited", it.value) }
        target.value = "changed"
        properties.forEach { assertEquals("changed", it.value) }

        val targets = List<Property<String>>(4) { SimpleObjectProperty("model") }
        val reverseProperties = listOf(
            label("view") { byBindBidirectionalText(targets[0]) }.textProperty(),
            text("view") { byBindBidirectionalText(targets[1]) }.textProperty(),
            textField("view") { byBindBidirectionalText(targets[2]) }.textProperty(),
            textArea("view") { byBindBidirectionalText(targets[3]) }.textProperty(),
        )
        reverseProperties.forEachIndexed { index, property ->
            assertEquals("view", targets[index].value)
            targets[index].value = "model edit"
            assertEquals("model edit", property.value)
            property.value = "view edit"
            assertEquals("view edit", targets[index].value)
        }
    }

    @Test
    fun booleanBindingsAcceptExpressionsAndGenericProperties() = runOnJavaFxThread {
        val source = SimpleBooleanProperty(false)
        val observable: ObservableValue<out Boolean> = source.not()
        val bound = listOf(
            checkBox { bindSelected(observable) }.selectedProperty(),
            radioButton { bindSelected(observable) }.selectedProperty(),
            switch { bindStatus(observable) }.statusProperty(),
            pane { bindDisable(observable) }.disableProperty(),
            pane { bindVisible(observable) }.visibleProperty(),
            pane { bindManaged(observable) }.managedProperty(),
        )
        bound.forEach { assertTrue(it.value) }
        source.set(true)
        bound.forEach { assertEquals(false, it.value) }

        val target: Property<Boolean> = SimpleObjectProperty(true)
        val bidirectional = listOf(
            checkBox { bindBidirectionalSelected(target) }.selectedProperty(),
            radioButton { bindBidirectionalSelected(target) }.selectedProperty(),
            switch { bindBidirectionalStatus(target) }.statusProperty(),
        )
        bidirectional.forEach { assertTrue(it.value) }
        target.value = false
        bidirectional.forEach { assertEquals(false, it.value) }
        bidirectional.first().value = true
        assertTrue(target.value)

        val reverseTargets = List<Property<Any>>(3) { SimpleObjectProperty<Any>() }
        val reverse = listOf(
            checkBox { byBindSelected(reverseTargets[0]) }.selectedProperty(),
            radioButton { byBindSelected(reverseTargets[1]) }.selectedProperty(),
            switch { byBindStatus(reverseTargets[2]) }.statusProperty(),
        )
        reverse.forEachIndexed { index, property ->
            assertEquals(property.value, reverseTargets[index].value)
            property.value = !property.value
            assertEquals(property.value, reverseTargets[index].value)
        }

        val reverseBidirectionalTargets = List<Property<Boolean>>(3) { SimpleObjectProperty(true) }
        val reverseBidirectional = listOf(
            checkBox { byBindBidirectionalSelected(reverseBidirectionalTargets[0]) }.selectedProperty(),
            radioButton { byBindBidirectionalSelected(reverseBidirectionalTargets[1]) }.selectedProperty(),
            switch { byBindBidirectionalStatus(reverseBidirectionalTargets[2]) }.statusProperty(),
        )
        reverseBidirectional.forEachIndexed { index, property ->
            assertEquals(property.value, reverseBidirectionalTargets[index].value)
            reverseBidirectionalTargets[index].value = true
            assertTrue(property.value)
            property.value = false
            assertEquals(false, reverseBidirectionalTargets[index].value)
        }
    }

    @Test
    fun numericBindingsAcceptExpressionsAndPropertiesOfSupertypes() = runOnJavaFxThread {
        val source = SimpleDoubleProperty(0.2)
        val observable: ObservableValue<out Number> = source.multiply(2)
        val progress = progressBar { bindProgress(observable) }
        val slider = slider { bindValue(observable) }
        assertEquals(0.4, progress.progress)
        assertEquals(0.4, slider.value)
        source.set(0.3)
        assertEquals(0.6, progress.progress)
        assertEquals(0.6, slider.value)

        val targets = List<Property<Any>>(12) { SimpleObjectProperty<Any>() }
        val region = pane {
            byBindWidth(targets[0])
            byBindHeight(targets[1])
            byBindPrefWidth(targets[2])
            byBindPrefHeight(targets[3])
            byBindMinWidth(targets[4])
            byBindMinHeight(targets[5])
            byBindMaxWidth(targets[6])
            byBindMaxHeight(targets[7])
        }
        region.resize(100.0, 50.0)
        region.setPrefSize(120.0, 60.0)
        region.setMinSize(10.0, 20.0)
        region.setMaxSize(500.0, 400.0)
        val image = imageView {
            byBindFitWidth(targets[8])
            byBindFitHeight(targets[9])
        }
        image.fitWidth = 80.0
        image.fitHeight = 40.0
        val reverseProgress = progressBar { byBindProgress(targets[10]) }
        reverseProgress.progress = 0.7
        val reverseSlider = slider { byBindValue(targets[11]) }
        reverseSlider.value = 12.0
        assertEquals(
            listOf(100.0, 50.0, 120.0, 60.0, 10.0, 20.0, 500.0, 400.0, 80.0, 40.0, 0.7, 12.0),
            targets.map { it.value },
        )
    }

    @Test
    fun comboBoxBindingsPreserveGenericVarianceAndBidirectionalUpdates() = runOnJavaFxThread {
        val source = ReadOnlyObjectWrapper("first")
        val combo = comboBox<CharSequence> { bindValue(source.readOnlyProperty) }
        source.set("second")
        assertEquals("second", combo.value)

        val destination: Property<Any> = SimpleObjectProperty<Any>()
        val reverse = comboBox<String> { byBindValue(destination) }
        reverse.value = "selected"
        assertEquals("selected", destination.value)

        val model: Property<String> = SimpleObjectProperty("model")
        val bidirectional = comboBox<String> { bindBidirectionalValue(model) }
        assertEquals("model", bidirectional.value)
        bidirectional.value = "edited"
        assertEquals("edited", model.value)
        model.value = "changed"
        assertEquals("changed", bidirectional.value)

        val reverseBidirectional = comboBox<String> {
            value("view")
            byBindBidirectionalValue(model)
        }
        assertEquals("view", model.value)
        model.value = "again"
        assertEquals("again", reverseBidirectional.value)
    }

    private fun <T> runOnJavaFxThread(action: () -> T): T {
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
