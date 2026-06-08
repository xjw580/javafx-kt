package club.xiaojiawei.kt.ext

import com.sun.javafx.application.PlatformImpl
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.LongProperty
import javafx.beans.property.StringProperty
import javafx.beans.value.WritableValue
import javafx.scene.Node
import javafx.scene.layout.*
import kotlin.reflect.KProperty


/**
 * @author 肖嘉威
 * @date 2025/8/12 14:23
 */

fun Node.removeSelfFromParent() {
    this.parent?.let {
        if (it is Pane) {
            it.children.remove(this)
        }
    }
}

/**
 * 确保在ui线程中执行
 */
inline fun runUI(crossinline block: () -> Unit) {
    if (PlatformImpl.isFxApplicationThread()) {
        block()
    } else {
        Platform.runLater { block() }
    }
}

inline fun runUILater(crossinline block: () -> Unit) {
    Platform.runLater { block() }
}

// ======================== Property代理 ========================

operator fun <T> WritableValue<T>.getValue(thisRef: Any?, property: KProperty<*>): T {
    return this.value
}

operator fun <T> WritableValue<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) {
    this.value = value
}

operator fun IntegerProperty.getValue(thisRef: Any?, property: KProperty<*>): Int {
    return this.value
}

operator fun IntegerProperty.setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
    this.value = value
}

operator fun LongProperty.getValue(thisRef: Any?, property: KProperty<*>): Long {
    return this.value
}

operator fun LongProperty.setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
    this.value = value
}

operator fun DoubleProperty.getValue(thisRef: Any?, property: KProperty<*>): Double {
    return this.value
}

operator fun DoubleProperty.setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
    this.value = value
}

operator fun BooleanProperty.getValue(thisRef: Any?, property: KProperty<*>): Boolean {
    return this.value
}

operator fun BooleanProperty.setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
    this.value = value
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