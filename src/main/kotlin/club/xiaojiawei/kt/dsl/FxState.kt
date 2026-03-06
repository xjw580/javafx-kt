package club.xiaojiawei.kt.dsl

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import javafx.scene.text.Text
import kotlin.reflect.KProperty

/**
 * 响应式状态容器，类似 Compose 的 MutableState
 *
 * 使用方式：
 * ```
 * val secondsState = state(0)
 * var seconds by secondsState
 *
 * label.observe(secondsState) { "已运行 $it 秒" }
 * ```
 *
 * @author 肖嘉威
 */

/**
 * FxState 公共接口，用于 observe 统一接受不同类型的 State
 */
interface FxStateBase {
    fun observableValue(): ObservableValue<*>
}

class FxState<T>(initial: T) : FxStateBase {

    private val property = SimpleObjectProperty(initial)

    fun property(): ObjectProperty<T> = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.property.set(value)
    }
}

class FxIntState(initial: Int) : FxStateBase {

    private val property = SimpleIntegerProperty(initial)

    fun property(): IntegerProperty = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.property.set(value)
    }
}

class FxDoubleState(initial: Double) : FxStateBase {

    private val property = SimpleDoubleProperty(initial)

    fun property(): DoubleProperty = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        this.property.set(value)
    }
}

class FxBooleanState(initial: Boolean) : FxStateBase {

    private val property = SimpleBooleanProperty(initial)

    fun property(): BooleanProperty = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        this.property.set(value)
    }
}

class FxStringState(initial: String) : FxStateBase {

    private val property = SimpleStringProperty(initial)

    fun property(): StringProperty = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.property.set(value)
    }
}

class FxLongState(initial: Long) : FxStateBase {

    private val property = SimpleLongProperty(initial)

    fun property(): LongProperty = property

    override fun observableValue(): ObservableValue<*> = property

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        this.property.set(value)
    }
}

// ======================== 工厂函数 ========================

fun <T> state(initial: T): FxState<T> = FxState(initial)

fun intState(initial: Int = 0): FxIntState = FxIntState(initial)

fun doubleState(initial: Double = 0.0): FxDoubleState = FxDoubleState(initial)

fun booleanState(initial: Boolean = false): FxBooleanState = FxBooleanState(initial)

fun stringState(initial: String = ""): FxStringState = FxStringState(initial)

fun longState(initial: Long = 0L): FxLongState = FxLongState(initial)

// ======================== observe 扩展函数 ========================

// --- 单个 FxState<T> 观察，block 接收当前值 ---

fun <T> Labeled.observe(state: FxState<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding(
        { block(state.property().get()) },
        state.property()
    )
    textProperty().bind(binding)
}

fun <T> Text.observe(state: FxState<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding(
        { block(state.property().get()) },
        state.property()
    )
    textProperty().bind(binding)
}

fun <T> TextInputControl.observe(state: FxState<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding(
        { block(state.property().get()) },
        state.property()
    )
    textProperty().bind(binding)
}

// --- 多个 State 观察，block 通过闭包捕获值 ---

fun Labeled.observe(vararg states: FxStateBase, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}

fun Text.observe(vararg states: FxStateBase, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}

fun TextInputControl.observe(vararg states: FxStateBase, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}
