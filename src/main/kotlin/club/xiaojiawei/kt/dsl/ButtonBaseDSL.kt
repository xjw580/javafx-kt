package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.beans.binding.Bindings
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ButtonBase
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup

@FXMarker
abstract class ButtonBaseBuilder<T : ButtonBase> : LabeledBuilder<T>() {

    fun onAction(handler: EventHandler<ActionEvent>?) {
        settings { onAction = handler }
    }

    fun byBindArmed(property: Property<in Boolean>) = settings { property.bind(armedProperty()) }

    fun addArmedListener(listener: ChangeListener<Boolean>) = settings {
        armedProperty().addListener(listener)
    }

    fun removeArmedListener(listener: ChangeListener<Boolean>) = settings {
        armedProperty().removeListener(listener)
    }
}

@FXMarker
abstract class ToggleButtonBaseBuilder<S : ToggleButton> : ButtonBaseBuilder<S>() {

    fun selected(selected: Boolean = true) {
        settings { isSelected = selected }
    }

    fun toggleGroup(group: ToggleGroup) {
        settings { this.toggleGroup = group }
    }

    fun listenSelected(selectedCallback: () -> Unit) {
        settings {
            selectedProperty().addListener { p0, p1, p2 ->
                if (p2) {
                    selectedCallback()
                }
            }
        }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    // 响应式观察
    fun <T> observeSelected(state: ObservableValue<T>, block: (T) -> Boolean) = settings {
        val binding = Bindings.createBooleanBinding({ block(state.value) }, state)
        selectedProperty().bind(binding)
    }

    // 数据绑定
    fun byBindSelected(property: Property<in Boolean>) = settings { property.bind(selectedProperty()) }

    fun bindSelected(property: ObservableValue<out Boolean>) = settings { selectedProperty().bind(property) }

    fun bindBidirectionalSelected(property: Property<Boolean>) =
        settings { selectedProperty().bindBidirectional(property) }

    fun byBindBidirectionalSelected(property: Property<Boolean>) =
        settings { property.bindBidirectional(selectedProperty()) }

    fun addSelectedListener(listener: ChangeListener<Boolean>) = settings {
        selectedProperty().addListener(listener)
    }

    fun removeSelectedListener(listener: ChangeListener<Boolean>) = settings {
        selectedProperty().removeListener(listener)
    }
}
