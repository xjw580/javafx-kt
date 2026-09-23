package club.xiaojiawei.kt.dsl

import javafx.application.Platform
import javafx.beans.property.SimpleStringProperty
import javafx.css.PseudoClass
import javafx.scene.Group
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.SubScene
import javafx.scene.layout.StackPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import java.nio.charset.StandardCharsets
import java.util.Base64
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class StyleDSLTest {

    @Test
    fun staticStylesRemainInline() {
        val node = Group()

        node.styled {
            backgroundColor("red")
            custom("-fx-padding", "1; 2")
        }

        assertEquals("-fx-background-color: red; -fx-padding: 1; 2", node.style)
    }

    @Test
    fun buildRejectsPseudoClassesButKeepsPureInlineContract() {
        val inline = styleBuilder { backgroundColor("red") }
        val pseudo = styleBuilder { hover { backgroundColor("blue") } }

        assertEquals("-fx-background-color: red", inline.build())
        assertFailsWith<IllegalStateException> { pseudo.build() }
    }

    @Test
    fun nativeCssAppliesBaseHoverPressedAndRestoresEachState() = runOnJavaFxThread {
        val node = StackPane()
        val scene = Scene(node, 80.0, 40.0)
        node.styled {
            backgroundColor("#112233")
            hover {
                backgroundColor("#445566")
                pressed { backgroundColor("#778899") }
            }
        }

        scene.root.applyCss()
        assertBackground(node, "#112233")

        node.setPseudo("hover", true)
        scene.root.applyCss()
        assertBackground(node, "#445566")

        node.setPseudo("pressed", true)
        scene.root.applyCss()
        assertBackground(node, "#778899")

        node.setPseudo("pressed", false)
        scene.root.applyCss()
        assertBackground(node, "#445566")

        node.setPseudo("hover", false)
        scene.root.applyCss()
        assertBackground(node, "#112233")
    }

    @Test
    fun namedAndCustomPseudoClassesUseNativeCssStates() = runOnJavaFxThread {
        val node = StackPane()
        val scene = Scene(node, 80.0, 40.0)
        node.styled {
            backgroundColor("#010101")
            selected { backgroundColor("#111111") }
            disabled { backgroundColor("#222222") }
            focused { backgroundColor("#333333") }
            armed { backgroundColor("#444444") }
            pseudoClass("attention") { backgroundColor("#555555") }
        }

        listOf(
            "selected" to "#111111",
            "disabled" to "#222222",
            "focused" to "#333333",
            "armed" to "#444444",
            "attention" to "#555555",
        ).forEach { (pseudoClass, color) ->
            node.setPseudo(pseudoClass, true)
            scene.root.applyCss()
            assertBackground(node, color)
            node.setPseudo(pseudoClass, false)
        }
    }

    @Test
    fun repeatedBlocksKeepDeclarationOrderAndImportantUsesNativeCascade() = runOnJavaFxThread {
        val node = StackPane()
        val scene = Scene(node, 80.0, 40.0)
        node.styled {
            backgroundColor("#010101")
            hover { backgroundColor("#111111") }
            hover { backgroundColor("#222222 !important") }
            hover { backgroundColor("#333333") }
        }

        node.setPseudo("hover", true)
        scene.root.applyCss()

        assertBackground(node, "#222222")
        val css = decodeCss(node.stylesheets.single())
        assertTrue(css.indexOf("#111111") < css.indexOf("#222222"))
        assertTrue(css.indexOf("#222222") < css.indexOf("#333333"))
    }

    @Test
    fun laterNestedRuleWinsWhenPseudoClassCombinationsHaveEqualSpecificity() = runOnJavaFxThread {
        val node = StackPane()
        val scene = Scene(node, 80.0, 40.0)
        node.styled {
            hover {
                pressed { backgroundColor("#111111") }
            }
            pressed {
                hover { backgroundColor("#222222") }
            }
        }

        node.setPseudo("hover", true)
        node.setPseudo("pressed", true)
        scene.root.applyCss()

        assertBackground(node, "#222222")
    }

    @Test
    fun stylesheetSerializationSuffixesEveryGroupedSelectorAndPreservesSemicolons() {
        val css = stylesheetBuilder {
            select(".first, .second") {
                custom("-fx-font-family", "'A; B'")
                hover {
                    backgroundColor("red")
                    pressed { backgroundColor("blue") }
                }
                hover { backgroundColor("green") }
            }
        }.build()

        assertTrue(css.contains(".first, .second {"))
        assertTrue(css.contains("-fx-font-family: 'A; B';"))
        assertTrue(css.contains(".first:hover, .second:hover {"))
        assertTrue(css.contains(".first:hover:pressed, .second:hover:pressed {"))
        assertTrue(css.indexOf("background-color: red") < css.lastIndexOf("background-color: green"))
        assertFailsWith<IllegalArgumentException> {
            stylesheetBuilder {
                select(".first,,.second") { backgroundColor("red") }
            }.build()
        }
    }

    @Test
    fun parentOwnsGeneratedStylesheetAndRepeatedStylingPreservesUserMetadata() = runOnJavaFxThread {
        val node = StackPane()
        val userStylesheet = "data:text/css;base64,"
        node.id = "user-id"
        node.styleClass += "user-class"
        node.stylesheets += userStylesheet
        node.styled { hover { backgroundColor("red") } }
        val generatedClass = node.styleClass.single { it != "user-class" }
        val generatedStylesheet = node.stylesheets.single { it != userStylesheet }

        node.styled { backgroundColor("blue") }

        assertEquals("user-id", node.id)
        assertTrue("user-class" in node.styleClass)
        assertFalse(generatedClass in node.styleClass)
        assertEquals(listOf(userStylesheet), node.stylesheets)
        assertFalse(generatedStylesheet in node.stylesheets)
        assertEquals("-fx-background-color: blue", node.style)
    }

    @Test
    fun nonParentStylesheetFollowsDirectParentAcrossScenesAndSubScene() = runOnJavaFxThread {
        val node = Rectangle(20.0, 20.0)
        val firstParent = StackPane(node)
        val firstScene = Scene(firstParent)
        val subRoot = StackPane()
        val subScene = SubScene(subRoot, 50.0, 50.0)
        val secondRoot = StackPane(subScene)
        val secondScene = Scene(secondRoot)
        node.styled {
            custom("-fx-fill", "#123456")
            hover { custom("-fx-fill", "#654321") }
        }

        assertEquals(1, firstParent.stylesheets.size)
        firstScene.root.applyCss()
        assertEquals(Color.web("#123456"), node.fill)

        firstParent.children.remove(node)
        assertTrue(firstParent.stylesheets.isEmpty())
        subRoot.children.add(node)
        assertEquals(1, subRoot.stylesheets.size)
        node.setPseudo("hover", true)
        secondScene.root.applyCss()
        assertEquals(Color.web("#654321"), node.fill)

        node.styled {}
        assertTrue(subRoot.stylesheets.isEmpty())
        val thirdParent = StackPane()
        subRoot.children.remove(node)
        thirdParent.children.add(node)
        assertTrue(thirdParent.stylesheets.isEmpty())
    }

    @Test
    fun detachedNonParentWaitsForParentBeforeAttachingStylesheet() = runOnJavaFxThread {
        val node = Rectangle(20.0, 20.0)
        node.styled { hover { custom("-fx-fill", "red") } }
        val parent = StackPane()

        assertTrue(parent.stylesheets.isEmpty())
        parent.children.add(node)

        assertEquals(1, parent.stylesheets.size)
    }

    @Test
    fun applicationSnapshotsMutableBuilderAndDelayBuilderCreatesIndependentNodes() = runOnJavaFxThread {
        val style = styleBuilder {
            backgroundColor("red")
            hover { backgroundColor("darkred") }
        }
        val builder = StackPaneBuilder().apply {
            delayMode()
            styled(style)
        }

        val first = builder.build()
        style.backgroundColor("blue")
        style.hover { backgroundColor("darkblue") }
        val second = builder.build()
        val root = StackPane(first, second)
        val scene = Scene(root)
        scene.root.applyCss()

        assertBackground(first, "red")
        assertBackground(second, "blue")
        assertNotEquals(first.styleClass.last(), second.styleClass.last())
        assertNotEquals(first.stylesheets.single(), second.stylesheets.single())
    }

    @Test
    fun boundStyleRejectionLeavesPreviousGeneratedResourcesIntact() = runOnJavaFxThread {
        val node = StackPane()
        node.styled { hover { backgroundColor("red") } }
        val oldClasses = node.styleClass.toList()
        val oldStylesheets = node.stylesheets.toList()
        val boundStyle = SimpleStringProperty("-fx-opacity: 0.5")
        node.styleProperty().bind(boundStyle)

        assertFailsWith<IllegalStateException> {
            node.styled { backgroundColor("blue") }
        }

        assertEquals(oldClasses, node.styleClass)
        assertEquals(oldStylesheets, node.stylesheets)
        assertEquals(boundStyle.value, node.style)
        node.styleProperty().unbind()
    }

    @Test
    fun genericPseudoClassRejectsMalformedNamesWithoutNormalization() {
        listOf("", ":hover", "two words", "1leading", "hover:pressed", "line\nbreak").forEach { name ->
            assertFailsWith<IllegalArgumentException>(name) {
                styleBuilder { pseudoClass(name) { backgroundColor("red") } }
            }
        }
    }

    @Suppress("DEPRECATION")
    @Test
    fun deprecatedHoverEffectDelegatesToHoverRule() {
        val css = stylesheetBuilder {
            styleClass("legacy") {
                hoverEffect("-fx-background-color", "red")
            }
        }.build()

        assertTrue(css.contains(".legacy:hover"))
        assertTrue(css.contains("-fx-background-color: red;"))
    }

    private fun Node.setPseudo(name: String, active: Boolean) {
        pseudoClassStateChanged(PseudoClass.getPseudoClass(name), active)
    }

    private fun assertBackground(node: StackPane, expected: String) {
        assertEquals(Color.web(expected), node.background.fills.single().fill)
    }

    private fun decodeCss(uri: String): String {
        val payload = uri.substringAfter("data:text/css;base64,")
        return String(Base64.getDecoder().decode(payload), StandardCharsets.UTF_8)
    }

    private fun <T> runOnJavaFxThread(action: () -> T): T {
        startJavaFx()
        if (Platform.isFxApplicationThread()) return action()
        val future = FutureTask(action)
        Platform.runLater(future)
        return future.get(10, TimeUnit.SECONDS)
    }

    private fun startJavaFx() {
        val started = CountDownLatch(1)
        try {
            Platform.startup { started.countDown() }
        } catch (_: IllegalStateException) {
            started.countDown()
        }
        assertTrue(started.await(10, TimeUnit.SECONDS))
    }
}
