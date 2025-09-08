package club.xiaojiawei.kt.dsl

import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.text.Text

/**
 * @author 肖嘉威
 * @date 2025/8/14 9:00
 */

// Pane 衍生
inline fun pane(config: PaneBuilder.() -> Unit): Pane {
    return paneBuilder(config).build()
}

inline fun paneBuilder(config: PaneBuilder.() -> Unit): PaneBuilder {
    return PaneBuilder().apply(config)
}

inline fun Pane.config(config: PaneBuilder.() -> Unit) {
    PaneBuilder().apply(config).config(this)
}

// VBox 衍生
inline fun vbox(config: VBoxBuilder.() -> Unit): VBox {
    return vboxBuilder(config).build()
}

inline fun vboxBuilder(config: VBoxBuilder.() -> Unit): VBoxBuilder {
    return VBoxBuilder().apply(config)
}

inline fun VBox.config(config: VBoxBuilder.() -> Unit) {
    VBoxBuilder().apply(config).config(this)
}

// HBox 衍生
inline fun hbox(config: HBoxBuilder.() -> Unit): HBox {
    return hboxBuilder(config).build()
}

inline fun hboxBuilder(config: HBoxBuilder.() -> Unit): HBoxBuilder {
    return HBoxBuilder().apply(config)
}

inline fun HBox.config(config: HBoxBuilder.() -> Unit) {
    HBoxBuilder().apply(config).config(this)
}

// StackPane 衍生
inline fun stackPane(config: StackPaneBuilder.() -> Unit): StackPane {
    return stackPaneBuilder(config).build()
}

inline fun stackPaneBuilder(config: StackPaneBuilder.() -> Unit): StackPaneBuilder {
    return StackPaneBuilder().apply(config)
}

inline fun StackPane.config(config: StackPaneBuilder.() -> Unit) {
    StackPaneBuilder().apply(config).config(this)
}

// BorderPane 衍生
inline fun borderPane(config: BorderPaneBuilder.() -> Unit): BorderPane {
    return borderPaneBuilder(config).build()
}

inline fun borderPaneBuilder(config: BorderPaneBuilder.() -> Unit): BorderPaneBuilder {
    return BorderPaneBuilder().apply(config)
}

inline fun BorderPane.config(config: BorderPaneBuilder.() -> Unit) {
    BorderPaneBuilder().apply(config).config(this)
}

// GridPane 衍生
inline fun gridPane(config: GridPaneBuilder.() -> Unit): GridPane {
    return gridPaneBuilder(config).build()
}

inline fun gridPaneBuilder(config: GridPaneBuilder.() -> Unit): GridPaneBuilder {
    return GridPaneBuilder().apply(config)
}

inline fun GridPane.config(config: GridPaneBuilder.() -> Unit) {
    GridPaneBuilder().apply(config).config(this)
}

// ======================== Node 组件 DSL 衍生 ========================

// Text 衍生
inline fun text(config: TextBuilder.() -> Unit): Text {
    return textBuilder(config).build()
}

inline fun text(text: String, config: TextBuilder.() -> Unit = {}): Text {
    return textBuilder {
        text(text)
        config()
    }.build()
}

inline fun textBuilder(config: TextBuilder.() -> Unit): TextBuilder {
    return TextBuilder().apply(config)
}

inline fun Text.config(config: TextBuilder.() -> Unit) {
    TextBuilder().apply(config).config(this)
}

// Label 衍生
inline fun label(config: LabelBuilder.() -> Unit): Label {
    return labelBuilder(config).build()
}

inline fun label(text: String, config: LabelBuilder.() -> Unit = {}): Label {
    return labelBuilder {
        text(text)
        config()
    }.build()
}

inline fun labelBuilder(config: LabelBuilder.() -> Unit): LabelBuilder {
    return LabelBuilder().apply(config)
}

inline fun Label.config(config: LabelBuilder.() -> Unit) {
    LabelBuilder().apply(config).config(this)
}

// Button 衍生
inline fun button(config: ButtonBuilder.() -> Unit): Button {
    return buttonBuilder(config).build()
}

inline fun button(text: String, config: ButtonBuilder.() -> Unit = {}): Button {
    return buttonBuilder {
        text(text)
        config()
    }.build()
}

inline fun buttonBuilder(config: ButtonBuilder.() -> Unit): ButtonBuilder {
    return ButtonBuilder().apply(config)
}

inline fun Button.config(config: ButtonBuilder.() -> Unit) {
    ButtonBuilder().apply(config).config(this)
}

