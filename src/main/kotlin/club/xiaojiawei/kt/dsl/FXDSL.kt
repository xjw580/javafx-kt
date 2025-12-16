package club.xiaojiawei.kt.dsl

import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.shape.Polygon
import javafx.scene.text.Text
import javafx.stage.Stage

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

//inline fun Pane.config(config: PaneBuilder.() -> Unit) :Pane{
//    PaneBuilder().apply(config).config(this)
//return this
//}

// VBox 衍生
inline fun vbox(config: VBoxBuilder.() -> Unit): VBox {
    return vboxBuilder(config).build()
}

inline fun vboxBuilder(config: VBoxBuilder.() -> Unit): VBoxBuilder {
    return VBoxBuilder().apply(config)
}

inline fun VBox.config(config: VBoxBuilder.() -> Unit): VBox {
    VBoxBuilder().apply(config).config(this)
    return this
}

// HBox 衍生
inline fun hbox(config: HBoxBuilder.() -> Unit): HBox {
    return hboxBuilder(config).build()
}

inline fun hboxBuilder(config: HBoxBuilder.() -> Unit): HBoxBuilder {
    return HBoxBuilder().apply(config)
}

inline fun HBox.config(config: HBoxBuilder.() -> Unit): HBox {
    HBoxBuilder().apply(config).config(this)
    return this
}

// StackPane 衍生
inline fun stackPane(config: StackPaneBuilder.() -> Unit): StackPane {
    return stackPaneBuilder(config).build()
}

inline fun stackPaneBuilder(config: StackPaneBuilder.() -> Unit): StackPaneBuilder {
    return StackPaneBuilder().apply(config)
}

inline fun StackPane.config(config: StackPaneBuilder.() -> Unit): StackPane {
    StackPaneBuilder().apply(config).config(this)
    return this
}

// BorderPane 衍生
inline fun borderPane(config: BorderPaneBuilder.() -> Unit): BorderPane {
    return borderPaneBuilder(config).build()
}

inline fun borderPaneBuilder(config: BorderPaneBuilder.() -> Unit): BorderPaneBuilder {
    return BorderPaneBuilder().apply(config)
}

inline fun BorderPane.config(config: BorderPaneBuilder.() -> Unit): BorderPane {
    BorderPaneBuilder().apply(config).config(this)
    return this
}

// GridPane 衍生
inline fun gridPane(config: GridPaneBuilder.() -> Unit): GridPane {
    return gridPaneBuilder(config).build()
}

inline fun gridPaneBuilder(config: GridPaneBuilder.() -> Unit): GridPaneBuilder {
    return GridPaneBuilder().apply(config)
}

inline fun GridPane.config(config: GridPaneBuilder.() -> Unit): GridPane {
    GridPaneBuilder().apply(config).config(this)
    return this
}

// AnchorPane 衍生
inline fun anchorPane(config: AnchorPaneBuilder.() -> Unit): AnchorPane {
    return AnchorPaneBuilder().apply(config).build()
}

inline fun AnchorPane.config(config: AnchorPaneBuilder.() -> Unit): AnchorPane {
    AnchorPaneBuilder().apply(config).config(this)
    return this
}

// FlowPane 衍生
inline fun flowPane(config: FlowPaneBuilder.() -> Unit): FlowPane {
    return FlowPaneBuilder().apply(config).build()
}

inline fun FlowPane.config(config: FlowPaneBuilder.() -> Unit): FlowPane {
    FlowPaneBuilder().apply(config).config(this)
    return this
}

// TilePane 衍生
inline fun tilePane(config: TilePaneBuilder.() -> Unit): TilePane {
    return TilePaneBuilder().apply(config).build()
}

inline fun TilePane.config(config: TilePaneBuilder.() -> Unit): TilePane {
    TilePaneBuilder().apply(config).config(this)
    return this
}

// ScrollPane 衍生
inline fun scrollPane(config: ScrollPaneBuilder.() -> Unit): ScrollPane {
    return ScrollPaneBuilder().apply(config).build()
}

inline fun ScrollPane.config(config: ScrollPaneBuilder.() -> Unit): ScrollPane {
    ScrollPaneBuilder().apply(config).config(this)
    return this
}

// SplitPane 衍生
inline fun splitPane(config: SplitPaneBuilder.() -> Unit): SplitPane {
    return SplitPaneBuilder().apply(config).build()
}

inline fun SplitPane.config(config: SplitPaneBuilder.() -> Unit): SplitPane {
    SplitPaneBuilder().apply(config).config(this)
    return this
}

// TitledPane 衍生
inline fun titledPane(config: TitledPaneBuilder.() -> Unit): TitledPane {
    return TitledPaneBuilder().apply(config).build()
}

inline fun titledPane(title: String, config: TitledPaneBuilder.() -> Unit = {}): TitledPane {
    return TitledPaneBuilder().apply {
        text(title)
        config()
    }.build()
}

