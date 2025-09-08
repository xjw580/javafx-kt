package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.FilterComboBox
import club.xiaojiawei.controls.Title
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.ComboBox
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.text.Text


/**
 * @author 肖嘉威
 * @date 2025/8/14 14:52
 */

@FXMarker
abstract class LayoutBuilder<T : Pane> : DslBuilder<T>() {

    protected val children: MutableList<Node> = mutableListOf()

    // 添加子节点
    fun add(node: Node) {
        children.add(node)
    }

    fun addAll(vararg nodes: Node) {
        children.addAll(nodes)
    }

    fun size(width: Double, height: Double) {
        settings {
            prefWidth = width
            prefHeight = height
        }
    }

    fun minSize(width: Double, height: Double) {
        settings {
            minWidth = width
            minHeight = height
        }
    }

    fun maxSize(width: Double, height: Double) {
        settings {
            maxWidth = width
            maxHeight = height
        }
    }

    fun padding(value: Double) {
        settings { padding = Insets(value) }
    }

    fun padding(top: Double = 0.0, right: Double = 0.0, bottom: Double = 0.0, left: Double = 0.0) {
        settings { padding = Insets(top, right, bottom, left) }
    }

    fun background(color: String) {
        settings {
            background = Background(BackgroundFill(Color.web(color), null, null))
        }
    }

    fun background(paint: Paint) {
        settings {
            background = Background(BackgroundFill(paint, null, null))
        }
    }

    fun border(color: String, width: Double = 1.0) {
        settings {
            border = Border(BorderStroke(Color.web(color), BorderStrokeStyle.SOLID, null, BorderWidths(width)))
        }
    }

    fun id(id: String) {
        settings { this.id = id }
    }

    fun styleClass(vararg classes: String) {
        settings { styleClass.addAll(classes) }
    }

    fun visible(visible: Boolean = true) {
        settings { isVisible = visible }
    }

    fun managed(managed: Boolean = true) {
        settings { isManaged = managed }
    }

    override fun build(): T {
        val pane = super.build()
        pane.children.addAll(children)
        return pane
    }
}

@FXMarker
abstract class PaneBaseBuilder<T : Pane> : LayoutBuilder<T>() {

    inline fun vbox(config: VBoxBuilder.() -> Unit) {
        add(VBoxBuilder().apply(config).build())
    }

    inline fun hbox(config: HBoxBuilder.() -> Unit) {
        add(HBoxBuilder().apply(config).build())
    }

    inline fun pane(config: PaneBuilder.() -> Unit) {
        add(PaneBuilder().apply(config).build())
    }

    fun text(text: String = "") = add(Text(text))

    fun text(config: TextBuilder.() -> Unit = {}) = add(TextBuilder().apply(config).build())

    fun text(text: String, config: Text.() -> Unit = {}) = add(Text(text).apply(config))

    operator fun String.unaryPlus() = text(this)

    inline fun label(text: String, config: (Label.() -> Unit) = {}) {
        add(Label(text).apply(config))
    }

    inline fun label(config: (LabelBuilder.() -> Unit) = {}) {
        add(LabelBuilder().apply(config).build())
    }

    inline fun title(config: (Title.() -> Unit) = {}) {
        val title = Title()
        config.invoke(title)
        add(title)
    }

    inline fun title(text: String, config: (Title.() -> Unit) = {}) {
        val title = Title(text)
        config.invoke(title)
        add(title)
    }

    inline fun checkBox(config: (CheckBoxBuilder.() -> Unit) = {}) {
        add(CheckBoxBuilder().apply {
            this.config()
        }.build())
    }

    inline fun checkBox(text: String, config: CheckBoxBuilder.() -> Unit = {}) {
        add(CheckBoxBuilder().apply {
            text(text)
            this.config()
        }.build())
    }

    inline fun radioButton(config: (RadioButtonBuilder.() -> Unit) = {}) {
        add(RadioButtonBuilder().apply {
            this.config()
        }.build())
    }

    inline fun radioButton(text: String, config: RadioButtonBuilder.() -> Unit = {}) {
        add(RadioButtonBuilder().apply {
            text(text)
            this.config()
        }.build())
    }


    inline fun <T> comboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(ComboBox<T>(FXCollections.observableArrayList(item)).apply(config))
    }

    inline fun <T> comboBox(config: (ComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            ComboBoxBuilder<T>().apply {
                this.config()
            }.build()
        )
    }

    inline fun <T> filterComboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(FilterComboBox<T>(FXCollections.observableArrayList(item)).apply(config))
    }

    inline fun <T> filterComboBox(config: (FilterComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            FilterComboBoxBuilder<T>().apply {
                this.config()
            }.build()
        )
    }

    inline fun button(text: String, config: (Button.() -> Unit) = {}) {
        add(Button(text).apply(config))
    }

    fun button(config: (ButtonBuilder.() -> Unit) = {}) {
        add(
            ButtonBuilder().apply {
                this.config()
            }.build()
        )
    }

    inline fun textField(config: (TextFieldBuilder.() -> Unit) = {}) {
        add(
            TextFieldBuilder().apply {
                this.config()
            }.build()
        )
    }
}

