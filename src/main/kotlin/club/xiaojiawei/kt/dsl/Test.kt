package club.xiaojiawei.kt.dsl

/**
 * @author 肖嘉威
 * @date 2025/8/12 16:18
 */

import javafx.application.Application
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.*
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.*
import javafx.stage.Stage
import javafx.util.StringConverter
import javafx.util.converter.NumberStringConverter

// =============================================================================
// DSL 构建器基础设施
// =============================================================================

// 基础容器构建器
abstract class ContainerBuilder<T : Pane> {
    protected val container: T
    protected val children = mutableListOf<Node>()

    constructor(container: T) {
        this.container = container
    }

    fun build(): T {
        container.children.addAll(children)
        return container
    }

    // 添加任意节点
    fun node(node: Node) {
        children.add(node)
    }
}

// StackPane DSL 构建器
class StackPaneBuilder : ContainerBuilder<StackPane>(StackPane()) {
    var alignment: Pos
        get() = container.alignment
        set(value) { container.alignment = value }

    fun button(text: String = "", config: Button.() -> Unit = {}) {
        children.add(Button(text).apply(config))
    }

    fun label(text: String = "", config: Label.() -> Unit = {}) {
        children.add(Label(text).apply(config))
    }

    fun textField(config: TextField.() -> Unit = {}) {
        children.add(TextField().apply(config))
    }

    fun vbox(config: VBoxBuilder.() -> Unit) {
        children.add(VBoxBuilder().apply(config).build())
    }

    fun hbox(config: HBoxBuilder.() -> Unit) {
        children.add(HBoxBuilder().apply(config).build())
    }
}

// VBox DSL 构建器
class VBoxBuilder : ContainerBuilder<VBox>(VBox()) {
    var spacing: Double
        get() = container.spacing
        set(value) { container.spacing = value }

    var alignment: Pos
        get() = container.alignment
        set(value) { container.alignment = value }

    // 暴露容器的属性绑定方法
    fun visibleProperty() = container.visibleProperty()
    fun managedProperty() = container.managedProperty()
    fun disableProperty() = container.disableProperty()

    // 便捷的绑定方法
    fun bindVisible(binding: javafx.beans.binding.BooleanBinding) {
        container.visibleProperty().bind(binding)
    }

    fun bindManaged(binding: javafx.beans.binding.BooleanBinding) {
        container.managedProperty().bind(binding)
    }

    fun bindDisable(binding: javafx.beans.binding.BooleanBinding) {
        container.disableProperty().bind(binding)
    }

    fun button(text: String = "", config: Button.() -> Unit = {}) {
        children.add(Button(text).apply(config))
    }

    fun label(text: String = "", config: Label.() -> Unit = {}) {
        children.add(Label(text).apply(config))
    }

    fun textField(config: TextField.() -> Unit = {}) {
        children.add(TextField().apply(config))
    }

    fun hbox(config: HBoxBuilder.() -> Unit) {
        children.add(HBoxBuilder().apply(config).build())
    }
}

// HBox DSL 构建器
class HBoxBuilder : ContainerBuilder<HBox>(HBox()) {
    var spacing: Double
        get() = container.spacing
        set(value) { container.spacing = value }

    var alignment: Pos
        get() = container.alignment
        set(value) { container.alignment = value }

    // 暴露容器的属性绑定方法
    fun visibleProperty() = container.visibleProperty()
    fun managedProperty() = container.managedProperty()
    fun disableProperty() = container.disableProperty()

    // 便捷的绑定方法
    fun bindVisible(binding: javafx.beans.binding.BooleanBinding) {
        container.visibleProperty().bind(binding)
    }

    fun bindManaged(binding: javafx.beans.binding.BooleanBinding) {
        container.managedProperty().bind(binding)
    }

    fun bindDisable(binding: javafx.beans.binding.BooleanBinding) {
        container.disableProperty().bind(binding)
    }

    fun button(text: String = "", config: Button.() -> Unit = {}) {
        children.add(Button(text).apply(config))
    }

    fun label(text: String = "", config: Label.() -> Unit = {}) {
        children.add(Label(text).apply(config))
    }

    fun textField(config: TextField.() -> Unit = {}) {
        children.add(TextField().apply(config))
    }
}

