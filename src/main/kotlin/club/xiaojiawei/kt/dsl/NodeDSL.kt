package club.xiaojiawei.kt.dsl

/**
 * @author 肖嘉威
 * @date 2025/8/14 16:19
 */

import club.xiaojiawei.controls.FilterComboBox
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.enums.BaseTransitionEnum
import club.xiaojiawei.kt.annotations.FXMarker
import javafx.beans.binding.Bindings
import javafx.beans.property.*
import javafx.beans.value.ChangeListener
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.NodeOrientation
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.*
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.scene.text.Text
import javafx.util.Callback
import javafx.util.Duration
import javafx.util.StringConverter

// 基础 Node 构建器
@FXMarker
abstract class NodeBuilder<T : Node> : DslBuilder<T>() {
    // --- 布局约束 & 元数据 ---
    fun userData(d: Any) = settings { userData = d }
    fun hgrow(p: Priority) = settings { HBox.setHgrow(this, p) }
    fun hgrowAlways() = hgrow(Priority.ALWAYS)
    fun hgrowSometimes() = hgrow(Priority.SOMETIMES)
    fun hgrowNever() = hgrow(Priority.NEVER)
    fun vgrow(p: Priority) = settings { VBox.setVgrow(this, p) }
    fun vgrowAlways() = vgrow(Priority.ALWAYS)
    fun vgrowSometimes() = vgrow(Priority.SOMETIMES)
    fun vgrowNever() = vgrow(Priority.NEVER)
    fun pickOnBounds(b: Boolean = true) = settings { isPickOnBounds = b }
    fun nodeOrientation(nodeOrientation: NodeOrientation) = settings { this.nodeOrientation = nodeOrientation }

    // --- 样式系统 (StyleColor & StyleSize) ---
    fun styleMain(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.MAIN, s)
    fun styleNormal(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.NORMAL, s)
    fun styleSuccess(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.SUCCESS, s)
    fun styleWarn(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.WARN, s)
    fun styleError(s: StyleSize = StyleSize.DEFAULT) = style(StyleColor.ERROR, s)
    fun styleRadius() = settings { styleClass.add("radius-ui") }
    fun styleRadiusBig() = settings { styleClass.add("radius-ui-big") }
    fun styleBg() = settings { styleClass.add("bg-ui") }
    fun styleBgHover() = settings { styleClass.add("bg-hover-ui") }
    fun styled(block: StyleBuilder.() -> Unit) = settings { this.styled(block) }
    fun styled(styleBuilder: StyleBuilder) = settings { style = styleBuilder.build() }

    // --- 基础 Node 属性 ---
    fun id(v: String) = settings { id = v }
    fun styleClass(vararg c: String) = settings { styleClass.addAll(c) }
    fun style(v: String) = settings { style = v }
    fun visible(v: Boolean = true) = settings { isVisible = v }
    fun managed(v: Boolean = true) = settings { isManaged = v }
    fun disable(v: Boolean = true) = settings { isDisable = v }
    fun opacity(v: Double) = settings { opacity = v }
    fun rotate(a: Double) = settings { rotate = a }
    fun scale(x: Double, y: Double = x) = settings { scaleX = x; scaleY = y }
    fun translate(x: Double, y: Double) = settings { translateX = x; translateY = y }
    fun mouseTransparent(mouseTransparent: Boolean) = settings { isMouseTransparent = mouseTransparent }

    // --- 光标控制 ---
    fun cursor(c: Cursor = Cursor.DEFAULT) = settings { cursor = c }
    fun cursorHand() = cursor(Cursor.HAND)
    fun cursorCrosshair() = cursor(Cursor.CROSSHAIR)
    fun cursorWait() = cursor(Cursor.WAIT)
    fun cursorMove() = cursor(Cursor.MOVE)

    // --- 鼠标 & 拖拽事件 ---
    fun onMouseClicked(h: EventHandler<MouseEvent>? = null) = settings { onMouseClicked = h }
    fun onMouseEntered(h: EventHandler<MouseEvent>? = null) = settings { onMouseEntered = h }
    fun onMouseExited(h: EventHandler<MouseEvent>? = null) = settings { onMouseExited = h }
    fun onMouseReleased(h: EventHandler<MouseEvent>? = null) = settings { onMouseReleased = h }
    fun onMousePressed(h: EventHandler<MouseEvent>? = null) = settings { onMousePressed = h }
    fun onMouseDragged(h: EventHandler<MouseEvent>? = null) = settings { onMouseDragged = h }
    fun onDragDetected(h: EventHandler<MouseEvent>? = null) = settings { onDragDetected = h }
    fun onDragDone(h: EventHandler<DragEvent>? = null) = settings { onDragDone = h }
    fun onDragOver(h: EventHandler<DragEvent>? = null) = settings { onDragOver = h }
    fun onDragExited(h: EventHandler<DragEvent>? = null) = settings { onDragExited = h }
    fun onDragDropped(h: EventHandler<DragEvent>? = null) = settings { onDragDropped = h }
    fun onScroll(h: EventHandler<ScrollEvent>? = null) = settings { onScroll = h }

    fun onMouseClicked(h: (MouseEvent) -> Unit = {}) = settings { onMouseClicked = EventHandler(h) }
    fun onMouseEntered(h: (MouseEvent) -> Unit = {}) = settings { onMouseEntered = EventHandler(h) }
    fun onMouseExited(h: (MouseEvent) -> Unit = {}) = settings { onMouseExited = EventHandler(h) }
    fun onMouseReleased(h: (MouseEvent) -> Unit = {}) = settings { onMouseReleased = EventHandler(h) }
    fun onMousePressed(h: (MouseEvent) -> Unit = {}) = settings { onMousePressed = EventHandler(h) }
    fun onMouseDragged(h: (MouseEvent) -> Unit = {}) = settings { onMouseDragged = EventHandler(h) }
    fun onDragDetected(h: (MouseEvent) -> Unit = {}) = settings { onDragDetected = EventHandler(h) }
    fun onDragDone(h: (DragEvent) -> Unit = {}) = settings { onDragDone = EventHandler(h) }
    fun onDragOver(h: (DragEvent) -> Unit = {}) = settings { onDragOver = EventHandler(h) }
    fun onDragExited(h: (DragEvent) -> Unit = {}) = settings { onDragExited = EventHandler(h) }
    fun onDragDropped(h: (DragEvent) -> Unit = {}) = settings { onDragDropped = EventHandler(h) }
    fun onScroll(h: (ScrollEvent) -> Unit = {}) = settings { onScroll = EventHandler(h) }

}

@FXMarker
abstract class RegionBaseBuilder<T : Region> : NodeBuilder<T>() {
    // --- 综合尺寸设定 ---
    fun size(w: Double = -1.0, h: Double = -1.0) = settings { prefWidth = w; prefHeight = h }
    fun minSize(w: Double = -1.0, h: Double = -1.0) = settings { minWidth = w; minHeight = h }
    fun maxSize(w: Double = -1.0, h: Double = -1.0) = settings { maxWidth = w; maxHeight = h }
    fun fixedSize(w: Double = -1.0, h: Double = -1.0) =
        settings { minWidth = w; minHeight = h; prefWidth = w; prefHeight = h; maxWidth = w; maxHeight = h }

