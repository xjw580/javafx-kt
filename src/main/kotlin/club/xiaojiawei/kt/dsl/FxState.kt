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

// --- 单个 Property 观察，block 接收当前值 ---

@Suppress("UNCHECKED_CAST")
fun <T> Labeled.observe(state: ObservableValue<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding({ block(state.value) }, state)
    textProperty().bind(binding)
}

@Suppress("UNCHECKED_CAST")
fun <T> Text.observe(state: ObservableValue<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding({ block(state.value) }, state)
    textProperty().bind(binding)
}

@Suppress("UNCHECKED_CAST")
fun <T> TextInputControl.observe(state: ObservableValue<T>, block: (T) -> String) {
    val binding = Bindings.createStringBinding({ block(state.value) }, state)
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