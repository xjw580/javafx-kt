package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.FilterComboBox
import club.xiaojiawei.controls.NotificationManager
import club.xiaojiawei.controls.Title
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.control.*
import javafx.scene.input.DragEvent
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.text.Text


/**
 * @author 肖嘉威
 * @date 2025/8/14 14:52
 */

@FXMarker
abstract class LayoutBuilder<T : Pane> : DslBuilder<T>() {

    operator fun Node.unaryPlus() = add(this)

    // 添加子节点
    fun add(node: Node) {
        settings {
            children.add(node)
        }
    }

    fun add(node: NodeBuilder<*>) {
        settings {
            children.add(node.build())
        }
    }

    fun add(node: PaneBaseBuilder<*>) {
        settings {
            children.add(node.build())
        }
    }

    fun addAll(vararg nodes: Node) {
        settings {
            children.addAll(nodes)
        }
    }


    fun addAll(vararg nodes: NodeBuilder<*>) {
        settings {
            children.addAll(nodes.map { it.build() })
        }
    }

    fun size(width: Double = -1.0, height: Double = -1.0) {
        settings {
            prefWidth = width
            prefHeight = height
        }
    }

    fun minSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            minWidth = width
            minHeight = height
        }
    }

    fun maxSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            maxWidth = width
            maxHeight = height
        }
    }

    fun prefWidth(width: Double) {
        settings {
            prefWidth = width
        }
    }

    fun prefHeight(height: Double) {
        settings {
            prefHeight = height
        }
    }

    fun minWidth(width: Double) {
        settings {
            minWidth = width
        }
    }

    fun minHeight(height: Double) {
        settings {
            minHeight = height
        }
    }

    fun maxWidth(width: Double) {
        settings {
            maxWidth = width
        }
    }

    fun maxHeight(height: Double) {
        settings {
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

    fun styled(block: StyleBuilder.() -> Unit) {
        settings {
            this.styled(block)
        }
    }

}

@FXMarker
abstract class PaneBaseBuilder<T : Pane> : LayoutBuilder<T>() {

    fun hgrow(priority: Priority) = settings {
        HBox.setHgrow(this, priority)
    }

    fun vgrow(priority: Priority) = settings {
        VBox.setVgrow(this, priority)
    }

    inline fun addVBox(config: VBoxBuilder.() -> Unit) {
        add(VBoxBuilder().apply(config))
    }

    inline fun addHBox(config: HBoxBuilder.() -> Unit) {
        add(HBoxBuilder().apply(config))
    }

    inline fun addPane(config: PaneBuilder.() -> Unit) {
        add(PaneBuilder().apply(config))
    }

    inline fun addStackPane(config: StackPaneBuilder.() -> Unit) {
        add(StackPaneBuilder().apply(config))
    }


    fun addText(text: String = "") = add(Text(text))

    fun addText(config: TextBuilder.() -> Unit = {}) = add(TextBuilder().apply(config))

    fun addText(text: String, config: Text.() -> Unit = {}) = add(Text(text).apply(config))

    inline fun addPolygon(points: List<Double>, config: (Polygon.() -> Unit) = {}) {
        add(Polygon().apply {
            this.points.addAll(points)
        }.apply(config))
    }

    inline fun addPolygon(config: (PolygonBuilder.() -> Unit) = {}) {
        add(PolygonBuilder().apply(config))
    }

    inline fun addLabel(text: String, config: (Label.() -> Unit) = {}) {
        add(Label(text).apply(config))
    }

    inline fun addLabel(config: (LabelBuilder.() -> Unit) = {}) {
        add(LabelBuilder().apply(config))
    }

    inline fun addCircle(config: (CircleBuilder.() -> Unit) = {}) {
        add(CircleBuilder().apply(config))
    }

    inline fun addCircle(radius: Double, fill: Color, config: (Circle.() -> Unit) = {}) {
        add(Circle(radius, fill).apply(config))
    }

    inline fun addTitle(config: (Title.() -> Unit) = {}) {
        val title = Title()
        config.invoke(title)
        add(title)
    }

    inline fun addTitle(text: String, config: (Title.() -> Unit) = {}) {
        val title = Title(text)
        config.invoke(title)
        add(title)
    }

    inline fun addCheckBox(config: (CheckBoxBuilder.() -> Unit) = {}) {
        add(CheckBoxBuilder().apply {
            this.config()
        })
    }

    inline fun addCheckBox(text: String, config: CheckBoxBuilder.() -> Unit = {}) {
        add(CheckBoxBuilder().apply {
            text(text)
            this.config()
        })
    }

    inline fun addRadioButton(config: (RadioButtonBuilder.() -> Unit) = {}) {
        add(RadioButtonBuilder().apply {
            this.config()
        })
    }

    inline fun addRadioButton(text: String, config: RadioButtonBuilder.() -> Unit = {}) {
        add(RadioButtonBuilder().apply {
            text(text)
            this.config()
        })
    }

    fun addRadioButtons(toggleGroup: ToggleGroup, vararg configs: RadioButtonBuilder.() -> Unit) {
        for (config in configs) {
            add(RadioButtonBuilder().apply {
                toggleGroup(toggleGroup)
                this.config()
            })
        }
    }

    inline fun addRadioButton(text: String, toggleGroup: ToggleGroup, config: RadioButtonBuilder.() -> Unit = {}) {
        add(RadioButtonBuilder().apply {
            text(text)
            toggleGroup(toggleGroup)
            this.config()
        })
    }


    inline fun <T> addComboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(ComboBox<T>(FXCollections.observableArrayList(item)).apply(config))
    }

    inline fun <T> addComboBox(config: (ComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            ComboBoxBuilder<T>().apply {
                this.config()
            }
        )
    }

    inline fun <T> addFilterComboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(FilterComboBox<T>(FXCollections.observableArrayList(item)).apply(config))
    }

    inline fun <T> addFilterComboBox(config: (FilterComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            FilterComboBoxBuilder<T>().apply {
                this.config()
            }
        )
    }

    inline fun addButton(text: String, config: (Button.() -> Unit) = {}) {
        add(Button(text).apply(config))
    }

    inline fun addButton(config: (ButtonBuilder.() -> Unit) = {}) {
        add(
            ButtonBuilder().apply {
                this.config()
            }
        )
    }

    inline fun addTextField(config: (TextFieldBuilder.() -> Unit) = {}) {
        add(
            TextFieldBuilder().apply {
                this.config()
            }
        )
    }

    fun onMouseClicked(handler: EventHandler<MouseEvent>?) {
        settings { onMouseClicked = handler }
    }

    fun onMouseEntered(handler: EventHandler<MouseEvent>?) {
        settings { onMouseEntered = handler }
    }

    fun onMouseExited(handler: EventHandler<MouseEvent>?) {
        settings { onMouseExited = handler }
    }

    fun onDragDetected(handler: EventHandler<MouseEvent>?) {
        settings { onDragDetected = handler }
    }

    fun onDragDone(handler: EventHandler<DragEvent>?) {
        settings { onDragDone = handler }
    }

    fun onDragOver(handler: EventHandler<DragEvent>?) {
        settings { onDragOver = handler }
    }

    fun onDragExited(handler: EventHandler<DragEvent>?) {
        settings { onDragExited = handler }
    }

    fun onDragDropped(handler: EventHandler<DragEvent>?) {
        settings { onDragDropped = handler }
    }

    fun style(style: String) {
        settings { this.style = style }
    }

}

