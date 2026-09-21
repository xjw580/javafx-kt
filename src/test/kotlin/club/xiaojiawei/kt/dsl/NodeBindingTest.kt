package club.xiaojiawei.kt.dsl

import javafx.beans.property.SimpleBooleanProperty
import javafx.scene.layout.VBox
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertSame
import kotlin.test.assertTrue

class NodeBindingTest {

    @Test
    fun individualBindingsTrackTheirOwnObservableValues() {
        val disabled = SimpleBooleanProperty(true)
        val visible = SimpleBooleanProperty(false)
        val managed = SimpleBooleanProperty(false)
        val node = pane {
            bindDisable(disabled)
            bindVisible(visible)
            bindManaged(managed)
        }

        assertTrue(node.isDisable)
        assertFalse(node.isVisible)
        assertFalse(node.isManaged)
        assertTrue(node.disableProperty().isBound)
        assertTrue(node.visibleProperty().isBound)
        assertTrue(node.managedProperty().isBound)

        disabled.set(false)
        assertFalse(node.isDisable)
        assertFalse(node.isVisible)
        visible.set(true)
        assertTrue(node.isVisible)
        assertFalse(node.isManaged)
        managed.set(true)
        assertTrue(node.isManaged)
    }

    @Test
    fun combinedBindingAcceptsExpressionsAndRemovesHiddenNodesFromLayout() {
        val hidden = SimpleBooleanProperty(false)
        val node = pane {
            prefHeight(20.0)
            bindVisibleAndManaged(hidden.not())
        }
        val sibling = pane { prefHeight(30.0) }
        val root = VBox(node, sibling)

        assertTrue(node.isVisible)
        assertTrue(node.isManaged)
        assertFalse(node.isDisable)
        assertEquals(50.0, root.prefHeight(-1.0))

        hidden.set(true)
        assertFalse(node.isVisible)
        assertFalse(node.isManaged)
        assertEquals(30.0, root.prefHeight(-1.0))

        hidden.set(false)
        assertTrue(node.isVisible)
        assertTrue(node.isManaged)
        assertEquals(50.0, root.prefHeight(-1.0))
    }

    @Test
    fun delayedBuilderBindsWhenBuiltUsingTheLatestSourceValues() {
        val disabled = SimpleBooleanProperty(false)
        val shown = SimpleBooleanProperty(true)
        val builder = PaneBuilder().apply {
            delayMode()
            bindDisable(disabled)
            bindVisibleAndManaged(shown)
        }
        val instance = builder.instance()
        assertFalse(instance.disableProperty().isBound)
        assertFalse(instance.visibleProperty().isBound)
        assertFalse(instance.managedProperty().isBound)

        disabled.set(true)
        shown.set(false)
        val node = builder.build()
        assertSame(instance, node)
        assertTrue(node.isDisable)
        assertFalse(node.isVisible)
        assertFalse(node.isManaged)

        disabled.set(false)
        shown.set(true)
        assertFalse(node.isDisable)
        assertTrue(node.isVisible)
        assertTrue(node.isManaged)
    }

    @Test
    fun configuringExistingNodeCanReplaceSeparateBindingsWithCombinedBinding() {
        val disabled = SimpleBooleanProperty(false)
        val visible = SimpleBooleanProperty(true)
        val managed = SimpleBooleanProperty(false)
        val node = VBox()
        assertSame(node, node.config {
            bindDisable(disabled.not())
            bindVisible(visible)
            bindManaged(managed)
        })
        assertTrue(node.isDisable)
        assertTrue(node.isVisible)
        assertFalse(node.isManaged)

        val shown = SimpleBooleanProperty(false)
        node.config { bindVisibleAndManaged(shown) }
        assertFalse(node.isVisible)
        assertFalse(node.isManaged)
        visible.set(false)
        managed.set(true)
        assertFalse(node.isVisible)
        assertFalse(node.isManaged)

        shown.set(true)
        disabled.set(true)
        assertTrue(node.isVisible)
        assertTrue(node.isManaged)
        assertFalse(node.isDisable)
    }
}
