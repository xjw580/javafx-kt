package club.xiaojiawei.kt.dsl

import club.xiaojiawei.controls.LabelSeparator
import club.xiaojiawei.kt.i18n.I18nContext
import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.geometry.HPos
import javafx.scene.Scene
import javafx.scene.control.ContentDisplay
import javafx.scene.control.Label
import javafx.scene.shape.Circle
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame
import kotlin.test.assertTrue

class LabelSeparatorDSLTest {

    @Test
    fun factoryConfiguresControlAndPreservesDefaults() = runOnJavaFxThread {
        val empty = labelSeparator()
        assertEquals("", empty.text)
        assertEquals(HPos.CENTER, empty.textAlignment)
        assertNull(empty.graphic)
        assertTrue(empty.styleClass.contains(LabelSeparator.DEFAULT_STYLE_CLASS))
        assertTrue(empty.userAgentStylesheet.endsWith("labelSeparator.css"))

        val icon = Circle(4.0)
        val separator = labelSeparator("initial") {
            +"分组标题"
            graphic(icon)
            contentDisplay(ContentDisplay.RIGHT)
            graphicTextGap(8.0)
            alignLeft()
            prefWidth(320.0)
            id("section")
        }
        assertEquals("分组标题", separator.text)
        assertSame(icon, separator.graphic)
        assertEquals(ContentDisplay.RIGHT, separator.contentDisplay)
        assertEquals(8.0, separator.graphicTextGap)
        assertEquals(HPos.LEFT, separator.textAlignment)
        assertEquals(320.0, separator.prefWidth)
        assertEquals("section", separator.id)
    }

    @Test
    fun configUpdatesExistingControlWithoutReplacingIt() = runOnJavaFxThread {
        val separator = LabelSeparator("original")
        val reusable = labelSeparatorConfig {
            text("updated")
            alignRight()
            graphic { Circle(3.0) }
        }
        assertSame(separator, separator.config(reusable))
        assertEquals("updated", separator.text)
        assertEquals(HPos.RIGHT, separator.textAlignment)
        assertTrue(separator.graphic is Circle)

        separator.config {
            text("")
            graphic(null)
            alignCenter()
        }
        assertEquals("", separator.text)
        assertNull(separator.graphic)
        assertEquals(HPos.CENTER, separator.textAlignment)
    }

    @Test
    fun textBindingsTrackChangesInBothDirections() = runOnJavaFxThread {
        val source = SimpleStringProperty("first")
        val separator = labelSeparator { bindText(source.concat("!")) }
        source.set("second")
        assertEquals("second!", separator.text)

        val bidirectional = labelSeparator { bindBidirectionalText(source) }
        assertEquals("second", bidirectional.text)
        bidirectional.text = "third"
        assertEquals("third", source.get())
        assertEquals("third!", separator.text)
        source.set("fourth")
        assertEquals("fourth", bidirectional.text)
    }

    @Test
    fun localizedFactoriesAndContainerEntriesTrackLocale() = runOnJavaFxThread {
        val context = I18nContext(
            baseName = "i18n.messages",
            supportedLocales = setOf(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH),
            initialLocale = Locale.SIMPLIFIED_CHINESE,
            classLoader = javaClass.classLoader,
        )
        val title = context.localized("title")
        val standalone = labelSeparator(title)
        val shorthand = labelSeparator { +title }
        val parent = vbox {
            addLabelSeparator()
            addLabelSeparator("固定标题") { alignLeft() }
            addLabelSeparator(title) { alignRight() }
        }
        val children = parent.children.map { it as LabelSeparator }
        assertEquals(listOf("", "固定标题", "中文标题"), children.map { it.text })
        assertEquals(HPos.LEFT, children[1].textAlignment)
        assertEquals(HPos.RIGHT, children[2].textAlignment)

        context.locale = Locale.ENGLISH
        assertEquals("English title", standalone.text)
        assertEquals("English title", shorthand.text)
        assertEquals("English title", children[2].text)
        assertEquals("固定标题", children[1].text)
    }

    @Test
    fun delayedBuildersDeferConfigurationAndCreateIndependentGraphics() = runOnJavaFxThread {
        var graphicBuilds = 0
        val builder = labelSeparatorBuilder {
            delayMode()
            text("delayed")
            graphic {
                graphicBuilds++
                Circle(4.0)
            }
        }
        val unconfigured = builder.instance()
        assertEquals("", unconfigured.text)
        assertEquals(0, graphicBuilds)
        val first = builder.build()
        val second = builder.build()
        assertSame(unconfigured, first)
        assertEquals("delayed", first.text)
        assertEquals("delayed", second.text)
        assertNotSame(first, second)
        assertNotSame(first.graphic, second.graphic)
        assertEquals(2, graphicBuilds)

        val parentBuilder = vboxBuilder {
            delayMode()
            addLabelSeparator("child") {
                graphic {
                    graphicBuilds++
                    Circle(2.0)
                }
            }
        }
        assertEquals(2, graphicBuilds)
        val firstParent = parentBuilder.build()
        val secondParent = parentBuilder.build()
        assertEquals(4, graphicBuilds)
        assertNotSame(firstParent.children.single(), secondParent.children.single())
        assertEquals("child", (firstParent.children.single() as LabelSeparator).text)
    }

    @Test
    fun dslControlRendersWithItsNativeSkinAndUpdatesBoundText() = runOnJavaFxThread {
        val source = SimpleStringProperty("分组标题")
        val root = vbox {
            padding(16.0)
            addLabelSeparator {
                bindText(source)
                graphic { Circle(4.0) }
                alignLeft()
            }
        }
        val scene = Scene(root, 360.0, 100.0)
        root.applyCss()
        root.layout()
        val separator = root.children.single() as LabelSeparator
        val renderedLabel = separator.lookup(".label") as Label
        assertEquals("分组标题", renderedLabel.text)
        assertTrue(separator.width > 0)
        assertTrue(separator.height > 0)
        assertFalse(separator.lookupAll(".separator").isEmpty())
        assertTrue(scene.snapshot(null).width > 0)

        source.set("更新标题")
        root.applyCss()
        root.layout()
        assertEquals("更新标题", renderedLabel.text)
    }

    private fun <T> runOnJavaFxThread(action: () -> T): T {
        val started = CountDownLatch(1)
        try {
            Platform.startup { started.countDown() }
        } catch (_: IllegalStateException) {
            started.countDown()
        }
        assertTrue(started.await(10, TimeUnit.SECONDS))
        val future = FutureTask(action)
        Platform.runLater(future)
        return future.get(10, TimeUnit.SECONDS)
    }
}
