package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class FileChooserDSLTest {
    @TempDir
    lateinit var directory: Path

    @Test
    fun fileChooserMatchesNativeConfigurationInBothModes() {
        for (mode in BuildMode.entries) {
            val expected = FileChooser().apply {
                title = "保存报表"
                initialDirectory = directory.toFile()
                initialFileName = "report.csv"
                extensionFilters.addAll(ExtensionFilter("表格", "*.csv", "*.tsv"), ExtensionFilter("所有文件", "*.*"))
                selectedExtensionFilter = extensionFilters[0]
            }
            val actual = fileChooser {
                setMode(mode)
                title("保存报表")
                initialDirectory(directory)
                initialFileName("report.csv")
                filter("表格", "*.csv", "*.tsv")
                allFilesFilter()
                selectedFilter("表格")
            }
            assertFileChooserEquals(expected, actual)
            assertSame(actual.extensionFilters[0], actual.selectedExtensionFilter)
        }
    }

    @Test
    fun predefinedFiltersMatchNativeExtensionListsAndOrder() {
        val expected = FileChooser().apply {
            title = "选择文件"
            extensionFilters.addAll(
                ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", "*.webp"),
                ExtensionFilter("视频文件", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv"),
                ExtensionFilter("音频文件", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg"),
                ExtensionFilter("文本文件", "*.txt", "*.log", "*.md"),
                ExtensionFilter("文档文件", "*.doc", "*.docx", "*.pdf", "*.xls", "*.xlsx", "*.ppt", "*.pptx"),
                ExtensionFilter("XML文件", "*.xml"),
                ExtensionFilter("JSON文件", "*.json"),
                ExtensionFilter("代码文件", "*.java", "*.kt", "*.js", "*.ts", "*.py", "*.cpp", "*.c", "*.h"),
                ExtensionFilter("压缩文件", "*.zip", "*.rar", "*.7z", "*.tar", "*.gz")
            )
        }
        val actual = fileChooser {
            imageFilter("*.webp")
            videoFilter()
            audioFilter()
            textFilter()
            documentFilter()
            xmlFilter()
            jsonFilter()
            codeFilter()
            archiveFilter()
        }
        assertFileChooserEquals(expected, actual)
    }

    @Test
    fun chooserConfigPreservesExistingFiltersAndIdentity() {
        val actual = FileChooser().apply { extensionFilters.add(ExtensionFilter("已有", "*.old")) }
        val expected = FileChooser().apply {
            extensionFilters.addAll(ExtensionFilter("已有", "*.old"), ExtensionFilter("新建", "*.new"))
            title = "更新"
            initialDirectory = directory.toFile()
            selectedExtensionFilter = extensionFilters[1]
        }
        assertSame(actual, actual.config {
            title("更新")
            initialDirectory(directory.toString())
            filter("新建", "*.new")
            selectedFilter("新建")
        })
        assertFileChooserEquals(expected, actual)
    }

    @Test
    fun directoryChooserMatchesNativeConstructionAndConfig() {
        for (mode in BuildMode.entries) {
            val expected = DirectoryChooser().apply {
                title = "选择输出目录"
                initialDirectory = directory.toFile()
            }
            val actual = directoryChooser {
                setMode(mode)
                title("选择输出目录")
                initialDirectory(directory.toFile())
            }
            assertEquals(expected.title, actual.title)
            assertEquals(expected.initialDirectory, actual.initialDirectory)
            assertSame(actual, actual.config { title("重新选择"); initialDirectory(directory.toString()) })
            expected.title = "重新选择"
            assertEquals(expected.title, actual.title)
            assertEquals(expected.initialDirectory, actual.initialDirectory)
        }
        assertEquals("选择文件夹", directoryChooser().title)
    }

    @Test
    fun invalidDirectoryIsIgnoredUnlessCheckingIsDisabled() {
        val missing = directory.resolve("missing").toFile()
        val expectedFile = FileChooser().apply { initialDirectory = directory.toFile() }
        val actualFile = fileChooser {
            initialDirectory(directory)
            initialDirectory(missing)
        }
        assertEquals(expectedFile.initialDirectory, actualFile.initialDirectory)
        val expectedDirectory = DirectoryChooser().apply { initialDirectory = directory.toFile() }
        val actualDirectory = directoryChooser {
            initialDirectory(directory)
            initialDirectory(missing)
        }
        assertEquals(expectedDirectory.initialDirectory, actualDirectory.initialDirectory)
        expectedFile.initialDirectory = missing
        expectedDirectory.initialDirectory = missing
        actualFile.config { initialDirectory(missing, autoCheck = false) }
        actualDirectory.config { initialDirectory(missing, autoCheck = false) }
        assertEquals(expectedFile.initialDirectory, actualFile.initialDirectory)
        assertEquals(expectedDirectory.initialDirectory, actualDirectory.initialDirectory)
    }

    @Test
    fun selectingUnknownFilterFailsInBothModes() {
        for (mode in BuildMode.entries) {
            assertFailsWith<IllegalArgumentException> {
                fileChooser {
                    setMode(mode)
                    filter("文本", "*.txt")
                    selectedFilter("缺失")
                }
            }
        }
    }

    private fun assertFileChooserEquals(expected: FileChooser, actual: FileChooser) {
        assertEquals(expected.title, actual.title)
        assertEquals(expected.initialDirectory, actual.initialDirectory)
        assertEquals(expected.initialFileName, actual.initialFileName)
        assertEquals(
            expected.extensionFilters.map { it.description to it.extensions },
            actual.extensionFilters.map { it.description to it.extensions })
        assertEquals(expected.selectedExtensionFilter?.description, actual.selectedExtensionFilter?.description)
    }
}
