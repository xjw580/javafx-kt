package club.xiaojiawei.kt.dsl

import javafx.stage.DirectoryChooser
import javafx.stage.FileChooser
import javafx.stage.FileChooser.ExtensionFilter
import javafx.stage.Window
import java.io.File

/**
 * 文件选择器DSL
 * 简化FileChooser和DirectoryChooser使用
 * @author 肖嘉威
 */

@FXMarker
class FileChooserBuilder {
    
    private var title: String = "选择文件"
    private var initialDirectory: File? = null
    private var initialFileName: String? = null
    private val extensionFilters = mutableListOf<ExtensionFilter>()
    private var selectedExtensionFilter: ExtensionFilter? = null
    
    fun title(title: String) {
        this.title = title
    }
    
    fun initialDirectory(path: String) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            this.initialDirectory = dir
        }
    }
    
    fun initialDirectory(file: File) {
        if (file.exists() && file.isDirectory) {
            this.initialDirectory = file
        }
    }
    
    fun initialFileName(name: String) {
        this.initialFileName = name
    }
    
    fun filter(description: String, vararg extensions: String) {
        extensionFilters.add(ExtensionFilter(description, extensions.toList()))
    }
    
    fun allFilesFilter() {
        extensionFilters.add(ExtensionFilter("所有文件", "*.*"))
    }
    
    fun imageFilter() {
        extensionFilters.add(ExtensionFilter("图片文件", "*.png", "*.jpg", "*.jpeg", "*.gif", "*.bmp"))
    }
    
    fun videoFilter() {
        extensionFilters.add(ExtensionFilter("视频文件", "*.mp4", "*.avi", "*.mkv", "*.mov", "*.flv"))
    }
    
    fun audioFilter() {
        extensionFilters.add(ExtensionFilter("音频文件", "*.mp3", "*.wav", "*.flac", "*.aac", "*.ogg"))
    }
    
    fun textFilter() {
        extensionFilters.add(ExtensionFilter("文本文件", "*.txt", "*.log", "*.md"))
    }
    
    fun documentFilter() {
        extensionFilters.add(
            ExtensionFilter("文档文件", "*.doc", "*.docx", "*.pdf", "*.xls", "*.xlsx", "*.ppt", "*.pptx")
        )
    }
    
    fun xmlFilter() {
        extensionFilters.add(ExtensionFilter("XML文件", "*.xml"))
    }
    
    fun jsonFilter() {
        extensionFilters.add(ExtensionFilter("JSON文件", "*.json"))
    }
    
    fun codeFilter() {
        extensionFilters.add(
            ExtensionFilter(
                "代码文件",
                "*.java", "*.kt", "*.js", "*.ts", "*.py", "*.cpp", "*.c", "*.h"
            )
        )
    }
    
    fun archiveFilter() {
        extensionFilters.add(ExtensionFilter("压缩文件", "*.zip", "*.rar", "*.7z", "*.tar", "*.gz"))
    }
    
    fun selectedFilter(description: String) {
        selectedExtensionFilter = extensionFilters.find { it.description == description }
    }
    
    fun build(): FileChooser {
        val fileChooser = FileChooser()
        fileChooser.title = title
        initialDirectory?.let { fileChooser.initialDirectory = it }
        initialFileName?.let { fileChooser.initialFileName = it }
        
        if (extensionFilters.isNotEmpty()) {
            fileChooser.extensionFilters.addAll(extensionFilters)
            selectedExtensionFilter?.let {
                fileChooser.selectedExtensionFilter = it
            }
        }
        
        return fileChooser
    }
    
    fun showOpen(owner: Window? = null): File? {
        return build().showOpenDialog(owner)
    }
    
    fun showOpenMultiple(owner: Window? = null): List<File>? {
        return build().showOpenMultipleDialog(owner)
    }
    
    fun showSave(owner: Window? = null): File? {
        return build().showSaveDialog(owner)
    }
}

