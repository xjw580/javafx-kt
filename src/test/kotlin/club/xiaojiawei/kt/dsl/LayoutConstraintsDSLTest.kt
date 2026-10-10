package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.GridPane
import javafx.scene.layout.HBox
import javafx.scene.layout.Priority
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import kotlin.test.assertEquals
import kotlin.test.assertSame

class LayoutConstraintsDSLTest {
    @Test
    fun anchorConstraintShortcutsMatchNativeSettersAndOverrides() = onFxThread {
        val actual = Region().apply {
            anchorConstraints {
                all(1.0)
                horizontal(2.0)
                vertical(3.0)
                left(4.0)
                bottom(5.0)
            }
        }
        val expected = Region().apply {
            AnchorPane.setTopAnchor(this, 3.0)
            AnchorPane.setBottomAnchor(this, 5.0)
            AnchorPane.setLeftAnchor(this, 4.0)
            AnchorPane.setRightAnchor(this, 2.0)
        }
        assertAnchors(expected, actual)
    }

    @Test
    fun gridConstraintPropertiesMatchNativeSetters() = onFxThread {
        val actual = Region().apply {
            gridConstraints {
                columnIndex(2)
                rowIndex(3)
                columnSpan(4)
                rowSpan(5)
                hgrow(Priority.ALWAYS)
                vgrow(Priority.SOMETIMES)
                halignment(HPos.RIGHT)
                valignment(VPos.BOTTOM)
                margin(9.0)
                margin(1.0, 2.0, 3.0, 4.0)
                fillWidth(false)
                fillHeight(false)
            }
        }
        val expected = Region().apply {
            GridPane.setColumnIndex(this, 2)
            GridPane.setRowIndex(this, 3)
            GridPane.setColumnSpan(this, 4)
            GridPane.setRowSpan(this, 5)
            GridPane.setHgrow(this, Priority.ALWAYS)
            GridPane.setVgrow(this, Priority.SOMETIMES)
            GridPane.setHalignment(this, HPos.RIGHT)
            GridPane.setValignment(this, VPos.BOTTOM)
            GridPane.setMargin(this, Insets(9.0))
            GridPane.setMargin(this, Insets(1.0, 2.0, 3.0, 4.0))
            GridPane.setFillWidth(this, false)
            GridPane.setFillHeight(this, false)
        }
        assertGridConstraints(expected, actual)
    }

    @Test
    fun uniformMarginsMatchNativeBoxAndGridConstraints() = onFxThread {
        val actual = Region().apply {
            gridConstraints { margin(6.0) }
            boxConstraints { margin(7.0) }
        }
        val expected = Region().apply {
            GridPane.setMargin(this, Insets(6.0))
            HBox.setMargin(this, Insets(7.0))
            VBox.setMargin(this, Insets(7.0))
        }
        assertEquals(GridPane.getMargin(expected), GridPane.getMargin(actual))
        assertBoxConstraints(expected, actual)
    }

    @Test
    fun boxConstraintsApplyToBothNativeBoxTypes() = onFxThread {
        val actual = Region().apply {
            boxConstraints {
                margin(1.0, 2.0, 3.0, 4.0)
                hgrow(Priority.SOMETIMES)
                vgrow(Priority.ALWAYS)
            }
        }
        val expected = Region().apply {
            HBox.setMargin(this, Insets(1.0, 2.0, 3.0, 4.0))
            VBox.setMargin(this, Insets(1.0, 2.0, 3.0, 4.0))
            HBox.setHgrow(this, Priority.SOMETIMES)
            VBox.setVgrow(this, Priority.ALWAYS)
        }
        assertBoxConstraints(expected, actual)
    }

