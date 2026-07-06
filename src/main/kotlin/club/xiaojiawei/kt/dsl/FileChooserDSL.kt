@file:Suppress("unused")

package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Window
import java.io.File
import java.nio.file.Path

/**
 * 文件选择器 DSL
 * @author 肖嘉威
 */

private const val DEFAULT_FILE_CHOOSER_TITLE = "选择文件"
private const val DEFAULT_DIRECTORY_CHOOSER_TITLE = "选择文件夹"

@FXMarker
class FileChooserBuilder : DslBuilder<FileChooser>() {

    override fun buildInstance(): FileChooser = FileChooser().apply {
        title = DEFAULT_FILE_CHOOSER_TITLE
    }

    fun title(title: String) = settings {
        this.title = title
    }

    fun initialDirectory(path: String) = initialDirectory(File(path))

    fun initialDirectory(path: Path) = initialDirectory(path.toFile())

    fun initialDirectory(file: File, autoCheck: Boolean = true) = settings {
        if ((file.exists() && file.isDirectory) || !autoCheck) {
            initialDirectory = file
        }
    }

    fun initialFileName(name: String) = settings {
        initialFileName = name
    }

    fun filter(description: String, vararg extensions: String) = settings {
        extensionFilters.add(ExtensionFilter(description, extensions.toList()))
    }

    fun allFilesFilter() = filter("所有文件", "*.*")

    fun imageFilter(vararg extensions: String = emptyArray()) =
        filter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp", *extensions)

    fun videoFilter(vararg extensions: String = emptyArray()) =
        filter("视频文件", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv", *extensions)

    fun audioFilter(vararg extensions: String = emptyArray()) =
        filter("音频文件", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg", *extensions)

    fun textFilter(vararg extensions: String = emptyArray()) = filter("文本文件", "*.txt", "*.log", "*.md", *extensions)

    fun documentFilter(vararg extensions: String = emptyArray()) =
        filter("文档文件", "*.doc", "*.docx", "*.pdf", "*.xls", "*.xlsx", "*.ppt", "*.pptx", *extensions)

    fun xmlFilter(vararg extensions: String = emptyArray()) = filter("XML文件", "*.xml", *extensions)

    fun jsonFilter(vararg extensions: String = emptyArray()) = filter("JSON文件", "*.json", *extensions)

    fun codeFilter(vararg extensions: String = emptyArray()) =
        filter("代码文件", "*.java", "*.kt", "*.js", "*.ts", "*.py", "*.cpp", "*.c", "*.h", *extensions)

    fun archiveFilter(vararg extensions: String = emptyArray()) =
        filter("压缩文件", "*.zip", "*.rar", "*.7z", "*.tar", "*.gz", *extensions)

    fun selectedFilter(description: String) = settings {
        selectedExtensionFilter = requireNotNull(extensionFilters.find { it.description == description }) {
            "未找到文件过滤器: $description"
        }
    }
}

@FXMarker
class DirectoryChooserBuilder : DslBuilder<DirectoryChooser>() {

    override fun buildInstance(): DirectoryChooser = DirectoryChooser().apply {
        title = DEFAULT_DIRECTORY_CHOOSER_TITLE
    }

    fun title(title: String) = settings {
        this.title = title
    }

    fun initialDirectory(path: String) = initialDirectory(File(path))

    fun initialDirectory(path: Path) = initialDirectory(path.toFile())

    fun initialDirectory(file: File, autoCheck: Boolean = true) = settings {
        if ((file.exists() && file.isDirectory) || !autoCheck) {
            initialDirectory = file
        }
    }
}

inline fun showFileSaveDialog(ownerWidow: Window? = null, config: FileChooserBuilder.() -> Unit = {}): File? =
    fileChooserBuilder(config).build().showSaveDialog(ownerWidow)

inline fun showFileOpenDialog(ownerWidow: Window? = null, config: FileChooserBuilder.() -> Unit = {}): File? =
    fileChooserBuilder(config).build().showOpenDialog(ownerWidow)

inline fun showFileMultipleDialog(ownerWidow: Window? = null, config: FileChooserBuilder.() -> Unit = {}): List<File> =
    fileChooserBuilder(config).build().showOpenMultipleDialog(ownerWidow) ?: emptyList()

inline fun showDirectoryChooserDialog(
    ownerWidow: Window? = null,
    config: DirectoryChooserBuilder.() -> Unit = {}
): File? =
    directoryChooserBuilder(config).build().showDialog(ownerWidow)

inline fun fileChooser(config: FileChooserBuilder.() -> Unit = {}): FileChooser =
    fileChooserBuilder(config).build()

inline fun fileChooserBuilder(config: FileChooserBuilder.() -> Unit = {}): FileChooserBuilder =
    FileChooserBuilder().apply(config)

fun fileChooserConfig(config: FileChooserBuilder.() -> Unit): FileChooserBuilder.() -> Unit =
    config

inline fun FileChooser.config(config: FileChooserBuilder.() -> Unit): FileChooser =
    apply {
        FileChooserBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }

inline fun directoryChooser(config: DirectoryChooserBuilder.() -> Unit = {}): DirectoryChooser =
    directoryChooserBuilder(config).build()

inline fun directoryChooserBuilder(config: DirectoryChooserBuilder.() -> Unit = {}): DirectoryChooserBuilder =
    DirectoryChooserBuilder().apply(config)

fun directoryChooserConfig(config: DirectoryChooserBuilder.() -> Unit): DirectoryChooserBuilder.() -> Unit =
    config

inline fun DirectoryChooser.config(config: DirectoryChooserBuilder.() -> Unit): DirectoryChooser =
    apply {
        DirectoryChooserBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }