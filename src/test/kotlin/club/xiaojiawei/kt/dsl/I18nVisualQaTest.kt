package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.i18n.I18nContext
import club.xiaojiawei.kt.i18n.i18n
import javafx.application.Platform
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.MenuBar
import javafx.scene.image.Image
import javafx.scene.layout.VBox
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import javax.imageio.ImageIO
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class I18nVisualQaTest {

    @Test
    fun capturesLocalizedSurfaceBeforeAndAfterLocaleSwitch() {
        runOnJavaFxThread {
            val context = I18nContext(
                baseName = "i18n.messages",
                supportedLocales = setOf(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH),
                initialLocale = Locale.SIMPLIFIED_CHINESE,
                classLoader = javaClass.classLoader,
            )
            val title = context.localized("title")
            val greeting = context.localized("greeting")
            val prompt = context.localized("prompt")
            val table = tableView<String> {
                addColumn(title, cellValue = { it })
            }.apply {
                items.add("示例数据 / Sample data")
                prefHeight = 140.0
            }
            val root = VBox(12.0).apply {
                padding = Insets(20.0)
                children.add(MenuBar(menu(title)))
                children.add(label(greeting))
                children.add(button(title))
                children.add(textField { promptText(prompt) })
                children.add(table)
            }
            val stage = stage { title(title) }.apply {
                scene = Scene(root, 480.0, 340.0)
                show()
            }

            root.applyCss()
            root.layout()
            writePng(stage.scene.snapshot(null), Path.of("target", "visual-qa", "i18n-zh.png"))
            assertEquals("中文标题", stage.title)

            context.locale = Locale.ENGLISH
            root.applyCss()
            root.layout()
            writePng(stage.scene.snapshot(null), Path.of("target", "visual-qa", "i18n-en.png"))
            assertEquals("English title", stage.title)

            stage.close()
        }
    }

    private fun writePng(image: Image, path: Path) {
        Files.createDirectories(path.parent)
        val width = image.width.toInt()
        val height = image.height.toInt()
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
        val reader = image.pixelReader
        repeat(height) { y ->
            repeat(width) { x ->
                bufferedImage.setRGB(x, y, reader.getArgb(x, y))
            }
        }
        assertTrue(ImageIO.write(bufferedImage, "png", path.toFile()))
    }

    private fun <T> runOnJavaFxThread(action: () -> T): T {
        startJavaFx()
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
