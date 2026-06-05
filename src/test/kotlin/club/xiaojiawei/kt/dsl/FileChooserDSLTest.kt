package club.xiaojiawei.kt.dsl

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import kotlin.io.path.createTempDirectory
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class FileChooserDSLTest {

    @Test
    fun `fileChooser should configure file chooser`() {
        val chooser = fileChooser {
            title("导入数据")
            initialFileName("data.json")
            jsonFilter()
            allFilesFilter()
            selectedFilter("JSON文件")
        }

        assertEquals("导入数据", chooser.title)
        assertEquals("data.json", chooser.initialFileName)
        assertEquals(listOf("JSON文件", "所有文件"), chooser.extensionFilters.map { it.description })
        assertEquals(listOf("*.json"), chooser.extensionFilters[0].extensions)
        assertSame(chooser.extensionFilters[0], chooser.selectedExtensionFilter)
    }

    @Test
    fun `fileChooserBuilder should build file chooser`() {
        val chooser = fileChooserBuilder {
            title("保存文本")
            textFilter()
        }.build()

        assertEquals("保存文本", chooser.title)
        assertEquals("文本文件", chooser.extensionFilters.single().description)
    }

    @Test
    fun `FileChooser config should configure existing instance`() {
        val chooser = FileChooser().config {
            title("更新文件")
            initialFileName("output.txt")
            textFilter()
        }

        assertEquals("更新文件", chooser.title)
        assertEquals("output.txt", chooser.initialFileName)
        assertEquals("文本文件", chooser.extensionFilters.single().description)
    }

    @Test
    fun `directoryChooser should configure directory chooser`() {
        val initialDirectory = createTempDirectory("directory-chooser-dsl").toFile()

        val chooser = directoryChooser {
            title("选择输出目录")
            initialDirectory(initialDirectory)
        }

        assertEquals("选择输出目录", chooser.title)
        assertEquals(initialDirectory, chooser.initialDirectory)
    }

    @Test
    fun `DirectoryChooser config should configure existing instance`() {
        val initialDirectory = createTempDirectory("directory-chooser-config").toFile()

        val chooser = DirectoryChooser().config {
            title("更新目录")
            initialDirectory(initialDirectory)
        }

        assertEquals("更新目录", chooser.title)
        assertEquals(initialDirectory, chooser.initialDirectory)
    }

    @Test
    fun `invalid initial directory should throw exception`() {
        val invalidDirectory = createTempDirectory("invalid-directory-parent").toFile().resolve("missing")

        assertFailsWith<IllegalArgumentException> {
            fileChooser {
                initialDirectory(invalidDirectory)
            }
        }

        assertFailsWith<IllegalArgumentException> {
            directoryChooser {
                initialDirectory(invalidDirectory)
            }
        }
    }
}
