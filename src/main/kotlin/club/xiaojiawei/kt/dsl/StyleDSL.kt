package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.beans.value.ChangeListener
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.UUID

/**
 * @author 肖嘉威
 * @date 2025/10/21 12:19
 */

enum class FontWeight {
    NORMAL, BOLD, BOLDER, LIGHTER, W100, W200, W300, W400, W500, W600, W700, W800, W900
}

enum class FontStyle {
    NORMAL, ITALIC, OBLIQUE
}

enum class Cursor {
    DEFAULT, HAND, WAIT, TEXT, CROSSHAIR, MOVE,
    E_RESIZE, W_RESIZE, N_RESIZE, S_RESIZE,
    NE_RESIZE, NW_RESIZE, SE_RESIZE, SW_RESIZE,
    H_RESIZE, V_RESIZE, NONE
}


enum class StyleSize {
    TINY, SMALL, BIG, DEFAULT
}

enum class StyleColor {
    MAIN, NORMAL, SUCCESS, WARN, ERROR, DEFAULT
}

@FXMarker
class StyleBuilder {
    private val styles = linkedMapOf<String, String>()
    private val pseudoRules = mutableListOf<PseudoRule>()

    private data class PseudoRule(
        val name: String,
        val style: StyleBuilder,
    )

    /**
     * 添加不带冒号的单个伪类；可在 [block] 中继续嵌套形成伪类组合。
     */
    fun pseudoClass(name: String, block: StyleBuilder.() -> Unit) {
        require(PSEUDO_CLASS_NAME.matches(name)) {
            "Pseudo-class name must be a single CSS identifier without ':' or whitespace: '$name'"
        }
        pseudoRules += PseudoRule(name, StyleBuilder().apply(block))
    }

    fun hover(block: StyleBuilder.() -> Unit) = pseudoClass("hover", block)

    fun pressed(block: StyleBuilder.() -> Unit) = pseudoClass("pressed", block)

    fun focused(block: StyleBuilder.() -> Unit) = pseudoClass("focused", block)

    fun disabled(block: StyleBuilder.() -> Unit) = pseudoClass("disabled", block)

    fun selected(block: StyleBuilder.() -> Unit) = pseudoClass("selected", block)

    fun armed(block: StyleBuilder.() -> Unit) = pseudoClass("armed", block)

    // 背景相关
    fun background(background: String) {
        styles["-fx-background"] = background
    }

    fun backgroundColor(color: String) {
        styles["-fx-background-color"] = color
    }

    fun backgroundColor(r: Int, g: Int, b: Int, a: Double = 1.0) {
        styles["-fx-background-color"] = "rgba(${r}, ${g}, ${b}, ${a})"
    }

    fun backgroundImage(url: String) {
        styles["-fx-background-image"] = "url('$url')"
    }

    fun backgroundRadius(value: Double) {
        styles["-fx-background-radius"] = value.toString()
    }

    fun backgroundInsets(value: Double) {
        styles["-fx-background-insets"] = value.toString()
    }

    fun backgroundSize(value: String) {
        styles["-fx-background-size"] = value
    }

    fun backgroundPosition(value: String) {
        styles["-fx-background-position"] = value
    }

    fun backgroundRepeat(value: String) {
        styles["-fx-background-repeat"] = value
    }

    // 边框相关
    fun borderColor(color: String) {
        styles["-fx-border-color"] = color
    }

    fun borderWidth(value: Double) {
        styles["-fx-border-width"] = value.toString()
    }

    fun borderRadius(value: Double) {
        styles["-fx-border-radius"] = value.toString()
    }

    fun borderStyle(value: String) {
        styles["-fx-border-style"] = value
    }

    fun borderInsets(value: Double) {
        styles["-fx-border-insets"] = value.toString()
    }

    // 字体相关
    fun fontSize(value: Double) {
        styles["-fx-font-size"] = "${value}px"
    }

    fun fontFamily(family: String) {
        styles["-fx-font-family"] = "'$family'"
    }

    fun fontWeight(weight: FontWeight) {
        styles["-fx-font-weight"] = weight.name.lowercase()
    }

    fun fontStyle(style: FontStyle) {
        styles["-fx-font-style"] = style.name.lowercase()
    }