@FXMarker
class PaneBuilder : PaneBaseBuilder<Pane>() {

    override fun buildInstance(): Pane = Pane()
}

@FXMarker
sealed class BoxBuilder<T : Pane> : PaneBaseBuilder<T>() {

    abstract fun spacing(spacing: Double)

    abstract fun alignment(pos: Pos)

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


@FXMarker
class VBoxBuilder : BoxBuilder<VBox>() {
    override fun buildInstance(): VBox = VBox()

    fun fillWidth(fillWidth: Boolean = true) = settings { isFillWidth = fillWidth }

    override fun spacing(spacing: Double) = settings { this.spacing = spacing }

    override fun alignment(pos: Pos) {
        settings {
            this.alignmentProperty().set(pos)
        }
    }
}

@FXMarker
class HBoxBuilder : BoxBuilder<HBox>() {
    override fun buildInstance(): HBox = HBox()

    fun fillHeight(fillHeight: Boolean = true) = settings { isFillHeight = fillHeight }

    override fun spacing(spacing: Double) = settings { this.spacing = spacing }

    override fun alignment(pos: Pos) {
        settings {
            this.alignmentProperty().set(pos)
        }
    }
}


@FXMarker
class StackPaneBuilder : PaneBaseBuilder<StackPane>() {

    override fun buildInstance(): StackPane = StackPane()

    fun alignment(alignment: Pos) {
        settings { this.alignment = alignment }
    }

    fun alignCenter() = alignment(Pos.CENTER)
    fun alignTop() = alignment(Pos.TOP_CENTER)
    fun alignBottom() = alignment(Pos.BOTTOM_CENTER)

}

@FXMarker
class BorderPaneBuilder : PaneBaseBuilder<BorderPane>() {

    override fun buildInstance(): BorderPane = BorderPane()

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

    override fun buildInstance(): GridPane = GridPane()

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

// ======================== AnchorPane ========================

@FXMarker
class AnchorPaneBuilder : PaneBaseBuilder<AnchorPane>() {
    override fun buildInstance(): AnchorPane = AnchorPane()

    inline fun anchor(node: Node, block: AnchorConstraints.() -> Unit) {
        add(node)
        node.anchorConstraints(block)
    }

    inline fun anchorVBox(block: VBoxBuilder.() -> Unit, anchorBlock: AnchorConstraints.() -> Unit) {
        val vbox = VBoxBuilder().apply(block).build()
        anchor(vbox, anchorBlock)
    }