// DSL 入口函数
fun stackPane(config: StackPaneBuilder.() -> Unit): StackPane {
    return StackPaneBuilder().apply(config).build()
}

fun vbox(config: VBoxBuilder.() -> Unit): VBox {
    return VBoxBuilder().apply(config).build()
}

fun hbox(config: HBoxBuilder.() -> Unit): HBox {
    return HBoxBuilder().apply(config).build()
}

// =============================================================================
// MVVM 架构 - Model层
// =============================================================================

data class User(
    val id: String,
    val name: String,
    val email: String,
    val age: Int
)

// 数据访问接口
interface UserRepository {
     fun getUserById(id: String): User?
     fun saveUser(user: User): Boolean
     fun getAllUsers(): List<User>
}

// 模拟实现
class MockUserRepository : UserRepository {
    private val users = mutableMapOf(
        "1" to User("1", "张三", "zhangsan@example.com", 25),
        "2" to User("2", "李四", "lisi@example.com", 30)
    )

    override  fun getUserById(id: String): User? = users[id]

    override  fun saveUser(user: User): Boolean {
        users[user.id] = user
        return true
    }

    override  fun getAllUsers(): List<User> = users.values.toList()
}

// =============================================================================
// MVVM 架构 - ViewModel层
// =============================================================================

// 基础ViewModel
abstract class BaseViewModel {
    val isLoading = SimpleBooleanProperty(false)
    val errorMessage = SimpleStringProperty("")

    protected fun setLoading(loading: Boolean) {
        isLoading.set(loading)
    }

    protected fun setError(message: String) {
        errorMessage.set(message)
    }

    protected fun clearError() {
        errorMessage.set("")
    }
}

// 用户详情ViewModel
class UserDetailViewModel(
    private val userRepository: UserRepository = MockUserRepository()
) : BaseViewModel() {

    // UI绑定属性
    val userName = SimpleStringProperty("")
    val userEmail = SimpleStringProperty("")
    val userAge = SimpleStringProperty("")
    val isEditMode = SimpleBooleanProperty(false)

    // 命令属性
    val canSave = SimpleBooleanProperty(false)

    private var currentUserId: String? = null

    init {
        // 设置验证逻辑
        setupValidation()
    }

    private fun setupValidation() {
        val nameValid = userName.isNotEmpty
        val emailValid = userEmail.isNotEmpty
        val ageValid = userAge.isNotEmpty

        canSave.bind(nameValid.and(emailValid).and(ageValid).and(isEditMode))
    }

    // 加载用户数据
    fun loadUser(userId: String) {
        currentUserId = userId
        setLoading(true)
        clearError()

        // 模拟异步加载
        Thread {
            try {
                Thread.sleep(1000) // 模拟网络延迟
                val user = userRepository.getUserById(userId)

                javafx.application.Platform.runLater {
                    if (user != null) {
                        userName.set(user.name)
                        userEmail.set(user.email)
                        userAge.set(user.age.toString())
                    } else {
                        setError("用户不存在")
                    }
                    setLoading(false)
                }
            } catch (e: Exception) {
                javafx.application.Platform.runLater {
                    setError("加载用户失败: ${e.message}")
                    setLoading(false)
                }
            }
        }.start()
    }

    // 进入编辑模式
    fun startEdit() {
        isEditMode.set(true)
        clearError()
    }

    // 取消编辑
    fun cancelEdit() {
        isEditMode.set(false)
        currentUserId?.let { loadUser(it) } // 重新加载原数据
    }

    // 保存用户
    fun saveUser() {
        if (!canSave.get()) return

        val userId = currentUserId ?: return
        val user = User(
            id = userId,
            name = userName.get(),
            email = userEmail.get(),
            age = userAge.get().toIntOrNull() ?: 0
        )

        setLoading(true)
        clearError()

        Thread {
            try {
                val success = userRepository.saveUser(user)
                javafx.application.Platform.runLater {
                    if (success) {
                        isEditMode.set(false)
                        setError("保存成功")
                    } else {
                        setError("保存失败")
                    }
                    setLoading(false)
                }
            } catch (e: Exception) {
                javafx.application.Platform.runLater {
                    setError("保存失败: ${e.message}")
                    setLoading(false)
                }
            }
        }.start()
    }
}

