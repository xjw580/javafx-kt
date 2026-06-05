package club.xiaojiawei.kt.dsl

import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.image.ImageView
import javafx.scene.layout.*
import javafx.scene.shape.Polygon
import javafx.scene.text.Text
import javafx.stage.Stage

/**
 * @author 肖嘉威
 * @date 2025/8/14 9:00
 */

abstract class DslBuilder<T>(
    buildMode: BuildMode = BuildMode.IMMEDIATE
) {

    var buildMode: BuildMode = buildMode
        private set

    private val builders: ArrayList<T.() -> Unit> = ArrayList()

    open fun style(styleColor: StyleColor = StyleColor.DEFAULT, styleSize: StyleSize = StyleSize.DEFAULT) {}

    abstract fun buildInstance(): T

    private var instanceInner: T? = null

    fun instance(): T {
        return instanceInner ?: buildInstance().apply { instanceInner = this }
    }

    fun immediateMode() {
        buildMode = BuildMode.IMMEDIATE
    }

    fun delayMode() {
        buildMode = BuildMode.DELAY
    }

    fun setMode(mode: BuildMode) {
        buildMode = mode
    }

    fun reserveSetting(minCapacity: Int) {
        builders.ensureCapacity(minCapacity)
    }

    open fun settings(settings: T.() -> Unit) {
        when (buildMode) {
            BuildMode.IMMEDIATE -> {
                instance().settings()
            }

            BuildMode.DELAY -> {
                builders.add(settings)
            }
        }
    }

    /**
     * 通过配置构建新的实例
     */
    open fun build(): T {
        val t = if (buildMode == BuildMode.IMMEDIATE) {
            builders.clear()
            instance()
        } else {
            instance().apply {
                for (function in builders) {
                    function()
                }
            }
        }
        instanceInner = null
        return t
    }

    /**
     * 将配置应用到传入的实例中
     */
    open fun config(t: T) {
        if (buildMode == BuildMode.DELAY) {
            for (function in builders) {
                t.function()
            }
        }
    }

    open fun clear() {
        builders.clear()
        instanceInner = null
    }

    fun settingsIf(condition: Boolean, settings: T.() -> Unit) {
        if (condition) {
            settings(settings)
        }
    }

    enum class BuildMode {
        IMMEDIATE,  // 立即模式，即时配置
        DELAY       // 延迟模式，可以重复利用
    }
}