package club.xiaojiawei.kt.ext

import javafx.scene.Node
import javafx.scene.control.ChoiceBox
import javafx.scene.control.ComboBox
import javafx.scene.control.ContextMenu
import javafx.scene.control.ListView
import javafx.scene.control.Menu
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import javafx.scene.control.SplitPane
import javafx.scene.control.TableView
import javafx.scene.control.ToolBar
import javafx.scene.layout.Pane

/** 按传入顺序追加子节点，返回集合是否发生变化；不会清空已有节点。 */
fun Pane.addChildren(vararg nodes: Node): Boolean = children.addAll(*nodes)

fun Pane.addChildren(nodes: Collection<Node>): Boolean = children.addAll(nodes)

fun Pane.addChildren(index: Int, node: Node): Unit = children.add(index, node)

fun Pane.addChildren(index: Int, nodes: Collection<Node>): Boolean = children.addAll(index, nodes)

fun Pane.clearChildren(): Unit = children.clear()

/** 替换原集合中的全部子节点，保留集合实例及其监听器。 */
fun Pane.setAllChildren(vararg nodes: Node): Boolean = children.setAll(*nodes)

fun Pane.setAllChildren(nodes: Collection<Node>): Boolean = children.setAll(nodes)

fun <T> ComboBox<T>.addItem(item: T): Boolean = items.add(item)

fun <T> ComboBox<T>.addItems(vararg items: T): Boolean = this.items.addAll(*items)

fun <T> ComboBox<T>.addItems(items: Collection<T>): Boolean = this.items.addAll(items)

fun <T> ComboBox<T>.addItem(index: Int, item: T): Unit = items.add(index, item)

fun <T> ComboBox<T>.addItems(index: Int, items: Collection<T>): Boolean = this.items.addAll(index, items)

fun <T> ComboBox<T>.clearItems(): Unit = items.clear()

fun <T> ComboBox<T>.setAllItems(vararg items: T): Boolean = this.items.setAll(*items)

fun <T> ComboBox<T>.setAllItems(items: Collection<T>): Boolean = this.items.setAll(items)

fun <T> ChoiceBox<T>.addItem(item: T): Boolean = items.add(item)

fun <T> ChoiceBox<T>.addItems(vararg items: T): Boolean = this.items.addAll(*items)

fun <T> ChoiceBox<T>.addItems(items: Collection<T>): Boolean = this.items.addAll(items)

fun <T> ChoiceBox<T>.addItem(index: Int, item: T): Unit = items.add(index, item)

fun <T> ChoiceBox<T>.addItems(index: Int, items: Collection<T>): Boolean = this.items.addAll(index, items)

fun <T> ChoiceBox<T>.clearItems(): Unit = items.clear()

fun <T> ChoiceBox<T>.setAllItems(vararg items: T): Boolean = this.items.setAll(*items)

fun <T> ChoiceBox<T>.setAllItems(items: Collection<T>): Boolean = this.items.setAll(items)

fun <T> ListView<T>.addItem(item: T): Boolean = items.add(item)

fun <T> ListView<T>.addItems(vararg items: T): Boolean = this.items.addAll(*items)

fun <T> ListView<T>.addItems(items: Collection<T>): Boolean = this.items.addAll(items)

fun <T> ListView<T>.addItem(index: Int, item: T): Unit = items.add(index, item)

fun <T> ListView<T>.addItems(index: Int, items: Collection<T>): Boolean = this.items.addAll(index, items)

fun <T> ListView<T>.clearItems(): Unit = items.clear()

fun <T> ListView<T>.setAllItems(vararg items: T): Boolean = this.items.setAll(*items)

fun <T> ListView<T>.setAllItems(items: Collection<T>): Boolean = this.items.setAll(items)

fun <T> TableView<T>.addItem(item: T): Boolean = items.add(item)

fun <T> TableView<T>.addItems(vararg items: T): Boolean = this.items.addAll(*items)

fun <T> TableView<T>.addItems(items: Collection<T>): Boolean = this.items.addAll(items)

fun <T> TableView<T>.addItem(index: Int, item: T): Unit = items.add(index, item)

