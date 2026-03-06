package club.xiaojiawei.kt.dsl

import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.scene.control.Labeled
import javafx.scene.control.TextInputControl
import javafx.scene.text.Text

/**
 * JavaFX Property 响应式观察扩展
 *
 * 直接使用 JavaFX Property，无需额外封装层。
 * 配合 FXExt.kt 中的 by 委托，实现类似 Compose 的响应式效果：
 *
 * ```
 * val secondsProperty = SimpleIntegerProperty(0)
 * var seconds by secondsProperty
 *
 * label.observe(secondsProperty) { "已运行 $seconds 秒" }
 * ```
 *
 * @author 肖嘉威
 */

// ======================== observe 扩展函数 ========================

// --- 单个 Property 观察，block 通过闭包捕获值 ---

fun Labeled.observe(state: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, state)
    textProperty().bind(binding)
}

fun Text.observe(state: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, state)
    textProperty().bind(binding)
}

fun TextInputControl.observe(state: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, state)
    textProperty().bind(binding)
}

// --- 多个 Property 观察，任一变化时自动更新 ---

fun Labeled.observes(vararg states: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, *states)
    textProperty().bind(binding)
}

fun Text.observes(vararg states: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, *states)
    textProperty().bind(binding)
}

fun TextInputControl.observes(vararg states: ObservableValue<*>, block: () -> String) {
    val binding = Bindings.createStringBinding({ block() }, *states)
    textProperty().bind(binding)
}

// ======================== IntegerProperty 特化扩展 ========================

operator fun IntegerProperty.plusAssign(delta: Int) {
    set(get() + delta)
}

operator fun IntegerProperty.minusAssign(delta: Int) {
    set(get() - delta)
}

operator fun IntegerProperty.timesAssign(factor: Int) {
    set(get() * factor)
}

operator fun IntegerProperty.divAssign(divisor: Int) {
    set(get() / divisor)
}

// ======================== LongProperty 特化扩展 ========================

operator fun LongProperty.plusAssign(delta: Long) {
    set(get() + delta)
}

operator fun LongProperty.minusAssign(delta: Long) {
    set(get() - delta)
}

operator fun LongProperty.timesAssign(factor: Long) {
    set(get() * factor)
}

operator fun LongProperty.divAssign(divisor: Long) {
    set(get() / divisor)
}

// ======================== DoubleProperty 特化扩展 ========================

operator fun DoubleProperty.plusAssign(delta: Double) {
    set(get() + delta)
}

operator fun DoubleProperty.minusAssign(delta: Double) {
    set(get() - delta)
}

operator fun DoubleProperty.timesAssign(factor: Double) {
    set(get() * factor)
}

operator fun DoubleProperty.divAssign(divisor: Double) {
    set(get() / divisor)
}

// ======================== BooleanProperty 特化扩展 ========================

/** 取反 */
fun BooleanProperty.toggle() {
    set(!get())
}

// ======================== StringProperty 特化扩展 ========================

/** 字符串拼接赋值 */
operator fun StringProperty.plusAssign(suffix: String) {
    set(get() + suffix)
}

/** 清空 */
fun StringProperty.clear() {
    set("")
}