package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.beans.property.Property
import javafx.beans.value.ChangeListener
import javafx.scene.Parent

/**
 * Parent 的公共配置，不提供对子节点集合的直接修改。
 *
 * 容器与控件均可继承这些能力，例如：
 * ```kotlin
 * vbox {
 *     stylesheet {
 *         styleClass("content") { opacity(0.8) }
 *     }
 *     addPane { styleClass("content") }
 * }
 * ```
 */
@FXMarker
abstract class ParentBaseBuilder<T : Parent> : NodeBuilder<T>() {

    fun stylesheets(vararg paths: String) = settings { stylesheets.addAll(paths) }

    fun stylesheet(builder: StylesheetBuilder) = settings {
        stylesheets.add(builder.toDataUri())
    }

    fun stylesheet(config: StylesheetBuilder.() -> Unit) = settings {
        stylesheets.add(StylesheetBuilder().apply(config).toDataUri())
    }

    /** 将只读布局状态绑定到调用方的属性。 */
    fun byBindNeedsLayout(property: Property<in Boolean>) = settings {
        property.bind(needsLayoutProperty())
    }

    fun addNeedsLayoutListener(listener: ChangeListener<Boolean>) = settings {
        needsLayoutProperty().addListener(listener)
    }

    fun removeNeedsLayoutListener(listener: ChangeListener<Boolean>) = settings {
        needsLayoutProperty().removeListener(listener)
    }
}