// 扩展属性，简化绑定语法
val StringProperty.isNotEmpty: BooleanBinding
    get() = this.isNotEqualTo("")

// =============================================================================
// MVVM 架构 - View层
// =============================================================================

class UserDetailView(private val viewModel: UserDetailViewModel) {

    // 方案1：使用成员变量保存节点引用
    private lateinit var nameTextField: TextField
    private lateinit var emailTextField: TextField
    private lateinit var ageTextField: TextField
    private lateinit var statusLabel: Label

    fun createView(): StackPane = stackPane {
        alignment = Pos.CENTER

        vbox {
            spacing = 20.0
            alignment = Pos.CENTER

            // 标题
            label("用户详情") {
                style = "-fx-font-size: 24px; -fx-font-weight: bold;"
            }

            // 错误消息（保存引用）
            label {
                statusLabel = this // 保存引用
                textProperty().bind(viewModel.errorMessage)
                visibleProperty().bind(viewModel.errorMessage.isNotEmpty)
                style = "-fx-text-fill: red;"
            }

            // 加载指示器
            label("加载中...") {
                visibleProperty().bind(viewModel.isLoading)
            }

            // 用户信息表单
            vbox {
                spacing = 10.0
                bindVisible(viewModel.isLoading.not())

                // 姓名
                hbox {
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT

                    label("姓名:") {
                        prefWidth = 80.0
                    }

                    textField {
                        nameTextField = this // 保存引用
                        textProperty().bindBidirectional(viewModel.userName)
                        editableProperty().bind(viewModel.isEditMode)
                        prefWidth = 200.0
                    }
                }

                // 邮箱
                hbox {
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT

                    label("邮箱:") {
                        prefWidth = 80.0
                    }

                    textField {
                        emailTextField = this // 保存引用
                        textProperty().bindBidirectional(viewModel.userEmail)
                        editableProperty().bind(viewModel.isEditMode)
                        prefWidth = 200.0
                    }
                }

                // 年龄
                hbox {
                    spacing = 10.0
                    alignment = Pos.CENTER_LEFT

                    label("年龄:") {
                        prefWidth = 80.0
                    }

                    textField {
                        ageTextField = this // 保存引用
                        textProperty().bindBidirectional(viewModel.userAge, object : StringConverter<String>(){
                            override fun toString(p0: String?): String? {
                                return p0
                            }

                            override fun fromString(p0: String?): String? {
                                return p0
                            }
                        })
                        editableProperty().bind(viewModel.isEditMode)
                        prefWidth = 200.0
                    }
                }
            }

            // 操作按钮
            hbox {
                spacing = 10.0
                alignment = Pos.CENTER
                bindVisible(viewModel.isLoading.not())

                // 编辑按钮
                button("编辑") {
                    visibleProperty().bind(viewModel.isEditMode.not())
                    setOnAction {
                        viewModel.startEdit()
                        // 方案1：直接操作节点
                        nameTextField.requestFocus() // 聚焦到姓名输入框
                    }
                }

                // 保存按钮
                button("保存") {
                    visibleProperty().bind(viewModel.isEditMode)
                    disableProperty().bind(viewModel.canSave.not())
                    setOnAction {
                        viewModel.saveUser()
                        // 方案1：操作其他节点
                        if (nameTextField.text.isEmpty()) {
                            nameTextField.requestFocus()
                            statusLabel.text = "请输入姓名"
                        }
                    }
                }

                // 取消按钮
                button("取消") {
                    visibleProperty().bind(viewModel.isEditMode)
                    setOnAction { viewModel.cancelEdit() }
                }

                // 方案1：演示复杂交互的按钮
                button("清空表单") {
                    setOnAction {
                        nameTextField.clear()
                        emailTextField.clear()
                        ageTextField.clear()
                        nameTextField.requestFocus()
                        statusLabel.text = "表单已清空"
                    }
                }
            }

            // 重新加载按钮
            button("重新加载用户1") {
                setOnAction { viewModel.loadUser("1") }
            }
        }
    }
}

// =============================================================================
// 方案2：DSL构建器支持引用保存
// =============================================================================

