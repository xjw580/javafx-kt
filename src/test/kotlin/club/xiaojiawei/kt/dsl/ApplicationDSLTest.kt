package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.scene.Scene
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.Modality
import javafx.stage.Stage
import javafx.stage.StageStyle
import javafx.stage.WindowEvent
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertSame

class ApplicationDSLTest {
    @Test
    fun sceneMatchesNativeConstructionInBothModes() = onFxThread {
        for (mode in BuildMode.entries) {
            val expected = Scene(VBox(), 640.0, 360.0).apply {
                fill = Color.BEIGE
                stylesheets.addAll("base.css", "screen.css")
            }
            val actual = scene {
                setMode(mode)
                root { VBox() }
                width = 640.0
                height = 360.0
                fill(Color.BEIGE)
                stylesheets("base.css", "screen.css")
            }
            assertEquals(expected.root.javaClass, actual.root.javaClass)
            assertEquals(expected.width, actual.width)
            assertEquals(expected.height, actual.height)
            assertEquals(expected.fill, actual.fill)
            assertEquals(expected.stylesheets, actual.stylesheets)
        }
    }

    @Test
    fun sceneConfigUpdatesExistingSceneAndKeepsItsRoot() = onFxThread {
        val root = VBox()
        val actual = Scene(root, 320.0, 240.0)
        val expected = Scene(VBox(), 320.0, 240.0).apply {
            fill = Color.ALICEBLUE
            stylesheets.add("extra.css")
        }
        assertSame(actual, actual.config {
            fill(Color.ALICEBLUE)
            stylesheets("extra.css")
        })
        assertSame(root, actual.root)
        assertEquals(expected.fill, actual.fill)
        assertEquals(expected.width, actual.width)
        assertEquals(expected.height, actual.height)
        assertEquals(expected.stylesheets, actual.stylesheets)
    }

    @Test
    fun sceneRequiresRootAndUsesNativeAutomaticSizingWithoutExplicitSize() = onFxThread {
        assertFailsWith<IllegalStateException> { scene {} }
        val expected = Scene(VBox())
        val actual = scene { root { VBox() } }
        assertEquals(expected.width, actual.width)
        assertEquals(expected.height, actual.height)
    }

    @Test
    fun stageMatchesNativePropertiesAndInstalledEventsInBothModes() = onFxThread {
        val owner = Stage()
        try {
            for (mode in BuildMode.entries) {
                val expectedEvents = mutableListOf<String>()
                val actualEvents = mutableListOf<String>()
                val expected = Stage().apply {
                    initOwner(owner)
                    initStyle(StageStyle.UTILITY)
                    initModality(Modality.WINDOW_MODAL)
                    title = "窗口"
                    scene = Scene(VBox(), 320.0, 240.0)
                    x = 40.0
                    y = 60.0
                    width = 500.0
                    height = 400.0
                    minWidth = 200.0
                    minHeight = 150.0
                    maxWidth = 900.0
                    maxHeight = 800.0
                    isResizable = false
                    isAlwaysOnTop = true
                    isMaximized = true
                    setOnShowing { expectedEvents.add("showing") }
                    setOnShown { expectedEvents.add("shown") }
                    setOnHiding { expectedEvents.add("hiding") }
                    setOnHidden { expectedEvents.add("hidden") }
                    setOnCloseRequest { expectedEvents.add("close") }
                }
                val actual = stage {
                    setMode(mode)
                    initOwner(owner)
                    initStyle(StageStyle.UTILITY)
                    initModality(Modality.WINDOW_MODAL)
                    title("窗口")
                    scene { root { VBox() }; width = 320.0; height = 240.0 }
                    pos(40.0, 60.0)
                    size(500.0, 400.0)
                    minSize(200.0, 150.0)
                    maxSize(900.0, 800.0)
                    resizable(false)
                    alwaysOnTop()
                    maximized()
                    onShowing { actualEvents.add("showing") }
                    onShown { actualEvents.add("shown") }
                    onHiding { actualEvents.add("hiding") }
                    onHidden { actualEvents.add("hidden") }
                    onCloseRequest { actualEvents.add("close") }
                }
                try {
                    assertSame(expected.owner, actual.owner)
                    assertEquals(expected.style, actual.style)
                    assertEquals(expected.modality, actual.modality)
                    assertEquals(expected.title, actual.title)
                    assertEquals(expected.x, actual.x)
                    assertEquals(expected.y, actual.y)
                    assertEquals(expected.width, actual.width)
                    assertEquals(expected.height, actual.height)
                    assertEquals(expected.minWidth, actual.minWidth)
                    assertEquals(expected.minHeight, actual.minHeight)
                    assertEquals(expected.maxWidth, actual.maxWidth)
                    assertEquals(expected.maxHeight, actual.maxHeight)
                    assertEquals(expected.isResizable, actual.isResizable)
                    assertEquals(expected.isAlwaysOnTop, actual.isAlwaysOnTop)
                    assertEquals(expected.isMaximized, actual.isMaximized)
                    assertEquals(expected.scene.width, actual.scene.width)
                    assertEquals(expected.scene.height, actual.scene.height)
                    for (window in listOf(expected, actual)) {
                        window.onShowing.handle(WindowEvent(window, WindowEvent.WINDOW_SHOWING))
                        window.onShown.handle(WindowEvent(window, WindowEvent.WINDOW_SHOWN))
                        window.onHiding.handle(WindowEvent(window, WindowEvent.WINDOW_HIDING))
                        window.onHidden.handle(WindowEvent(window, WindowEvent.WINDOW_HIDDEN))
                        window.onCloseRequest.handle(WindowEvent(window, WindowEvent.WINDOW_CLOSE_REQUEST))
                    }
                    assertEquals(listOf("showing", "shown", "hiding", "hidden", "close"), expectedEvents)
                    assertEquals(expectedEvents, actualEvents)
                    assertFalse(actual.isShowing)
                } finally {
                    expected.close()
                    actual.close()
                }
            }
        } finally {
            owner.close()
        }
    }

    @Test
    fun existingStageBuilderAndConfigPreserveIdentity() = onFxThread {
        val actual = Stage()
        val expected = Stage()
        try {
            expected.title = "已有窗口"
            expected.scene = Scene(VBox())
            assertSame(actual, StageBuilder(actual).apply {
                title("已有窗口")
                root { VBox() }
            }.build())
            assertSame(actual, actual.config { x(120.0); y(80.0) })
            expected.x = 120.0
            expected.y = 80.0
            assertEquals(expected.title, actual.title)
            assertEquals(expected.scene.root.javaClass, actual.scene.root.javaClass)
            assertEquals(expected.x, actual.x)
            assertEquals(expected.y, actual.y)
        } finally {
            actual.close()
            expected.close()
        }
    }
}
