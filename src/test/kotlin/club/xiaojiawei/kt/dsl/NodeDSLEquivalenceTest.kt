package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.FilterComboBox
import club.xiaojiawei.controls.Switch
import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleDoubleProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.geometry.Insets
import javafx.geometry.NodeOrientation
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Cursor
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ContentDisplay
import javafx.scene.control.ContextMenu
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Labeled
import javafx.scene.control.ListView
import javafx.scene.control.MenuItem
import javafx.scene.control.Pagination
import javafx.scene.control.ProgressBar
import javafx.scene.control.RadioButton
import javafx.scene.control.Separator
import javafx.scene.control.Slider
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import javafx.scene.control.ToggleGroup
import javafx.scene.control.Tooltip
import javafx.scene.image.ImageView
import javafx.scene.image.WritableImage
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.shape.Circle
import javafx.scene.shape.Polygon
import javafx.scene.shape.Shape
import javafx.scene.shape.StrokeLineCap
import javafx.scene.shape.StrokeLineJoin
import javafx.scene.shape.StrokeType
import javafx.scene.text.Font
import javafx.scene.text.Text
import javafx.util.Duration
import javafx.util.StringConverter
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NodeDSLEquivalenceTest {

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun labelIncludesAllInheritedNodeParentRegionControlAndLabeledSettings(mode: BuildMode) = onFxThread {
        val expected = Label("标题").apply {
            id = "title"
            styleClass.addAll("headline", "important")
            style = "-fx-opacity: 0.7;"
            isVisible = false
            isManaged = false
            isDisable = true
            opacity = 0.8
            rotate = 15.0
            scaleX = 1.2
            scaleY = 0.9
            translateX = 4.0
            translateY = 8.0
            isMouseTransparent = true
            accessibleText = "标题说明"
            cursor = Cursor.HAND
            userData = "metadata"
            isPickOnBounds = false
            nodeOrientation = NodeOrientation.RIGHT_TO_LEFT
            HBox.setHgrow(this, Priority.ALWAYS)
            VBox.setVgrow(this, Priority.SOMETIMES)
            stylesheets.add("data:text/css,.headline%7B-fx-opacity:0.7;%7D")
            setMinSize(50.0, 20.0)
            setPrefSize(180.0, 60.0)
            setMaxSize(300.0, 90.0)
            padding = Insets(1.0, 2.0, 3.0, 4.0)
            tooltip = Tooltip("帮助")
            contextMenu = ContextMenu(MenuItem("复制"))
            font = Font.font("System", 18.0)
            textFill = Color.DARKBLUE
            alignment = Pos.BOTTOM_RIGHT
            contentDisplay = ContentDisplay.RIGHT
            graphic = Circle(3.0)
            isWrapText = true
        }
        val actual = label {
            setMode(mode)
            text("标题")
            id("title")
            styleClass("headline", "important")
            style("-fx-opacity: 0.7;")
            visible(false)
            managed(false)
            disable()
            opacity(0.8)
            rotate(15.0)
            scale(1.2, 0.9)
            translate(4.0, 8.0)
            mouseTransparent(true)
            accessibleText("标题说明")
            cursorHand()
            userData("metadata")
            pickOnBounds(false)
            nodeOrientation(NodeOrientation.RIGHT_TO_LEFT)
            hgrowAlways()
            vgrowSometimes()
            stylesheets("data:text/css,.headline%7B-fx-opacity:0.7;%7D")
            minSize(50.0, 20.0)
            size(180.0, 60.0)
            maxSize(300.0, 90.0)
            padding(1.0, 2.0, 3.0, 4.0)
            toolTip("帮助")
            contextMenu(ContextMenu(MenuItem("复制")))
            font(Font.font("System", 18.0))
            textFill(Color.DARKBLUE)
            alignBottomRight()
            contentDisplay(ContentDisplay.RIGHT)
            graphic { Circle(3.0) }
            wrapText()
        }
        assertLabeledEquals(expected, actual)
        assertEquals(expected.isWrapText, actual.isWrapText)
        assertEquals((expected.graphic as Circle).radius, (actual.graphic as Circle).radius)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun buttonPropertiesAndFiredActionsMatchNative(mode: BuildMode) = onFxThread {
        val expectedEvents = mutableListOf<String>()
        val actualEvents = mutableListOf<String>()
        val expected = Button("执行").apply {
            isDefaultButton = true
            isCancelButton = true
            onAction = EventHandler { expectedEvents.add((it.source as Button).text) }
        }
        val actual = button {
            setMode(mode)
            text("执行")
            defaultButton()
            cancelButton()
            onAction { event: ActionEvent -> actualEvents.add((event.source as Button).text) }
        }
        assertLabeledEquals(expected, actual)
        assertEquals(expected.isDefaultButton, actual.isDefaultButton)
        assertEquals(expected.isCancelButton, actual.isCancelButton)
        repeat(2) { expected.fire(); actual.fire() }
        assertEquals(expectedEvents, actualEvents)
        assertEquals(2, actualEvents.size)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun checkBoxTriStateTransitionsAndActionsMatchNative(mode: BuildMode) = onFxThread {
        var expectedActions = 0
        var actualActions = 0
        val expected = CheckBox("选择").apply {
            isSelected = true
            isAllowIndeterminate = true
            isIndeterminate = true
            onAction = EventHandler { expectedActions++ }
        }
        val actual = checkBox {
            setMode(mode)
            text("选择")
            selected()
            allowIndeterminate()
            indeterminate()
            onAction { actualActions++ }
        }
        assertLabeledEquals(expected, actual)
        repeat(4) {
            assertEquals(expected.isSelected, actual.isSelected)
            assertEquals(expected.isIndeterminate, actual.isIndeterminate)
            assertEquals(expected.isAllowIndeterminate, actual.isAllowIndeterminate)
            expected.fire()
            actual.fire()
        }
        assertEquals(expectedActions, actualActions)
        assertEquals(4, actualActions)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun radioButtonsPreserveToggleGroupExclusivity(mode: BuildMode) = onFxThread {
        val expectedGroup = ToggleGroup()
        val actualGroup = ToggleGroup()
        val expected = listOf("甲", "乙").map { caption ->
            RadioButton(caption).apply { toggleGroup = expectedGroup; isSelected = true }
        }
        val actual = listOf("甲", "乙").map { caption ->
            radioButton {
                setMode(mode)
                text(caption)
                toggleGroup(actualGroup)
                selected()
            }
        }
        expected.zip(actual).forEach { (native, dsl) -> assertLabeledEquals(native, dsl) }
        assertEquals(expected.map { it.isSelected }, actual.map { it.isSelected })
        expected.first().fire()
        actual.first().fire()
        assertEquals(expected.map { it.isSelected }, actual.map { it.isSelected })
        assertSame(actual.first(), actualGroup.selectedToggle)
        assertEquals(expectedGroup.toggles.size, actualGroup.toggles.size)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun textFieldSettingsAndBidirectionalEditsMatchNative(mode: BuildMode) = onFxThread {
        val expectedState = SimpleStringProperty("初值")
        val actualState = SimpleStringProperty("初值")
        val expected = TextField().apply {
            promptText = "输入"
            isEditable = false
            font = Font.font(17.0)
            prefColumnCount = 24
            alignment = Pos.CENTER_RIGHT
            textProperty().bindBidirectional(expectedState)
        }
        val actual = textField {
            setMode(mode)
            promptText("输入")
            editable(false)
            fontSize(17.0)
            prefColumnCount(24)
            alignment(Pos.CENTER_RIGHT)
            bindBidirectionalText(actualState)
        }
        assertTextInputEquals(expected, actual)
        assertEquals(expected.prefColumnCount, actual.prefColumnCount)
        assertEquals(expected.alignment, actual.alignment)
        expectedState.set("模型更新")
        actualState.set("模型更新")
        assertEquals(expected.text, actual.text)
        expected.text = "控件更新"
        actual.text = "控件更新"
        assertEquals(expectedState.get(), actualState.get())
        assertEquals("控件更新", actualState.get())
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun textAreaMultilineContentAndLayoutMatchNative(mode: BuildMode) = onFxThread {
        val expected = TextArea("第一行\n第二行").apply {
            promptText = "多行输入"
            isEditable = false
            font = Font.font(15.0)
            isWrapText = true
            prefColumnCount = 32
            prefRowCount = 6
        }
        val actual = textArea {
            setMode(mode)
            text("第一行\n第二行")
            promptText("多行输入")
            editable(false)
            fontSize(15.0)
            wrapText()
            prefColumnCount(32)
            prefRowCount(6)
        }
        assertTextInputEquals(expected, actual)
        assertEquals(expected.isWrapText, actual.isWrapText)
        assertEquals(expected.prefColumnCount, actual.prefColumnCount)
        assertEquals(expected.prefRowCount, actual.prefRowCount)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun comboBoxItemsConverterAndSelectionMatchNative(mode: BuildMode) = onFxThread {
        val expected = ComboBox<Int>().apply {
            items.addAll(1, 2)
            items.add(3)
            value = 2
            promptText = "选择数字"
            isEditable = true
            visibleRowCount = 5
            converter = object : StringConverter<Int>() {
                override fun toString(value: Int?): String? = value?.let { "#$it" }
                override fun fromString(value: String?): Int? = value?.removePrefix("#")?.toInt()
            }
        }
        val actual = comboBox<Int> {
            setMode(mode)
            items(1, 2)
            items(listOf(3))
            value(2)
            promptText("选择数字")
            editable()
            visibleRowCount(5)
            converter(fromString = { it?.removePrefix("#")?.toInt() }, toString = { it?.let { value -> "#$value" } })
        }
        assertControlEquals(expected, actual)
        assertEquals(expected.items, actual.items)
        assertEquals(expected.value, actual.value)
        assertEquals(expected.promptText, actual.promptText)
        assertEquals(expected.isEditable, actual.isEditable)
        assertEquals(expected.visibleRowCount, actual.visibleRowCount)
        assertEquals(expected.converter.toString(3), actual.converter.toString(3))
        assertEquals(expected.converter.fromString("#9"), actual.converter.fromString("#9"))
        expected.selectionModel.select(0)
        actual.selectionModel.select(0)
        assertEquals(expected.value, actual.value)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun observableComboBoxItemsAndBoundValueStayLive(mode: BuildMode) = onFxThread {
        val expectedItems = FXCollections.observableArrayList("甲", "乙")
        val actualItems = FXCollections.observableArrayList("甲", "乙")
        val expectedValue = SimpleStringProperty("甲")
        val actualValue = SimpleStringProperty("甲")
        val expected = ComboBox(expectedItems).apply { valueProperty().bind(expectedValue) }
        val actual = comboBox<String> {
            setMode(mode)
            items("被替换")
            items(actualItems)
            bindValue(actualValue)
        }
        assertSame(actualItems, actual.items)
        expectedItems.add("丙")
        actualItems.add("丙")
        expectedValue.set("丙")
        actualValue.set("丙")
        assertEquals(expected.items, actual.items)
        assertEquals(expected.value, actual.value)
        assertEquals(expected.valueProperty().isBound, actual.valueProperty().isBound)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun filterComboBoxMatchesDirectCustomControlConstruction(mode: BuildMode) = onFxThread {
        val expected = FilterComboBox<String>().apply {
            items.addAll("Alpha", "Beta")
            isIgnoreCase = false
            promptText = "筛选"
            visibleRowCount = 4
            value = "Beta"
        }
        val actual = FilterComboBoxBuilder<String>().apply {
            setMode(mode)
            items("Alpha", "Beta")
            ignoreCase(false)
            promptText("筛选")
            visibleRowCount(4)
            value("Beta")
        }.build()
        assertControlEquals(expected, actual)
        assertEquals(expected.items, actual.items)
        assertEquals(expected.isIgnoreCase, actual.isIgnoreCase)
        assertEquals(expected.promptText, actual.promptText)
        assertEquals(expected.visibleRowCount, actual.visibleRowCount)
        assertEquals(expected.value, actual.value)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun listViewObservableItemsOrientationAndEditingMatchNative(mode: BuildMode) = onFxThread {
        val expectedItems = FXCollections.observableArrayList("甲", "乙")
        val actualItems = FXCollections.observableArrayList("甲", "乙")
        val expected = ListView(expectedItems).apply {
            orientation = Orientation.HORIZONTAL
            isEditable = true
        }
        val actual = listView<String> {
            setMode(mode)
            items("旧项")
            items(actualItems)
            orientation(Orientation.HORIZONTAL)
            editable()
        }
        assertControlEquals(expected, actual)
        assertSame(actualItems, actual.items)
        expectedItems.add("丙")
        actualItems.add("丙")
        assertEquals(expected.items, actual.items)
        assertEquals(expected.orientation, actual.orientation)
        assertEquals(expected.isEditable, actual.isEditable)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun tableViewColumnsProduceTheSameCellValues(mode: BuildMode) = onFxThread {
        val expected = TableView<String>().apply {
            items.addAll("alpha", "beta")
            isEditable = true
            columns.add(TableColumn<String, String>("名称").apply {
                minWidth = 30.0
                prefWidth = 100.0
                maxWidth = 200.0
                isSortable = false
                isResizable = false
                isReorderable = false
                isEditable = false
                sortType = TableColumn.SortType.DESCENDING
                setCellValueFactory { SimpleStringProperty(it.value.uppercase()) }
            })
        }
        val actual = tableView<String> {
            setMode(mode)
            items("alpha", "beta")
            editable()
            addColumn<String>("名称", { it.uppercase() }) {
                minWidth(30.0)
                prefWidth(100.0)
                maxWidth(200.0)
                sortable(false)
                resizable(false)
                reorderable(false)
                editable(false)
                sortType(TableColumn.SortType.DESCENDING)
            }
        }
        assertControlEquals(expected, actual)
        assertEquals(expected.items, actual.items)
        assertEquals(expected.isEditable, actual.isEditable)
        assertEquals(expected.columns.size, actual.columns.size)
        val expectedColumn = expected.columns.single()
        val actualColumn = actual.columns.single()
        assertEquals(expectedColumn.text, actualColumn.text)
        assertEquals(expectedColumn.minWidth, actualColumn.minWidth)
        assertEquals(expectedColumn.prefWidth, actualColumn.prefWidth)
        assertEquals(expectedColumn.maxWidth, actualColumn.maxWidth)
        assertEquals(expectedColumn.isSortable, actualColumn.isSortable)
        assertEquals(expectedColumn.isResizable, actualColumn.isResizable)
        assertEquals(expectedColumn.isReorderable, actualColumn.isReorderable)
        assertEquals(expectedColumn.isEditable, actualColumn.isEditable)
        assertEquals(expectedColumn.sortType, actualColumn.sortType)
        expected.items.indices.forEach { assertEquals(expectedColumn.getCellData(it), actualColumn.getCellData(it)) }
        assertEquals(listOf("ALPHA", "BETA"), actual.items.indices.map { actualColumn.getCellData(it) })
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun tableColumnObservableCellFactoryReflectsModelChanges(mode: BuildMode) = onFxThread {
        val expectedModel = SimpleStringProperty("原值")
        val actualModel = SimpleStringProperty("原值")
        val expected = TableColumn<SimpleStringProperty, String>("属性").apply {
            setCellValueFactory { it.value }
        }
        val actual = tableColumnBuilder<SimpleStringProperty, String> {
            setMode(mode)
            text("属性")
            cellValueFactory { it }
        }.build()
        val expectedTable = TableView(FXCollections.observableArrayList(expectedModel)).apply { columns.add(expected) }
        val actualTable = TableView(FXCollections.observableArrayList(actualModel)).apply { columns.add(actual) }
        val expectedValue = expectedTable.columns.single().getCellObservableValue(0)
        val actualValue = actualTable.columns.single().getCellObservableValue(0)
        assertSame(actualModel, actualValue)
        assertEquals(expected.text, actual.text)
        expectedModel.set("新值")
        actualModel.set("新值")
        assertEquals(expectedValue.value, actualValue.value)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun progressBarBindingAndIndeterminateModeMatchNative(mode: BuildMode) = onFxThread {
        val expectedState = SimpleDoubleProperty(0.25)
        val actualState = SimpleDoubleProperty(0.25)
        val expected = ProgressBar().apply { progressProperty().bind(expectedState) }
        val actual = progressBar { setMode(mode); bindProgress(actualState) }
        assertControlEquals(expected, actual)
        listOf(0.0, 0.75, ProgressBar.INDETERMINATE_PROGRESS).forEach { value ->
            expectedState.set(value)
            actualState.set(value)
            assertEquals(expected.progress, actual.progress)
            assertEquals(expected.isIndeterminate, actual.isIndeterminate)
        }
        val nativeIndeterminate = ProgressBar(ProgressBar.INDETERMINATE_PROGRESS)
        val dslIndeterminate = progressBar { setMode(mode); indeterminate() }
        assertEquals(nativeIndeterminate.progress, dslIndeterminate.progress)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun sliderRangeTicksAndOutOfRangeValuesMatchNative(mode: BuildMode) = onFxThread {
        val expected = Slider().apply {
            min = -10.0
            max = 20.0
            value = 5.0
            orientation = Orientation.VERTICAL
            isShowTickLabels = true
            isShowTickMarks = true
            majorTickUnit = 5.0
            isSnapToTicks = true
        }
        val actual = slider {
            setMode(mode)
            range(-10.0, 20.0)
            value(5.0)
            orientation(Orientation.VERTICAL)
            showTickLabels()
            showTickMarks()
            majorTickUnit(5.0)
            snapToTicks()
        }
        assertControlEquals(expected, actual)
        assertEquals(expected.min, actual.min)
        assertEquals(expected.max, actual.max)
        assertEquals(expected.value, actual.value)
        assertEquals(expected.orientation, actual.orientation)
        assertEquals(expected.isShowTickLabels, actual.isShowTickLabels)
        assertEquals(expected.isShowTickMarks, actual.isShowTickMarks)
        assertEquals(expected.majorTickUnit, actual.majorTickUnit)
        assertEquals(expected.isSnapToTicks, actual.isSnapToTicks)
        expected.value = 100.0
        actual.config { value(100.0) }
        assertEquals(expected.value, actual.value)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun imageViewImageSizingAndLiveBindingMatchNative(mode: BuildMode) = onFxThread {
        val image = WritableImage(2, 3)
        val expectedWidth = SimpleDoubleProperty(24.0)
        val actualWidth = SimpleDoubleProperty(24.0)
        val expected = ImageView(image).apply {
            fitWidth = 24.0
            fitHeight = 36.0
            isPreserveRatio = true
            isSmooth = false
            fitWidthProperty().bind(expectedWidth)
        }
        val actual = imageView {
            setMode(mode)
            image(image)
            fitSize(24.0, 36.0)
            preserveRatio()
            smooth(false)
            bindFitWidth(actualWidth)
        }
        assertNodeEquals(expected, actual)
        assertSame(expected.image, actual.image)
        assertEquals(expected.fitHeight, actual.fitHeight)
        assertEquals(expected.isPreserveRatio, actual.isPreserveRatio)
        assertEquals(expected.isSmooth, actual.isSmooth)
        expectedWidth.set(48.0)
        actualWidth.set(48.0)
        assertEquals(expected.fitWidth, actual.fitWidth)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun separatorOrientationAndPaginationBoundsMatchNative(mode: BuildMode) = onFxThread {
        val expectedSeparator = Separator(Orientation.VERTICAL)
        val actualSeparator = separator { setMode(mode); vertical() }
        assertControlEquals(expectedSeparator, actualSeparator)
        assertEquals(expectedSeparator.orientation, actualSeparator.orientation)
        val expectedPages = Pagination().apply {
            pageCount = 12
            currentPageIndex = 3
            maxPageIndicatorCount = 5
        }
        val actualPages = pagination {
            setMode(mode)
            pageCount(12)
            currentPageIndex(3)
            maxPageIndicatorCount(5)
        }
        assertControlEquals(expectedPages, actualPages)
        assertEquals(expectedPages.pageCount, actualPages.pageCount)
        assertEquals(expectedPages.currentPageIndex, actualPages.currentPageIndex)
        assertEquals(expectedPages.maxPageIndicatorCount, actualPages.maxPageIndicatorCount)
        expectedPages.currentPageIndex = 100
        actualPages.config { currentPageIndex(100) }
        assertEquals(expectedPages.currentPageIndex, actualPages.currentPageIndex)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun circleAndPolygonShareNativeShapeStrokeSemantics(mode: BuildMode) = onFxThread {
        val expected = Circle(12.0).apply {
            fill = Color.CORNFLOWERBLUE
            stroke = Color.DARKBLUE
            strokeWidth = 2.5
            strokeType = StrokeType.OUTSIDE
            strokeLineJoin = StrokeLineJoin.BEVEL
            strokeLineCap = StrokeLineCap.ROUND
            strokeDashArray.setAll(3.0, 7.0)
            strokeDashOffset = 1.5
            strokeMiterLimit = 4.0
            isSmooth = false
        }
        val actual = CircleBuilder().apply {
            setMode(mode)
            radius(12.0)
            fill(Color.CORNFLOWERBLUE)
            stroke(Color.DARKBLUE)
            strokeWidth(2.5)
            strokeType(StrokeType.OUTSIDE)
            strokeLineJoin(StrokeLineJoin.BEVEL)
            strokeLineCap(StrokeLineCap.ROUND)
            strokeDashArray(3.0, 7.0)
            strokeDashOffset(1.5)
            strokeMiterLimit(4.0)
            smooth(false)
        }.build()
        assertShapeEquals(expected, actual)
        assertEquals(expected.radius, actual.radius)
        val expectedPolygon = Polygon().apply {
            points.addAll(9.0, 9.0)
            points.clear()
            points.addAll(0.0, 0.0, 10.0, 0.0)
            points.addAll(5.0, 8.0)
            fill = Color.ORANGE
            strokeDashArray.setAll(2.0, 4.0)
            strokeDashArray.clear()
        }
        val actualPolygon = polygon {
            setMode(mode)
            point(9.0, 9.0)
            clearPoint()
            points(listOf(0.0, 0.0, 10.0, 0.0))
            points(5.0, 8.0)
            fill(Color.ORANGE)
            strokeDashArray(2.0, 4.0)
            strokeDashArray()
        }
        assertShapeEquals(expectedPolygon, actualPolygon)
        assertEquals(expectedPolygon.points, actualPolygon.points)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun textAndVisibilityBindingsTrackNativeChanges(mode: BuildMode) = onFxThread {
        val expectedText = SimpleStringProperty("原文")
        val actualText = SimpleStringProperty("原文")
        val expectedVisible = SimpleBooleanProperty(true)
        val actualVisible = SimpleBooleanProperty(true)
        val expected = Text().apply {
            textProperty().bind(expectedText)
            visibleProperty().bind(expectedVisible)
            managedProperty().bind(expectedVisible)
            fill = Color.PURPLE
        }
        val actual = text {
            setMode(mode)
            bindText(actualText)
            bindVisibleAndManaged(actualVisible)
            fill(Color.PURPLE)
        }
        assertShapeEquals(expected, actual)
        assertEquals(expected.text, actual.text)
        expectedText.set("更新")
        actualText.set("更新")
        expectedVisible.set(false)
        actualVisible.set(false)
        assertEquals(expected.text, actual.text)
        assertEquals(expected.isVisible, actual.isVisible)
        assertEquals(expected.isManaged, actual.isManaged)
        assertTrue(actual.textProperty().isBound)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun switchMatchesDirectCustomControlConstruction(mode: BuildMode) = onFxThread {
        val expected = Switch().apply {
            transitionDuration = Duration.ZERO
            status = false
            size = 32.0
            isLoading = false
            padding = Insets(3.0)
        }
        val actual = switch {
            setMode(mode)
            transitionDuration(Duration.ZERO)
            status(false)
            size(32.0)
            loading(false)
            padding(3.0)
        }
        assertRegionEquals(expected, actual)
        assertEquals(expected.status, actual.status)
        assertEquals(expected.size, actual.size)
        assertEquals(expected.isLoading, actual.isLoading)
        assertEquals(expected.transitionDuration, actual.transitionDuration)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun regionAndSpacersHaveNativeSizingAndConstraints(mode: BuildMode) = onFxThread {
        val expected = Region().apply { setMinSize(40.0, 20.0); setPrefSize(40.0, 20.0); setMaxSize(40.0, 20.0) }
        val actual = region { setMode(mode); fixedSize(40.0, 20.0) }
        assertRegionEquals(expected, actual)
        val expectedHorizontal = Region().apply { HBox.setHgrow(this, Priority.SOMETIMES) }
        val expectedVertical = Region().apply { VBox.setVgrow(this, Priority.ALWAYS) }
        assertRegionEquals(expectedHorizontal, hSpacer(Priority.SOMETIMES))
        assertRegionEquals(expectedVertical, vSpacer())
    }

    private fun assertNodeEquals(expected: Node, actual: Node) {
        assertEquals(expected.javaClass, actual.javaClass)
        assertEquals(expected.id, actual.id)
        assertEquals(expected.styleClass, actual.styleClass)
        assertEquals(expected.style, actual.style)
        assertEquals(expected.isVisible, actual.isVisible)
        assertEquals(expected.isManaged, actual.isManaged)
        assertEquals(expected.isDisable, actual.isDisable)
        assertEquals(expected.opacity, actual.opacity)
        assertEquals(expected.rotate, actual.rotate)
        assertEquals(expected.scaleX, actual.scaleX)
        assertEquals(expected.scaleY, actual.scaleY)
        assertEquals(expected.translateX, actual.translateX)
        assertEquals(expected.translateY, actual.translateY)
        assertEquals(expected.isMouseTransparent, actual.isMouseTransparent)
        assertEquals(expected.accessibleText, actual.accessibleText)
        assertEquals(expected.cursor, actual.cursor)
        assertEquals(expected.userData, actual.userData)
        assertEquals(expected.isPickOnBounds, actual.isPickOnBounds)
        assertEquals(expected.nodeOrientation, actual.nodeOrientation)
        assertEquals(HBox.getHgrow(expected), HBox.getHgrow(actual))
        assertEquals(VBox.getVgrow(expected), VBox.getVgrow(actual))
    }

    private fun assertRegionEquals(expected: Region, actual: Region) {
        assertNodeEquals(expected, actual)
        assertEquals(expected.stylesheets, actual.stylesheets)
        assertEquals(expected.minWidth, actual.minWidth)
        assertEquals(expected.minHeight, actual.minHeight)
        assertEquals(expected.prefWidth, actual.prefWidth)
        assertEquals(expected.prefHeight, actual.prefHeight)
        assertEquals(expected.maxWidth, actual.maxWidth)
        assertEquals(expected.maxHeight, actual.maxHeight)
        assertEquals(expected.padding, actual.padding)
    }

    private fun assertControlEquals(expected: Control, actual: Control) {
        assertRegionEquals(expected, actual)
        assertEquals(expected.tooltip?.text, actual.tooltip?.text)
        assertEquals(expected.contextMenu?.items?.map { it.text }, actual.contextMenu?.items?.map { it.text })
    }

    private fun assertLabeledEquals(expected: Labeled, actual: Labeled) {
        assertControlEquals(expected, actual)
        assertEquals(expected.text, actual.text)
        assertEquals(expected.font, actual.font)
        assertEquals(expected.textFill, actual.textFill)
        assertEquals(expected.alignment, actual.alignment)
        assertEquals(expected.contentDisplay, actual.contentDisplay)
        assertEquals(expected.graphic?.javaClass, actual.graphic?.javaClass)
    }

    private fun assertTextInputEquals(expected: TextInputControl, actual: TextInputControl) {
        assertControlEquals(expected, actual)
        assertEquals(expected.text, actual.text)
        assertEquals(expected.promptText, actual.promptText)
        assertEquals(expected.isEditable, actual.isEditable)
        assertEquals(expected.font, actual.font)
    }

    private fun assertShapeEquals(expected: Shape, actual: Shape) {
        assertNodeEquals(expected, actual)
        assertEquals(expected.fill, actual.fill)
        assertEquals(expected.stroke, actual.stroke)
        assertEquals(expected.strokeWidth, actual.strokeWidth)
        assertEquals(expected.strokeType, actual.strokeType)
        assertEquals(expected.strokeLineJoin, actual.strokeLineJoin)
        assertEquals(expected.strokeLineCap, actual.strokeLineCap)
        assertEquals(expected.strokeDashArray, actual.strokeDashArray)
        assertEquals(expected.strokeDashOffset, actual.strokeDashOffset)
        assertEquals(expected.strokeMiterLimit, actual.strokeMiterLimit)
        assertEquals(expected.isSmooth, actual.isSmooth)
    }
}