    // 文本相关
    fun textFill(color: String) {
        styles["-fx-text-fill"] = color
    }

    fun textAlignment(alignment: String) {
        styles["-fx-text-alignment"] = alignment
    }

    fun underline(enable: Boolean) {
        styles["-fx-underline"] = enable.toString()
    }

    fun strikethrough(enable: Boolean) {
        styles["-fx-strikethrough"] = enable.toString()
    }

    // 内边距
    fun padding(value: Double) {
        styles["-fx-padding"] = value.toString()
    }

    fun padding(top: Double, right: Double, bottom: Double, left: Double) {
        styles["-fx-padding"] = "$top $right $bottom $left"
    }

    // 效果相关
    fun opacity(value: Double) {
        styles["-fx-opacity"] = value.toString()
    }

    fun cursor(cursor: Cursor) {
        styles["-fx-cursor"] = cursor.name.lowercase().replace('_', '-')
    }

    fun effect(effect: String) {
        styles["-fx-effect"] = effect
    }

    fun dropShadow(radius: Double, offsetX: Double, offsetY: Double, color: String) {
        styles["-fx-effect"] = "dropshadow(gaussian, $color, $radius, 0, $offsetX, $offsetY)"
    }

    fun innerShadow(radius: Double, offsetX: Double, offsetY: Double, color: String) {
        styles["-fx-effect"] = "innershadow(gaussian, $color, $radius, 0, $offsetX, $offsetY)"
    }

    // 对齐相关
    fun alignment(value: String) {
        styles["-fx-alignment"] = value
    }

    fun alignCenter() = alignment("CENTER")
    fun alignTop() = alignment("TOP_CENTER")
    fun alignBottom() = alignment("BOTTOM_CENTER")
    fun alignLeft() = alignment("CENTER_LEFT")
    fun alignRight() = alignment("CENTER_RIGHT")
    fun alignTopLeft() = alignment("TOP_LEFT")
    fun alignTopRight() = alignment("TOP_RIGHT")
    fun alignBottomLeft() = alignment("BOTTOM_LEFT")
    fun alignBottomRight() = alignment("BOTTOM_RIGHT")
    fun alignBaseLeft() = alignment("BASELINE_LEFT")
    fun alignBaseCenter() = alignment("BASELINE_CENTER")
    fun alignBaseRight() = alignment("BASELINE_RIGHT")

    // 形状相关
    fun shape(value: String) {
        styles["-fx-shape"] = value
    }

    fun region(value: String) {
        styles["-fx-region"] = value
    }

    // 鼠标交互
    @Deprecated(
        message = "Use hover { custom(property, value) }",
        replaceWith = ReplaceWith("hover { custom(property, value) }"),
    )
    fun hoverEffect(property: String, value: String) {
        hover { custom(property, value) }
    }

    // 控件特定
    fun promptTextFill(color: String) {
        styles["-fx-prompt-text-fill"] = color
    }

    fun highlightFill(color: String) {
        styles["-fx-highlight-fill"] = color
    }

    fun highlightTextFill(color: String) {
        styles["-fx-highlight-text-fill"] = color
    }

    fun accentColor(color: String) {
        styles["-fx-accent"] = color
    }

    fun focusColor(color: String) {
        styles["-fx-focus-color"] = color
    }

    fun faintFocusColor(color: String) {
        styles["-fx-faint-focus-color"] = color
    }

    // 间距相关
    fun spacing(value: Double) {
        styles["-fx-spacing"] = value.toString()
    }

    fun hgap(value: Double) {
        styles["-fx-hgap"] = value.toString()
    }

    fun vgap(value: Double) {
        styles["-fx-vgap"] = value.toString()
    }

    // 尺寸相关
    fun minWidth(value: Double) {
        styles["-fx-min-width"] = value.toString()
    }

    fun minHeight(value: Double) {
        styles["-fx-min-height"] = value.toString()
    }

    fun maxWidth(value: Double) {
        styles["-fx-max-width"] = value.toString()
    }

    fun maxHeight(value: Double) {
        styles["-fx-max-height"] = value.toString()
    }

    fun prefWidth(value: Double) {
        styles["-fx-pref-width"] = value.toString()
    }