// 增强的DSL构建器，支持引用保存
class EnhancedVBoxBuilder : ContainerBuilder<VBox>(VBox()) {
    val nodeReferences = mutableMapOf<String, Node>()

    var spacing: Double
        get() = container.spacing
        set(value) { container.spacing = value }

    var alignment: Pos
        get() = container.alignment
        set(value) { container.alignment = value }

    fun bindVisible(binding: javafx.beans.binding.BooleanBinding) {
        container.visibleProperty().bind(binding)
    }

    // 支持命名引用的构建方法
    fun button(text: String = "", name: String? = null, config: Button.() -> Unit = {}) {
        val button = Button(text).apply(config)
        children.add(button)
        name?.let { nodeReferences[it] = button }
    }

    fun label(text: String = "", name: String? = null, config: Label.() -> Unit = {}) {
        val label = Label(text).apply(config)
        children.add(label)
        name?.let { nodeReferences[it] = label }
    }

    fun textField(name: String? = null, config: TextField.() -> Unit = {}) {
        val textField = TextField().apply(config)
        children.add(textField)
        name?.let { nodeReferences[it] = textField }
    }

    // 获取保存的引用
    fun getNode(name: String): Node? = nodeReferences[name]

    inline fun <reified T : Node> getTypedNode(name: String): T? =
        nodeReferences[name] as? T
}

// =============================================================================
// 方案3：回调函数传递引用
// =============================================================================

class CallbackBasedView(private val viewModel: UserDetailViewModel) {

    fun createViewWithCallbacks(): StackPane = stackPane {
        alignment = Pos.CENTER

        vbox {
            spacing = 20.0
            alignment = Pos.CENTER

            label("用户详情") {
                style = "-fx-font-size: 24px; -fx-font-weight: bold;"
            }

            // 方案3：使用局部变量和回调
            var nameField: TextField? = null
            var emailField: TextField? = null
            var statusLabel: Label? = null

            label {
                statusLabel = this
                textProperty().bind(viewModel.errorMessage)
                visibleProperty().bind(viewModel.errorMessage.isNotEmpty)
                style = "-fx-text-fill: red;"
            }

            vbox {
                spacing = 10.0
                bindVisible(viewModel.isLoading.not())

                hbox {
                    spacing = 10.0

                    label("姓名:") { prefWidth = 80.0 }
                    textField {
                        nameField = this
                        textProperty().bindBidirectional(viewModel.userName)
                        editableProperty().bind(viewModel.isEditMode)
                        prefWidth = 200.0
                    }
                }

                hbox {
                    spacing = 10.0

                    label("邮箱:") { prefWidth = 80.0 }
                    textField {
                        emailField = this
                        textProperty().bindBidirectional(viewModel.userEmail)
                        editableProperty().bind(viewModel.isEditMode)
                        prefWidth = 200.0
                    }
                }
            }

            hbox {
                spacing = 10.0
                alignment = Pos.CENTER
                bindVisible(viewModel.isLoading.not())

                button("编辑") {
                    visibleProperty().bind(viewModel.isEditMode.not())
                    setOnAction {
                        viewModel.startEdit()
                        nameField?.requestFocus()
                    }
                }

                button("验证并聚焦") {
                    setOnAction {
                        when {
                            nameField?.text?.isEmpty() == true -> {
                                nameField?.requestFocus()
                                statusLabel?.text = "请输入姓名"
                            }
                            emailField?.text?.isEmpty() == true -> {
                                emailField?.requestFocus()
                                statusLabel?.text = "请输入邮箱"
                            }
                            else -> {
                                statusLabel?.text = "验证通过"
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 主应用程序
// =============================================================================

class MVVMDSLApp : Application() {

    override fun start(primaryStage: Stage) {
        // 创建ViewModel
        val viewModel = UserDetailViewModel()

        // 创建View
        val view = UserDetailView(viewModel)

        // 创建场景
        val scene = Scene(view.createView(), 600.0, 500.0)

        // 设置舞台
        primaryStage.title = "JavaFX MVVM + DSL 示例"
        primaryStage.scene = scene
        primaryStage.show()

        // 初始加载数据
        viewModel.loadUser("1")
    }
}

fun main() {
    Application.launch(MVVMDSLApp::class.java)
}