    @ParameterizedTest
    @EnumSource(BuildMode::class)
    fun addWithConstraintsPreservesNodeIdentityAndChildOrder(mode: BuildMode) = onFxThread {
        val anchored = Region()
        val gridded = Region()
        val boxed = Region()
        val actual = pane {
            setMode(mode)
            assertSame(anchored, addWithAnchor(anchored) { all(8.0) })
            assertSame(gridded, addWithGrid(gridded) {
                columnIndex(2)
                rowIndex(1)
                columnSpan(3)
                hgrow(Priority.ALWAYS)
            })
            assertSame(boxed, addWithBox(boxed) {
                margin(4.0)
                vgrow(Priority.SOMETIMES)
            })
        }
        val expectedAnchor = Region().apply {
            AnchorPane.setTopAnchor(this, 8.0)
            AnchorPane.setBottomAnchor(this, 8.0)
            AnchorPane.setLeftAnchor(this, 8.0)
            AnchorPane.setRightAnchor(this, 8.0)
        }
        val expectedGrid = Region().apply {
            GridPane.setColumnIndex(this, 2)
            GridPane.setRowIndex(this, 1)
            GridPane.setColumnSpan(this, 3)
            GridPane.setHgrow(this, Priority.ALWAYS)
        }
        val expectedBox = Region().apply {
            HBox.setMargin(this, Insets(4.0))
            VBox.setMargin(this, Insets(4.0))
            VBox.setVgrow(this, Priority.SOMETIMES)
        }
        assertEquals(listOf(anchored, gridded, boxed), actual.children.toList())
        assertAnchors(expectedAnchor, anchored)
        assertGridConstraints(expectedGrid, gridded)
        assertBoxConstraints(expectedBox, boxed)
    }

    @Test
    fun directNodeExtensionsMatchNativeStaticMethods() = onFxThread {
        val actual = Region().apply {
            setLeftAnchor(1.0)
            setRightAnchor(2.0)
            setTopAnchor(3.0)
            setBottomAnchor(4.0)
            setHgrow(Priority.SOMETIMES)
            setVgrow(Priority.NEVER)
        }
        val expected = Region().apply {
            AnchorPane.setLeftAnchor(this, 1.0)
            AnchorPane.setRightAnchor(this, 2.0)
            AnchorPane.setTopAnchor(this, 3.0)
            AnchorPane.setBottomAnchor(this, 4.0)
            HBox.setHgrow(this, Priority.SOMETIMES)
            VBox.setVgrow(this, Priority.NEVER)
        }
        assertAnchors(expected, actual)
        assertBoxConstraints(expected, actual)
        actual.setHgrowAlways()
        actual.setVgrowAlways(Priority.ALWAYS)
        HBox.setHgrow(expected, Priority.ALWAYS)
        VBox.setVgrow(expected, Priority.ALWAYS)
        assertBoxConstraints(expected, actual)
    }

    private fun assertAnchors(expected: Node, actual: Node) {
        assertEquals(AnchorPane.getTopAnchor(expected), AnchorPane.getTopAnchor(actual))
        assertEquals(AnchorPane.getBottomAnchor(expected), AnchorPane.getBottomAnchor(actual))
        assertEquals(AnchorPane.getLeftAnchor(expected), AnchorPane.getLeftAnchor(actual))
        assertEquals(AnchorPane.getRightAnchor(expected), AnchorPane.getRightAnchor(actual))
    }

    private fun assertGridConstraints(expected: Node, actual: Node) {
        assertEquals(GridPane.getColumnIndex(expected), GridPane.getColumnIndex(actual))
        assertEquals(GridPane.getRowIndex(expected), GridPane.getRowIndex(actual))
        assertEquals(GridPane.getColumnSpan(expected), GridPane.getColumnSpan(actual))
        assertEquals(GridPane.getRowSpan(expected), GridPane.getRowSpan(actual))
        assertEquals(GridPane.getHgrow(expected), GridPane.getHgrow(actual))
        assertEquals(GridPane.getVgrow(expected), GridPane.getVgrow(actual))
        assertEquals(GridPane.getHalignment(expected), GridPane.getHalignment(actual))
        assertEquals(GridPane.getValignment(expected), GridPane.getValignment(actual))
        assertEquals(GridPane.getMargin(expected), GridPane.getMargin(actual))
        assertEquals(GridPane.isFillWidth(expected), GridPane.isFillWidth(actual))
        assertEquals(GridPane.isFillHeight(expected), GridPane.isFillHeight(actual))
    }

    private fun assertBoxConstraints(expected: Node, actual: Node) {
        assertEquals(HBox.getMargin(expected), HBox.getMargin(actual))
        assertEquals(VBox.getMargin(expected), VBox.getMargin(actual))
        assertEquals(HBox.getHgrow(expected), HBox.getHgrow(actual))
        assertEquals(VBox.getVgrow(expected), VBox.getVgrow(actual))
    }
}