    fun prefHeight(value: Double) {
        styles["-fx-pref-height"] = value.toString()
    }

    // 进度条/滑块相关
    fun barFill(color: String) {
        styles["-fx-bar-fill"] = color
    }

    fun trackColor(color: String) {
        styles["-fx-track-color"] = color
    }

    // 表格相关
    fun cellSize(value: Double) {
        styles["-fx-cell-size"] = value.toString()
    }

    fun fixedCellSize(value: Double) {
        styles["-fx-fixed-cell-size"] = value.toString()
    }

    // 滚动条相关
    fun scrollbarWidth(value: Double) {
        styles["-fx-scrollbar-width"] = value.toString()
    }

    // 旋转/变换
    fun rotate(degrees: Double) {
        styles["-fx-rotate"] = degrees.toString()
    }

    fun scaleX(value: Double) {
        styles["-fx-scale-x"] = value.toString()
    }

    fun scaleY(value: Double) {
        styles["-fx-scale-y"] = value.toString()
    }

    fun translateX(value: Double) {
        styles["-fx-translate-x"] = value.toString()
    }

    fun translateY(value: Double) {
        styles["-fx-translate-y"] = value.toString()
    }

    // 混合模式
    fun blendMode(mode: String) {
        styles["-fx-blend-mode"] = mode
    }

    // 自定义样式
    fun custom(property: String, value: String) {
        styles[property] = value
    }

    /**
     * 构建纯 inline 样式；含伪类时应改用 [Node.styled] 或 [StylesheetBuilder]。
     */
    fun build(): String {
        check(pseudoRules.isEmpty()) {
            "StyleBuilder.build() only supports inline styles; use Node.styled or StylesheetBuilder for pseudo-classes"
        }
        return inlineStyle()
    }

    internal fun snapshot(): StyleSnapshot = StyleSnapshot(
        declarations = styles.entries.map { it.key to it.value },
        pseudoRules = pseudoRules.map { PseudoRuleSnapshot(it.name, it.style.snapshot()) },
    )

    private fun inlineStyle(): String = styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }

    private companion object {
        val PSEUDO_CLASS_NAME = Regex("""(?:-?[_a-zA-Z\u0080-\uFFFF]|--)[-_a-zA-Z0-9\u0080-\uFFFF]*""")
    }
}

internal data class PseudoRuleSnapshot(
    val name: String,
    val style: StyleSnapshot,
)

internal data class StyleSnapshot(
    val declarations: List<Pair<String, String>>,
    val pseudoRules: List<PseudoRuleSnapshot>,
) {
    val hasPseudoClasses: Boolean
        get() = pseudoRules.isNotEmpty()

    fun inlineStyle(): String = declarations.joinToString("; ") { (property, value) -> "$property: $value" }

    fun stylesheet(selector: String): String {
        val selectors = selector.split(',').map(String::trim)
        require(selectors.all(String::isNotEmpty)) { "Selector groups must not contain empty selectors: '$selector'" }
        return buildString { appendRules(selectors, emptyList(), this@StyleSnapshot) }
    }

    private fun StringBuilder.appendRules(
        selectors: List<String>,
        pseudoClasses: List<String>,
        style: StyleSnapshot,
    ) {
        if (style.declarations.isNotEmpty()) {
            append(selectors.joinToString(", ") { selector ->
                selector + pseudoClasses.joinToString(separator = "") { ":$it" }
            })
            append(" {\n")
            style.declarations.forEach { (property, value) ->
                append("    ").append(property).append(": ").append(value).append(";\n")
            }
            append("}\n\n")
        }
        style.pseudoRules.forEach { pseudoRule ->
            appendRules(selectors, pseudoClasses + pseudoRule.name, pseudoRule.style)
        }
    }
}

@FXMarker
class StylesheetBuilder : DslBuilder<String>() {

    val ruleMap = mutableMapOf<String, StyleBuilder>()

    /**
     * 通用选择器
     * 示例: select(".my-button") { ... }
     */
    inline fun select(selector: String, block: StyleBuilder.() -> Unit) {
        val builder = StyleBuilder()
        builder.block()
        ruleMap[selector] = builder
    }

    /**
     * 类选择器 (自动补 .)
     */
    inline fun styleClass(className: String, block: StyleBuilder.() -> Unit) =
        select(".$className", block)