    // --- 宽高独立设定 ---
    fun prefWidth(v: Double) = settings { prefWidth = v }
    fun minWidth(v: Double) = settings { minWidth = v }
    fun maxWidth(v: Double) = settings { maxWidth = v }
    fun fixedWidth(v: Double) = settings { prefWidth = v; maxWidth = v; minWidth = v }
    fun prefHeight(v: Double) = settings { prefHeight = v }
    fun minHeight(v: Double) = settings { minHeight = v }
    fun maxHeight(v: Double) = settings { maxHeight = v }
    fun fixedHeigh(v: Double) = settings { prefHeight = v; maxHeight = v; minHeight = v }

    // --- 基础宽高 (只读属性) 绑定与监听 ---
    fun byBindWidth(p: DoubleProperty) = settings { p.bind(widthProperty()) }
    fun byBindHeight(p: DoubleProperty) = settings { p.bind(heightProperty()) }
    fun addWidthListener(l: ChangeListener<Number>) = settings { widthProperty().addListener(l) }
    fun addHeightListener(l: ChangeListener<Number>) = settings { heightProperty().addListener(l) }
    fun removeWidthListener(l: ChangeListener<Number>) = settings { widthProperty().removeListener(l) }
    fun removeHeightListener(l: ChangeListener<Number>) = settings { heightProperty().removeListener(l) }

    // --- Pref 宽高绑定与监听 ---
    fun bindPrefWidth(p: ObservableValue<out Number>) = settings { prefWidthProperty().bind(p) }
    fun bindPrefHeight(p: ObservableValue<out Number>) = settings { prefHeightProperty().bind(p) }
    fun byBindPrefWidth(p: DoubleProperty) = settings { p.bind(prefWidthProperty()) }
    fun byBindPrefHeight(p: DoubleProperty) = settings { p.bind(prefHeightProperty()) }
    fun addPrefWidthListener(l: ChangeListener<Number>) = settings { prefWidthProperty().addListener(l) }
    fun addPrefHeightListener(l: ChangeListener<Number>) = settings { prefHeightProperty().addListener(l) }
    fun removePrefWidthListener(l: ChangeListener<Number>) = settings { prefWidthProperty().removeListener(l) }
    fun removePrefHeightListener(l: ChangeListener<Number>) = settings { prefHeightProperty().removeListener(l) }

    // --- Min 宽高绑定与监听 ---
    fun bindMinWidth(p: ObservableValue<out Number>) = settings { minWidthProperty().bind(p) }
    fun bindMinHeight(p: ObservableValue<out Number>) = settings { minHeightProperty().bind(p) }
    fun byBindMinWidth(p: DoubleProperty) = settings { p.bind(minWidthProperty()) }
    fun byBindMinHeight(p: DoubleProperty) = settings { p.bind(minHeightProperty()) }
    fun addMinWidthListener(l: ChangeListener<Number>) = settings { minWidthProperty().addListener(l) }
    fun addMinHeightListener(l: ChangeListener<Number>) = settings { minHeightProperty().addListener(l) }
    fun removeMinWidthListener(l: ChangeListener<Number>) = settings { minWidthProperty().removeListener(l) }
    fun removeMinHeightListener(l: ChangeListener<Number>) = settings { minHeightProperty().removeListener(l) }

    // --- Max 宽高绑定与监听 ---
    fun bindMaxWidth(p: ObservableValue<out Number>) = settings { maxWidthProperty().bind(p) }
    fun bindMaxHeight(p: ObservableValue<out Number>) = settings { maxHeightProperty().bind(p) }
    fun byBindMaxWidth(p: DoubleProperty) = settings { p.bind(maxWidthProperty()) }
    fun byBindMaxHeight(p: DoubleProperty) = settings { p.bind(maxHeightProperty()) }
    fun addMaxWidthListener(l: ChangeListener<Number>) = settings { maxWidthProperty().addListener(l) }
    fun addMaxHeightListener(l: ChangeListener<Number>) = settings { maxHeightProperty().addListener(l) }
    fun removeMaxWidthListener(l: ChangeListener<Number>) = settings { maxWidthProperty().removeListener(l) }
    fun removeMaxHeightListener(l: ChangeListener<Number>) = settings { maxHeightProperty().removeListener(l) }
}

@FXMarker
class RegionBuilder : RegionBaseBuilder<Region>() {

    override fun buildInstance(): Region = Region()

}

@FXMarker
abstract class LabeledBuilder<T : Labeled> : RegionBaseBuilder<T>() {

    fun contentDisplay(contentDisplay: ContentDisplay) = settings { this.contentDisplay = contentDisplay }

    fun graphic(node: Node) = settings { graphic = node }

    fun graphic(builder: () -> Node) = settings { graphic = builder() }

    fun text(text: String) = settings { this.text = text }

    operator fun String.unaryPlus() = text(this)

    /**
     * 观察单个 Property，值变化时自动更新 text
     */
    fun <T> observe(state: ObservableValue<T>, block: (T) -> String) = settings {
        val binding = Bindings.createStringBinding({ block(state.value) }, state)
        textProperty().bind(binding)
    }

    /**
     * 观察多个 Property，任一值变化时自动更新 text
     */
    fun observes(vararg states: ObservableValue<*>, block: () -> String) = settings {
        val binding = Bindings.createStringBinding({ block() }, *states)
        textProperty().bind(binding)
    }

    // 数据绑定
    fun byBindText(property: StringProperty) = settings { property.bind(textProperty()) }

    fun bindText(property: StringProperty) = settings { textProperty().bind(property) }

    fun bindBidirectionalText(property: StringProperty) = settings { textProperty().bindBidirectional(property) }

    fun byBindBidirectionalText(property: StringProperty) = settings { property.bindBidirectional(textProperty()) }

    fun font(font: Font) = settings { this.font = font }

    fun font(family: String, size: Double) {
        settings { font = Font.font(family, size) }
    }

    fun font(family: String, weight: FontWeight, size: Double) {
        settings { font = Font.font(family, weight, size) }
    }

    fun fontSize(size: Double) {
        settings { font = Font.font(size) }
    }

    fun textFill(color: String) = settings {
        textFill = Color.web(color)
    }

    fun textFill(color: Paint) = settings {
        textFill = color
    }