    inline fun anchorHBox(block: HBoxBuilder.() -> Unit, anchorBlock: AnchorConstraints.() -> Unit) {
        val hbox = HBoxBuilder().apply(block).build()
        anchor(hbox, anchorBlock)
    }
}

// ======================== FlowPane ========================

@FXMarker
class FlowPaneBuilder : PaneBaseBuilder<FlowPane>() {
    override fun buildInstance(): FlowPane = FlowPane()

    fun orientation(orientation: Orientation) = settings {
        this.orientation = orientation
    }

    fun hgap(gap: Double) = settings {
        hgap = gap
    }

    fun vgap(gap: Double) = settings {
        vgap = gap
    }

    fun gap(gap: Double) {
        hgap(gap)
        vgap(gap)
    }

    fun alignment(pos: javafx.geometry.Pos) = settings {
        alignment = pos
    }

    fun columnHalignment(halignment: javafx.geometry.HPos) = settings {
        columnHalignment = halignment
    }

    fun rowValignment(valignment: javafx.geometry.VPos) = settings {
        rowValignment = valignment
    }

    fun prefWrapLength(length: Double) = settings {
        prefWrapLength = length
    }
}

// ======================== TilePane ========================

@FXMarker
class TilePaneBuilder : PaneBaseBuilder<TilePane>() {
    override fun buildInstance(): TilePane = TilePane()

    fun orientation(orientation: Orientation) = settings {
        this.orientation = orientation
    }

    fun hgap(gap: Double) = settings {
        hgap = gap
    }

    fun vgap(gap: Double) = settings {
        vgap = gap
    }

    fun gap(gap: Double) {
        hgap(gap)
        vgap(gap)
    }

    fun alignment(pos: javafx.geometry.Pos) = settings {
        alignment = pos
    }

    fun tileAlignment(pos: javafx.geometry.Pos) = settings {
        tileAlignment = pos
    }

    fun prefRows(rows: Int) = settings {
        prefRows = rows
    }

    fun prefColumns(columns: Int) = settings {
        prefColumns = columns
    }

    fun prefTileWidth(width: Double) = settings {
        prefTileWidth = width
    }

    fun prefTileHeight(height: Double) = settings {
        prefTileHeight = height
    }
}

// ======================== ScrollPane ========================

@FXMarker
class ScrollPaneBuilder : DslBuilder<ScrollPane>() {
    override fun buildInstance(): ScrollPane = ScrollPane()

    fun content(node: Node) = settings {
        content = node
    }

    fun content(builder: NodeBuilder<*>) = settings {
        content = builder.build()
    }

    fun fitToWidth(fit: Boolean = true) = settings {
        isFitToWidth = fit
    }

    fun fitToHeight(fit: Boolean = true) = settings {
        isFitToHeight = fit
    }

    fun hbarPolicy(policy: ScrollPane.ScrollBarPolicy) = settings {
        hbarPolicy = policy
    }

    fun vbarPolicy(policy: ScrollPane.ScrollBarPolicy) = settings {
        vbarPolicy = policy
    }

    fun pannable(pannable: Boolean = true) = settings {
        isPannable = pannable
    }

    fun hvalue(value: Double) = settings {
        hvalue = value
    }

    fun vvalue(value: Double) = settings {
        vvalue = value
    }

    fun hmin(min: Double) = settings {
        hmin = min
    }

    fun hmax(max: Double) = settings {
        hmax = max
    }

    fun vmin(min: Double) = settings {
        vmin = min
    }

    fun vmax(max: Double) = settings {
        vmax = max
    }
}

// ======================== SplitPane ========================

@FXMarker
class SplitPaneBuilder : DslBuilder<SplitPane>() {
    override fun buildInstance(): SplitPane = SplitPane()

    fun orientation(orientation: Orientation) = settings {
        this.orientation = orientation
    }

    fun items(vararg nodes: Node) = settings {
        items.addAll(nodes)
    }

    fun dividerPosition(index: Int, position: Double) = settings {
        setDividerPosition(index, position)
    }

    fun dividerPositions(vararg positions: Double) = settings {
        setDividerPositions(*positions)
    }

    operator fun Node.unaryPlus() {
        settings { items.add(this@unaryPlus) }
    }
}

// ======================== TitledPane ========================

@FXMarker
class TitledPaneBuilder : DslBuilder<TitledPane>() {
    override fun buildInstance(): TitledPane = TitledPane()

    fun text(text: String) = settings {
        this.text = text
    }

    fun content(node: Node) = settings {
        content = node
    }

    fun content(builder: NodeBuilder<*>) = settings {
        content = builder.build()
    }

    fun expanded(expanded: Boolean = true) = settings {
        isExpanded = expanded
    }

    fun collapsible(collapsible: Boolean = true) = settings {
        isCollapsible = collapsible
    }

    fun animated(animated: Boolean = true) = settings {
        isAnimated = animated
    }

    fun graphic(node: Node) = settings {
        graphic = node
    }
}
