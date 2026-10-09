package club.xiaojiawei.kt.dsl

import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.value.ChangeListener
import javafx.scene.Group
import javafx.scene.layout.VBox
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue

class ParentDSLTest {

    @Test
    fun immediateBuilderAddsStylesheetsInOrderAndRetainsNodeAndPaneCapabilities() {
        val shared = stylesheetBuilder { styleClass("shared") { opacity(0.8) } }
        val local = stylesheetBuilder { styleClass("local") { opacity(0.5) } }
        val builder = vboxBuilder {
            id("root")
            spacing(8.0)
            stylesheets("base.css", "theme.css")
            stylesheet(shared)
            stylesheet { styleClass("local") { opacity(0.5) } }
            addPane { id("child") }
        }
        val node = builder.instance()

        assertEquals(listOf("base.css", "theme.css", shared.toDataUri(), local.toDataUri()), node.stylesheets)
        assertEquals("root", node.id)
        assertEquals(8.0, node.spacing)
        assertEquals("child", node.children.single().id)
        assertSame(node, builder.build())
    }

    @Test
    fun delayedBuilderDefersAllStylesheetFormsAndCanBeReused() {
        var configurations = 0
        val shared = stylesheetBuilder { styleClass("shared") { opacity(0.8) } }
        val builder = paneBuilder {
            delayMode()
            stylesheets("base.css")
            stylesheet(shared)
            stylesheet {
                configurations++
                styleClass("local") { opacity(0.5) }
            }
        }
        val unconfigured = builder.instance()
        assertTrue(unconfigured.stylesheets.isEmpty())
        assertEquals(0, configurations)

        val first = builder.build()
        val second = builder.build()
        assertSame(unconfigured, first)
        assertNotSame(first, second)
        assertEquals(2, configurations)
        assertEquals(3, first.stylesheets.size)
        assertEquals(first.stylesheets, second.stylesheets)
        first.stylesheets.clear()
        assertEquals(3, second.stylesheets.size)
    }

    @Test
    fun existingNodeConfigurationAppendsStylesheetsAndAcceptsEmptyInput() {
        val node = VBox()
        node.stylesheets.add("existing.css")
        val shared = stylesheetBuilder { styleClass("shared") { opacity(0.8) } }
        val local = stylesheetBuilder { styleClass("local") { opacity(0.5) } }

        assertSame(node, node.config {
            stylesheets()
            stylesheets("additional.css")
            stylesheet(shared)
            stylesheet { styleClass("local") { opacity(0.5) } }
        })
        assertEquals(
            listOf("existing.css", "additional.css", shared.toDataUri(), local.toDataUri()),
            node.stylesheets,
        )
    }

    @Test
    fun layoutStateBindingAndListenerRemovalFollowActualLayoutPasses() {
        val needsLayout = SimpleBooleanProperty()
        val changes = mutableListOf<Boolean>()
        val listener = ChangeListener<Boolean> { _, _, value -> changes.add(value) }
        val node = vbox {
            byBindNeedsLayout(needsLayout)
            addNeedsLayoutListener(listener)
        }
        assertTrue(needsLayout.isBound)
        node.layout()
        changes.clear()
        assertFalse(needsLayout.get())

        node.requestLayout()
        assertTrue(needsLayout.get())
        node.layout()
        assertFalse(needsLayout.get())
        assertEquals(listOf(true, false), changes)

        node.config { removeNeedsLayoutListener(listener) }
        node.requestLayout()
        assertTrue(needsLayout.get())
        assertEquals(listOf(true, false), changes)
    }

    @Test
    fun nonRegionParentSupportsDelayedLayoutBindingAndListenerRegistration() {
        val needsLayout = SimpleObjectProperty<Any>()
        val changes = mutableListOf<Boolean>()
        val builder = object : ParentBaseBuilder<Group>() {
            override fun buildInstance(): Group = Group()
        }.apply {
            delayMode()
            id("group")
            stylesheets("group.css")
            byBindNeedsLayout(needsLayout)
            addNeedsLayoutListener { _, _, value -> changes.add(value) }
        }
        val unconfigured = builder.instance()
        assertFalse(needsLayout.isBound)
        unconfigured.layout()
        assertTrue(changes.isEmpty())

        val group = builder.build()
        assertSame(unconfigured, group)
        assertEquals("group", group.id)
        assertEquals(listOf("group.css"), group.stylesheets)
        assertTrue(needsLayout.isBound)
        group.layout()
        changes.clear()
        group.requestLayout()
        assertEquals(true, needsLayout.get())
        group.layout()
        assertEquals(false, needsLayout.get())
        assertEquals(listOf(true, false), changes)
    }
}
