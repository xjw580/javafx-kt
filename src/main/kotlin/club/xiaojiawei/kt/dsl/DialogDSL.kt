//package club.xiaojiawei.kt.dsl
//
//import javafx.scene.control.*
//import javafx.scene.control.ButtonBar.ButtonData
//import javafx.scene.layout.GridPane
//import javafx.scene.layout.Priority
//import javafx.stage.Modality
//import javafx.stage.Window
//import javafx.util.Callback
//
///**
// * 对话框DSL
// * 简化Dialog创建
// * @author 肖嘉威
// */
//
//@FXMarker
//class AlertBuilder {
//
//    private var alertType: Alert.AlertType = Alert.AlertType.INFORMATION
//    private var title: String = ""
//    private var headerText: String? = null
//    private var contentText: String = ""
//    private var owner: Window? = null
//    private var modality: Modality = Modality.APPLICATION_MODAL
//    private val buttonTypes = mutableListOf<ButtonType>()
//    private var onResult: ((ButtonType) -> Unit)? = null
//
//    fun type(type: Alert.AlertType) {
//        this.alertType = type
//    }
//
//    fun information() {
//        alertType = Alert.AlertType.INFORMATION
//    }
//
//    fun warning() {
//        alertType = Alert.AlertType.WARNING
//    }
//
//    fun error() {
//        alertType = Alert.AlertType.ERROR
//    }
//
//    fun confirmation() {
//        alertType = Alert.AlertType.CONFIRMATION
//    }
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun content(content: String) {
//        this.contentText = content
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun modality(modality: Modality) {
//        this.modality = modality
//    }
//
//    fun buttons(vararg types: ButtonType) {
//        buttonTypes.clear()
//        buttonTypes.addAll(types)
//    }
//
//    fun onResult(handler: (ButtonType) -> Unit) {
//        this.onResult = handler
//    }
//
//    fun build(): Alert {
//        val alert = Alert(alertType)
//        alert.title = title
//        alert.headerText = headerText
//        alert.contentText = contentText
//
//        owner?.let { alert.initOwner(it) }
//        alert.initModality(modality)
//
//        if (buttonTypes.isNotEmpty()) {
//            alert.buttonTypes.setAll(buttonTypes)
//        }
//
//        return alert
//    }
//
//    fun show(): ButtonType? {
//        val alert = build()
//        val result = alert.showAndWait()
//        if (result.isPresent) {
//            onResult?.invoke(result.get())
//            return result.get()
//        }
//        return null
//    }
//}
//
//@FXMarker
//class TextInputDialogBuilder {
//
//    private var title: String = ""
//    private var headerText: String? = null
//    private var contentText: String = ""
//    private var defaultValue: String = ""
//    private var owner: Window? = null
//    private var onResult: ((String?) -> Unit)? = null
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun content(content: String) {
//        this.contentText = content
//    }
//
//    fun defaultValue(value: String) {
//        this.defaultValue = value
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun onResult(handler: (String?) -> Unit) {
//        this.onResult = handler
//    }
//
//    fun build(): TextInputDialog {
//        val dialog = TextInputDialog(defaultValue)
//        dialog.title = title
//        dialog.headerText = headerText
//        dialog.contentText = contentText
//        owner?.let { dialog.initOwner(it) }
//        return dialog
//    }
//
//    fun show(): String? {
//        val dialog = build()
//        val result = dialog.showAndWait()
//        val value = if (result.isPresent) result.get() else null
//        onResult?.invoke(value)
//        return value
//    }
//}
//
//@FXMarker
//class ChoiceDialogBuilder<T> {
//
//    private var title: String = ""
//    private var headerText: String? = null
//    private var contentText: String = ""
//    private var defaultChoice: T? = null
//    private val choices = mutableListOf<T>()
//    private var owner: Window? = null
//    private var onResult: ((T?) -> Unit)? = null
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun content(content: String) {
//        this.contentText = content
//    }
//
//    fun defaultChoice(choice: T) {
//        this.defaultChoice = choice
//    }
//
//    fun choices(vararg items: T) {
//        choices.clear()
//        choices.addAll(items)
//    }
//
//    fun choices(items: List<T>) {
//        choices.clear()
//        choices.addAll(items)
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun onResult(handler: (T?) -> Unit) {
//        this.onResult = handler
//    }
//
//    fun build(): ChoiceDialog<T> {
//        val dialog = ChoiceDialog<T>(defaultChoice, choices)
//        dialog.title = title
//        dialog.headerText = headerText
//        dialog.contentText = contentText
//        owner?.let { dialog.initOwner(it) }
//        return dialog
//    }
//
//    fun show(): T? {
//        val dialog = build()
//        val result = dialog.showAndWait()
//        val value = if (result.isPresent) result.get() else null
//        onResult?.invoke(value)
//        return value
//    }
//}
//
//@FXMarker
//class CustomDialogBuilder<R> {
//
//    private var title: String = ""
//    private var headerText: String? = null
//    private var owner: Window? = null
//    private var contentBuilder: (() -> javafx.scene.layout.Pane)? = null
//    private val buttonData = mutableListOf<Pair<String, ButtonData>>()
//    private var resultConverter: ((ButtonType) -> R?)? = null
//    private var onShowing: (() -> Unit)? = null
//    private var onShown: (() -> Unit)? = null
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun content(builder: () -> javafx.scene.layout.Pane) {
//        this.contentBuilder = builder
//    }
//
//    inline fun contentVBox(crossinline block: VBoxBuilder.() -> Unit) {
//        contentBuilder = { VBoxBuilder().apply(block).build() }
//    }
//
//    inline fun contentGridPane(crossinline block: GridPaneBuilder.() -> Unit) {
//        contentBuilder = { GridPaneBuilder().apply(block).build() }
//    }
//
//    fun addButton(text: String, data: ButtonData = ButtonData.OTHER) {
//        buttonData.add(text to data)
//    }
//
//    fun okButton(text: String = "确定") {
//        addButton(text, ButtonData.OK_DONE)
//    }
//
//    fun cancelButton(text: String = "取消") {
//        addButton(text, ButtonData.CANCEL_CLOSE)
//    }
//
//    fun resultConverter(converter: (ButtonType) -> R?) {
//        this.resultConverter = converter
//    }
//
//    fun onShowing(handler: () -> Unit) {
//        this.onShowing = handler
//    }
//
//    fun onShown(handler: () -> Unit) {
//        this.onShown = handler
//    }
//
//    fun build(): Dialog<R> {
//        val dialog = Dialog<R>()
//        dialog.title = title
//        dialog.headerText = headerText
//        owner?.let { dialog.initOwner(it) }
//
//        contentBuilder?.let {
//            dialog.dialogPane.content = it()
//        }
//
//        // 添加按钮
//        val buttonTypes = buttonData.map { (text, data) ->
//            ButtonType(text, data)
//        }
//        dialog.dialogPane.buttonTypes.setAll(buttonTypes)
//
//        // 设置结果转换器
//        resultConverter?.let {
//            dialog.resultConverter = Callback(it)
//        }
//
//        // 事件处理
//        onShowing?.let { handler ->
//            dialog.setOnShowing { handler() }
//        }
//
//        onShown?.let { handler ->
//            dialog.setOnShown { handler() }
//        }
//
//        return dialog
//    }
//
//    fun show(): R? {
//        val dialog = build()
//        val result = dialog.showAndWait()
//        return if (result.isPresent) result.get() else null
//    }
//}
//
//// 进度对话框
//@FXMarker
//class ProgressDialogBuilder {
//
//    private var title: String = "请稍候..."
//    private var headerText: String? = "正在处理"
//    private var contentText: String = ""
//    private var progress: Double = -1.0
//    private var owner: Window? = null
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun content(content: String) {
//        this.contentText = content
//    }
//
//    fun progress(progress: Double) {
//        this.progress = progress
//    }
//
//    fun indeterminate() {
//        this.progress = -1.0
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun build(): Dialog<Void> {
//        val dialog = Dialog<Void>()
//        dialog.title = title
//        dialog.headerText = headerText
//        owner?.let { dialog.initOwner(it) }
//
//        val progressBar = ProgressBar(progress)
//        progressBar.prefWidth(300.0)
//
//        val content = vbox {
//            spacing(10.0)
//            padding(20.0)
//
//            if (contentText.isNotEmpty()) {
//                addLabel(contentText)
//            }
//            add(progressBar)
//        }
//
//        dialog.dialogPane.content = content
//
//        return dialog
//    }
//
//    fun show(): Dialog<Void> {
//        val dialog = build()
//        dialog.show()
//        return dialog
//    }
//}
//
//// 表单对话框
//@FXMarker
//class FormDialogBuilder {
//
//    data class Field(
//        val label: String,
//        val key: String,
//        val defaultValue: String = "",
//        val required: Boolean = false,
//        val validator: ((String) -> Boolean)? = null
//    )
//
//    private var title: String = ""
//    private var headerText: String? = null
//    private var owner: Window? = null
//    private val fields = mutableListOf<Field>()
//    private val textFields = mutableMapOf<String, TextField>()
//
//    fun title(title: String) {
//        this.title = title
//    }
//
//    fun header(header: String?) {
//        this.headerText = header
//    }
//
//    fun owner(owner: Window) {
//        this.owner = owner
//    }
//
//    fun field(
//        label: String,
//        key: String,
//        defaultValue: String = "",
//        required: Boolean = false,
//        validator: ((String) -> Boolean)? = null
//    ) {
//        fields.add(Field(label, key, defaultValue, required, validator))
//    }
//
//    fun build(): Dialog<Map<String, String>> {
//        val dialog = Dialog<Map<String, String>>()
//        dialog.title = title
//        dialog.headerText = headerText
//        owner?.let { dialog.initOwner(it) }
//
//        val grid = GridPane()
//        grid.hgap = 10.0
//        grid.vgap = 10.0
//        grid.padding = javafx.geometry.Insets(20.0)
//
//        fields.forEachIndexed { index, field ->
//            val label = Label(field.label + if (field.required) " *" else "")
//            val textField = TextField(field.defaultValue)
//            textField.promptText = field.label
//
//            textFields[field.key] = textField
//
//            grid.add(label, 0, index)
//            grid.add(textField, 1, index)
//
//            GridPane.setHgrow(textField, Priority.ALWAYS)
//        }
//
//        dialog.dialogPane.content = grid
//        dialog.dialogPane.buttonTypes.addAll(ButtonType.OK, ButtonType.CANCEL)
//
//        // 结果转换
//        dialog.resultConverter = Callback { buttonType ->
//            if (buttonType == ButtonType.OK) {
//                val result = mutableMapOf<String, String>()
//                textFields.forEach { (key, textField) ->
//                    result[key] = textField.text
//                }
//
//                // 验证
//                var valid = true
//                fields.forEach { field ->
//                    val value = textFields[field.key]?.text ?: ""
//                    if (field.required && value.isBlank()) {
//                        valid = false
//                    }
//                    if (field.validator != null && !field.validator.invoke(value)) {
//                        valid = false
//                    }
//                }
//
//                if (valid) result else null
//            } else {
//                null
//            }
//        }
//
//        // 聚焦第一个字段
//        javafx.application.Platform.runLater {
//            textFields.values.firstOrNull()?.requestFocus()
//        }
//
//        return dialog
//    }
//
//    fun show(): Map<String, String>? {
//        val dialog = build()
//        val result = dialog.showAndWait()
//        return if (result.isPresent) result.get() else null
//    }
//}
//
//// DSL入口函数
//inline fun alert(block: AlertBuilder.() -> Unit): AlertBuilder {
//    return AlertBuilder().apply(block)
//}
//
//inline fun textInputDialog(block: TextInputDialogBuilder.() -> Unit): TextInputDialogBuilder {
//    return TextInputDialogBuilder().apply(block)
//}
//
//inline fun <T> choiceDialog(block: ChoiceDialogBuilder<T>.() -> Unit): ChoiceDialogBuilder<T> {
//    return ChoiceDialogBuilder<T>().apply(block)
//}
//
//inline fun <R> customDialog(block: CustomDialogBuilder<R>.() -> Unit): CustomDialogBuilder<R> {
//    return CustomDialogBuilder<R>().apply(block)
//}
//
//inline fun progressDialog(block: ProgressDialogBuilder.() -> Unit): ProgressDialogBuilder {
//    return ProgressDialogBuilder().apply(block)
//}
//
//inline fun formDialog(block: FormDialogBuilder.() -> Unit): FormDialogBuilder {
//    return FormDialogBuilder().apply(block)
//}
//
//// 便捷函数
//object Dialogs {
//
//    fun showInfo(title: String, header: String? = null, content: String, owner: Window? = null) {
//        alert {
//            information()
//            title(title)
//            header(header)
//            content(content)
//            owner?.let { owner(it) }
//        }.show()
//    }
//
//    fun showWarning(title: String, header: String? = null, content: String, owner: Window? = null) {
//        alert {
//            warning()
//            title(title)
//            header(header)
//            content(content)
//            owner?.let { owner(it) }
//        }.show()
//    }
//
//    fun showError(title: String, header: String? = null, content: String, owner: Window? = null) {
//        alert {
//            error()
//            title(title)
//            header(header)
//            content(content)
//            owner?.let { owner(it) }
//        }.show()
//    }
//
//    fun confirm(
//        title: String,
//        header: String? = null,
//        content: String,
//        owner: Window? = null
//    ): Boolean {
//        val result = alert {
//            confirmation()
//            title(title)
//            header(header)
//            content(content)
//            owner?.let { owner(it) }
//        }.show()
//        return result == ButtonType.OK
//    }
//
//    fun input(
//        title: String,
//        header: String? = null,
//        content: String,
//        defaultValue: String = "",
//        owner: Window? = null
//    ): String? {
//        return textInputDialog {
//            title(title)
//            header(header)
//            content(content)
//            defaultValue(defaultValue)
//            owner?.let { owner(it) }
//        }.show()
//    }
//
//    fun <T> choose(
//        title: String,
//        header: String? = null,
//        content: String,
//        choices: List<T>,
//        defaultChoice: T? = null,
//        owner: Window? = null
//    ): T? {
//        return choiceDialog<T> {
//            title(title)
//            header(header)
//            content(content)
//            choices(choices)
//            defaultChoice?.let { defaultChoice(it) }
//            owner?.let { owner(it) }
//        }.show()
//    }
//}