fun <T> TableView<T>.addItems(index: Int, items: Collection<T>): Boolean = this.items.addAll(index, items)

fun <T> TableView<T>.clearItems(): Unit = items.clear()

fun <T> TableView<T>.setAllItems(vararg items: T): Boolean = this.items.setAll(*items)

fun <T> TableView<T>.setAllItems(items: Collection<T>): Boolean = this.items.setAll(items)

fun Menu.addItem(item: MenuItem): Boolean = items.add(item)

fun Menu.addItems(vararg items: MenuItem): Boolean = this.items.addAll(*items)

fun Menu.addItems(items: Collection<MenuItem>): Boolean = this.items.addAll(items)

fun Menu.addItem(index: Int, item: MenuItem): Unit = items.add(index, item)

fun Menu.addItems(index: Int, items: Collection<MenuItem>): Boolean = this.items.addAll(index, items)

fun Menu.clearItems(): Unit = items.clear()

fun Menu.setAllItems(vararg items: MenuItem): Boolean = this.items.setAll(*items)

fun Menu.setAllItems(items: Collection<MenuItem>): Boolean = this.items.setAll(items)

fun ContextMenu.addItem(item: MenuItem): Boolean = items.add(item)

fun ContextMenu.addItems(vararg items: MenuItem): Boolean = this.items.addAll(*items)

fun ContextMenu.addItems(items: Collection<MenuItem>): Boolean = this.items.addAll(items)

fun ContextMenu.addItem(index: Int, item: MenuItem): Unit = items.add(index, item)

fun ContextMenu.addItems(index: Int, items: Collection<MenuItem>): Boolean = this.items.addAll(index, items)

fun ContextMenu.clearItems(): Unit = items.clear()

fun ContextMenu.setAllItems(vararg items: MenuItem): Boolean = this.items.setAll(*items)

fun ContextMenu.setAllItems(items: Collection<MenuItem>): Boolean = this.items.setAll(items)

fun MenuButton.addItem(item: MenuItem): Boolean = items.add(item)

fun MenuButton.addItems(vararg items: MenuItem): Boolean = this.items.addAll(*items)

fun MenuButton.addItems(items: Collection<MenuItem>): Boolean = this.items.addAll(items)

fun MenuButton.addItem(index: Int, item: MenuItem): Unit = items.add(index, item)

fun MenuButton.addItems(index: Int, items: Collection<MenuItem>): Boolean = this.items.addAll(index, items)

fun MenuButton.clearItems(): Unit = items.clear()

fun MenuButton.setAllItems(vararg items: MenuItem): Boolean = this.items.setAll(*items)

fun MenuButton.setAllItems(items: Collection<MenuItem>): Boolean = this.items.setAll(items)

fun SplitPane.addItem(item: Node): Boolean = items.add(item)

fun SplitPane.addItems(vararg items: Node): Boolean = this.items.addAll(*items)

fun SplitPane.addItems(items: Collection<Node>): Boolean = this.items.addAll(items)

fun SplitPane.addItem(index: Int, item: Node): Unit = items.add(index, item)

fun SplitPane.addItems(index: Int, items: Collection<Node>): Boolean = this.items.addAll(index, items)

fun SplitPane.clearItems(): Unit = items.clear()

fun SplitPane.setAllItems(vararg items: Node): Boolean = this.items.setAll(*items)

fun SplitPane.setAllItems(items: Collection<Node>): Boolean = this.items.setAll(items)

fun ToolBar.addItem(item: Node): Boolean = items.add(item)

fun ToolBar.addItems(vararg items: Node): Boolean = this.items.addAll(*items)

fun ToolBar.addItems(items: Collection<Node>): Boolean = this.items.addAll(items)

fun ToolBar.addItem(index: Int, item: Node): Unit = items.add(index, item)

fun ToolBar.addItems(index: Int, items: Collection<Node>): Boolean = this.items.addAll(index, items)

fun ToolBar.clearItems(): Unit = items.clear()

fun ToolBar.setAllItems(vararg items: Node): Boolean = this.items.setAll(*items)

fun ToolBar.setAllItems(items: Collection<Node>): Boolean = this.items.setAll(items)
