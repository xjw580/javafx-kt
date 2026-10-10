package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class MenuDSLTest {
    @Test
    fun menuItemMatchesNativePropertiesAndActionInBothModes() = onFxThread {
        for (mode in BuildMode.entries) {
            var expectedCalls = 0
            var actualCalls = 0
            val expected = MenuItem("_打开").apply {
                isMnemonicParsing = false
                onAction = EventHandler { expectedCalls++ }
            }
            val actual = menuItem {
                setMode(mode)
                text("_打开")
                mnemonicParsing(false)
                onAction(EventHandler<ActionEvent> { actualCalls++ })
            }
            assertMenuItemEquals(expected, actual)
            expected.fire()
            actual.fire()
            assertEquals(1, expectedCalls)
            assertEquals(expectedCalls, actualCalls)
            assertSame(actual, actual.config { text("更新"); onAction(null) })
            expected.text = "更新"
            expected.onAction = null
            assertMenuItemEquals(expected, actual)
            expected.fire()
            actual.fire()
            assertEquals(expectedCalls, actualCalls)
        }
    }

    @Test
    fun radioMenuItemListenerAdditionAndRemovalMatchNative() = onFxThread {
        val expectedChanges = mutableListOf<Pair<Boolean, Boolean>>()
        val actualChanges = mutableListOf<Pair<Boolean, Boolean>>()
        val expectedListener = ChangeListener<Boolean> { _, old, new -> expectedChanges.add(old to new) }
        val actualListener = ChangeListener<Boolean> { _, old, new -> actualChanges.add(old to new) }
        val expected = RadioMenuItem("模式").apply {
            isMnemonicParsing = false
            selectedProperty().addListener(expectedListener)
        }
        val actual = radioMenuItem("模式") {
            mnemonicParsing(false)
            addSelectedListener(actualListener)
        }
        assertMenuItemEquals(expected, actual)
        expected.isSelected = true
        actual.isSelected = true
        assertEquals(expected.isSelected, actual.isSelected)
        assertEquals(listOf(false to true), expectedChanges)
        assertEquals(expectedChanges, actualChanges)
        expected.selectedProperty().removeListener(expectedListener)
        assertSame(actual, actual.config(radioMenuItemConfig { removeSelectedListener(actualListener) }))
        expected.isSelected = false
        actual.isSelected = false
        assertEquals(expectedChanges, actualChanges)
    }

    @Test
    fun nestedMenuAndContextMenuMatchNativeOrderInBothModes() = onFxThread {
        for (mode in BuildMode.entries) {
            val expected = ContextMenu(
                MenuItem("打开"),
                Menu("选项", null, RadioMenuItem("自动"), Menu("更多", null, MenuItem("关于")))
            )
            val actual = contextMenu {
                setMode(mode)
                addMenuItem { +"打开" }
                addMenu {
                    +"选项"
                    addRadioMenuItem { +"自动" }
                    addMenu { +"更多"; addMenuItem { +"关于" } }
                }
            }
            assertEquals(expected.items.size, actual.items.size)
            expected.items.zip(actual.items).forEach { (e, a) -> assertMenuItemEquals(e, a) }
        }
    }

    @Test
    fun menuAndContextConfigAppendToExistingItems() = onFxThread {
        val expectedMenu = Menu("菜单", null, MenuItem("已有")).apply {
            items.add(MenuItem("新增"))
        }
        val actualMenu = Menu("旧菜单", null, MenuItem("已有"))
        assertSame(actualMenu, actualMenu.config(menuConfig {
            text("菜单")
            addMenuItem { text("新增") }
        }))
        assertMenuItemEquals(expectedMenu, actualMenu)
        val expectedContext = ContextMenu(MenuItem("已有"), expectedMenu)
        val actualContext = ContextMenu(MenuItem("已有"))
        assertSame(actualContext, actualContext.config {
            addMenu {
                text("菜单")
                addMenuItem { text("已有") }
                addMenuItem { text("新增") }
            }
        })
        expectedContext.items.zip(actualContext.items).forEach { (e, a) -> assertMenuItemEquals(e, a) }
        assertEquals(expectedContext.items.size, actualContext.items.size)
    }

    @Test
    fun delayedMenusBuildIndependentItemsAndClearRemovesProviders() = onFxThread {
        val builder = menuBuilder {
            delayMode()
            text("可复用")
            addMenuItem { text("子项") }
        }
        val first = builder.build()
        val second = builder.build()
        assertMenuItemEquals(Menu("可复用", null, MenuItem("子项")), first)
        assertMenuItemEquals(first, second)
        assertNotSame(first.items.single(), second.items.single())
        builder.clear()
        assertMenuItemEquals(Menu(), builder.build())

        val contextBuilder = contextMenuBuilder {
            delayMode()
            addMenuItem { text("子项") }
        }
        assertMenuItemEquals(MenuItem("子项"), contextBuilder.build().items.single())
        contextBuilder.clear()
        assertEquals(ContextMenu().items, contextBuilder.build().items)
    }

    private fun assertMenuItemEquals(expected: MenuItem, actual: MenuItem) {
        assertEquals(expected.javaClass, actual.javaClass)
        assertEquals(expected.text, actual.text)
        assertEquals(expected.isMnemonicParsing, actual.isMnemonicParsing)
        assertEquals(expected.styleClass, actual.styleClass)
        if (expected is Menu && actual is Menu) {
            assertEquals(expected.items.size, actual.items.size)
            expected.items.zip(actual.items).forEach { (e, a) -> assertMenuItemEquals(e, a) }
        }
    }
}
