package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.ico.AbstractIco
import javafx.beans.value.ChangeListener
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.control.ContextMenu
import javafx.scene.control.Menu
import javafx.scene.control.MenuItem
import javafx.scene.control.RadioMenuItem
import javafx.scene.layout.HBox
import java.beans.PropertyChangeEvent

/**
 * @author 肖嘉威
 * @date 2025/8/14 8:22
 */

@FXMarker
class ContextMenuBuilder : DslBuilder<ContextMenu>() {

    override fun buildInstance(): ContextMenu = ContextMenu()

    private val menuItemBuilders: MutableList<() -> MenuItem> = mutableListOf()

    fun addMenuItem(config: MenuItemBuilder.() -> Unit) {
        menuItemBuilders.add {
            MenuItemBuilder().apply { config() }.build()
        }
    }

    fun addMenu(config: MenuBuilder.() -> Unit) {
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

    operator fun String.unaryPlus() = text(this)

    open fun ico(ico: AbstractIco, width: Double = 20.0) {
        settings {
            graphic = HBox().apply {
                children.add(ico)
                prefWidth = width
                style = "-fx-alignment: CENTER_LEFT"
            }
        }
    }

    operator fun AbstractIco.unaryPlus() = ico(this)

    open fun onAction(handler: (EventHandler<ActionEvent>)?) {
        settings {
            onAction = handler
        }
    }

}

@FXMarker
class MenuBuilder() : MenuItemBaseBuilder<Menu>() {

    override fun buildInstance(): Menu = Menu()

    private val menuItemProviders: MutableList<() -> MenuItem> = mutableListOf()

    fun addMenuItem(config: MenuItemBuilder.() -> Unit) {
        menuItemProviders.add {
            MenuItemBuilder().apply { config() }.build()
        }
    }

    fun addRadioMenuItem(config: RadioMenuItemBuilder.() -> Unit) {
        menuItemProviders.add {
            RadioMenuItemBuilder().apply { config() }.build()
        }
    }

    fun addMenu(config: MenuBuilder.() -> Unit) {
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

    override fun buildInstance(): MenuItem = MenuItem()

    override fun style(styleColor: StyleColor , styleSize: StyleSize) {
        settings {
            styleClass.add("context-menu-ui")
        }
    }
}


@FXMarker
class RadioMenuItemBuilder() : MenuItemBaseBuilder<RadioMenuItem>(){

    override fun buildInstance(): RadioMenuItem = RadioMenuItem()

    init {
        RadioMenuItem().selectedProperty().addListener { observable, oldValue, newValue ->}
    }

    fun addSelectedListener(listener: ChangeListener<Boolean>) {
        settings {
            selectedProperty().addListener(listener)
        }
    }

    fun removeSelectedListener(listener: ChangeListener<Boolean>) {
        settings {
            selectedProperty().removeListener(listener)
        }
    }

    override fun style(styleColor: StyleColor , styleSize: StyleSize) {
        settings {
            styleClass.add("menu-item-ui")
//            when (styleColor) {
//                StyleColor.MAIN -> styleClass.add("radio-button-ui-main")
//                StyleColor.NORMAL -> styleClass.add("radio-button-ui-normal")
//                StyleColor.SUCCESS -> styleClass.add("radio-button-ui-success")
//                StyleColor.WARN -> styleClass.add("radio-button-ui-warn")
//                StyleColor.ERROR -> styleClass.add("radio-button-ui-error")
//                StyleColor.DEFAULT -> {}
//            }
            when (styleSize) {
                StyleSize.TINY -> styleClass.add("menu-item-ui-tiny")
                StyleSize.SMALL -> styleClass.add("menu-item-ui-small")
                StyleSize.BIG -> styleClass.add("menu-item-ui-big")
                StyleSize.DEFAULT -> {}
            }
        }
    }
}