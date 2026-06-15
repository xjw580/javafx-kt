package club.xiaojiawei.kt.dsl

import javafx.beans.property.SimpleStringProperty
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.shape.Rectangle
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class TableViewDSLTest {

    private data class Row(
        val id: String,
        val name: String,
        val role: String
    )

    @Test
    fun `tableColumn should configure column properties`() {
        val graphic = Rectangle(8.0, 8.0)
        val column = tableColumn<Row, String>("姓名") {
            prefWidth(160.0)
            minWidth(80.0)
            maxWidth(240.0)
            sortable(false)
            editable()
            resizable(false)
            reorderable(false)
            visible(false)
            style("-fx-alignment: CENTER;")
            styleClass("name-column", "center-column")
            graphic(graphic)
            comparator(compareBy { it.length })
            sortType(TableColumn.SortType.DESCENDING)
        }

        assertEquals("姓名", column.text)
        assertEquals(160.0, column.prefWidth)
        assertEquals(80.0, column.minWidth)
        assertEquals(240.0, column.maxWidth)
        assertFalse(column.isSortable)
        assertTrue(column.isEditable)
        assertFalse(column.isResizable)
        assertFalse(column.isReorderable)
        assertFalse(column.isVisible)
        assertEquals("-fx-alignment: CENTER;", column.style)
        assertEquals(listOf("name-column", "center-column"), column.styleClass)
        assertSame(graphic, column.graphic)
        assertEquals(TableColumn.SortType.DESCENDING, column.sortType)
        assertTrue(column.comparator.compare("aa", "b") > 0)
    }

    @Test
    fun `cellValue should read value from row`() {
        val column = tableColumn<Row, String>("ID") {
            cellValue { it.id }
        }

        assertEquals("1", column.getCellObservableValue(Row("1", "张三", "管理员")).value)
    }

    @Test
    fun `cellValueFactory should support observable value`() {
        val column = tableColumn<Row, String>("姓名") {
            cellValueFactory { SimpleStringProperty(it.name) }
        }

        assertEquals("李四", column.getCellObservableValue(Row("2", "李四", "普通用户")).value)
    }

    @Test
    fun `tableView should add columns`() {
        val tableView = tableView<Row> {
            addColumn<String>("ID") { cellValue { it.id } }
            addColumn("姓名", cellValue = { it.name }) {
                prefWidth(120.0)
            }
        }

        assertEquals(listOf("ID", "姓名"), tableView.columns.map { it.text })
        assertEquals("张三", tableView.columns[1].getCellObservableValue(Row("1", "张三", "管理员")).value)
        assertEquals(120.0, tableView.columns[1].prefWidth)
    }

    @Test
    fun `TableView config should configure existing instance`() {
        val tableView = TableView<Row>().config {
            addColumn<String>("ID") { cellValue { it.id } }
            addColumn<String>("角色") { cellValue { it.role } }
        }

        assertEquals(listOf("ID", "角色"), tableView.columns.map { it.text })
        assertEquals("普通用户", tableView.columns[1].getCellObservableValue(Row("3", "王五", "普通用户")).value)
    }

    @Test
    fun `TableColumnBuilder should add nested columns`() {
        val groupColumn = tableColumn<Row, String>("用户") {
            addColumn<String>("姓名") { cellValue { it.name } }
            addColumn("角色", cellValue = { it.role })
        }

        assertEquals("用户", groupColumn.text)
        assertEquals(listOf("姓名", "角色"), groupColumn.columns.map { it.text })
        assertEquals("管理员", groupColumn.columns[1].getCellObservableValue(Row("1", "赵六", "管理员")).value)
    }
}
