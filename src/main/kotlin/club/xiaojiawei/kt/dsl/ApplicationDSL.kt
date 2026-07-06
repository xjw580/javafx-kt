package club.xiaojiawei.kt.dsl

/**
 * @author 肖嘉威
 * @date 2026/1/7 10:02
 */
import club.xiaojiawei.JavaFXUI
import club.xiaojiawei.kt.annotations.FXMarker
import javafx.application.Application
import javafx.event.EventHandler
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.paint.Paint
import javafx.stage.*

// 全局变量，用于在 launch 和 start 之间传递配置块
private var primaryStageConfig: (StageBuilder.() -> Unit) = {}

/**
 * 顶级启动函数
 */
fun launchApp(config: StageBuilder.() -> Unit) {
    primaryStageConfig = config
    // 内部启动 JavaFX Application
    Application.launch(ProxyApp::class.java)
}

/**
 * 隐藏的代理类
 */
class ProxyApp : Application() {
    override fun start(primaryStage: Stage) {
        val builder = StageBuilder(primaryStage)
        builder.primaryStageConfig()
        builder.build()
        primaryStage.show()
    }
}


@FXMarker
class SceneBuilder : DslBuilder<Scene>() {

    // 基础参数，用于构造函数
    private var rootNode: Parent? = null
    var width: Double = -1.0
    var height: Double = -1.0

    /**
     * 定义根节点
     */
    fun root(provider: () -> Parent) {
        rootNode = provider()
    }

    fun fill(color: Paint) = settings {
        fill = color
    }

    /**
     * 添加样式表
     */
    override fun style(styleColor: StyleColor, styleSize: StyleSize) {
        settings {
            JavaFXUI.addStylesheet(this)
        }
    }

    fun stylesheets(vararg paths: String) {
        settings { stylesheets.addAll(paths) }
    }

    fun stylesheet(builder: StylesheetBuilder) {
        settings {
            stylesheets.add(builder.toDataUri())
        }
    }

    fun stylesheet(config: StylesheetBuilder.() -> Unit) {
        settings {
            stylesheets.add(StylesheetBuilder().apply { config() }.toDataUri())
        }
    }

    /**
     * 实现基类的实例化方法
     */
    override fun buildInstance(): Scene {
        val root = rootNode ?: throw IllegalStateException("Scene 必须通过 root { ... } 指定根布局")
        return if (width > 0 && height > 0) {
            Scene(root, width, height)
        } else {
            Scene(root)
        }
    }

}

@FXMarker
class StageBuilder(private val existingStage: Stage? = null) : DslBuilder<Stage>() {

    /**
     * 如果传入了 existingStage 则使用它，否则创建一个新的 Stage
     */
    override fun buildInstance(): Stage = existingStage ?: Stage()

    // --- 窗口属性 ---

    fun title(title: String) = settings {
        this.title = title
    }

    operator fun String.unaryPlus() {
        settings {
            this.title = this@unaryPlus
        }
    }

    fun root(provider: () -> Parent) {
        scene {
            root(provider)
        }
    }

    fun style() = settings {
        JavaFXUI.addjavafxUIStylesheet(scene)
    }

    fun initOwner(window: Window?) {
        window ?: return
        settings {
            initOwner(window)
        }
    }

    fun initStyle(stageStyle: StageStyle) = settings {
        initStyle(stageStyle)
    }

    fun initModality(modality: Modality) = settings {
        initModality(modality)
    }

    fun pos(x: Double? = null, y: Double? = null) = settings {
        x?.let {
            this.x = it
        }
        y?.let {
            this.y = it
        }
    }

    fun x(x: Double) = settings {
        this.x = x
    }

    fun y(y: Double) = settings {
        this.y = y
    }

    fun size(width: Double = -1.0, height: Double = -1.0) {
        settings {
            this.width = width
            this.height = height
        }
    }

    fun minSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            this.minWidth = width
            this.minHeight = height
        }
    }

    fun maxSize(width: Double = -1.0, height: Double = -1.0) {
        settings {
            this.maxWidth = width
            this.maxHeight = height
        }
    }

    fun onHiding(action: (WindowEvent) -> Unit) = settings {
        onHiding = EventHandler(action)
    }

    fun onHidden(action: (WindowEvent) -> Unit) = settings {
        onHidden = EventHandler(action)
    }

    fun onShown(action: (WindowEvent) -> Unit) = settings {
        onShown = EventHandler(action)
    }

    fun onShowing(action: (WindowEvent) -> Unit) = settings {
        onShowing = EventHandler(action)
    }

    fun onCloseRequest(action: (WindowEvent) -> Unit) = settings {
        onCloseRequest = EventHandler(action)
    }

    fun resizable(value: Boolean = true) = settings { isResizable = value }

    fun alwaysOnTop(value: Boolean = true) = settings { isAlwaysOnTop = value }

    fun maximized(value: Boolean = true) = settings { isMaximized = value }

    fun scene(config: SceneBuilder.() -> Unit) {
        settings {
            scene = SceneBuilder().apply { config() }.build()
        }
    }
}

// Scene 衍生
inline fun scene(config: SceneBuilder.() -> Unit): Scene =
    sceneBuilder(config).build()

inline fun sceneBuilder(config: SceneBuilder.() -> Unit): SceneBuilder =
    SceneBuilder().apply(config)

fun sceneConfig(config: SceneBuilder.() -> Unit): SceneBuilder.() -> Unit =
    config

inline fun Scene.config(config: SceneBuilder.() -> Unit): Scene =
    apply {
        SceneBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// Stage 衍生
inline fun stage(config: StageBuilder.() -> Unit): Stage =
    stageBuilder(config).build()

inline fun stageBuilder(config: StageBuilder.() -> Unit): StageBuilder =
    StageBuilder().apply(config)

fun stageConfig(config: StageBuilder.() -> Unit): StageBuilder.() -> Unit =
    config

inline fun Stage.config(config: StageBuilder.() -> Unit): Stage =
    apply {
        StageBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }