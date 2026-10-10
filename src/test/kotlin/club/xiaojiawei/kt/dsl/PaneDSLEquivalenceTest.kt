package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.ScrollPane
import javafx.scene.control.SplitPane
import javafx.scene.control.TitledPane
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.Background
import javafx.scene.layout.BackgroundFill
import javafx.scene.layout.Border
import javafx.scene.layout.BorderPane
import javafx.scene.layout.BorderStroke
import javafx.scene.layout.BorderStrokeStyle
import javafx.scene.layout.BorderWidths
import javafx.scene.layout.ColumnConstraints
import javafx.scene.layout.FlowPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.RowConstraints
import javafx.scene.layout.StackPane
import javafx.scene.layout.TilePane
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals
import kotlin.test.assertSame

class PaneDSLEquivalenceTest {
    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun regionDimensionsMatchNativeSetters(mode: BuildMode) = onFxThread {
        val actual = RegionBuilder().apply {
            setMode(mode)
            fixedSize(120.0, 80.0)
            minSize(20.0, 10.0)
            maxSize(240.0, 160.0)
            padding(1.0, 2.0, 3.0, 4.0)
        }.build()
        val expected = Region().apply {
            setMinSize(120.0, 80.0)
            setPrefSize(120.0, 80.0)
            setMaxSize(120.0, 80.0)
            setMinSize(20.0, 10.0)
            setMaxSize(240.0, 160.0)
            padding = Insets(1.0, 2.0, 3.0, 4.0)
        }
        assertRegion(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun panePreservesChildOrderAndDecoration(mode: BuildMode) = onFxThread {
        val actual = pane {
            setMode(mode)
            background("#123456")
            border("#abcdef", 2.0)
            add(null as Node?)
            add { null }
            +Label("first")
            add(LabelBuilder().apply { text("second") })
            addAll(Label("third"), Label("fourth"))
            addAll(LabelBuilder().apply { text("fifth") })
        }
        val expected = Pane().apply {
            background = Background(BackgroundFill(Color.web("#123456"), null, null))
            border = Border(BorderStroke(Color.web("#abcdef"), BorderStrokeStyle.SOLID, null, BorderWidths(2.0)))
            children.addAll((listOf("first", "second", "third", "fourth", "fifth")).map(::Label))
        }
        assertEquals(expected.background, actual.background)
        assertEquals(expected.border, actual.border)
        assertChildren(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun nestedBoxesMatchNativeStructureAndLayout(mode: BuildMode) = onFxThread {
        val actual = vbox {
            setMode(mode)
            spacing(7.0)
            alignment(Pos.BOTTOM_RIGHT)
            fillWidth(false)
            padding(5.0)
            addHBox {
                spacing(3.0)
                alignment(Pos.CENTER_LEFT)
                fillHeight(false)
                addRegion { id = "first"; setPrefSize(30.0, 20.0) }
                addRegion { id = "second"; setPrefSize(40.0, 25.0) }
            }
            addRegion { id = "third"; setPrefSize(50.0, 15.0) }
        }
        val expected = VBox().apply {
            spacing = 7.0
            alignment = Pos.BOTTOM_RIGHT
            isFillWidth = false
            padding = Insets(5.0)
            children.add(HBox().apply {
                spacing = 3.0
                alignment = Pos.CENTER_LEFT
                isFillHeight = false
                children.addAll(
                    Region().apply { id = "first"; setPrefSize(30.0, 20.0) },
                    Region().apply { id = "second"; setPrefSize(40.0, 25.0) }
                )
            })
            children.add(Region().apply { id = "third"; setPrefSize(50.0, 15.0) })
        }
        assertEquals(expected.spacing, actual.spacing)
        assertEquals(expected.alignment, actual.alignment)
        assertEquals(expected.isFillWidth, actual.isFillWidth)
        assertRegion(expected, actual)
        assertChildren(expected, actual)
        val expectedRow = expected.children.first() as HBox
        val actualRow = actual.children.first() as HBox
        assertEquals(expectedRow.spacing, actualRow.spacing)
        assertEquals(expectedRow.alignment, actualRow.alignment)
        assertEquals(expectedRow.isFillHeight, actualRow.isFillHeight)
        expected.resize(200.0, 150.0)
        actual.resize(200.0, 150.0)
        expected.layout()
        actual.layout()
        expected.children.zip(actual.children).forEach { (native, dsl) ->
            assertEquals(native.boundsInParent, dsl.boundsInParent)
        }
        expectedRow.children.zip(actualRow.children).forEach { (native, dsl) ->
            assertEquals(native.boundsInParent, dsl.boundsInParent)
        }
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun stackAndBorderSlotsMatchNativeContainers(mode: BuildMode) = onFxThread {
        val actual = borderPane {
            setMode(mode)
            topNode(Label("top"))
            bottomNode(Label("bottom"))
            leftNode(Label("left"))
            rightNode(Label("right"))
            centerNode(stackPane {
                setMode(mode)
                alignment(Pos.TOP_RIGHT)
                addLabel("back")
                addLabel("front")
            })
        }
        val expected = BorderPane().apply {
            top = Label("top")
            bottom = Label("bottom")
            left = Label("left")
            right = Label("right")
            center = StackPane().apply {
                alignment = Pos.TOP_RIGHT
                children.addAll(Label("back"), Label("front"))
            }
        }
        assertChildren(expected, actual)
        listOf(
            expected.top to actual.top, expected.bottom to actual.bottom,
            expected.left to actual.left, expected.right to actual.right
        ).forEach { (native, dsl) ->
            assertEquals((native as Label).text, (dsl as Label).text)
        }
        assertEquals((expected.center as StackPane).alignment, (actual.center as StackPane).alignment)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun gridRowsCellsAndConstraintsMatchNativeCalls(mode: BuildMode) = onFxThread {
        val actual = gridPane {
            setMode(mode)
            hgap(4.0)
            vgap(6.0)
            alignment(Pos.BOTTOM_LEFT)
            columnConstraints {
                column(
                    width = 100.0, percent = 60.0, hgrow = Priority.ALWAYS,
                    halignment = HPos.RIGHT, fillWidth = false
                )
                column { minWidth = 20.0; maxWidth = 200.0 }
            }
            rowConstraints {
                row(
                    height = 40.0, percent = 70.0, vgrow = Priority.SOMETIMES,
                    valignment = VPos.BOTTOM, fillHeight = false
                )
                row { minHeight = 10.0; maxHeight = 90.0 }
            }
            row {
                cell(colSpan = 2) { Label("wide") }
                skipCell()
                cellBuilder(rowSpan = 2) {
                    itemLabel("configured")
                    hgrow(Priority.ALWAYS)
                    vgrow(Priority.SOMETIMES)
                    margin(Insets(2.0))
                    halignment(HPos.RIGHT)
                    valignment(VPos.BOTTOM)
                }
            }
            skipRow()
            row { cell { Label("last") } }
        }
        val expected = GridPane().apply {
            hgap = 4.0
            vgap = 6.0
            alignment = Pos.BOTTOM_LEFT
            columnConstraints.addAll(
                ColumnConstraints().apply {
                    prefWidth = 100.0; percentWidth = 60.0; hgrow = Priority.ALWAYS
                    halignment = HPos.RIGHT; isFillWidth = false
                },
                ColumnConstraints().apply { minWidth = 20.0; maxWidth = 200.0 }
            )
            rowConstraints.addAll(
                RowConstraints().apply {
                    prefHeight = 40.0; percentHeight = 70.0; vgrow = Priority.SOMETIMES
                    valignment = VPos.BOTTOM; isFillHeight = false
                },
                RowConstraints().apply { minHeight = 10.0; maxHeight = 90.0 }
            )
            add(Label("wide"), 0, 0, 2, 1)
            add(Label("configured").apply {
                GridPane.setHgrow(this, Priority.ALWAYS)
                GridPane.setVgrow(this, Priority.SOMETIMES)
                GridPane.setMargin(this, Insets(2.0))
                GridPane.setHalignment(this, HPos.RIGHT)
                GridPane.setValignment(this, VPos.BOTTOM)
            }, 3, 0, 1, 2)
            add(Label("last"), 0, 2, 1, 1)
        }
        assertEquals(expected.hgap, actual.hgap)
        assertEquals(expected.vgap, actual.vgap)
        assertEquals(expected.alignment, actual.alignment)
        assertChildren(expected, actual)
        expected.children.zip(actual.children).forEach { (native, dsl) ->
            assertEquals(GridPane.getColumnIndex(native), GridPane.getColumnIndex(dsl))
            assertEquals(GridPane.getRowIndex(native), GridPane.getRowIndex(dsl))
            assertEquals(GridPane.getColumnSpan(native), GridPane.getColumnSpan(dsl))
            assertEquals(GridPane.getRowSpan(native), GridPane.getRowSpan(dsl))
            assertEquals(GridPane.getHgrow(native), GridPane.getHgrow(dsl))
            assertEquals(GridPane.getVgrow(native), GridPane.getVgrow(dsl))
            assertEquals(GridPane.getMargin(native), GridPane.getMargin(dsl))
            assertEquals(GridPane.getHalignment(native), GridPane.getHalignment(dsl))
            assertEquals(GridPane.getValignment(native), GridPane.getValignment(dsl))
        }
        assertEquals(expected.columnConstraints.size, actual.columnConstraints.size)
        expected.columnConstraints.zip(actual.columnConstraints).forEach { (native, dsl) ->
            assertEquals(native.minWidth, dsl.minWidth)
            assertEquals(native.prefWidth, dsl.prefWidth)
            assertEquals(native.maxWidth, dsl.maxWidth)
            assertEquals(native.percentWidth, dsl.percentWidth)
            assertEquals(native.hgrow, dsl.hgrow)
            assertEquals(native.halignment, dsl.halignment)
            assertEquals(native.isFillWidth, dsl.isFillWidth)
        }
        assertEquals(expected.rowConstraints.size, actual.rowConstraints.size)
        expected.rowConstraints.zip(actual.rowConstraints).forEach { (native, dsl) ->
            assertEquals(native.minHeight, dsl.minHeight)
            assertEquals(native.prefHeight, dsl.prefHeight)
            assertEquals(native.maxHeight, dsl.maxHeight)
            assertEquals(native.percentHeight, dsl.percentHeight)
            assertEquals(native.vgrow, dsl.vgrow)
            assertEquals(native.valignment, dsl.valignment)
            assertEquals(native.isFillHeight, dsl.isFillHeight)
        }
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun anchorNestedBoxesMatchNativeAnchors(mode: BuildMode) = onFxThread {
        val actual = anchorPane {
            setMode(mode)
            anchorVBox({ spacing(4.0); addLabel("vertical") }, { all(8.0) })
            anchorHBox({ spacing(6.0); addLabel("horizontal") }, { top(2.0); right(3.0) })
        }
        val expected = AnchorPane().apply {
            children.addAll(
                VBox(4.0, Label("vertical")).apply {
                    AnchorPane.setTopAnchor(this, 8.0); AnchorPane.setRightAnchor(this, 8.0)
                    AnchorPane.setBottomAnchor(this, 8.0); AnchorPane.setLeftAnchor(this, 8.0)
                },
                HBox(6.0, Label("horizontal")).apply {
                    AnchorPane.setTopAnchor(this, 2.0); AnchorPane.setRightAnchor(this, 3.0)
                }
            )
        }
        assertChildren(expected, actual)
        assertEquals((expected.children[0] as VBox).spacing, (actual.children[0] as VBox).spacing)
        assertEquals((expected.children[1] as HBox).spacing, (actual.children[1] as HBox).spacing)
        expected.children.zip(actual.children).forEach { (native, dsl) ->
            assertEquals(AnchorPane.getTopAnchor(native), AnchorPane.getTopAnchor(dsl))
            assertEquals(AnchorPane.getRightAnchor(native), AnchorPane.getRightAnchor(dsl))
            assertEquals(AnchorPane.getBottomAnchor(native), AnchorPane.getBottomAnchor(dsl))
            assertEquals(AnchorPane.getLeftAnchor(native), AnchorPane.getLeftAnchor(dsl))
        }
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun flowPaneMatchesNativeWrappingProperties(mode: BuildMode) = onFxThread {
        val actual = flowPane {
            setMode(mode)
            orientation(Orientation.VERTICAL)
            gap(5.0)
            vgap(9.0)
            alignment(Pos.BOTTOM_RIGHT)
            columnHalignment(HPos.RIGHT)
            rowValignment(VPos.BOTTOM)
            prefWrapLength(240.0)
            addLabel("one")
            addLabel("two")
        }
        val expected = FlowPane().apply {
            orientation = Orientation.VERTICAL
            hgap = 5.0; vgap = 9.0
            alignment = Pos.BOTTOM_RIGHT
            columnHalignment = HPos.RIGHT
            rowValignment = VPos.BOTTOM
            prefWrapLength = 240.0
            children.addAll(Label("one"), Label("two"))
        }
        assertEquals(expected.orientation, actual.orientation)
        assertEquals(expected.hgap, actual.hgap)
        assertEquals(expected.vgap, actual.vgap)
        assertEquals(expected.alignment, actual.alignment)
        assertEquals(expected.columnHalignment, actual.columnHalignment)
        assertEquals(expected.rowValignment, actual.rowValignment)
        assertEquals(expected.prefWrapLength, actual.prefWrapLength)
        assertChildren(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun tilePaneMatchesNativeTileProperties(mode: BuildMode) = onFxThread {
        val actual = tilePane {
            setMode(mode)
            orientation(Orientation.VERTICAL)
            gap(4.0)
            hgap(8.0)
            alignment(Pos.TOP_RIGHT)
            tileAlignment(Pos.BOTTOM_LEFT)
            prefRows(3)
            prefColumns(4)
            prefTileWidth(60.0)
            prefTileHeight(30.0)
            addLabel("one")
            addLabel("two")
        }
        val expected = TilePane().apply {
            orientation = Orientation.VERTICAL
            hgap = 8.0; vgap = 4.0
            alignment = Pos.TOP_RIGHT
            tileAlignment = Pos.BOTTOM_LEFT
            prefRows = 3; prefColumns = 4
            prefTileWidth = 60.0; prefTileHeight = 30.0
            children.addAll(Label("one"), Label("two"))
        }
        assertEquals(expected.orientation, actual.orientation)
        assertEquals(expected.hgap, actual.hgap)
        assertEquals(expected.vgap, actual.vgap)
        assertEquals(expected.alignment, actual.alignment)
        assertEquals(expected.tileAlignment, actual.tileAlignment)
        assertEquals(expected.prefRows, actual.prefRows)
        assertEquals(expected.prefColumns, actual.prefColumns)
        assertEquals(expected.prefTileWidth, actual.prefTileWidth)
        assertEquals(expected.prefTileHeight, actual.prefTileHeight)
        assertChildren(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun scrollPaneMatchesNativeContentAndRanges(mode: BuildMode) = onFxThread {
        val actual = scrollPane {
            setMode(mode)
            content { vbox { addLabel("content") } }
            fitToWidth()
            fitToHeight()
            pannable()
            hbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS)
            vbarPolicy(ScrollPane.ScrollBarPolicy.NEVER)
            hmin(-1.0); hmax(2.0); hvalue(0.4)
            vmin(-2.0); vmax(3.0); vvalue(0.6)
        }
        val expected = ScrollPane(VBox(Label("content"))).apply {
            isFitToWidth = true; isFitToHeight = true; isPannable = true
            hbarPolicy = ScrollPane.ScrollBarPolicy.ALWAYS
            vbarPolicy = ScrollPane.ScrollBarPolicy.NEVER
            hmin = -1.0; hmax = 2.0; hvalue = 0.4
            vmin = -2.0; vmax = 3.0; vvalue = 0.6
        }
        assertEquals(expected.isFitToWidth, actual.isFitToWidth)
        assertEquals(expected.isFitToHeight, actual.isFitToHeight)
        assertEquals(expected.isPannable, actual.isPannable)
        assertEquals(expected.hbarPolicy, actual.hbarPolicy)
        assertEquals(expected.vbarPolicy, actual.vbarPolicy)
        assertEquals(expected.hmin, actual.hmin)
        assertEquals(expected.hmax, actual.hmax)
        assertEquals(expected.hvalue, actual.hvalue)
        assertEquals(expected.vmin, actual.vmin)
        assertEquals(expected.vmax, actual.vmax)
        assertEquals(expected.vvalue, actual.vvalue)
        assertChildren(expected.content as VBox, actual.content as VBox)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun splitPaneMatchesNativeItemsAndDividerPositions(mode: BuildMode) = onFxThread {
        val actual = splitPane {
            setMode(mode)
            orientation(Orientation.VERTICAL)
            items(Label("first"), Label("second"))
            +Label("third")
            dividerPositions(0.25, 0.7)
            dividerPosition(1, 0.8)
        }
        val expected = SplitPane(Label("first"), Label("second"), Label("third")).apply {
            orientation = Orientation.VERTICAL
            setDividerPositions(0.25, 0.7)
            setDividerPosition(1, 0.8)
        }
        assertEquals(expected.orientation, actual.orientation)
        assertEquals(expected.items.map { (it as Label).text }, actual.items.map { (it as Label).text })
        assertEquals(expected.dividerPositions.toList(), actual.dividerPositions.toList())
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun titledPaneMatchesNativeContentAndExpansion(mode: BuildMode) = onFxThread {
        val actual = titledPane {
            setMode(mode)
            text("details")
            content(LabelBuilder().apply { text("body") })
            animated(false)
            expanded(false)
            collapsible(false)
        }
        val expected = TitledPane("details", Label("body")).apply {
            isAnimated = false
            isExpanded = false
            isCollapsible = false
        }
        assertEquals(expected.text, actual.text)
        assertEquals((expected.content as Label).text, (actual.content as Label).text)
        assertEquals(expected.isAnimated, actual.isAnimated)
        assertEquals(expected.isExpanded, actual.isExpanded)
        assertEquals(expected.isCollapsible, actual.isCollapsible)
    }

    @Test
    fun configUpdatesExistingContainerWithoutReplacingChildren() = onFxThread {
        val actual = VBox(Label("existing"))
        val result = actual.config {
            spacing(12.0)
            fillWidth(false)
            addHBox { addLabel("new") }
        }
        val expected = VBox(Label("existing")).apply {
            spacing = 12.0
            isFillWidth = false
            children.add(HBox(Label("new")))
        }
        assertSame(actual, result)
        assertEquals(expected.spacing, actual.spacing)
        assertEquals(expected.isFillWidth, actual.isFillWidth)
        assertChildren(expected, actual)
    }

    private fun assertRegion(expected: Region, actual: Region) {
        assertEquals(expected.minWidth, actual.minWidth)
        assertEquals(expected.minHeight, actual.minHeight)
        assertEquals(expected.prefWidth, actual.prefWidth)
        assertEquals(expected.prefHeight, actual.prefHeight)
        assertEquals(expected.maxWidth, actual.maxWidth)
        assertEquals(expected.maxHeight, actual.maxHeight)
        assertEquals(expected.padding, actual.padding)
    }

    private fun assertChildren(expected: Pane, actual: Pane) {
        assertEquals(expected.children.size, actual.children.size)
        expected.children.zip(actual.children).forEach { (native, dsl) ->
            assertEquals(native.javaClass, dsl.javaClass)
            assertEquals(native.id, dsl.id)
            if (native is Labeled && dsl is Labeled) assertEquals(native.text, dsl.text)
            if (native is Pane && dsl is Pane) assertChildren(native, dsl)
        }
    }
}