    /**
     * ID 选择器 (自动补 #)
     */
    inline fun id(idName: String, block: StyleBuilder.() -> Unit) =
        select("#$idName", block)

    inline fun id(node: Node, block: StyleBuilder.() -> Unit) =
        select("#${node.id}", block)

    /**
     * 类型选择器 (如 Label, Button)
     */
    inline fun type(typeName: String, block: StyleBuilder.() -> Unit) =
        select(typeName, block)

    inline fun type(clazz: Class<*>, block: StyleBuilder.() -> Unit) =
        select(clazz.simpleName, block)

    inline fun type(node: Node, block: StyleBuilder.() -> Unit) =
        select(node::class.java.simpleName, block)

    override fun buildInstance(): String = ""

    /**
     * 构建最终的 CSS 字符串
     */
    override fun build(): String {
        return buildString {
            ruleMap.forEach { (selector, style) ->
                append(style.snapshot().stylesheet(selector))
            }
        }
    }

    fun toDataUri(): String {
        return build().toDataUri()
    }
}

// Style 衍生
inline fun stylesheetBuilder(config: StylesheetBuilder.() -> Unit): StylesheetBuilder =
    StylesheetBuilder().apply(config)

fun stylesheetConfig(config: StylesheetBuilder.() -> Unit): StylesheetBuilder.() -> Unit =
    config

inline fun Scene.configStylesheet(config: StylesheetBuilder.() -> Unit): Scene =
    apply {
        stylesheets.add(StylesheetBuilder().apply(config).toDataUri())
    }


inline fun styleBuilder(config: StyleBuilder.() -> Unit): StyleBuilder =
    StyleBuilder().apply(config)

fun styleConfig(config: StyleBuilder.() -> Unit): StyleBuilder.() -> Unit =
    config

fun Node.styled(block: StyleBuilder.() -> Unit) {
    styled(StyleBuilder().apply(block))
}

/**
 * 替换本函数上次应用的样式。含伪类时使用 Parent author stylesheet，
 * 以保留 JavaFX 原生级联和 !important 行为。
 */
fun Node.styled(styleBuilder: StyleBuilder) {
    val snapshot = styleBuilder.snapshot()
    val generatedStyle = if (snapshot.hasPseudoClasses) {
        val className = "javafx-kt-style-${UUID.randomUUID().toString().replace("-", "")}"
        GeneratedNodeStyle(this, className, snapshot.stylesheet(".$className").toDataUri())
    } else {
        null
    }

    check(!styleProperty().isBound) { "Cannot apply styled() while Node.styleProperty is bound" }
    (properties[GENERATED_STYLE_KEY] as? GeneratedNodeStyle)?.dispose()
    style = generatedStyle?.let { "" } ?: snapshot.inlineStyle()
    generatedStyle?.install()
}

private class GeneratedNodeStyle(
    private val node: Node,
    private val className: String,
    private val stylesheet: String,
) {
    private var stylesheetOwner: Parent? = null
    private val parentListener = ChangeListener<Parent?> { _, _, newParent ->
        attachTo(newParent)
    }

    fun install() {
        node.styleClass += className
        if (node is Parent) {
            attachTo(node)
        } else {
            node.parentProperty().addListener(parentListener)
            attachTo(node.parent)
        }
        node.properties[GENERATED_STYLE_KEY] = this
    }

    fun dispose() {
        if (node !is Parent) {
            node.parentProperty().removeListener(parentListener)
        }
        stylesheetOwner?.stylesheets?.remove(stylesheet)
        stylesheetOwner = null
        node.styleClass.remove(className)
        node.properties.remove(GENERATED_STYLE_KEY)
    }

    private fun attachTo(parent: Parent?) {
        if (stylesheetOwner === parent) return
        stylesheetOwner?.stylesheets?.remove(stylesheet)
        stylesheetOwner = parent
        parent?.stylesheets?.add(stylesheet)
    }
}

private fun String.toDataUri(): String {
    val payload = Base64.getEncoder().encodeToString(toByteArray(StandardCharsets.UTF_8))
    return "data:text/css;base64,$payload"
}

private val GENERATED_STYLE_KEY = Any()