// TextField 衍生
inline fun textField(config: TextFieldBuilder.() -> Unit = {}): TextField {
    return textFieldBuilder(config).build()
}

inline fun textField(text: String, config: TextFieldBuilder.() -> Unit = {}): TextField {
    return textFieldBuilder {
        text(text)
        config()
    }.build()
}

inline fun textFieldBuilder(config: TextFieldBuilder.() -> Unit): TextFieldBuilder {
    return TextFieldBuilder().apply(config)
}

inline fun TextField.config(config: TextFieldBuilder.() -> Unit) {
    TextFieldBuilder().apply(config).config(this)
}

// TextArea 衍生
inline fun textArea(config: TextAreaBuilder.() -> Unit = {}): TextArea {
    return textAreaBuilder(config).build()
}

inline fun textArea(text: String, config: TextAreaBuilder.() -> Unit = {}): TextArea {
    return textAreaBuilder {
        text(text)
        config()
    }.build()
}

inline fun textAreaBuilder(config: TextAreaBuilder.() -> Unit): TextAreaBuilder {
    return TextAreaBuilder().apply(config)
}

inline fun TextArea.config(config: TextAreaBuilder.() -> Unit) {
    TextAreaBuilder().apply(config).config(this)
}

// CheckBox 衍生
inline fun checkBox(config: CheckBoxBuilder.() -> Unit): CheckBox {
    return checkBoxBuilder(config).build()
}

inline fun checkBox(text: String, config: CheckBoxBuilder.() -> Unit = {}): CheckBox {
    return checkBoxBuilder {
        text(text)
        config()
    }.build()
}

inline fun checkBoxBuilder(config: CheckBoxBuilder.() -> Unit): CheckBoxBuilder {
    return CheckBoxBuilder().apply(config)
}

inline fun CheckBox.config(config: CheckBoxBuilder.() -> Unit) {
    CheckBoxBuilder().apply(config).config(this)
}

// RadioButton 衍生
inline fun radioButton(config: RadioButtonBuilder.() -> Unit): RadioButton {
    return radioButtonBuilder(config).build()
}

inline fun radioButton(text: String, config: RadioButtonBuilder.() -> Unit = {}): RadioButton {
    return radioButtonBuilder {
        text(text)
        config()
    }.build()
}

inline fun radioButtonBuilder(config: RadioButtonBuilder.() -> Unit): RadioButtonBuilder {
    return RadioButtonBuilder().apply(config)
}

inline fun RadioButton.config(config: RadioButtonBuilder.() -> Unit) {
    RadioButtonBuilder().apply(config).config(this)
}

// ComboBox 衍生
inline fun <T> comboBox(config: ComboBoxBuilder<T>.() -> Unit): ComboBox<T> {
    return comboBoxBuilder(config).build()
}

inline fun <T> comboBox(items: List<T>, config: ComboBoxBuilder<T>.() -> Unit = {}): ComboBox<T> {
    return comboBoxBuilder {
        items(items)
        config()
    }.build()
}

inline fun <T> comboBoxBuilder(config: ComboBoxBuilder<T>.() -> Unit): ComboBoxBuilder<T> {
    return ComboBoxBuilder<T>().apply(config)
}

inline fun <T> ComboBox<T>.config(config: ComboBoxBuilder<T>.() -> Unit) {
    ComboBoxBuilder<T>().apply(config).config(this)
}

// ListView 衍生
inline fun <T> listView(config: ListViewBuilder<T>.() -> Unit): ListView<T> {
    return listViewBuilder(config).build()
}

inline fun <T> listView(items: List<T>, config: ListViewBuilder<T>.() -> Unit = {}): ListView<T> {
    return listViewBuilder {
        items(items)
        config()
    }.build()
}

inline fun <T> listViewBuilder(config: ListViewBuilder<T>.() -> Unit): ListViewBuilder<T> {
    return ListViewBuilder<T>().apply(config)
}

inline fun <T> ListView<T>.config(config: ListViewBuilder<T>.() -> Unit) {
    ListViewBuilder<T>().apply(config).config(this)
}

// TableView 衍生
inline fun <T> tableView(config: TableViewBuilder<T>.() -> Unit): TableView<T> {
    return tableViewBuilder(config).build()
}

inline fun <T> tableView(items: List<T>, config: TableViewBuilder<T>.() -> Unit = {}): TableView<T> {
    return tableViewBuilder {
        items(items)
        config()
    }.build()
}

