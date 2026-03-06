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
abstract class FxStateBase<T> {
    abstract fun observableValue(): Property<T>
    open fun value(): T = observableValue().value
    override fun toString(): String = value().toString()
}

class FxState<T>(initial: T) : FxStateBase<T>() {

    private val property = SimpleObjectProperty(initial)

    fun property(): ObjectProperty<T> = property

    override fun observableValue(): Property<T> = property

    override fun value(): T = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.property.set(value)
    }
}

class FxIntState(initial: Int) : FxStateBase<Number>() {

    private val property = SimpleIntegerProperty(initial)

    fun property(): IntegerProperty = property

    override fun observableValue(): Property<Number> = property

    override fun value(): Int = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Int {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        this.property.set(value)
    }

    operator fun inc(): FxIntState {
        property.set(property.get() + 1)
        return this
    }

    operator fun dec(): FxIntState {
        property.set(property.get() - 1)
        return this
    }

    operator fun plusAssign(delta: Int) {
        property.set(property.get() + delta)
    }

    operator fun minusAssign(delta: Int) {
        property.set(property.get() - delta)
    }

    operator fun timesAssign(factor: Int) {
        property.set(property.get() * factor)
    }

    operator fun divAssign(divisor: Int) {
        property.set(property.get() / divisor)
    }

    operator fun unaryMinus(): Int = -property.get()

    operator fun compareTo(other: Int): Int = property.get().compareTo(other)
}

class FxDoubleState(initial: Double) : FxStateBase<Number>() {

    private val property = SimpleDoubleProperty(initial)

    fun property(): DoubleProperty = property

    override fun observableValue(): Property<Number> = property

    override fun value(): Double = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Double {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        this.property.set(value)
    }

    operator fun inc(): FxDoubleState {
        property.set(property.get() + 1.0)
        return this
    }

    operator fun dec(): FxDoubleState {
        property.set(property.get() - 1.0)
        return this
    }

    operator fun plusAssign(delta: Double) {
        property.set(property.get() + delta)
    }

    operator fun minusAssign(delta: Double) {
        property.set(property.get() - delta)
    }

    operator fun timesAssign(factor: Double) {
        property.set(property.get() * factor)
    }

    operator fun divAssign(divisor: Double) {
        property.set(property.get() / divisor)
    }

    operator fun unaryMinus(): Double = -property.get()

    operator fun compareTo(other: Double): Int = property.get().compareTo(other)
}

class FxBooleanState(initial: Boolean) : FxStateBase<Boolean>() {

    private val property = SimpleBooleanProperty(initial)

    fun property(): BooleanProperty = property

    override fun observableValue(): Property<Boolean> = property

    override fun value(): Boolean = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Boolean {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        this.property.set(value)
    }

    /** 取反 */
    fun toggle() {
        property.set(!property.get())
    }

    operator fun not(): Boolean = !property.get()

    infix fun and(other: Boolean): Boolean = property.get() && other

    infix fun or(other: Boolean): Boolean = property.get() || other

    infix fun and(other: FxBooleanState): Boolean = property.get() && other.property.get()

    infix fun or(other: FxBooleanState): Boolean = property.get() || other.property.get()
}

class FxStringState(initial: String) : FxStateBase<String>() {

    private val property = SimpleStringProperty(initial)

    fun property(): StringProperty = property

    override fun observableValue(): Property<String> = property

    override fun value(): String = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): String {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        this.property.set(value)
    }

    /** 字符串拼接赋值 */
    operator fun plusAssign(suffix: String) {
        property.set(property.get() + suffix)
    }

    /** 清空 */
    fun clear() {
        property.set("")
    }

    fun isEmpty(): Boolean = property.get().isEmpty()

    fun isNotEmpty(): Boolean = property.get().isNotEmpty()

    fun isBlank(): Boolean = property.get().isBlank()

    fun isNotBlank(): Boolean = property.get().isNotBlank()

    fun length(): Int = property.get().length

    operator fun contains(other: CharSequence): Boolean = property.get().contains(other)
}

class FxLongState(initial: Long) : FxStateBase<Number>() {

    private val property = SimpleLongProperty(initial)

    fun property(): LongProperty = property

    override fun observableValue(): Property<Number> = property

    override fun value(): Long = property.value

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Long {
        return this.property.get()
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        this.property.set(value)
    }

    operator fun inc(): FxLongState {
        property.set(property.get() + 1)
        return this
    }

    operator fun dec(): FxLongState {
        property.set(property.get() - 1)
        return this
    }

    operator fun plusAssign(delta: Long) {
        property.set(property.get() + delta)
    }

    operator fun minusAssign(delta: Long) {
        property.set(property.get() - delta)
    }

    operator fun timesAssign(factor: Long) {
        property.set(property.get() * factor)
    }

    operator fun divAssign(divisor: Long) {
        property.set(property.get() / divisor)
    }

    operator fun unaryMinus(): Long = -property.get()

    operator fun compareTo(other: Long): Int = property.get().compareTo(other)
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

fun Labeled.observe(vararg states: FxStateBase<*>, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}

fun Text.observe(vararg states: FxStateBase<*>, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}

fun TextInputControl.observe(vararg states: FxStateBase<*>, block: () -> String) {
    val binding = Bindings.createStringBinding(
        { block() },
        *states.map { it.observableValue() }.toTypedArray()
    )
    textProperty().bind(binding)
}


fun main() {
    val stringState = FxStringState("hello")
    stringState+="kotlin"
    println(stringState)
    var secondsState = FxIntState(0)
    println(secondsState.property().get())
    secondsState++
    println(secondsState.property().get())
    secondsState--
    println(secondsState.property().get())
}