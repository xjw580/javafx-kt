package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.FilterComboBox
import club.xiaojiawei.controls.Title
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.geometry.*
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
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * @author 肖嘉威
 * @date 2025/8/14 14:52
 */

@FXMarker
abstract class LayoutBuilder<T : Pane> : DslBuilder<T>() {

    operator fun Node.unaryPlus() = add(this)

    fun userData(data: Any) {
        settings {
            userData = data
        }
    }

    // 添加子节点
    fun add(node: Node) {
        settings {
            children.add(node)
        }
    }

    fun add(builder: () -> Node) = settings {
        children.add(builder())
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

    fun fixedSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            minWidth = width
            minHeight = height
            maxWidth = width
            maxHeight = height
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

    fun styled(styleBuilder: StyleBuilder) {
        settings {
            style = styleBuilder.build()
        }
    }

}

// ======================== Pane ========================

@FXMarker
abstract class PaneBaseBuilder<T : Pane> : LayoutBuilder<T>() {

    fun hgrow(priority: Priority) = settings {
        HBox.setHgrow(this, priority)
    }

    fun hgrowAlways() = settings {
        HBox.setHgrow(this, Priority.ALWAYS)
    }

    fun hgrowSometimes() = settings {
        HBox.setHgrow(this, Priority.SOMETIMES)
    }

    fun hgrowNever() = settings {
        HBox.setHgrow(this, Priority.NEVER)
    }

    fun vgrow(priority: Priority) = settings {
        VBox.setVgrow(this, priority)
    }

    fun vgrowAlways() = settings {
        VBox.setVgrow(this, Priority.ALWAYS)
    }

    fun vgrowSometimes() = settings {
        VBox.setVgrow(this, Priority.SOMETIMES)
    }

    fun vgrowNever() = settings {
        VBox.setVgrow(this, Priority.NEVER)
    }

