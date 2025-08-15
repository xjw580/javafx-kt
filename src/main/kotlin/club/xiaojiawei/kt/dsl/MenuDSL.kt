package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.ico.AbstractIco
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.layout.HBox

/**
 * @author 肖嘉威
 * @date 2025/8/14 8:22
 */

@FXMarker
class ContextMenuBuilder : DslBuilder<ContextMenu>() {

    override fun instance(): ContextMenu = ContextMenu()

    private val menuItemBuilders: MutableList<() -> MenuItem> = mutableListOf()

    fun menuItem(config: MenuItemBuilder.() -> Unit) {
        menuItemBuilders.add {
            MenuItemBuilder().apply { config() }.build()
        }
    }

    fun menu(config: MenuBuilder.() -> Unit) {
        menuItemBuilders.add {
            MenuBuilder().apply { config() }.build()
        }
    }

    override fun style(styleColor: StyleColor , styleSize: StyleSize) {
        settings {
            styleClass.add("context-menu-ui")
        }
    }

    override fun build(): ContextMenu {
        val contextMenu = super.build()
        applyMenuItems(contextMenu)
        return contextMenu
    }

    override fun config(t: ContextMenu) {
        super.config(t)
        applyMenuItems(t)
    }

    private fun applyMenuItems(contextMenu: ContextMenu) {
        contextMenu.items.addAll(
            menuItemBuilders.map { it() }.toList()
        )
    }
}

@FXMarker
abstract class MenuItemBaseBuilder<T : MenuItem> : DslBuilder<T>() {

    open fun text(text: String) {
        settings {
            this.text = text
        }
    }

    open fun ico(ico: AbstractIco, width: Double = 20.0) {
        settings {
            graphic = HBox().apply {
                children.add(ico)
                prefWidth = width
                setStyle("-fx-alignment: CENTER_LEFT")
            }
        }
    }

    open fun onAction(handler: (EventHandler<ActionEvent>)?) {
        settings {
            onAction = handler
        }
    }

}

@FXMarker
class MenuBuilder() : MenuItemBaseBuilder<Menu>() {

    override fun instance(): Menu = Menu()

    private val menuItemProviders: MutableList<() -> MenuItem> = mutableListOf()

    fun menuItem(config: MenuItemBuilder.() -> Unit) {
        menuItemProviders.add {
            MenuItemBuilder().apply { config() }.build()
        }
    }

    fun menu(config: MenuBuilder.() -> Unit) {
        menuItemProviders.add {
            MenuBuilder().apply { config() }.build()
        }
    }

    override fun style(styleColor: StyleColor , styleSize: StyleSize) {
        settings {
            styleClass.add("menu-ui")
        }
    }

    override fun build(): Menu {
        val menu = super.build()
        applyMenuItems(menu)
        return menu
    }

    override fun config(t: Menu) {
        super.config(t)
        applyMenuItems(t)
    }

    private fun applyMenuItems(menu: Menu) {
        menu.items.addAll(
            menuItemProviders.map { it() }.toList()
        )
    }

}

@FXMarker
class MenuItemBuilder : MenuItemBaseBuilder<MenuItem>() {

    override fun instance(): MenuItem = MenuItem()

    override fun style(styleColor: StyleColor , styleSize: StyleSize) {
        settings {
            styleClass.add("context-menu-ui")
        }
    }
}
