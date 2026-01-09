package club.xiaojiawei.kt.dsl

import javafx.scene.Node

/**
 * @author 肖嘉威
 * @date 2025/10/21 12:19
 */

@FXMarker
class StyleBuilder {
    private val styles = mutableMapOf<String, String>()

    // 背景相关
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
    fun hoverEffect(property: String, value: String) {
        // 注: hover 需要通过伪类实现,这里提供基础支持
        styles["$property:hover"] = value
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

    fun build(): String = styles.entries.joinToString("; ") { "${it.key}: ${it.value}" }
}

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

fun Node.styled(block: StyleBuilder.() -> Unit) {
    style = StyleBuilder().apply(block).build()
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
        val sb = StringBuilder()
        ruleMap.forEach { (selector, style) ->
            sb.append(selector).append(" {\n")
            // 这里的 style.build() 会返回 "key: value; key: value"
            // 我们稍微格式化一下美化输出
            style.build().split("; ").forEach {
                if (it.isNotBlank()) sb.append("    ").append(it).append(";\n")
            }
            sb.append("}\n\n")
        }
        return sb.toString()
    }

    fun toDataUri(): String {
        val css = build()
        val encoder = java.util.Base64.getEncoder()
        val base64 = encoder.encodeToString(css.toByteArray())
        return "data:text/css;base64,$base64"
    }
}