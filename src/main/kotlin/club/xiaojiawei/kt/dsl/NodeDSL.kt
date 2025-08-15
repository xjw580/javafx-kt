package club.xiaojiawei.kt.dsl

/**
 * @author 肖嘉威
 * @date 2025/8/14 16:19
 */

import club.xiaojiawei.controls.FilterComboBox
import javafx.beans.property.Property
import javafx.collections.ObservableList
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.MouseEvent
import javafx.scene.layout.Region
import javafx.scene.text.Font
import javafx.scene.text.FontWeight
import javafx.util.StringConverter

// 基础 Node 构建器
@FXMarker
abstract class NodeBuilder<T : Node> : DslBuilder<T>() {

    fun styleNormal(styleSize: StyleSize = StyleSize.DEFAULT) {
        style(StyleColor.NORMAL, styleSize)
    }

    fun styleSuccess(styleSize: StyleSize = StyleSize.DEFAULT) {
        style(StyleColor.SUCCESS, styleSize)
    }

    fun styleWarn(styleSize: StyleSize = StyleSize.DEFAULT) {
        style(StyleColor.WARN, styleSize)
    }

    fun styleError(styleSize: StyleSize = StyleSize.DEFAULT) {
        style(StyleColor.ERROR, styleSize)
    }

    // 通用 Node 属性
    fun id(id: String) {
        settings { this.id = id }
    }

    fun styleClass(vararg classes: String) {
        settings { styleClass.addAll(classes) }
    }

    fun style(style: String) {
        settings { this.style = style }
    }

    fun visible(visible: Boolean = true) {
        settings { isVisible = visible }
    }

    fun managed(managed: Boolean = true) {
        settings { isManaged = managed }
    }

    fun disable(disabled: Boolean = true) {
        settings { isDisable = disabled }
    }

    fun opacity(opacity: Double) {
        settings { this.opacity = opacity }
    }

    fun rotate(angle: Double) {
        settings { rotate = angle }
    }

    fun scale(x: Double, y: Double = x) {
        settings {
            scaleX = x
            scaleY = y
        }
    }

    fun translate(x: Double, y: Double) {
        settings {
            translateX = x
            translateY = y
        }
    }

    fun prefSize(width: Double, height: Double) {
        settings {
            if (this is Region) {
                prefWidth = width
                prefHeight = height
            }
        }
    }

    fun minSize(width: Double, height: Double) {
        settings {
            if (this is Region) {
                minWidth = width
                minHeight = height
            }
        }
    }

    fun maxSize(width: Double, height: Double) {
        settings {
            if (this is Region) {
                maxWidth = width
                maxHeight = height
            }
        }
    }

    // 事件处理
    fun onMouseClicked(handler: EventHandler<MouseEvent>) {
        settings { onMouseClicked = handler }
    }

    fun onMouseEntered(handler: EventHandler<MouseEvent>) {
        settings { onMouseEntered = handler }
    }

    fun onMouseExited(handler: EventHandler<MouseEvent>) {
        settings { onMouseExited = handler }
    }

    // 直接配置 Node
    fun configure(block: T.() -> Unit) {
        settings(block)
    }
}

// Label 构建器
@FXMarker
class LabelBuilder : LabeledBuilder<Label>() {

    override fun instance(): Label = Label()

    fun alignment(alignment: Pos) {
        settings { this.alignment = alignment }
    }

    fun wrapText(wrap: Boolean = true) {
        settings { isWrapText = wrap }
    }

    fun graphic(node: Node) {
        settings { graphic = node }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("label-ui")
        }
    }
}

// Button 构建器
@FXMarker
class ButtonBuilder : LabeledBuilder<Button>() {

    override fun instance(): Button = Button()

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    fun graphic(node: Node) {
        settings { graphic = node }
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
class TextFieldBuilder : NodeBuilder<TextField>() {

    override fun instance(): TextField = TextField()

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
    fun bind(property: Property<String>) {
        settings { textProperty().bindBidirectional(property) }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("text-field-ui")
        }
    }
}

// TextArea 构建器
@FXMarker
class TextAreaBuilder : NodeBuilder<TextArea>() {

    override fun instance(): TextArea = TextArea()

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

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("text-area-ui")
        }
    }
}

@FXMarker
abstract class LabeledBuilder<T : Labeled> : NodeBuilder<T>() {

    fun text(text: String) = settings { this.text = text }

    operator fun String.unaryPlus() = text(this)

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

}

// CheckBox 构建器
@FXMarker
class CheckBoxBuilder : LabeledBuilder<CheckBox>() {

    override fun instance(): CheckBox = CheckBox()

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

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("check-box-ui")
        }
    }
}

// RadioButton 构建器
@FXMarker
class RadioButtonBuilder : LabeledBuilder<RadioButton>() {

    override fun instance(): RadioButton = RadioButton()

    fun selected(selected: Boolean = true) {
        settings { isSelected = selected }
    }

    fun toggleGroup(group: ToggleGroup) {
        settings { this.toggleGroup = group }
    }

    fun onAction(handler: EventHandler<ActionEvent>) {
        settings { onAction = handler }
    }

    fun onAction(handler: () -> Unit) {
        settings { onAction = EventHandler { handler() } }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("radio-button-ui")
        }
    }
}

@FXMarker
abstract class ComboBoxBaseBuilder<S : ComboBox<T>, T> : NodeBuilder<S>() {

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

    fun converter(fromString: (String?) -> T? = { null }, toString: (T?) -> String?) {
        settings {
            converter = object : StringConverter<T>() {
                override fun toString(p0: T?): String? = toString(p0)

                override fun fromString(p0: String?): T? = fromString(p0)
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

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("combo-box-ui")
            when (styleColor) {
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

    override fun instance(): FilterComboBox<T> = FilterComboBox()

    fun ignoreCase(ignoreCase: Boolean) {
        settings {
            isIgnoreCase = ignoreCase
        }
    }

}

// ComboBox 构建器
@FXMarker
open class ComboBoxBuilder<T> : ComboBoxBaseBuilder<ComboBox<T>, T>() {

    override fun instance(): ComboBox<T> = ComboBox<T>()

}

// ListView 构建器
@FXMarker
class ListViewBuilder<T> : NodeBuilder<ListView<T>>() {

    override fun instance(): ListView<T> = ListView<T>()

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
        }
    }
}

// TableView 构建器
@FXMarker
class TableViewBuilder<T> : NodeBuilder<TableView<T>>() {

    override fun instance(): TableView<T> = TableView<T>()

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

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("table-view-ui")
        }
    }
}

// ProgressBar 构建器
@FXMarker
class ProgressBarBuilder : NodeBuilder<ProgressBar>() {

    override fun instance(): ProgressBar = ProgressBar()

    fun progress(progress: Double) {
        settings { this.progress = progress }
    }

    fun indeterminate() {
        settings { progress = ProgressBar.INDETERMINATE_PROGRESS }
    }

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("progress-bar-ui")
        }
    }
}

// Slider 构建器
@FXMarker
class SliderBuilder : NodeBuilder<Slider>() {

    override fun instance(): Slider = Slider()

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

    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            styleClass.add("slider-ui")
        }
    }
}

// ImageView 构建器
@FXMarker
class ImageViewBuilder : NodeBuilder<ImageView>() {

    override fun instance(): ImageView = ImageView()

    fun image(url: String) {
        settings { image = Image(url) }
    }

    fun image(image: Image) {
        settings { this.image = image }
    }

    fun fitSize(width: Double, height: Double) {
        settings {
            fitWidth = width
            fitHeight = height
        }
    }

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