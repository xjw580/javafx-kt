package club.xiaojiawei.kt.ext

import com.sun.javafx.application.PlatformImpl
import javafx.application.Platform
import javafx.beans.property.BooleanProperty
import javafx.beans.property.DoubleProperty
import javafx.beans.property.IntegerProperty
import javafx.beans.property.LongProperty
import javafx.beans.value.WritableValue
import javafx.scene.Node
import javafx.scene.layout.Pane
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