@FXMarker
class PaneBuilder : PaneBaseBuilder<Pane>() {

    override fun instance(): Pane = Pane()
}

@FXMarker
class VBoxBuilder : PaneBaseBuilder<VBox>() {

    override fun instance(): VBox = VBox()

    fun spacing(spacing: Double) = settings { this.spacing = spacing }

    fun alignment(alignment: Pos) = settings { this.alignment = alignment }

    fun fillWidth(fillWidth: Boolean = true) = settings { isFillWidth = fillWidth }

    fun alignCenter() = alignment(Pos.CENTER)
    fun alignTop() = alignment(Pos.TOP_CENTER)
    fun alignBottom() = alignment(Pos.BOTTOM_CENTER)
    fun alignLeft() = alignment(Pos.CENTER_LEFT)
    fun alignRight() = alignment(Pos.CENTER_RIGHT)

}

@FXMarker
class HBoxBuilder : PaneBaseBuilder<HBox>() {

    override fun instance(): HBox = HBox()

    fun spacing(spacing: Double) = settings { this.spacing = spacing }

    fun alignment(alignment: Pos) = settings { this.alignment = alignment }

    fun fillHeight(fillHeight: Boolean = true) = settings { isFillHeight = fillHeight }

    fun alignCenter() = alignment(Pos.CENTER)
    fun alignLeft() = alignment(Pos.CENTER_LEFT)
    fun alignRight() = alignment(Pos.CENTER_RIGHT)
    fun alignTop() = alignment(Pos.TOP_CENTER)
    fun alignBottom() = alignment(Pos.BOTTOM_CENTER)

}

@FXMarker
class StackPaneBuilder : PaneBaseBuilder<StackPane>() {

    override fun instance(): StackPane = StackPane()

    fun alignment(alignment: Pos) {
        settings { this.alignment = alignment }
    }

    fun alignCenter() = alignment(Pos.CENTER)
    fun alignTop() = alignment(Pos.TOP_CENTER)
    fun alignBottom() = alignment(Pos.BOTTOM_CENTER)

}

@FXMarker
class BorderPaneBuilder : PaneBaseBuilder<BorderPane>() {

    override fun instance(): BorderPane = BorderPane()

    fun topNode(node: Node) {
        settings { top = node }
    }

    fun bottomNode(node: Node) {
        settings { bottom = node }
    }

    fun leftNode(node: Node) {
        settings { left = node }
    }

    fun rightNode(node: Node) {
        settings { right = node }
    }

    fun centerNode(node: Node) {
        settings { center = node }
    }

}

@FXMarker
class GridPaneBuilder : PaneBaseBuilder<GridPane>() {

    override fun instance(): GridPane = GridPane()

    fun hgap(gap: Double) {
        settings { hgap = gap }
    }

    fun vgap(gap: Double) {
        settings { vgap = gap }
    }

    fun gap(gap: Double) {
        hgap(gap)
        vgap(gap)
    }

    fun alignment(alignment: Pos) {
        settings { this.alignment = alignment }
    }

    fun add(node: Node, col: Int = 0, row: Int = 0, colspan: Int = 1, rowspan: Int = 1) {
        settings {
            add(node, col, row, colspan, rowspan)
        }
    }

    inline fun cell(col: Int = 0, row: Int = 0, colspan: Int = 1, rowspan: Int = 1, config: CellBuilder.() -> Unit) {
        val cellBuilder = CellBuilder(col, row, colspan, rowspan)
        cellBuilder.config()
        settings {
            add(cellBuilder.node, col, row, colspan, rowspan)
        }
    }

    @FXMarker
    class CellBuilder(
        private val col: Int,
        private val row: Int,
        private val colspan: Int,
        private val rowspan: Int
    ) {
        lateinit var node: Node

        inline fun vbox(config: VBoxBuilder.() -> Unit) {
            node = VBoxBuilder().apply(config).build()
        }

        inline fun hbox(config: HBoxBuilder.() -> Unit) {
            node = HBoxBuilder().apply(config).build()
        }

        inline fun label(text: String, config: (Label.() -> Unit) = {}) {
            node = Label(text).apply(config)
        }

        inline fun button(text: String, config: (Button.() -> Unit) = {}) {
            node = Button(text).apply(config)
        }

        inline fun textField(config: (TextField.() -> Unit) = {}) {
            node = TextField().apply(config)
        }

        fun custom(node: Node) {
            this.node = node
        }
    }
}