inline fun <T> tableViewBuilder(config: TableViewBuilder<T>.() -> Unit): TableViewBuilder<T> {
    return TableViewBuilder<T>().apply(config)
}

inline fun <T> TableView<T>.config(config: TableViewBuilder<T>.() -> Unit) {
    TableViewBuilder<T>().apply(config).config(this)
}

// ProgressBar 衍生
inline fun progressBar(config: ProgressBarBuilder.() -> Unit = {}): ProgressBar {
    return progressBarBuilder(config).build()
}

inline fun progressBarBuilder(config: ProgressBarBuilder.() -> Unit): ProgressBarBuilder {
    return ProgressBarBuilder().apply(config)
}

inline fun ProgressBar.config(config: ProgressBarBuilder.() -> Unit) {
    ProgressBarBuilder().apply(config).config(this)
}

// Slider 衍生
inline fun slider(config: SliderBuilder.() -> Unit = {}): Slider {
    return sliderBuilder(config).build()
}

inline fun sliderBuilder(config: SliderBuilder.() -> Unit): SliderBuilder {
    return SliderBuilder().apply(config)
}

inline fun Slider.config(config: SliderBuilder.() -> Unit) {
    SliderBuilder().apply(config).config(this)
}

// ImageView 衍生
inline fun imageView(config: ImageViewBuilder.() -> Unit = {}): ImageView {
    return imageViewBuilder(config).build()
}

inline fun imageView(url: String, config: ImageViewBuilder.() -> Unit = {}): ImageView {
    return imageViewBuilder {
        image(url)
        config()
    }.build()
}

inline fun imageViewBuilder(config: ImageViewBuilder.() -> Unit): ImageViewBuilder {
    return ImageViewBuilder().apply(config)
}

inline fun ImageView.config(config: ImageViewBuilder.() -> Unit) {
    ImageViewBuilder().apply(config).config(this)
}

// ContextMenu 衍生
inline fun contextMenu(config: ContextMenuBuilder.() -> Unit): ContextMenu {
    return contextMenuBuilder(config).build()
}

inline fun contextMenuBuilder(config: ContextMenuBuilder.() -> Unit): ContextMenuBuilder {
    return ContextMenuBuilder().apply(config)
}

inline fun ContextMenu.config(config: ContextMenuBuilder.() -> Unit) {
    ContextMenuBuilder().apply(config).config(this)
}

// MenuItem 衍生
inline fun menuItem(config: MenuItemBuilder.() -> Unit): MenuItem {
    return menuItemBuilder(config).build()
}

inline fun menuItem(text: String, config: MenuItemBuilder.() -> Unit = {}): MenuItem {
    return menuItemBuilder {
        text(text)
        config()
    }.build()
}

inline fun menuItemBuilder(config: MenuItemBuilder.() -> Unit): MenuItemBuilder {
    return MenuItemBuilder().apply(config)
}

inline fun MenuItem.config(config: MenuItemBuilder.() -> Unit) {
    MenuItemBuilder().apply(config).config(this)
}

// Menu 衍生
inline fun menu(config: MenuBuilder.() -> Unit): Menu {
    return menuBuilder(config).build()
}

inline fun menu(text: String, config: MenuBuilder.() -> Unit = {}): Menu {
    return menuBuilder {
        text(text)
        config()
    }.build()
}

inline fun menuBuilder(config: MenuBuilder.() -> Unit): MenuBuilder {
    return MenuBuilder().apply(config)
}

inline fun Menu.config(config: MenuBuilder.() -> Unit) {
    MenuBuilder().apply(config).config(this)
}

enum class StyleSize() {
    TINY, SMALL, BIG, DEFAULT
}

enum class StyleColor() {
    MAIN, NORMAL, SUCCESS, WARN, ERROR, DEFAULT
}

abstract class DslBuilder<T>() {

    private val builders: MutableList<T.() -> Unit> = mutableListOf()

    open fun style(styleColor: StyleColor = StyleColor.DEFAULT, styleSize: StyleSize = StyleSize.DEFAULT) {}

    abstract fun instance(): T

    open fun settings(settings: T.() -> Unit) {
        builders.add(settings)
    }

    /**
     * 通过配置构建新的实例
     */
    open fun build(): T {
        val t = instance().apply {
            builders.forEach { it() }
        }
        return t
    }

    /**
     * 将配置应用到传入的实例中
     */
    open fun config(t: T) {
        t.apply {
            builders.forEach { it() }
        }
    }

}