inline fun TitledPane.config(config: TitledPaneBuilder.() -> Unit): TitledPane {
    TitledPaneBuilder().apply(config).config(this)
    return this
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

inline fun Text.config(config: TextBuilder.() -> Unit): Text {
    TextBuilder().apply(config).config(this)
    return this
}

// Polygon 衍生
inline fun polygon(config: PolygonBuilder.() -> Unit): Polygon {
    return polygonBuilder(config).build()
}

inline fun polygon(points: DoubleArray, config: PolygonBuilder.() -> Unit = {}): Polygon {
    return polygonBuilder {
        points(*points)
        config()
    }.build()
}

inline fun polygon(points: List<Double>, config: PolygonBuilder.() -> Unit = {}): Polygon {
    return polygonBuilder {
        points(points)
        config()
    }.build()
}

inline fun polygonBuilder(config: PolygonBuilder.() -> Unit): PolygonBuilder {
    return PolygonBuilder().apply(config)
}

inline fun Polygon.config(config: PolygonBuilder.() -> Unit): Polygon {
    PolygonBuilder().apply(config).config(this)
    return this
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

inline fun Label.config(config: LabelBuilder.() -> Unit): Label {
    LabelBuilder().apply(config).config(this)
    return this
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

inline fun Button.config(config: ButtonBuilder.() -> Unit): Button {
    ButtonBuilder().apply(config).config(this)
    return this
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

inline fun TextField.config(config: TextFieldBuilder.() -> Unit): TextField {
    TextFieldBuilder().apply(config).config(this)
    return this
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

inline fun TextArea.config(config: TextAreaBuilder.() -> Unit): TextArea {
    TextAreaBuilder().apply(config).config(this)
    return this
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

inline fun CheckBox.config(config: CheckBoxBuilder.() -> Unit): CheckBox {
    CheckBoxBuilder().apply(config).config(this)
    return this
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

inline fun radioButton(
    text: String,
    toggleGroup: ToggleGroup,
    config: RadioButtonBuilder.() -> Unit = {}
): RadioButton {
    return radioButtonBuilder {
        text(text)
        toggleGroup(toggleGroup)
        config()
    }.build()
}

inline fun radioButtonBuilder(config: RadioButtonBuilder.() -> Unit): RadioButtonBuilder {
    return RadioButtonBuilder().apply(config)
}

inline fun RadioButton.config(config: RadioButtonBuilder.() -> Unit): RadioButton {
    RadioButtonBuilder().apply(config).config(this)
    return this
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

inline fun <T> ComboBox<T>.config(config: ComboBoxBuilder<T>.() -> Unit): ComboBox<T> {
    ComboBoxBuilder<T>().apply(config).config(this)
    return this
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

inline fun <T> ListView<T>.config(config: ListViewBuilder<T>.() -> Unit): ListView<T> {
    ListViewBuilder<T>().apply(config).config(this)
    return this
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

inline fun <T> TableView<T>.config(config: TableViewBuilder<T>.() -> Unit): TableView<T> {
    TableViewBuilder<T>().apply(config).config(this)
    return this
}

// ProgressBar 衍生
inline fun progressBar(config: ProgressBarBuilder.() -> Unit = {}): ProgressBar {
    return progressBarBuilder(config).build()
}

inline fun progressBarBuilder(config: ProgressBarBuilder.() -> Unit): ProgressBarBuilder {
    return ProgressBarBuilder().apply(config)
}

inline fun ProgressBar.config(config: ProgressBarBuilder.() -> Unit): ProgressBar {
    ProgressBarBuilder().apply(config).config(this)
    return this
}

// Slider 衍生
inline fun slider(config: SliderBuilder.() -> Unit = {}): Slider {
    return sliderBuilder(config).build()
}

inline fun sliderBuilder(config: SliderBuilder.() -> Unit): SliderBuilder {
    return SliderBuilder().apply(config)
}

inline fun Slider.config(config: SliderBuilder.() -> Unit): Slider {
    SliderBuilder().apply(config).config(this)
    return this
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

inline fun ImageView.config(config: ImageViewBuilder.() -> Unit): ImageView {
    ImageViewBuilder().apply(config).config(this)
    return this
}

// Separator 衍生
inline fun separator(config: SeparatorBuilder.() -> Unit = {}): Separator {
    return separatorBuilder(config).build()
}

inline fun separatorBuilder(config: SeparatorBuilder.() -> Unit): SeparatorBuilder {
    return SeparatorBuilder().apply(config)
}

inline fun Separator.config(config: SeparatorBuilder.() -> Unit): Separator {
    SeparatorBuilder().apply(config).config(this)
    return this
}

// ContextMenu 衍生
inline fun contextMenu(config: ContextMenuBuilder.() -> Unit): ContextMenu {
    return contextMenuBuilder(config).build()
}

inline fun contextMenuBuilder(config: ContextMenuBuilder.() -> Unit): ContextMenuBuilder {
    return ContextMenuBuilder().apply(config)
}

inline fun ContextMenu.config(config: ContextMenuBuilder.() -> Unit): ContextMenu {
    ContextMenuBuilder().apply(config).config(this)
    return this
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

inline fun MenuItem.config(config: MenuItemBuilder.() -> Unit): MenuItem {
    MenuItemBuilder().apply(config).config(this)
    return this
}

// RadioMenuItem 衍生
inline fun radioMenuItem(config: RadioMenuItemBuilder.() -> Unit): RadioMenuItem {
    return radioMenuItemBuilder(config).build()
}

inline fun radioMenuItem(text: String, config: RadioMenuItemBuilder.() -> Unit = {}): RadioMenuItem {
    return radioMenuItemBuilder {
        text(text)
        config()
    }.build()
}

inline fun radioMenuItemBuilder(config: RadioMenuItemBuilder.() -> Unit): RadioMenuItemBuilder {
    return RadioMenuItemBuilder().apply(config)
}

inline fun RadioMenuItem.config(config: RadioMenuItemBuilder.() -> Unit): RadioMenuItem {
    RadioMenuItemBuilder().apply(config).config(this)
    return this
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

inline fun Menu.config(config: MenuBuilder.() -> Unit): Menu {
    MenuBuilder().apply(config).config(this)
    return this
}

// Style 衍生
inline fun stylesheetBuilder(config: StylesheetBuilder.() -> Unit): StylesheetBuilder {
    return StylesheetBuilder().apply(config)
}

inline fun Scene.configStylesheet(config: StylesheetBuilder.() -> Unit): Scene {
    this.stylesheets.add(StylesheetBuilder().apply(config).toDataUri())
    return this
}

inline fun styleBuilder(config: StyleBuilder.() -> Unit): StyleBuilder {
    return StyleBuilder().apply(config)
}

enum class StyleSize {
    TINY, SMALL, BIG, DEFAULT
}

enum class StyleColor {
    MAIN, NORMAL, SUCCESS, WARN, ERROR, DEFAULT
}

// Scene 衍生
inline fun scene(config: SceneBuilder.() -> Unit) = SceneBuilder().apply(config).build()

inline fun sceneBuilder(config: SceneBuilder.() -> Unit) = SceneBuilder().apply(config)

inline fun Scene.config(config: SceneBuilder.() -> Unit): Scene {
    SceneBuilder().apply(config).config(this)
    return this
}

// Stage 衍生
inline fun stage(config: StageBuilder.() -> Unit) = StageBuilder().apply(config).build()

inline fun stageBuilder(config: StageBuilder.() -> Unit) = StageBuilder().apply(config)

inline fun Stage.config(config: StageBuilder.() -> Unit): Stage {
    StageBuilder().apply(config).config(this)
    return this
}

abstract class DslBuilder<T>(
    buildMode: BuildMode = BuildMode.DELAY
) {

    var buildMode: BuildMode = buildMode
        private set

    private val builders: ArrayList<T.() -> Unit> = ArrayList()

    open fun style(styleColor: StyleColor = StyleColor.DEFAULT, styleSize: StyleSize = StyleSize.DEFAULT) {}

    abstract fun buildInstance(): T

    private var instanceInner: T? = null

    fun instance(): T {
        return instanceInner ?: buildInstance().apply { instanceInner = this }
    }

    fun immediateMode() {
        buildMode = BuildMode.IMMEDIATE
    }

    fun delayMode() {
        buildMode = BuildMode.DELAY
    }

    fun setMode(mode: BuildMode) {
        buildMode = mode
    }

    fun reserveSetting(minCapacity: Int) {
        builders.ensureCapacity(minCapacity)
    }

    open fun settings(settings: T.() -> Unit) {
        when (buildMode) {
            BuildMode.IMMEDIATE -> {
                instance().settings()
            }

            BuildMode.DELAY -> {
                builders.add(settings)
            }
        }
    }

    /**
     * 通过配置构建新的实例
     */
    open fun build(): T {
        val t = if (buildMode == BuildMode.IMMEDIATE) {
            instance()
        } else {
            instance().apply {
                builders.forEach { it() }
            }
        }
        instanceInner = null
        builders.clear()
        return t
    }

    /**
     * 将配置应用到传入的实例中
     */
    open fun config(t: T) {
        if (buildMode == BuildMode.DELAY) {
            t.apply {
                builders.forEach { it() }
            }
        }
    }

    open fun clear() {
        builders.clear()
        instanceInner = null
    }

    fun settingsIf(condition: Boolean, settings: T.() -> Unit) {
        if (condition) {
            settings(settings)
        }
    }

    enum class BuildMode {
        IMMEDIATE,  // 立即模式
        DELAY       // 延迟模式
    }
}