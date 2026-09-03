package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.controls.messageDialog
import club.xiaojiawei.kt.i18n.I18nContext
import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.RadioButton
import javafx.scene.control.TextField
import javafx.scene.control.TitledPane
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.VBox
import javafx.scene.text.Text
import javafx.stage.Stage
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class I18nDSLTest {

    @Test
    fun displayTextTracksLocaleWhileEditableTextRemainsUserOwned() {
        runOnJavaFxThread {
            val context = testContext()
            val title = context.localized("title")
            val prompt = context.localized("prompt")
            val input = context.localized("input")

            val label = label(title)
            val text = text(title)
            val button = button(title)
            val checkBox = checkBox(title)
            val radioButton = radioButton(title)
            val groupedRadioButton = radioButton(title, ToggleGroup())
            val menu = menu(title)
            val menuItem = menuItem(title)
            val radioMenuItem = radioMenuItem(title)
            val titledPane = titledPane(title)
            val stage = stage { title(title) }
            val column = tableColumn<String, String> { text(title) }
            val table = tableView<String> {
                addColumn(title, cellValue = { it })
                addColumn<String>(title)
            }
            val chooser = fileChooser { title(title) }
            val directoryChooser = directoryChooser { title(title) }
            val field = textField(input) { promptText(prompt) }
            val area = textArea(input) { promptText(prompt) }
            val comboBox = comboBox<String> { promptText(prompt) }
            val cellLabel = GridPaneBuilder.CellBuilder().apply { itemLabel(title) }.node as Label
            val cellButton = GridPaneBuilder.CellBuilder().apply { itemButton(title) }.node as Button
            val cellField = GridPaneBuilder.CellBuilder().apply { itemTextField(input) }.node as TextField
            val container = vbox {
                addText(title)
                addText(title) { wrappingWidth = 120.0 }
                addLabel(title)
                addTitle(title)
                addCheckBox(title)
                addRadioButton(title)
                addRadioButton(title, ToggleGroup())
                addButton(title)
            }

            assertEquals("中文标题", label.text)
            assertEquals("中文标题", text.text)
            assertEquals("中文标题", button.text)
            assertEquals("中文标题", checkBox.text)
            assertEquals("中文标题", radioButton.text)
            assertEquals("中文标题", groupedRadioButton.text)
            assertEquals("中文标题", menu.text)
            assertEquals("中文标题", menuItem.text)
            assertEquals("中文标题", radioMenuItem.text)
            assertEquals("中文标题", titledPane.text)
            assertEquals("中文标题", stage.title)
            assertEquals("中文标题", column.text)
            assertEquals(List(2) { "中文标题" }, table.columns.map { it.text })
            assertEquals("中文标题", chooser.title)
            assertEquals("中文标题", directoryChooser.title)
            assertEquals("中文输入", field.text)
            assertEquals("请输入", field.promptText)
            assertEquals("中文输入", area.text)
            assertEquals("请输入", area.promptText)
            assertEquals("请输入", comboBox.promptText)
            assertEquals("中文标题", cellLabel.text)
            assertEquals("中文标题", cellButton.text)
            assertEquals("中文输入", cellField.text)

            context.locale = Locale.ENGLISH

            assertEquals("English title", label.text)
            assertEquals("English title", text.text)
            assertEquals("English title", button.text)
            assertEquals("English title", checkBox.text)
            assertEquals("English title", radioButton.text)
            assertEquals("English title", groupedRadioButton.text)
            assertEquals("English title", menu.text)
            assertEquals("English title", menuItem.text)
            assertEquals("English title", radioMenuItem.text)
            assertEquals("English title", titledPane.text)
            assertEquals("English title", stage.title)
            assertEquals("English title", column.text)
            assertEquals(List(2) { "English title" }, table.columns.map { it.text })
            assertEquals("English title", chooser.title)
            assertEquals("English title", directoryChooser.title)
            assertEquals("中文输入", field.text)
            assertEquals("Enter text", field.promptText)
            assertEquals("中文输入", area.text)
            assertEquals("Enter text", area.promptText)
            assertEquals("Enter text", comboBox.promptText)
            assertEquals("English title", cellLabel.text)
            assertEquals("English title", cellButton.text)
            assertEquals("中文输入", cellField.text)
            assertEquals(
                List(8) { "English title" },
                container.children.map {
                    when (it) {
                        is Text -> it.text
                        is Label -> it.text
                        is TitledPane -> it.text
                        is CheckBox -> it.text
                        is RadioButton -> it.text
                        is Button -> it.text
                        else -> error("Unexpected node: ${it::class.qualifiedName}")
                    }
                },
            )
            stage.close()
        }
    }

    @Test
    fun localizedTextSupportsTheSameUnaryPlusSyntaxAsString() {
        runOnJavaFxThread {
            val context = testContext()
            val title = context.localized("title")
            val input = context.localized("input")
            val label = label { +title }
            val text = text { +title }
            val field = textField { +input }
            val area = textArea { +input }
            val column = tableColumn<String, String> { +title }
            val menuItem = menuItem { +title }
            val stage = stage { +title }

            context.locale = Locale.ENGLISH

            assertEquals("English title", label.text)
            assertEquals("English title", text.text)
            assertEquals("中文输入", field.text)
            assertEquals("中文输入", area.text)
            assertEquals("English title", column.text)
            assertEquals("English title", menuItem.text)
            assertEquals("English title", stage.title)
            stage.close()
        }
    }

    @Test
    fun messageDialogAcceptsLocalizedHeadingContentAndButtons() {
        runOnJavaFxThread {
            val context = testContext()
            val title = context.localized("title")
            val root = VBox()
            val owner = Stage().apply { scene = Scene(root, 400.0, 300.0) }

            messageDialog(root) {
                heading(title)
                content(title)
                okButton(title)
                cancelButton(title)
            }
            context.locale = Locale.ENGLISH

            assertEquals("English title", title.value)
            owner.close()
        }
    }

    @Test
    fun fileFilterDescriptionUsesLocaleAtBuildTime() {
        runOnJavaFxThread {
            val context = testContext()
            val description = context.localized("filter.description")
            val firstChooser = fileChooser {
                filter(description, "*.txt")
                selectedFilter(description)
            }

            context.locale = Locale.ENGLISH
            val secondChooser = fileChooser {
                filter(description, "*.txt")
            }

            assertEquals("文本文件", firstChooser.extensionFilters.single().description)
            assertEquals("文本文件", firstChooser.selectedExtensionFilter.description)
            assertEquals("Text files", secondChooser.extensionFilters.single().description)
        }
    }

    private fun testContext() = I18nContext(
        baseName = "i18n.messages",
        supportedLocales = setOf(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH),
        initialLocale = Locale.SIMPLIFIED_CHINESE,
        classLoader = javaClass.classLoader,
    )

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
