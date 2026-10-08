package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.LabelSeparator
import club.xiaojiawei.kt.annotations.FXMarker
import club.xiaojiawei.kt.i18n.LocalizedText
import javafx.beans.property.StringProperty
import javafx.beans.value.ObservableValue
import javafx.geometry.HPos
import javafx.scene.Node
import javafx.scene.control.ContentDisplay

@FXMarker
class LabelSeparatorBuilder : ControlBuilder<LabelSeparator>() {

    override fun buildInstance(): LabelSeparator = LabelSeparator()

    fun text(text: String) = settings { this.text = text }

    fun text(text: LocalizedText) = settings { textProperty().bind(text.binding()) }

    operator fun String.unaryPlus() = text(this)

    operator fun LocalizedText.unaryPlus() = text(this)

    fun bindText(text: ObservableValue<out String>) = settings { textProperty().bind(text) }

    fun bindBidirectionalText(text: StringProperty) = settings { textProperty().bindBidirectional(text) }

    fun graphic(node: Node?) = settings { graphic = node }

    fun graphic(builder: () -> Node?) = settings { graphic = builder() }

    fun contentDisplay(contentDisplay: ContentDisplay) = settings { this.contentDisplay = contentDisplay }

    fun graphicTextGap(gap: Double) = settings { graphicTextGap = gap }

    fun textAlignment(alignment: HPos) = settings { textAlignment = alignment }

    fun alignLeft() = textAlignment(HPos.LEFT)

    fun alignCenter() = textAlignment(HPos.CENTER)

    fun alignRight() = textAlignment(HPos.RIGHT)
}

/** 创建带文本和图形的水平分隔线，控件自动加载默认样式。 */
inline fun labelSeparator(config: LabelSeparatorBuilder.() -> Unit = {}): LabelSeparator =
    labelSeparatorBuilder(config).build()

inline fun labelSeparator(text: String, config: LabelSeparatorBuilder.() -> Unit = {}): LabelSeparator =
    labelSeparatorBuilder {
        text(text)
        config()
    }.build()

inline fun labelSeparator(text: LocalizedText, config: LabelSeparatorBuilder.() -> Unit = {}): LabelSeparator =
    labelSeparatorBuilder {
        text(text)
        config()
    }.build()

inline fun labelSeparatorBuilder(config: LabelSeparatorBuilder.() -> Unit): LabelSeparatorBuilder =
    LabelSeparatorBuilder().apply(config)

fun labelSeparatorConfig(config: LabelSeparatorBuilder.() -> Unit): LabelSeparatorBuilder.() -> Unit = config

inline fun LabelSeparator.config(config: LabelSeparatorBuilder.() -> Unit): LabelSeparator =
    apply {
        LabelSeparatorBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }
