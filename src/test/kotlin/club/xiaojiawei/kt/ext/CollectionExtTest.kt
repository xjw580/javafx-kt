package club.xiaojiawei.kt.ext

import javafx.application.Platform
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.scene.Scene
import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitMenuButton
import javafx.scene.control.SplitPane
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.Pane
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class CollectionExtTest {

    @Test
    fun childrenAppendInOrderAndRetainJavaFxValidation() = onFxThread {
        val pane = VBox()
        val first = Text("first")
        val second = Text("second")
        val third = Text("third")
        val fourth = Text("fourth")
        assertTrue(pane.addChildren(first))
        assertTrue(pane.addChildren(second, third))
        assertTrue(pane.addChildren(listOf(fourth)))
        assertFalse(pane.addChildren())
        assertFalse(pane.addChildren(emptyList()))
        assertEquals<List<Node>>(listOf(first, second, third, fourth), pane.children)
        pane.children.forEach { assertSame(pane, it.parent) }

        val scene = Scene(pane)
        pane.applyCss()
        pane.layout()
        assertSame(scene, fourth.scene)
        assertFailsWith<IllegalArgumentException> { pane.addChildren(first) }
        assertEquals<List<Node>>(listOf(first, second, third, fourth), pane.children)
    }

    @Test
    fun dataItemsAppendWithoutReplacingObservableList() = onFxThread {
        val values = FXCollections.observableArrayList("existing")
        val combo = ComboBox(values)
        var changes = 0
        values.addListener(ListChangeListener { changes++ })
        assertTrue(combo.addItem("single"))
        assertTrue(combo.addItems("one", "two"))
        assertTrue(combo.addItems(listOf("three")))
        assertFalse(combo.addItems())
        assertFalse(combo.addItems(emptyList()))
        assertSame(values, combo.items)
        assertEquals(listOf("existing", "single", "one", "two", "three"), values)
        assertEquals(3, changes)

        val choice = ChoiceBox<String>()
        choice.addItem("single")
        choice.addItems("one", "two")
        choice.addItems(listOf("three"))
        assertEquals(values.drop(1), choice.items)

        val choiceOriginalItems = choice.items
        choice.addItem(0, "front")
        assertTrue(choice.addItems(1, listOf("middle")))
        assertEquals(listOf("front", "middle"), choice.items.take(2))
        choice.setAllItems("replacement", "second")
        assertEquals(listOf("replacement", "second"), choice.items)
        choice.setAllItems(listOf("only"))
        assertEquals(listOf("only"), choice.items)
        choice.clearItems()
        assertTrue(choice.items.isEmpty())
        assertSame(choiceOriginalItems, choice.items)

        val list = ListView<String>()
        list.addItem("single")
        list.addItems("one", "two")
        list.addItems(listOf("three"))
        assertEquals(values.drop(1), list.items)

        val listOriginalItems = list.items
        list.addItem(0, "front")
        assertTrue(list.addItems(1, listOf("middle")))
        assertEquals(listOf("front", "middle"), list.items.take(2))
        list.setAllItems("replacement", "second")
        assertEquals(listOf("replacement", "second"), list.items)
        list.setAllItems(listOf("only"))
        assertEquals(listOf("only"), list.items)
        list.clearItems()
        assertTrue(list.items.isEmpty())
        assertSame(listOriginalItems, list.items)

        val table = TableView<String>()
        table.addItem("single")
        table.addItems("one", "two")
        table.addItems(listOf("three"))
        assertEquals(values.drop(1), table.items)

        val tableOriginalItems = table.items
        table.addItem(0, "front")
        assertTrue(table.addItems(1, listOf("middle")))
        assertEquals(listOf("front", "middle"), table.items.take(2))
        table.setAllItems("replacement", "second")
        assertEquals(listOf("replacement", "second"), table.items)
        table.setAllItems(listOf("only"))
        assertEquals(listOf("only"), table.items)
        table.clearItems()
        assertTrue(table.items.isEmpty())
        assertSame(tableOriginalItems, table.items)

        val originalItems = combo.items
        combo.addItem(0, "front")
        assertTrue(combo.addItems(1, listOf("middle")))
        assertEquals(listOf("front", "middle"), combo.items.take(2))
        combo.setAllItems("replacement", "second")
        assertEquals(listOf("replacement", "second"), combo.items)
        combo.setAllItems(listOf("only"))
        assertEquals(listOf("only"), combo.items)
        combo.clearItems()
        assertTrue(combo.items.isEmpty())
        assertSame(originalItems, combo.items)
    }

    @Test
    fun collectionValuedAndNullableItemsRemainSingleElements() = onFxThread {
        val list = ListView<List<String>>()
        val value = listOf("a", "b")
        list.addItem(value)
        list.addItems(listOf(value))
        assertEquals(listOf(value, value), list.items)

        val nullable = ComboBox<String?>()
        nullable.addItem(null)
        nullable.addItems("value", null)
        assertEquals(listOf(null, "value", null), nullable.items)
    }

    @Test
    fun menuItemsSupportMenusContextMenusAndMenuButtons() = onFxThread {
        val menu = Menu()
        val menuItems = List(4) { MenuItem("menu-$it") }
        menu.addItem(menuItems[0])
        menu.addItems(menuItems[1], menuItems[2])
        menu.addItems(menuItems.takeLast(1))
        assertFalse(menu.addItems(emptyList()))
        assertEquals(menuItems, menu.items)
        menuItems.forEach { assertSame(menu, it.parentMenu) }

        val menuOriginalItems = menu.items
        menu.addItem(0, MenuItem("front"))
        assertTrue(menu.addItems(1, listOf(MenuItem("middle"))))
        assertEquals(listOf("front", "middle"), menu.items.take(2).map { it.text })
        menu.setAllItems(MenuItem("replacement"), MenuItem("second"))
        assertEquals(listOf("replacement", "second"), menu.items.map { it.text })
        menu.setAllItems(listOf(MenuItem("only")))
        assertEquals(listOf("only"), menu.items.map { it.text })
        menu.clearItems()
        assertTrue(menu.items.isEmpty())
        assertSame(menuOriginalItems, menu.items)

        val context = ContextMenu()
        val contextItems = List(4) { MenuItem("context-$it") }
        context.addItem(contextItems[0])
        context.addItems(contextItems[1], contextItems[2])
        context.addItems(contextItems.takeLast(1))
        assertEquals(contextItems, context.items)
        contextItems.forEach { assertSame(context, it.parentPopup) }

        val contextOriginalItems = context.items
        context.addItem(0, MenuItem("front"))
        assertTrue(context.addItems(1, listOf(MenuItem("middle"))))
        assertEquals(listOf("front", "middle"), context.items.take(2).map { it.text })
        context.setAllItems(MenuItem("replacement"), MenuItem("second"))
        assertEquals(listOf("replacement", "second"), context.items.map { it.text })
        context.setAllItems(listOf(MenuItem("only")))
        assertEquals(listOf("only"), context.items.map { it.text })
        context.clearItems()
        assertTrue(context.items.isEmpty())
        assertSame(contextOriginalItems, context.items)

        for (button in listOf(MenuButton(), SplitMenuButton())) {
            val buttonItems = List(4) { MenuItem("button-$it") }
            button.addItem(buttonItems[0])
            button.addItems(buttonItems[1], buttonItems[2])
            button.addItems(buttonItems.takeLast(1))
            assertEquals(buttonItems, button.items)

            val buttonOriginalItems = button.items
            button.addItem(0, MenuItem("front"))
            assertTrue(button.addItems(1, listOf(MenuItem("middle"))))
            assertEquals(listOf("front", "middle"), button.items.take(2).map { it.text })
            button.setAllItems(MenuItem("replacement"), MenuItem("second"))
            assertEquals(listOf("replacement", "second"), button.items.map { it.text })
            button.setAllItems(listOf(MenuItem("only")))
            assertEquals(listOf("only"), button.items.map { it.text })
            button.clearItems()
            assertTrue(button.items.isEmpty())
            assertSame(buttonOriginalItems, button.items)
        }
    }

    @Test
    fun nodeItemsSupportSplitPaneAndToolBar() = onFxThread {
        val split = SplitPane()
        val splitItems = List(4) { Pane() }
        split.addItem(splitItems[0])
        split.addItems(splitItems[1], splitItems[2])
        split.addItems(splitItems.takeLast(1))
        assertFalse(split.addItems())
        assertEquals<List<Node>>(splitItems, split.items)

        val splitOriginalItems = split.items
        val splitFront = Pane()
        val splitMiddle = Pane()
        split.addItem(0, splitFront)
        assertTrue(split.addItems(1, listOf(splitMiddle)))
        assertEquals<List<Node>>(listOf(splitFront, splitMiddle), split.items.take(2))
        split.setAllItems(splitMiddle, splitFront)
        assertEquals<List<Node>>(listOf(splitMiddle, splitFront), split.items)
        split.setAllItems(listOf(splitFront))
        assertEquals<List<Node>>(listOf(splitFront), split.items)
        split.clearItems()
        assertTrue(split.items.isEmpty())
        assertSame(splitOriginalItems, split.items)

        val bar = ToolBar()
        val barItems = List(4) { Pane() }
        bar.addItem(barItems[0])
        bar.addItems(barItems[1], barItems[2])
        bar.addItems(barItems.takeLast(1))
        assertFalse(bar.addItems(emptyList()))
        assertEquals<List<Node>>(barItems, bar.items)

        val barOriginalItems = bar.items
        val barFront = Pane()
        val barMiddle = Pane()
        bar.addItem(0, barFront)
        assertTrue(bar.addItems(1, listOf(barMiddle)))
        assertEquals<List<Node>>(listOf(barFront, barMiddle), bar.items.take(2))
        bar.setAllItems(barMiddle, barFront)
        assertEquals<List<Node>>(listOf(barMiddle, barFront), bar.items)
        bar.setAllItems(listOf(barFront))
        assertEquals<List<Node>>(listOf(barFront), bar.items)
        bar.clearItems()
        assertTrue(bar.items.isEmpty())
        assertSame(barOriginalItems, bar.items)
    }

    @Test
    fun indexedChildrenReplacementAndClearUpdateParentRelationships() = onFxThread {
        val pane = VBox()
        val children = pane.children
        val first = Text("first")
        val last = Text("last")
        val middle = Text("middle")
        pane.addChildren(last)
        pane.addChildren(0, first)
        assertTrue(pane.addChildren(1, listOf(middle)))
        val tail = Text("tail")
        assertTrue(pane.addChildren(children.size, listOf(tail)))
        assertEquals<List<Node>>(listOf(first, middle, last, tail), children)
        assertFalse(pane.addChildren(children.size, emptyList()))
        assertFailsWith<IndexOutOfBoundsException> { pane.addChildren(-1, listOf(Text())) }
        assertFailsWith<IndexOutOfBoundsException> { pane.addChildren(children.size + 1, Text()) }
        assertEquals<List<Node>>(listOf(first, middle, last, tail), children)

        pane.setAllChildren(middle, first)
        assertEquals<List<Node>>(listOf(middle, first), children)
        assertNull(last.parent)
        assertNull(tail.parent)
        pane.setAllChildren(listOf(last))
        assertEquals<List<Node>>(listOf(last), children)
        assertSame(pane, last.parent)
        assertNull(first.parent)
        assertNull(middle.parent)
        pane.clearChildren()
        assertTrue(children.isEmpty())
        assertNull(last.parent)
        pane.clearChildren()
        assertSame(children, pane.children)
        pane.addChildren(first)
        pane.setAllChildren(emptyList())
        assertTrue(children.isEmpty())
        assertNull(first.parent)
    }

    @Test
    fun indexedItemsPreserveIntegerVarargsAndObservableListNotifications() = onFxThread {
        val list = ListView<Int>()
        val items = list.items
        list.addItems(1, 2, 3)
        assertEquals(listOf(1, 2, 3), items)
        assertTrue(list.addItems(items.size, listOf(4)))
        assertFalse(list.addItems(items.size, emptyList()))
        assertFailsWith<IndexOutOfBoundsException> { list.addItems(-1, listOf(5)) }
        assertFailsWith<IndexOutOfBoundsException> { list.addItem(items.size + 1, 5) }
        assertEquals(listOf(1, 2, 3, 4), items)

        var changes = 0
        items.addListener(ListChangeListener { changes++ })
        val replacements = FXCollections.observableArrayList(8, 9)
        list.setAllItems(replacements)
        assertEquals(listOf(8, 9), items)
        assertSame(items, list.items)
        assertEquals(1, changes)
        replacements.add(10)
        assertEquals(listOf(8, 9), items)
        list.setAllItems(emptyList())
        assertTrue(items.isEmpty())
        assertEquals(2, changes)
        list.clearItems()
        assertEquals(2, changes)
    }

    private fun onFxThread(action: () -> Unit) {
        val task = FutureTask(action)
        try {
            Platform.startup { Platform.setImplicitExit(false) }
        } catch (_: IllegalStateException) {
            // 其他测试已启动 JavaFX，复用同一个工具包。
        }
        Platform.runLater(task)
        task.get(10, TimeUnit.SECONDS)
    }
}
