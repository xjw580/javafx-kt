package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import club.xiaojiawei.kt.i18n.LocalizedText
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ObservableValue
import javafx.scene.control.TextInputControl
import javafx.scene.text.Font

@FXMarker
abstract class TextInputControlBuilder<S : TextInputControl> : ControlBuilder<S>() {

    fun text(text: String) {
        settings { this.text = text }
    }

    operator fun String.unaryPlus() = text(this)

    fun text(text: LocalizedText) {
        settings { this.text = text.value }
    }

    operator fun LocalizedText.unaryPlus() = text(this)

    fun <T> observe(state: ObservableValue<T>, block: (T) -> String) = settings {
        val binding = Bindings.createStringBinding({ block(state.value) }, state)
        textProperty().bind(binding)
    }

    fun observes(vararg states: ObservableValue<*>, block: () -> String) = settings {
        val binding = Bindings.createStringBinding({ block() }, *states)
        textProperty().bind(binding)
    }

    fun promptText(prompt: String) {
        settings { promptText = prompt }
    }

    fun promptText(prompt: LocalizedText) {
        settings { promptTextProperty().bind(prompt.binding()) }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    fun font(font: Font) {
        settings { this.font = font }
    }

    fun fontSize(size: Double) {
        settings { font = Font.font(size) }
    }

    // 数据绑定
    fun byBindText(property: Property<in String>) {
        settings { property.bind(textProperty()) }
    }

    fun bindText(property: ObservableValue<out String>) {
        settings { textProperty().bind(property) }
    }

    fun bindBidirectionalText(property: Property<String>) {
        settings { textProperty().bindBidirectional(property) }
    }

    fun byBindBidirectionalText(property: Property<String>) {
        settings { property.bindBidirectional(textProperty()) }
    }
}