    fun alignment(alignment: Pos) = settings { this.alignment = alignment }
    fun alignCenter() = alignment(Pos.CENTER)
    fun alignTop() = alignment(Pos.TOP_CENTER)
    fun alignBottom() = alignment(Pos.BOTTOM_CENTER)
    fun alignLeft() = alignment(Pos.CENTER_LEFT)
    fun alignRight() = alignment(Pos.CENTER_RIGHT)
    fun alignTopLeft() = alignment(Pos.TOP_LEFT)
    fun alignTopRight() = alignment(Pos.TOP_RIGHT)
    fun alignBottomLeft() = alignment(Pos.BOTTOM_LEFT)
    fun alignBottomRight() = alignment(Pos.BOTTOM_RIGHT)
    fun alignBaseLeft() = alignment(Pos.BASELINE_LEFT)
    fun alignBaseCenter() = alignment(Pos.BASELINE_CENTER)
    fun alignBaseRight() = alignment(Pos.BASELINE_RIGHT)

}


// Label 构建器
@FXMarker
class LabelBuilder : LabeledBuilder<Label>() {

    override fun buildInstance(): Label = Label()

    fun wrapText(wrap: Boolean = true) {
        settings { isWrapText = wrap }
    }


    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("label-ui")
            when (styleColor) {
                StyleColor.MAIN -> {}
                StyleColor.NORMAL -> styleClass.add("label-ui-normal")
                StyleColor.SUCCESS -> styleClass.add("label-ui-success")
                StyleColor.WARN -> styleClass.add("label-ui-warn")
                StyleColor.ERROR -> styleClass.add("label-ui-error")
                StyleColor.DEFAULT -> {}
            }
            when (styleSize) {
                StyleSize.TINY -> styleClass.add("label-ui-tiny")
                StyleSize.SMALL -> styleClass.add("label-ui-small")
                StyleSize.BIG -> styleClass.add("label-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}

// Circle 构建器
@FXMarker
class CircleBuilder : NodeBuilder<Circle>() {

    override fun buildInstance(): Circle = Circle()

    fun radius(radius: Double) {
        settings {
            this.radius = radius
        }
    }

    fun fill(color: Color) {
        settings {
            this.fill = color
        }
    }

}


// Text 构建器
@FXMarker
class TextBuilder : NodeBuilder<Text>() {

    override fun buildInstance(): Text = Text()

    fun text(text: String) = settings { this.text = text }

    operator fun String.unaryPlus() = text(this)

    fun <T> observe(state: ObservableValue<T>, block: (T) -> String) = settings {
        val binding = Bindings.createStringBinding({ block(state.value) }, state)
        textProperty().bind(binding)
    }

    fun observes(vararg states: ObservableValue<*>, block: () -> String) = settings {
        val binding = Bindings.createStringBinding({ block() }, *states)
        textProperty().bind(binding)
    }

    // 数据绑定
    fun byBindText(property: StringProperty) = settings { property.bind(textProperty()) }

    fun bindText(property: StringProperty) = settings { textProperty().bind(property) }

    fun bindBidirectionalText(property: StringProperty) = settings { textProperty().bindBidirectional(property) }

    fun byBindBidirectionalText(property: StringProperty) = settings { property.bindBidirectional(textProperty()) }

}

@FXMarker
class PolygonBuilder : NodeBuilder<Polygon>() {

    override fun buildInstance(): Polygon = Polygon()

    fun point(x: Double, y: Double) {
        settings {
            points.addAll(x, y)
        }
    }

    fun points(vararg points: Double) {
        settings {
            this.points.addAll(points.toList())
        }
    }

    fun points(points: List<Double>) {
        settings {
            this.points.addAll(points)
        }
    }

    fun clearPoint() {
        settings { points.clear() }
    }

    fun fill(paint: Paint) = settings { fill = paint }

    fun stroke(paint: Paint) = settings { stroke = paint }

    fun strokeWidth(width: Double) = settings { strokeWidth = width }

    fun strokeType(type: StrokeType) = settings { strokeType = type }

    fun strokeLineJoin(join: StrokeLineJoin) = settings { strokeLineJoin = join }

    fun strokeLineCap(cap: StrokeLineCap) = settings { strokeLineCap = cap }

    fun strokeDashArray(vararg dashes: Double) {
        settings { strokeDashArray.setAll(dashes.toList()) }
    }

    fun strokeDashOffset(offset: Double) = settings { strokeDashOffset = offset }

    fun strokeMiterLimit(limit: Double) = settings { strokeMiterLimit = limit }

    fun smooth(smooth: Boolean = true) = settings { isSmooth = smooth }

}

// Button 构建器
@FXMarker
class ButtonBuilder : LabeledBuilder<Button>() {

    override fun buildInstance(): Button = Button()

    fun onAction(handler: EventHandler<ActionEvent>?) {
        settings { onAction = handler }
    }

    fun onAction(handler: (ActionEvent) -> Unit) {
        settings { onAction = EventHandler(handler) }
    }

    fun defaultButton(default: Boolean = true) {
        settings { isDefaultButton = default }
    }

    fun cancelButton(cancel: Boolean = true) {
        settings { isCancelButton = cancel }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("btn-ui")
            when (styleColor) {
                StyleColor.MAIN -> {}
                StyleColor.NORMAL -> styleClass.add("btn-ui-normal")
                StyleColor.SUCCESS -> styleClass.add("btn-ui-success")
                StyleColor.WARN -> styleClass.add("btn-ui-warn")
                StyleColor.ERROR -> styleClass.add("btn-ui-error")
                StyleColor.DEFAULT -> {}
            }
            when (styleSize) {
                StyleSize.TINY -> {}
                StyleSize.SMALL -> styleClass.add("btn-ui-small")
                StyleSize.BIG -> styleClass.add("btn-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }

}

// TextField 构建器
@FXMarker
class TextFieldBuilder : RegionBaseBuilder<TextField>() {

    override fun buildInstance(): TextField = TextField()

    fun text(text: String) {
        settings { this.text = text }
    }

    operator fun String.unaryPlus() = text(this)

    fun <T> observe(state: ObservableValue<T>, block: (T) -> String) = settings {
        val binding = Bindings.createStringBinding({ block(state.value) }, state)
        textProperty().bind(binding)
    }

    fun observes(vararg states: ObservableValue<*>, block: () -> String) = settings {
        val binding = Bindings.createStringBinding({ block() }, *states)
        textProperty().bind(binding)
    }

    fun promptText(prompt: String) {
        settings { promptText = prompt }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    fun prefColumnCount(count: Int) {
        settings { prefColumnCount = count }
    }

    fun alignment(alignment: Pos) {
        settings { this.alignment = alignment }
    }

    fun font(font: Font) {
        settings { this.font = font }
    }

    fun fontSize(size: Double) {
        settings { font = Font.font(size) }
    }

    // 数据绑定
    fun byBindText(property: StringProperty) {
        settings { property.bind(textProperty()) }
    }

    fun bindText(property: StringProperty) {
        settings { textProperty().bind(property) }
    }

    fun bindBidirectionalText(property: StringProperty) {
        settings { textProperty().bindBidirectional(property) }
    }

    fun byBindBidirectionalText(property: StringProperty) {
        settings { property.bindBidirectional(textProperty()) }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("text-field-ui")
            when (styleSize) {
                StyleSize.TINY -> {}
                StyleSize.SMALL -> styleClass.add("text-field-ui-small")
                StyleSize.BIG -> styleClass.add("text-field-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}

// TextArea 构建器
@FXMarker
class TextAreaBuilder : RegionBaseBuilder<TextArea>() {

    override fun buildInstance(): TextArea = TextArea()

    fun text(text: String) {
        settings { this.text = text }
    }

    operator fun String.unaryPlus() = text(this)

    fun promptText(prompt: String) {
        settings { promptText = prompt }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    fun wrapText(wrap: Boolean = true) {
        settings { isWrapText = wrap }
    }

    fun prefRowCount(rows: Int) {
        settings { prefRowCount = rows }
    }

    fun prefColumnCount(columns: Int) {
        settings { prefColumnCount = columns }
    }

    fun font(font: Font) {
        settings { this.font = font }
    }

    fun fontSize(size: Double) {
        settings { font = Font.font(size) }
    }

    // 响应式观察
    fun <T> observe(state: ObservableValue<T>, block: (T) -> String) = settings {
        val binding = Bindings.createStringBinding({ block(state.value) }, state)
        textProperty().bind(binding)
    }

    fun observes(vararg states: ObservableValue<*>, block: () -> String) = settings {
        val binding = Bindings.createStringBinding({ block() }, *states)
        textProperty().bind(binding)
    }

    // 数据绑定
    fun byBindText(property: StringProperty) = settings { property.bind(textProperty()) }

    fun bindText(property: StringProperty) = settings { textProperty().bind(property) }

    fun bindBidirectionalText(property: StringProperty) = settings { textProperty().bindBidirectional(property) }

    fun byBindBidirectionalText(property: StringProperty) = settings { property.bindBidirectional(textProperty()) }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("text-area-ui")
        }
    }
}

// CheckBox 构建器
@FXMarker
class CheckBoxBuilder : LabeledBuilder<CheckBox>() {

    override fun buildInstance(): CheckBox = CheckBox()

    fun selected(selected: Boolean = true) {
        settings { isSelected = selected }
    }

    fun indeterminate(indeterminate: Boolean = true) {
        settings { isIndeterminate = indeterminate }
    }

    fun allowIndeterminate(allow: Boolean = true) {
        settings { isAllowIndeterminate = allow }
    }

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    // 响应式观察
    fun <T> observeSelected(state: ObservableValue<T>, block: (T) -> Boolean) = settings {
        val binding = Bindings.createBooleanBinding({ block(state.value) }, state)
        selectedProperty().bind(binding)
    }

    // 数据绑定
    fun byBindSelected(property: BooleanProperty) {
        settings { property.bind(selectedProperty()) }
    }

    fun bindSelected(property: BooleanProperty) {
        settings { selectedProperty().bind(property) }
    }

    fun bindBidirectionalSelected(property: BooleanProperty) {
        settings { selectedProperty().bindBidirectional(property) }
    }

    fun byBindBidirectionalSelected(property: BooleanProperty) {
        settings { property.bindBidirectional(selectedProperty()) }
    }

    fun addSelectedListener(listener: ChangeListener<Boolean>) = settings {
        selectedProperty().addListener(listener)
    }

    fun removeSelectedListener(listener: ChangeListener<Boolean>) = settings {
        selectedProperty().removeListener(listener)
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) = settings {
        styleClass.add("check-box-ui")
        when (styleColor) {
            StyleColor.MAIN -> styleClass.add("check-box-ui-main")
            StyleColor.NORMAL -> styleClass.add("check-box-ui-normal")
            StyleColor.SUCCESS -> styleClass.add("check-box-ui-success")
            StyleColor.WARN -> styleClass.add("check-box-ui-warn")
            StyleColor.ERROR -> styleClass.add("check-box-ui-error")
            StyleColor.DEFAULT -> {}
        }
//        when (styleSize) {
//            StyleSize.TINY -> {}
//            StyleSize.SMALL -> styleClass.add("check-box-ui-small")
//            StyleSize.BIG -> styleClass.add("check-box-ui-big")
//            StyleSize.DEFAULT -> {}
//        }
    }
}

// RadioButton 构建器
@FXMarker
class RadioButtonBuilder : LabeledBuilder<RadioButton>() {

    override fun buildInstance(): RadioButton = RadioButton()

    fun selected(selected: Boolean = true) {
        settings { isSelected = selected }
    }

    fun toggleGroup(group: ToggleGroup) {
        settings { this.toggleGroup = group }
    }

    fun listenSelected(selectedCallback: () -> Unit) {
        settings {
            selectedProperty().addListener { p0, p1, p2 ->
                if (p2) {
                    selectedCallback()
                }
            }
        }
    }

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    // 响应式观察
    fun <T> observeSelected(state: ObservableValue<T>, block: (T) -> Boolean) = settings {
        val binding = Bindings.createBooleanBinding({ block(state.value) }, state)
        selectedProperty().bind(binding)
    }

    // 数据绑定
    fun byBindSelected(property: BooleanProperty) = settings { property.bind(selectedProperty()) }

    fun bindSelected(property: BooleanProperty) = settings { selectedProperty().bind(property) }

    fun bindBidirectionalSelected(property: BooleanProperty) =
        settings { selectedProperty().bindBidirectional(property) }

    fun byBindBidirectionalSelected(property: BooleanProperty) =
        settings { property.bindBidirectional(selectedProperty()) }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("radio-button-ui")
            when (styleColor) {
                StyleColor.MAIN -> styleClass.add("radio-button-ui-main")
                StyleColor.NORMAL -> styleClass.add("radio-button-ui-normal")
                StyleColor.SUCCESS -> styleClass.add("radio-button-ui-success")
                StyleColor.WARN -> styleClass.add("radio-button-ui-warn")
                StyleColor.ERROR -> styleClass.add("radio-button-ui-error")
                StyleColor.DEFAULT -> {}
            }
            when (styleSize) {
                StyleSize.TINY -> styleClass.add("radio-button-ui-tiny")
                StyleSize.SMALL -> styleClass.add("radio-button-ui-small")
                StyleSize.BIG -> styleClass.add("radio-button-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}

@FXMarker
abstract class ComboBoxBaseBuilder<S : ComboBox<T>, T> : RegionBaseBuilder<S>() {

    fun items(vararg items: T) {
        settings { this.items.addAll(items) }
    }

    fun items(items: List<T>) {
        settings { this.items.addAll(items) }
    }

    fun items(items: ObservableList<T>) {
        settings { this.items = items }
    }

    fun converter(stringConverter: StringConverter<T>) {
        settings {
            converter = stringConverter
        }
    }

    fun converter(fromString: ((String?) -> T?)? = null, toString: ((T?) -> String?)?) {
        settings {
            converter = object : StringConverter<T>() {
                override fun toString(p0: T?): String? = toString?.invoke(p0)

                override fun fromString(p0: String?): T? = fromString?.invoke(p0)
            }
        }
    }

    fun value(value: T) {
        settings { this.value = value }
    }

    fun promptText(prompt: String) {
        settings { promptText = prompt }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    fun visibleRowCount(count: Int) {
        settings { visibleRowCount = count }
    }

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    // 响应式观察
    fun <V> observeValue(state: ObservableValue<V>, block: (V) -> T) = settings {
        val binding = Bindings.createObjectBinding({ block(state.value) }, state)
        valueProperty().bind(binding)
    }

    // 数据绑定
    fun byBindValue(property: ObjectProperty<T>) = settings { property.bind(valueProperty()) }

    fun bindValue(property: ObjectProperty<T>) = settings { valueProperty().bind(property) }

    fun bindBidirectionalValue(property: ObjectProperty<T>) = settings { valueProperty().bindBidirectional(property) }

    fun byBindBidirectionalValue(property: ObjectProperty<T>) = settings { property.bindBidirectional(valueProperty()) }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("combo-box-ui")
            when (styleColor) {
                StyleColor.MAIN -> {}
                StyleColor.NORMAL -> styleClass.add("combo-box-ui-normal")
                StyleColor.SUCCESS -> styleClass.add("combo-box-ui-success")
                StyleColor.WARN -> styleClass.add("combo-box-ui-warn")
                StyleColor.ERROR -> styleClass.add("combo-box-ui-error")
                StyleColor.DEFAULT -> {}
            }
            when (styleSize) {
                StyleSize.TINY -> {}
                StyleSize.SMALL -> styleClass.add("combo-box-ui-small")
                StyleSize.BIG -> styleClass.add("combo-box-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }

}

@FXMarker
class FilterComboBoxBuilder<T> : ComboBoxBaseBuilder<FilterComboBox<T>, T>() {

    override fun buildInstance(): FilterComboBox<T> = FilterComboBox()

    fun ignoreCase(ignoreCase: Boolean) {
        settings {
            isIgnoreCase = ignoreCase
        }
    }

}

// ComboBox 构建器
@FXMarker
open class ComboBoxBuilder<T> : ComboBoxBaseBuilder<ComboBox<T>, T>() {

    override fun buildInstance(): ComboBox<T> = ComboBox<T>()

    fun addValueListener(changeListener: ChangeListener<in T>) {
        settings {
            valueProperty().addListener(changeListener)
        }
    }

    fun removeValueListener(changeListener: ChangeListener<in T>) {
        settings {
            valueProperty().removeListener(changeListener)
        }
    }

//    fun addItemsListener(changeListener: ChangeListener<in ObservableList<T?>>) {
//        settings {
//            itemsProperty().addListener(changeListener)
//        }
//    }
//
//    fun removeItemsListener(changeListener: ChangeListener<in ObservableList<T?>>) {
//        settings {
//            itemsProperty().removeListener(changeListener)
//        }
//    }

}

// ListView 构建器
@FXMarker
class ListViewBuilder<T> : RegionBaseBuilder<ListView<T>>() {

    override fun buildInstance(): ListView<T> = ListView<T>()

    fun items(vararg items: T) {
        settings { this.items.addAll(items) }
    }

    fun items(items: List<T>) {
        settings { this.items.addAll(items) }
    }

    fun items(items: ObservableList<T>) {
        settings { this.items = items }
    }

    fun orientation(orientation: javafx.geometry.Orientation) {
        settings { this.orientation = orientation }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("list-view-ui")
            when (styleSize) {
                StyleSize.TINY -> styleClass.add("list-view-ui-tiny")
                StyleSize.SMALL -> styleClass.add("list-view-ui-small")
                StyleSize.BIG -> styleClass.add("table-view-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}

// TableColumn 构建器
@FXMarker
class TableColumnBuilder<S, T> : DslBuilder<TableColumn<S, T>>() {

    override fun buildInstance(): TableColumn<S, T> = TableColumn()

    fun text(text: String) = settings { this.text = text }

    operator fun String.unaryPlus() = text(this)

    fun cellValue(valueFactory: (S) -> T) = cellDataValueFactory {
        ReadOnlyObjectWrapper(valueFactory(it.value))
    }

    fun cellValueFactory(valueFactory: (S) -> ObservableValue<T>) = cellDataValueFactory {
        valueFactory(it.value)
    }

    fun cellDataValueFactory(
        valueFactory: (TableColumn.CellDataFeatures<S, T>) -> ObservableValue<T>
    ) = settings {
        cellValueFactory = Callback { valueFactory(it) }
    }

    fun cellFactory(cellFactory: (TableColumn<S, T>) -> TableCell<S, T>) = settings {
        this.cellFactory = Callback { cellFactory(it) }
    }

    fun prefWidth(v: Double) = settings { prefWidth = v }
    fun minWidth(v: Double) = settings { minWidth = v }
    fun maxWidth(v: Double) = settings { maxWidth = v }
    fun sortable(v: Boolean = true) = settings { isSortable = v }
    fun resizable(v: Boolean = true) = settings { isResizable = v }
    fun reorderable(v: Boolean = true) = settings { isReorderable = v }
    fun editable(v: Boolean = true) = settings { isEditable = v }
    fun visible(v: Boolean = true) = settings { isVisible = v }
    fun style(v: String) = settings { style = v }
    fun styleClass(vararg c: String) = settings { styleClass.addAll(c) }
    fun graphic(node: Node) = settings { graphic = node }
    fun graphic(builder: () -> Node) = settings { graphic = builder() }
    fun contextMenu(contextMenu: ContextMenu) = settings { this.contextMenu = contextMenu }
    fun comparator(comparator: Comparator<T>) = settings { this.comparator = comparator }
    fun sortType(sortType: TableColumn.SortType) = settings { this.sortType = sortType }

    fun addColumn(column: TableColumn<S, *>) = settings {
        columns.add(column)
    }

    fun addColumns(vararg columns: TableColumn<S, *>) = settings {
        this.columns.addAll(columns)
    }

    fun setColumns(vararg columns: TableColumn<S, *>) = settings {
        this.columns.setAll(*columns)
    }

    fun clearColumns() = settings {
        columns.clear()
    }

    fun <V> addColumn(
        text: String = "",
        config: TableColumnBuilder<S, V>.() -> Unit = {}
    ) = settings {
        columns.add(tableColumn(text, config))
    }

    fun <V> addColumn(
        text: String,
        cellValue: (S) -> V,
        config: TableColumnBuilder<S, V>.() -> Unit = {}
    ) = settings {
        columns.add(tableColumn<S, V>(text) {
            cellValue(cellValue)
            config()
        })
    }
}

// TableView 构建器
@FXMarker
class TableViewBuilder<T> : RegionBaseBuilder<TableView<T>>() {

    override fun buildInstance(): TableView<T> = TableView<T>()

    fun items(vararg items: T) {
        settings { this.items.addAll(items) }
    }

    fun items(items: List<T>) {
        settings { this.items.addAll(items) }
    }

    fun items(items: ObservableList<T>) {
        settings { this.items = items }
    }

    fun editable(editable: Boolean = true) {
        settings { isEditable = editable }
    }

    fun sortPolicy(policy: javafx.util.Callback<TableView<T>, Boolean>) {
        settings { sortPolicy = policy }
    }

    fun columnResizePolicy(policy: javafx.util.Callback<TableView.ResizeFeatures<*>, Boolean>) {
        settings { columnResizePolicy = policy }
    }

    fun columns() = settings {

    }

    fun addColumn(column: TableColumn<T, *>) = settings {
        columns.add(column)
    }

    fun addColumns(vararg columns: TableColumn<T, *>) = settings {
        this.columns.addAll(columns)
    }

    fun setColumns(vararg columns: TableColumn<T, *>) = settings {
        this.columns.setAll(*columns)
    }

    fun clearColumns() = settings {
        columns.clear()
    }

    fun <V> addColumn(
        text: String = "",
        config: TableColumnBuilder<T, V>.() -> Unit = {}
    ) = settings {
        columns.add(tableColumn(text, config))
    }

    fun <V> addColumn(
        text: String,
        cellValue: (T) -> V,
        config: TableColumnBuilder<T, V>.() -> Unit = {}
    ) = settings {
        columns.add(tableColumn<T, V>(text) {
            cellValue(cellValue)
            config()
        })
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("table-view-ui")
            when (styleSize) {
                StyleSize.TINY -> {}
                StyleSize.SMALL -> styleClass.add("table-view-ui-small")
                StyleSize.BIG -> styleClass.add("table-view-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}

// ProgressBar 构建器
@FXMarker
class ProgressBarBuilder : RegionBaseBuilder<ProgressBar>() {

    override fun buildInstance(): ProgressBar = ProgressBar()

    fun progress(progress: Double = 0.0) {
        settings { this.progress = progress }
    }

    fun indeterminate() {
        settings { progress = ProgressBar.INDETERMINATE_PROGRESS }
    }

    // 响应式观察
    fun <T> observeProgress(state: ObservableValue<T>, block: (T) -> Double) = settings {
        val binding = Bindings.createDoubleBinding({ block(state.value) }, state)
        progressProperty().bind(binding)
    }

    // 数据绑定
    fun byBindProgress(property: DoubleProperty) = settings { property.bind(progressProperty()) }

    fun bindProgress(property: DoubleProperty) = settings { progressProperty().bind(property) }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("progress-bar-ui")
        }
    }
}

// Slider 构建器
@FXMarker
class SliderBuilder : RegionBaseBuilder<Slider>() {

    override fun buildInstance(): Slider = Slider()

    fun range(min: Double, max: Double) {
        settings {
            this.min = min
            this.max = max
        }
    }

    fun value(value: Double) {
        settings { this.value = value }
    }

    fun orientation(orientation: javafx.geometry.Orientation) {
        settings { this.orientation = orientation }
    }

    fun showTickLabels(show: Boolean = true) {
        settings { isShowTickLabels = show }
    }

    fun showTickMarks(show: Boolean = true) {
        settings { isShowTickMarks = show }
    }

    fun majorTickUnit(unit: Double) {
        settings { majorTickUnit = unit }
    }

    fun snapToTicks(snap: Boolean = true) {
        settings { isSnapToTicks = snap }
    }

    // 响应式观察
    fun <T> observeValue(state: ObservableValue<T>, block: (T) -> Double) = settings {
        val binding = Bindings.createDoubleBinding({ block(state.value) }, state)
        valueProperty().bind(binding)
    }

    // 数据绑定
    fun byBindValue(property: DoubleProperty) = settings { property.bind(valueProperty()) }

    fun bindValue(property: DoubleProperty) = settings { valueProperty().bind(property) }

    fun addValueListener(listener: ChangeListener<Number>) = settings { valueProperty().addListener(listener) }

    fun removeValueListener(listener: ChangeListener<Number>) = settings { valueProperty().removeListener(listener) }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("slider-ui")
        }
    }
}

// ImageView 构建器
@FXMarker
class ImageViewBuilder : NodeBuilder<ImageView>() {

    override fun buildInstance(): ImageView = ImageView()

    fun image(url: String) {
        settings { image = Image(url) }
    }

    fun image(image: Image) {
        settings { this.image = image }
    }

    fun fitSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            fitWidth = width
            fitHeight = height
        }
    }

    fun bindFitWidth(p: ObservableValue<out Number>) = settings { fitWidthProperty().bind(p) }
    fun bindFitHeight(p: ObservableValue<out Number>) = settings { fitHeightProperty().bind(p) }
    fun byBindFitWidth(p: DoubleProperty) = settings { p.bind(fitWidthProperty()) }
    fun byBindFitHeight(p: DoubleProperty) = settings { p.bind(fitHeightProperty()) }

    fun preserveRatio(preserve: Boolean = true) {
        settings { isPreserveRatio = preserve }
    }

    fun smooth(smooth: Boolean = true) {
        settings { isSmooth = smooth }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("image-view-ui")
        }
    }
}

@FXMarker
class SeparatorBuilder : RegionBaseBuilder<Separator>() {
    override fun buildInstance() = Separator()


    fun orientation(orientation: Orientation) = settings {
        this.orientation = orientation
    }

    fun vertical() = orientation(Orientation.VERTICAL)

    fun horizontal() = orientation(Orientation.HORIZONTAL)

}

@FXMarker
class PaginationBuilder : RegionBaseBuilder<Pagination>() {

    override fun buildInstance(): Pagination = Pagination()

    fun pageCount(pageCount: Int) = settings {
        this.pageCount = pageCount
    }

    fun currentPageIndex(currentPageIndex: Int) = settings {
        this.currentPageIndex = currentPageIndex
    }

    fun maxPageIndicatorCount(maxPageIndicatorCount: Int) = settings {
        this.maxPageIndicatorCount = maxPageIndicatorCount
    }
}

// Switch 构建器
@FXMarker
open class SwitchBuilder : RegionBaseBuilder<Switch>() {

    override fun buildInstance(): Switch = Switch()

    fun addStatusListener(changeListener: ChangeListener<Boolean>) {
        settings {
            statusProperty().addListener(changeListener)
        }
    }

    fun removeStatusListener(changeListener: ChangeListener<Boolean>) {
        settings {
            statusProperty().removeListener(changeListener)
        }
    }

    fun status(status: Boolean) = settings { this.status = status }

    fun transitionDuration(transitionDuration: Duration) = settings { this.transitionDuration = transitionDuration }

    fun transitionType(transitionType: BaseTransitionEnum) = settings { this.transitionType = transitionType }

    fun size(size: Double) = settings { this.size = size }

    fun loading(loading: Boolean) = settings { this.isLoading = loading }

    // 响应式观察
    fun <T> observeStatus(state: ObservableValue<T>, block: (T) -> Boolean) = settings {
        val binding = Bindings.createBooleanBinding({ block(state.value) }, state)
        statusProperty().bind(binding)
    }

    // 数据绑定
    fun byBindStatus(property: BooleanProperty) {
        settings { property.bind(statusProperty()) }
    }

    fun bindStatus(property: BooleanProperty) {
        settings { statusProperty().bind(property) }
    }

    fun bindBidirectionalStatus(property: BooleanProperty) {
        settings { statusProperty().bindBidirectional(property) }
    }

    fun byBindBidirectionalStatus(property: BooleanProperty) {
        settings { property.bindBidirectional(statusProperty()) }
    }
}


// Text 衍生
inline fun text(config: TextBuilder.() -> Unit): Text =
    textBuilder(config).build()

inline fun text(text: String, config: TextBuilder.() -> Unit = {}): Text =
    textBuilder {
        text(text)
        config()
    }.build()

inline fun textBuilder(config: TextBuilder.() -> Unit): TextBuilder =
    TextBuilder().apply(config)

fun textConfig(config: TextBuilder.() -> Unit): TextBuilder.() -> Unit =
    config

inline fun Text.config(config: TextBuilder.() -> Unit): Text =
    apply {
        TextBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Polygon 衍生
inline fun polygon(config: PolygonBuilder.() -> Unit): Polygon =
    polygonBuilder(config).build()

inline fun polygon(points: DoubleArray, config: PolygonBuilder.() -> Unit = {}): Polygon =
    polygonBuilder {
        points(*points)
        config()
    }.build()

inline fun polygon(points: List<Double>, config: PolygonBuilder.() -> Unit = {}): Polygon =
    polygonBuilder {
        points(points)
        config()
    }.build()

inline fun polygonBuilder(config: PolygonBuilder.() -> Unit): PolygonBuilder =
    PolygonBuilder().apply(config)

fun polygonConfig(config: PolygonBuilder.() -> Unit): PolygonBuilder.() -> Unit =
    config

inline fun Polygon.config(config: PolygonBuilder.() -> Unit): Polygon =
    apply {
        PolygonBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Region 衍生
inline fun region(config: RegionBuilder.() -> Unit): Region =
    regionBuilder(config).build()

inline fun regionBuilder(config: RegionBuilder.() -> Unit): RegionBuilder =
    RegionBuilder().apply(config)

fun regionConfig(config: RegionBuilder.() -> Unit): RegionBuilder.() -> Unit =
    config

// 如果后续 RegionBuilder 支持 config(region)，可以再打开
// inline fun Region.config(config: RegionBuilder.() -> Unit): Region =
//     apply {
//         RegionBuilder().apply {
//             delayMode()
//             config()
//         }.config(this@config)
//     }


// Label 衍生
inline fun label(config: LabelBuilder.() -> Unit): Label =
    labelBuilder(config).build()

inline fun label(text: String, config: LabelBuilder.() -> Unit = {}): Label =
    labelBuilder {
        text(text)
        config()
    }.build()

inline fun labelBuilder(config: LabelBuilder.() -> Unit): LabelBuilder =
    LabelBuilder().apply(config)

fun labelConfig(config: LabelBuilder.() -> Unit): LabelBuilder.() -> Unit =
    config

inline fun Label.config(config: LabelBuilder.() -> Unit): Label =
    apply {
        LabelBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Button 衍生
inline fun button(config: ButtonBuilder.() -> Unit): Button =
    buttonBuilder(config).build()

inline fun button(text: String, config: ButtonBuilder.() -> Unit = {}): Button =
    buttonBuilder {
        text(text)
        config()
    }.build()

inline fun buttonBuilder(config: ButtonBuilder.() -> Unit): ButtonBuilder =
    ButtonBuilder().apply(config)

fun buttonConfig(config: ButtonBuilder.() -> Unit): ButtonBuilder.() -> Unit =
    config

inline fun Button.config(config: ButtonBuilder.() -> Unit): Button =
    apply {
        ButtonBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// TextField 衍生
inline fun textField(config: TextFieldBuilder.() -> Unit = {}): TextField =
    textFieldBuilder(config).build()

inline fun textField(text: String, config: TextFieldBuilder.() -> Unit = {}): TextField =
    textFieldBuilder {
        text(text)
        config()
    }.build()

inline fun textFieldBuilder(config: TextFieldBuilder.() -> Unit): TextFieldBuilder =
    TextFieldBuilder().apply(config)

fun textFieldConfig(config: TextFieldBuilder.() -> Unit): TextFieldBuilder.() -> Unit =
    config

inline fun TextField.config(config: TextFieldBuilder.() -> Unit): TextField =
    apply {
        TextFieldBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// TextArea 衍生
inline fun textArea(config: TextAreaBuilder.() -> Unit = {}): TextArea =
    textAreaBuilder(config).build()

inline fun textArea(text: String, config: TextAreaBuilder.() -> Unit = {}): TextArea =
    textAreaBuilder {
        text(text)
        config()
    }.build()

inline fun textAreaBuilder(config: TextAreaBuilder.() -> Unit): TextAreaBuilder =
    TextAreaBuilder().apply(config)

fun textAreaConfig(config: TextAreaBuilder.() -> Unit): TextAreaBuilder.() -> Unit =
    config

inline fun TextArea.config(config: TextAreaBuilder.() -> Unit): TextArea =
    apply {
        TextAreaBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// CheckBox 衍生
inline fun checkBox(config: CheckBoxBuilder.() -> Unit): CheckBox =
    checkBoxBuilder(config).build()

inline fun checkBox(text: String, config: CheckBoxBuilder.() -> Unit = {}): CheckBox =
    checkBoxBuilder {
        text(text)
        config()
    }.build()

inline fun checkBoxBuilder(config: CheckBoxBuilder.() -> Unit): CheckBoxBuilder =
    CheckBoxBuilder().apply(config)

fun checkBoxConfig(config: CheckBoxBuilder.() -> Unit): CheckBoxBuilder.() -> Unit =
    config

inline fun CheckBox.config(config: CheckBoxBuilder.() -> Unit): CheckBox =
    apply {
        CheckBoxBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// RadioButton 衍生
inline fun radioButton(config: RadioButtonBuilder.() -> Unit): RadioButton =
    radioButtonBuilder(config).build()

inline fun radioButton(text: String, config: RadioButtonBuilder.() -> Unit = {}): RadioButton =
    radioButtonBuilder {
        text(text)
        config()
    }.build()

inline fun radioButton(
    text: String,
    toggleGroup: ToggleGroup,
    config: RadioButtonBuilder.() -> Unit = {}
): RadioButton =
    radioButtonBuilder {
        text(text)
        toggleGroup(toggleGroup)
        config()
    }.build()

inline fun radioButtonBuilder(config: RadioButtonBuilder.() -> Unit): RadioButtonBuilder =
    RadioButtonBuilder().apply(config)

fun radioButtonConfig(config: RadioButtonBuilder.() -> Unit): RadioButtonBuilder.() -> Unit =
    config

inline fun RadioButton.config(config: RadioButtonBuilder.() -> Unit): RadioButton =
    apply {
        RadioButtonBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// ComboBox 衍生
inline fun <T> comboBox(config: ComboBoxBuilder<T>.() -> Unit): ComboBox<T> =
    comboBoxBuilder(config).build()

inline fun <T> comboBox(items: List<T>, config: ComboBoxBuilder<T>.() -> Unit = {}): ComboBox<T> =
    comboBoxBuilder {
        items(items)
        config()
    }.build()

inline fun <T> comboBoxBuilder(config: ComboBoxBuilder<T>.() -> Unit): ComboBoxBuilder<T> =
    ComboBoxBuilder<T>().apply(config)

fun <T> comboBoxConfig(config: ComboBoxBuilder<T>.() -> Unit): ComboBoxBuilder<T>.() -> Unit =
    config

inline fun <T> ComboBox<T>.config(config: ComboBoxBuilder<T>.() -> Unit): ComboBox<T> =
    apply {
        ComboBoxBuilder<T>().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// ListView 衍生
inline fun <T> listView(config: ListViewBuilder<T>.() -> Unit): ListView<T> =
    listViewBuilder(config).build()

inline fun <T> listView(items: List<T>, config: ListViewBuilder<T>.() -> Unit = {}): ListView<T> =
    listViewBuilder {
        items(items)
        config()
    }.build()

inline fun <T> listViewBuilder(config: ListViewBuilder<T>.() -> Unit): ListViewBuilder<T> =
    ListViewBuilder<T>().apply(config)

fun <T> listViewConfig(config: ListViewBuilder<T>.() -> Unit): ListViewBuilder<T>.() -> Unit =
    config

inline fun <T> ListView<T>.config(config: ListViewBuilder<T>.() -> Unit): ListView<T> =
    apply {
        ListViewBuilder<T>().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// TableColumn 衍生
inline fun <S, T> tableColumn(
    text: String = "",
    config: TableColumnBuilder<S, T>.() -> Unit = {}
): TableColumn<S, T> =
    tableColumnBuilder(text, config).build()

inline fun <S, T> tableColumnBuilder(
    text: String = "",
    config: TableColumnBuilder<S, T>.() -> Unit = {}
): TableColumnBuilder<S, T> =
    TableColumnBuilder<S, T>().apply {
        text(text)
        config()
    }

fun <S, T> tableColumnConfig(
    config: TableColumnBuilder<S, T>.() -> Unit
): TableColumnBuilder<S, T>.() -> Unit =
    config

inline fun <S, T> TableColumn<S, T>.config(
    config: TableColumnBuilder<S, T>.() -> Unit
): TableColumn<S, T> =
    apply {
        TableColumnBuilder<S, T>().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// TableView 衍生
inline fun <T> tableView(config: TableViewBuilder<T>.() -> Unit): TableView<T> =
    tableViewBuilder(config).build()

inline fun <T> tableView(items: List<T>, config: TableViewBuilder<T>.() -> Unit = {}): TableView<T> =
    tableViewBuilder {
        items(items)
        config()
    }.build()

inline fun <T> tableViewBuilder(config: TableViewBuilder<T>.() -> Unit): TableViewBuilder<T> =
    TableViewBuilder<T>().apply(config)

fun <T> tableViewConfig(config: TableViewBuilder<T>.() -> Unit): TableViewBuilder<T>.() -> Unit =
    config

inline fun <T> TableView<T>.config(config: TableViewBuilder<T>.() -> Unit): TableView<T> =
    apply {
        TableViewBuilder<T>().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// ProgressBar 衍生
inline fun progressBar(config: ProgressBarBuilder.() -> Unit = {}): ProgressBar =
    progressBarBuilder(config).build()

inline fun progressBarBuilder(config: ProgressBarBuilder.() -> Unit): ProgressBarBuilder =
    ProgressBarBuilder().apply(config)

fun progressBarConfig(config: ProgressBarBuilder.() -> Unit): ProgressBarBuilder.() -> Unit =
    config

inline fun ProgressBar.config(config: ProgressBarBuilder.() -> Unit): ProgressBar =
    apply {
        ProgressBarBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Slider 衍生
inline fun slider(config: SliderBuilder.() -> Unit = {}): Slider =
    sliderBuilder(config).build()

inline fun sliderBuilder(config: SliderBuilder.() -> Unit): SliderBuilder =
    SliderBuilder().apply(config)

fun sliderConfig(config: SliderBuilder.() -> Unit): SliderBuilder.() -> Unit =
    config

inline fun Slider.config(config: SliderBuilder.() -> Unit): Slider =
    apply {
        SliderBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// ImageView 衍生
inline fun imageView(config: ImageViewBuilder.() -> Unit = {}): ImageView =
    imageViewBuilder(config).build()

inline fun imageView(url: String, config: ImageViewBuilder.() -> Unit = {}): ImageView =
    imageViewBuilder {
        image(url)
        config()
    }.build()

inline fun imageViewBuilder(config: ImageViewBuilder.() -> Unit): ImageViewBuilder =
    ImageViewBuilder().apply(config)

fun imageViewConfig(config: ImageViewBuilder.() -> Unit): ImageViewBuilder.() -> Unit =
    config

inline fun ImageView.config(config: ImageViewBuilder.() -> Unit): ImageView =
    apply {
        ImageViewBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Separator 衍生
inline fun separator(config: SeparatorBuilder.() -> Unit = {}): Separator =
    separatorBuilder(config).build()

inline fun separatorBuilder(config: SeparatorBuilder.() -> Unit): SeparatorBuilder =
    SeparatorBuilder().apply(config)

fun separatorConfig(config: SeparatorBuilder.() -> Unit): SeparatorBuilder.() -> Unit =
    config

inline fun Separator.config(config: SeparatorBuilder.() -> Unit): Separator =
    apply {
        SeparatorBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Pagination 衍生
inline fun pagination(config: PaginationBuilder.() -> Unit = {}): Pagination =
    paginationBuilder(config).build()

inline fun paginationBuilder(config: PaginationBuilder.() -> Unit): PaginationBuilder =
    PaginationBuilder().apply(config)

fun paginationConfig(config: PaginationBuilder.() -> Unit): PaginationBuilder.() -> Unit =
    config

inline fun Pagination.config(config: PaginationBuilder.() -> Unit): Pagination =
    apply {
        PaginationBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Spacer 衍生
inline fun hSpacer(priority: Priority = Priority.ALWAYS, config: Region.() -> Unit = {}): Region =
    Region().apply {
        config()
        HBox.setHgrow(this, priority)
    }

inline fun vSpacer(priority: Priority = Priority.ALWAYS, config: Region.() -> Unit = {}): Region =
    Region().apply {
        config()
        VBox.setVgrow(this, priority)
    }


// Switch 衍生
inline fun switch(config: SwitchBuilder.() -> Unit): Switch =
    switchBuilder(config).build()

inline fun switchBuilder(config: SwitchBuilder.() -> Unit): SwitchBuilder =
    SwitchBuilder().apply(config)

fun switchConfig(config: SwitchBuilder.() -> Unit): SwitchBuilder.() -> Unit =
    config