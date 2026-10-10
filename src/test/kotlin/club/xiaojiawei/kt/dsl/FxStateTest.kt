package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.test.onFxThread
import javafx.beans.binding.Bindings
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.text.Text
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FxStateTest {

    @Test
    fun singleStateObservationMatchesNativeBindingsForEveryTextTarget() = onFxThread {
        val state = SimpleIntegerProperty(2)
        val actualLabel = Label().apply { observe(state) { "Count: $it" } }
        val actualText = Text().apply { observe(state) { "Count: $it" } }
        val actualInput = TextField().apply { observe(state) { "Count: $it" } }
        val expectedLabel = Label()
        val expectedText = Text()
        val expectedInput = TextField()
        val nativeBinding = Bindings.createStringBinding({ "Count: ${state.get()}" }, state)
        listOf(expectedLabel.textProperty(), expectedText.textProperty(), expectedInput.textProperty()).forEach {
            it.bind(nativeBinding)
        }
        val properties = listOf(
            expectedLabel.textProperty() to actualLabel.textProperty(),
            expectedText.textProperty() to actualText.textProperty(),
            expectedInput.textProperty() to actualInput.textProperty(),
        )

        listOf(2, 7, -1).forEach { value ->
            state.set(value)
            properties.forEach { (expected, actual) ->
                assertTrue(actual.isBound)
                assertEquals(expected.get(), actual.get())
            }
        }
        properties.forEach { (expected, actual) ->
            expected.unbind()
            actual.unbind()
        }
        state.set(99)
        properties.forEach { (expected, actual) -> assertEquals(expected.get(), actual.get()) }
    }

    @Test
    fun multipleStateObservationTracksEachDependencyLikeNativeBindings() = onFxThread {
        val name = SimpleStringProperty("item")
        val count = SimpleIntegerProperty(1)
        val actualLabel = Label().apply { observes(name, count) { "${name.get()}: ${count.get()}" } }
        val actualText = Text().apply { observes(name, count) { "${name.get()}: ${count.get()}" } }
        val actualInput = TextField().apply { observes(name, count) { "${name.get()}: ${count.get()}" } }
        val expectedLabel = Label()
        val expectedText = Text()
        val expectedInput = TextField()
        val binding = Bindings.createStringBinding({ "${name.get()}: ${count.get()}" }, name, count)
        listOf(expectedLabel.textProperty(), expectedText.textProperty(), expectedInput.textProperty()).forEach {
            it.bind(binding)
        }
        val properties = listOf(
            expectedLabel.textProperty() to actualLabel.textProperty(),
            expectedText.textProperty() to actualText.textProperty(),
            expectedInput.textProperty() to actualInput.textProperty(),
        )

        fun assertTextMatches() = properties.forEach { (expected, actual) ->
            assertTrue(actual.isBound)
            assertEquals(expected.get(), actual.get())
        }

        assertTextMatches()
        name.set("renamed")
        assertTextMatches()
        count.set(5)
        assertTextMatches()
        name.set("")
        assertTextMatches()
    }

    @Test
    fun replacingObservationDetachesOldStateLikeNativeRebinding() = onFxThread {
        val original = SimpleStringProperty("original")
        val replacement = SimpleStringProperty("replacement")
        val actual = Label().apply { observe(original) { it } }
        val expected = Label().apply { textProperty().bind(original) }

        actual.observe(replacement) { it }
        expected.textProperty().bind(replacement)
        original.set("obsolete")
        assertEquals(expected.text, actual.text)
        replacement.set("current")
        assertEquals(expected.text, actual.text)
    }
}