    inline fun addVBox(config: VBoxBuilder.() -> Unit) {
        add(VBoxBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addHBox(config: HBoxBuilder.() -> Unit) {
        add(HBoxBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addPane(config: PaneBuilder.() -> Unit) {
        add(PaneBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addStackPane(config: StackPaneBuilder.() -> Unit) {
        add(StackPaneBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addGridPane(config: GridPaneBuilder.() -> Unit) {
        add(GridPaneBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addImageView(config: ImageViewBuilder.() -> Unit) {
        add(ImageViewBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }


    fun addText(text: String = "") = add(Text(text))

    fun addText(config: TextBuilder.() -> Unit = {}) = add(TextBuilder().apply {
        setMode(this@PaneBaseBuilder.buildMode)
        config()
    })

    fun addText(text: String, config: Text.() -> Unit = {}) = add(Text(text).apply {
        setMode(this@PaneBaseBuilder.buildMode)
        config()
    })

    inline fun addPolygon(points: List<Double>, config: (Polygon.() -> Unit) = {}) {
        add(Polygon().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            this.points.addAll(points)
        }.apply(config))
    }

    inline fun addPolygon(config: (PolygonBuilder.() -> Unit) = {}) {
        add(PolygonBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addLabel(text: String, config: (Label.() -> Unit) = {}) {
        add(Label(text).apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addLabel(config: (LabelBuilder.() -> Unit) = {}) {
        add(LabelBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addCircle(config: (CircleBuilder.() -> Unit) = {}) {
        add(CircleBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addCircle(radius: Double, fill: Color, config: (Circle.() -> Unit) = {}) {
        add(Circle(radius, fill).apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
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
            setMode(this@PaneBaseBuilder.buildMode)
            this.config()
        })
    }

    inline fun addCheckBox(text: String, config: CheckBoxBuilder.() -> Unit = {}) {
        add(CheckBoxBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            text(text)
            this.config()
        })
    }

    inline fun addRadioButton(config: (RadioButtonBuilder.() -> Unit) = {}) {
        add(RadioButtonBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            this.config()
        })
    }

    inline fun addRadioButton(text: String, config: RadioButtonBuilder.() -> Unit = {}) {
        add(RadioButtonBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            text(text)
            this.config()
        })
    }

    fun addRadioButtons(toggleGroup: ToggleGroup, vararg configs: RadioButtonBuilder.() -> Unit) {
        for (config in configs) {
            add(RadioButtonBuilder().apply {
                setMode(this@PaneBaseBuilder.buildMode)
                toggleGroup(toggleGroup)
                this.config()
            })
        }
    }

    inline fun addRadioButton(text: String, toggleGroup: ToggleGroup, config: RadioButtonBuilder.() -> Unit = {}) {
        add(RadioButtonBuilder().apply {
            setMode(this@PaneBaseBuilder.buildMode)
            text(text)
            toggleGroup(toggleGroup)
            this.config()
        })
    }


    inline fun <T> addComboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(ComboBox<T>(FXCollections.observableArrayList(item)).apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun <T> addComboBox(config: (ComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            ComboBoxBuilder<T>().apply {
                setMode(this@PaneBaseBuilder.buildMode)
                this.config()
            }
        )
    }

    inline fun <T> addFilterComboBox(item: List<T>, config: (ComboBox<T>.() -> Unit) = {}) {
        add(FilterComboBox<T>(FXCollections.observableArrayList(item)).apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun <T> addFilterComboBox(config: (FilterComboBoxBuilder<T>.() -> Unit) = {}) {
        add(
            FilterComboBoxBuilder<T>().apply {
                setMode(this@PaneBaseBuilder.buildMode)
                this.config()
            }
        )
    }

    inline fun addButton(text: String, config: (Button.() -> Unit) = {}) {
        add(Button(text).apply {
            setMode(this@PaneBaseBuilder.buildMode)
            config()
        })
    }

    inline fun addButton(config: (ButtonBuilder.() -> Unit) = {}) {
        add(
            ButtonBuilder().apply {
                setMode(this@PaneBaseBuilder.buildMode)
                this.config()
            }
        )
    }

    inline fun addTextField(config: (TextFieldBuilder.() -> Unit) = {}) {
        add(
            TextFieldBuilder().apply {
                setMode(this@PaneBaseBuilder.buildMode)
                this.config()
            }
        )
    }

    inline fun addSeparator(config: (SeparatorBuilder.() -> Unit) = {}) = add(SeparatorBuilder().apply {
        setMode(this@PaneBaseBuilder.buildMode)
        config()
    })

    fun onMouseReleased(handler: EventHandler<MouseEvent>?) {
        settings { onMouseReleased = handler }
    }

    fun onMousePressed(handler: EventHandler<MouseEvent>?) {
        settings { onMousePressed = handler }
    }

    fun onMouseDragged(handler: EventHandler<MouseEvent>?) {
        settings { onMouseDragged = handler }
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

// ======================== VBox ========================

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

// ======================== HBox ========================

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

// ======================== StackPane ========================

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

// ======================== BorderPane ========================

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

// ======================== GridPane ========================

@FXMarker
class GridPaneBuilder : PaneBaseBuilder<GridPane>() {
    override fun buildInstance(): GridPane = GridPane()

    // 用于追踪当前行号
    private var rowCounter = 0

    // --- 基础属性 ---
//    var hgap: Double by settingsProperty(0.0) { hgap = it }
//    var vgap: Double by settingsProperty(0.0) { vgap = it }

    fun hgap(gap: Double) = settings { hgap = gap }

    fun vgap(gap: Double) = settings { vgap = gap }


    fun gap(value: Double) = settings {
        hgap = value
        vgap = value
    }

    fun alignment(pos: Pos) = settings { alignment = pos }

    // --- 约束配置 ---
    fun columnConstraints(config: ColumnConstraintsBuilder.() -> Unit) {
        settings { columnConstraints.setAll(ColumnConstraintsBuilder().apply(config).constraints) }
    }

    fun rowConstraints(config: RowConstraintsBuilder.() -> Unit) {
        settings { rowConstraints.setAll(RowConstraintsBuilder().apply(config).constraints) }
    }

    // --- 行作用域 ---
    fun row(config: GridPaneRowScope.() -> Unit) {
        GridPaneRowScope(rowCounter++).apply(config)
    }

    fun <T : Node> add(
        node: T,
        col: Int = 0,
        row: Int = 0,
        colSpan: Int = 1,
        rowSpan: Int = 1,
        block: (GridPaneCellScope.() -> Unit)? = null
    ): T {
        settings { add(node, col, row, colSpan, rowSpan) }
        block?.let { GridPaneCellScope(node).apply(it) }
        return node
    }

    // --- 内部作用域类 ---

    /**
     * 行内作用域，自动管理列索引
     */
    inner class GridPaneRowScope(val rowIndex: Int) {
        var colCounter = 0

        inline fun cell(
            colSpan: Int = 1,
            rowSpan: Int = 1,
            builder: () -> Node
        ) {
            // 使用当前列计数器，并自动递增
            add(builder(), colCounter, rowIndex, colSpan, rowSpan)
            colCounter += colSpan
        }

        inline fun cellBuilder(
            colSpan: Int = 1,
            rowSpan: Int = 1,
            config: CellBuilder.() -> Unit
        ) {
            val cellBuilder = CellBuilder()
            cellBuilder.config()
            val node = cellBuilder.node ?: return

            // 使用当前列计数器，并自动递增
            add(node, colCounter, rowIndex, colSpan, rowSpan) {
                hgrow = cellBuilder.hgrow
                vgrow = cellBuilder.vgrow
                margin = cellBuilder.margin
                halignment = cellBuilder.halignment
                valignment = cellBuilder.valignment
            }
            colCounter += colSpan
        }

        // 跳过指定列数（占位）
        fun skip(count: Int = 1) {
            colCounter += count
        }
    }

    // 静态约束配置
    class GridPaneCellScope(private val node: Node) {
        var hgrow: Priority? by staticProperty(GridPane::setHgrow, node)
        var vgrow: Priority? by staticProperty(GridPane::setVgrow, node)
        var margin: Insets? by staticProperty(GridPane::setMargin, node)
        var halignment: HPos? by staticProperty(GridPane::setHalignment, node)
        var valignment: VPos? by staticProperty(GridPane::setValignment, node)
    }

    class CellBuilder {
        var node: Node? = null
        var hgrow: Priority? = null
        var vgrow: Priority? = null
        var margin: Insets? = null
        var halignment: HPos? = null
        var valignment: VPos? = null

        fun hgrow(hgrow: Priority?) {
            this.hgrow = hgrow
        }

        fun vgrow(vgrow: Priority?) {
            this.vgrow = vgrow
        }

        fun margin(margin: Insets?) {
            this.margin = margin
        }

        fun halignment(halignment: HPos?) {
            this.halignment = halignment
        }

        fun valignment(valignment: VPos?) {
            this.valignment = valignment
        }

        inline fun item(builder: () -> Node): Node {
            val node = builder()
            this.node = node
            return node
        }

        inline fun <T : Node> item(node: T, block: (T.() -> Unit) = {}): T {
            this.node = node
            node.block()
            return node
        }

        inline fun <T : NodeBuilder<*>> item(builder: T, block: (T.() -> Unit) = {}): Node {
            builder.block()
            val node = builder.build()
            this.node = node
            return node
        }

        inline fun itemLabel(text: String = "", block: LabelBuilder.() -> Unit = {}) {
            val item = LabelBuilder().apply {
                if (text.isNotEmpty()) {
                    +text
                }
                block()
            }.build()
            item(item)
        }

        inline fun itemButton(text: String = "", block: ButtonBuilder.() -> Unit = {}) {
            val item = ButtonBuilder().apply {
                if (text.isNotEmpty()) {
                    +text
                }
                block()
            }.build()
            item(item)
        }

        inline fun itemTextField(text: String = "", block: TextFieldBuilder.() -> Unit = {}) {
            val item = TextFieldBuilder().apply {
                if (text.isNotEmpty()) {
                    +text
                }
                block()
            }.build()
            item(item)
        }
    }
}

// --- 约束构建器实现 ---
@FXMarker
class ColumnConstraintsBuilder {
    val constraints = mutableListOf<ColumnConstraints>()

    inline fun column(config: ColumnConstraints.() -> Unit) {
        constraints.add(ColumnConstraints().apply { config() })
    }

    fun column(
        width: Double? = null,
        percent: Double? = null,
        hgrow: Priority? = null,
        halignment: HPos? = null,
        fillWidth: Boolean = true
    ) {
        val c = ColumnConstraints().apply {
            width?.let { prefWidth = it }
            percent?.let { percentWidth = it }
            hgrow?.let { this.hgrow = it }
            halignment?.let { this.halignment = it }
            isFillWidth = fillWidth
        }
        constraints.add(c)
    }
}

@FXMarker
class RowConstraintsBuilder {
    val constraints = mutableListOf<RowConstraints>()

    inline fun row(config: RowConstraints.() -> Unit) {
        constraints.add(RowConstraints().apply { config() })
    }

    fun row(
        height: Double? = null,
        percent: Double? = null,
        vgrow: Priority? = null,
        valignment: VPos? = null,
        fillHeight: Boolean = true
    ) {
        val r = RowConstraints().apply {
            height?.let { prefHeight = it }
            percent?.let { percentHeight = it }
            vgrow?.let { this.vgrow = it }
            valignment?.let { this.valignment = it }
            isFillHeight = fillHeight
        }
        constraints.add(r)
    }
}

// 用于处理组件自身的属性（如 hgap, vgap）
//fun <T> settingsProperty(initialValue: T, setter: GridPane.(T) -> Unit) =
//    object : ReadWriteProperty<GridPaneBuilder, T> {
//        private var field = initialValue
//        override fun getValue(thisRef: GridPaneBuilder, property: KProperty<*>): T = field
//        override fun setValue(thisRef: GridPaneBuilder, property: KProperty<*>, value: T) {
//            field = value
//            thisRef.settings { setter(this, value) }
//        }
//    }

// 用于处理 GridPane 的静态约束属性（如 hgrow, margin）
fun <T> staticProperty(setter: (Node, T?) -> Unit, node: Node) =
    object : ReadWriteProperty<Any, T?> {
        private var field: T? = null
        override fun getValue(thisRef: Any, property: KProperty<*>): T? = field
        override fun setValue(thisRef: Any, property: KProperty<*>, value: T?) {
            field = value
            setter(node, value)
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
        val vbox = VBoxBuilder().apply {
            setMode(this@AnchorPaneBuilder.buildMode)
            block(this)
        }.build()
        anchor(vbox, anchorBlock)
    }

    inline fun anchorHBox(block: HBoxBuilder.() -> Unit, anchorBlock: AnchorConstraints.() -> Unit) {
        val hbox = HBoxBuilder().apply {
            setMode(this@AnchorPaneBuilder.buildMode)
            block(this)
        }.build()
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

    fun graphic(builder: () -> Node) = settings { graphic = builder() }
}