@FXMarker
class DirectoryChooserBuilder {
    
    private var title: String = "选择文件夹"
    private var initialDirectory: File? = null
    
    fun title(title: String) {
        this.title = title
    }
    
    fun initialDirectory(path: String) {
        val dir = File(path)
        if (dir.exists() && dir.isDirectory) {
            this.initialDirectory = dir
        }
    }
    
    fun initialDirectory(file: File) {
        if (file.exists() && file.isDirectory) {
            this.initialDirectory = file
        }
    }
    
    fun build(): DirectoryChooser {
        val directoryChooser = DirectoryChooser()
        directoryChooser.title = title
        initialDirectory?.let { directoryChooser.initialDirectory = it }
        return directoryChooser
    }
    
    fun show(owner: Window? = null): File? {
        return build().showDialog(owner)
    }
}

// DSL入口函数
inline fun fileChooser(block: FileChooserBuilder.() -> Unit): FileChooserBuilder {
    return FileChooserBuilder().apply(block)
}

inline fun directoryChooser(block: DirectoryChooserBuilder.() -> Unit): DirectoryChooserBuilder {
    return DirectoryChooserBuilder().apply(block)
}

// 便捷函数
object FileChoosers {
    
    /**
     * 选择单个文件
     */
    fun openFile(
        title: String = "打开文件",
        initialDir: String? = null,
        owner: Window? = null,
        filters: (FileChooserBuilder.() -> Unit)? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            filters?.invoke(this)
        }.showOpen(owner)
    }
    
    /**
     * 选择多个文件
     */
    fun openFiles(
        title: String = "打开文件",
        initialDir: String? = null,
        owner: Window? = null,
        filters: (FileChooserBuilder.() -> Unit)? = null
    ): List<File>? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            filters?.invoke(this)
        }.showOpenMultiple(owner)
    }
    
    /**
     * 保存文件
     */
    fun saveFile(
        title: String = "保存文件",
        initialDir: String? = null,
        initialFileName: String? = null,
        owner: Window? = null,
        filters: (FileChooserBuilder.() -> Unit)? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            initialFileName?.let { initialFileName(it) }
            filters?.invoke(this)
        }.showSave(owner)
    }
    
    /**
     * 选择文件夹
     */
    fun openDirectory(
        title: String = "选择文件夹",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return directoryChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
        }.show(owner)
    }
    
    /**
     * 选择图片文件
     */
    fun openImage(
        title: String = "选择图片",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            imageFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 选择多个图片文件
     */
    fun openImages(
        title: String = "选择图片",
        initialDir: String? = null,
        owner: Window? = null
    ): List<File>? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            imageFilter()
            allFilesFilter()
        }.showOpenMultiple(owner)
    }
    
    /**
     * 选择视频文件
     */
    fun openVideo(
        title: String = "选择视频",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            videoFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 选择音频文件
     */
    fun openAudio(
        title: String = "选择音频",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            audioFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 选择文本文件
     */
    fun openText(
        title: String = "打开文本文件",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            textFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 保存文本文件
     */
    fun saveText(
        title: String = "保存文本文件",
        initialDir: String? = null,
        initialFileName: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            initialFileName?.let { initialFileName(it) }
            textFilter()
            allFilesFilter()
        }.showSave(owner)
    }
    
    /**
     * 选择JSON文件
     */
    fun openJson(
        title: String = "打开JSON文件",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            jsonFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 保存JSON文件
     */
    fun saveJson(
        title: String = "保存JSON文件",
        initialDir: String? = null,
        initialFileName: String = "data.json",
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            initialFileName(initialFileName)
            jsonFilter()
            allFilesFilter()
        }.showSave(owner)
    }
    
    /**
     * 选择XML文件
     */
    fun openXml(
        title: String = "打开XML文件",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            xmlFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
    
    /**
     * 选择代码文件
     */
    fun openCode(
        title: String = "打开代码文件",
        initialDir: String? = null,
        owner: Window? = null
    ): File? {
        return fileChooser {
            title(title)
            initialDir?.let { initialDirectory(it) }
            codeFilter()
            allFilesFilter()
        }.showOpen(owner)
    }
}

/**
 * 文件选择结果处理器
 */
@FXMarker
class FileSelectionHandler {
    
    private var onSuccess: ((File) -> Unit)? = null
    private var onMultipleSuccess: ((List<File>) -> Unit)? = null
    private var onCancel: (() -> Unit)? = null
    private var onError: ((Exception) -> Unit)? = null
    
    fun onSuccess(handler: (File) -> Unit) {
        this.onSuccess = handler
    }
    
    fun onMultipleSuccess(handler: (List<File>) -> Unit) {
        this.onMultipleSuccess = handler
    }
    
    fun onCancel(handler: () -> Unit) {
        this.onCancel = handler
    }
    
    fun onError(handler: (Exception) -> Unit) {
        this.onError = handler
    }
    
    fun handleSingle(file: File?) {
        try {
            if (file != null) {
                onSuccess?.invoke(file)
            } else {
                onCancel?.invoke()
            }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
    
    fun handleMultiple(files: List<File>?) {
        try {
            if (files != null && files.isNotEmpty()) {
                onMultipleSuccess?.invoke(files)
            } else {
                onCancel?.invoke()
            }
        } catch (e: Exception) {
            onError?.invoke(e)
        }
    }
}

// 带结果处理的文件选择
inline fun selectFile(
    owner: Window? = null,
    crossinline chooserConfig: FileChooserBuilder.() -> Unit = {},
    crossinline handler: FileSelectionHandler.() -> Unit
) {
    val file = fileChooser(chooserConfig).showOpen(owner)
    FileSelectionHandler().apply(handler).handleSingle(file)
}

inline fun selectFiles(
    owner: Window? = null,
    crossinline chooserConfig: FileChooserBuilder.() -> Unit = {},
    crossinline handler: FileSelectionHandler.() -> Unit
) {
    val files = fileChooser(chooserConfig).showOpenMultiple(owner)
    FileSelectionHandler().apply(handler).handleMultiple(files)
}

inline fun selectSaveFile(
    owner: Window? = null,
    crossinline chooserConfig: FileChooserBuilder.() -> Unit = {},
    crossinline handler: FileSelectionHandler.() -> Unit
) {
    val file = fileChooser(chooserConfig).showSave(owner)
    FileSelectionHandler().apply(handler).handleSingle(file)
}

inline fun selectDirectory(
    owner: Window? = null,
    crossinline chooserConfig: DirectoryChooserBuilder.() -> Unit = {},
    crossinline handler: FileSelectionHandler.() -> Unit
) {
    val directory = directoryChooser(chooserConfig).show(owner)
    FileSelectionHandler().apply(handler).handleSingle(directory)
}

/**
 * 文件过滤器预设
 */
object FileFilterPresets {
    
    val IMAGE = { builder: FileChooserBuilder ->
        builder.imageFilter()
    }
    
    val VIDEO = { builder: FileChooserBuilder ->
        builder.videoFilter()
    }
    
    val AUDIO = { builder: FileChooserBuilder ->
        builder.audioFilter()
    }
    
    val TEXT = { builder: FileChooserBuilder ->
        builder.textFilter()
    }
    
    val DOCUMENT = { builder: FileChooserBuilder ->
        builder.documentFilter()
    }
    
    val CODE = { builder: FileChooserBuilder ->
        builder.codeFilter()
    }
    
    val ARCHIVE = { builder: FileChooserBuilder ->
        builder.archiveFilter()
    }
    
    val ALL_MEDIA = { builder: FileChooserBuilder ->
        builder.imageFilter()
        builder.videoFilter()
        builder.audioFilter()
    }
    
    val ALL = { builder: FileChooserBuilder ->
        builder.allFilesFilter()
    }
}
