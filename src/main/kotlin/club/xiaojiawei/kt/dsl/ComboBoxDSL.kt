package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import club.xiaojiawei.kt.ext.addItems
import club.xiaojiawei.kt.i18n.LocalizedText
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ComboBox
import javafx.scene.control.ComboBoxBase
import javafx.util.StringConverter

/** ComboBoxBase 公共配置，可供日期选择器、颜色选择器等子类复用。 */
@FXMarker
abstract class ComboBoxBaseBuilder<S : ComboBoxBase<T>, T> : ControlBuilder<S>() {

    fun value(value: T) {
        settings { this.value = value }
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

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    // 响应式观察
    fun <V> observeValue(state: ObservableValue<V>, block: (V) -> T) = settings {
        val binding = Bindings.createObjectBinding({ block(state.value) }, state)
        valueProperty().bind(binding)
    }

    // 数据绑定
    fun byBindValue(property: Property<in T>) = settings { property.bind(valueProperty()) }

    fun bindValue(property: ObservableValue<out T>) = settings { valueProperty().bind(property) }

    fun bindBidirectionalValue(property: Property<T>) = settings { valueProperty().bindBidirectional(property) }

    fun byBindBidirectionalValue(property: Property<T>) = settings { property.bindBidirectional(valueProperty()) }

    fun addValueListener(changeListener: ChangeListener<in T>) {
        settings {
            valueProperty().addListener(changeListener)
        }
    }

    fun removeValueListener(changeListener: ChangeListener<in T>) {
        settings {
            valueProperty().removeListener(changeListener)
        }
    }
}

/** ComboBox 专属的列表、转换器与主题样式配置。 */
@FXMarker
abstract class ComboBoxControlBuilder<S : ComboBox<T>, T> : ComboBoxBaseBuilder<S, T>() {

    fun items(vararg items: T) {
        settings { this.addItems(*items) }
    }

    fun items(items: List<T>) {
        settings { this.addItems(items) }
    }

    fun items(items: ObservableList<T>) {
        settings { this.items = items }
    }

    fun converter(stringConverter: StringConverter<T>) {
        settings {
            converter = stringConverter
        }
    }

    fun converter(fromString: ((String?) -> T?)? = null, toString: ((T?) -> String?)?) {
        settings {
            converter = object : StringConverter<T>() {
                override fun toString(p0: T?): String? = toString?.invoke(p0)

                override fun fromString(p0: String?): T? = fromString?.invoke(p0)
            }
        }
    }

    fun visibleRowCount(count: Int) {
        settings { visibleRowCount = count }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("combo-box-ui")
            when (styleColor) {
                StyleColor.MAIN -> {}
                StyleColor.NORMAL -> styleClass.add("combo-box-ui-normal")
                StyleColor.SUCCESS -> styleClass.add("combo-box-ui-success")
                StyleColor.WARN -> styleClass.add("combo-box-ui-warn")
                StyleColor.ERROR -> styleClass.add("combo-box-ui-error")
                StyleColor.DEFAULT -> {}
            }
            when (styleSize) {
                StyleSize.TINY -> {}
                StyleSize.SMALL -> styleClass.add("combo-box-ui-small")
                StyleSize.BIG -> styleClass.add("combo-box-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}
