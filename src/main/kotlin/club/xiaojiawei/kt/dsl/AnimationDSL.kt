package club.xiaojiawei.kt.dsl

import javafx.animation.*
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.util.Duration

/**
 * 动画DSL
 * @author 肖嘉威
 */

@FXMarker
open class AnimationBuilder {

    private var duration: Duration = Duration.millis(300.0)
    private var delay: Duration = Duration.ZERO
    private var cycleCount: Int = 1
    private var autoReverse: Boolean = false
    private var onFinished: EventHandler<ActionEvent>? = null

    fun duration(millis: Double) {
        duration = Duration.millis(millis)
    }

    fun delay(millis: Double) {
        delay = Duration.millis(millis)
    }

    fun cycleCount(count: Int) {
        cycleCount = count
    }

    fun infinite() {
        cycleCount = Timeline.INDEFINITE
    }

    fun autoReverse(reverse: Boolean = true) {
        autoReverse = reverse
    }

    fun onFinished(handler: (ActionEvent) -> Unit) {
        onFinished = EventHandler(handler)
    }

    protected fun applyCommonSettings(animation: Animation) {
        animation.delay = delay
        animation.cycleCount = cycleCount
        animation.isAutoReverse = autoReverse
        onFinished?.let { animation.onFinished = it }
    }

    protected fun getDuration() = duration
}

// 淡入淡出动画
@FXMarker
class FadeAnimationBuilder : AnimationBuilder() {

    private var fromOpacity: Double? = null
    private var toOpacity: Double = 1.0

    fun from(opacity: Double) {
        fromOpacity = opacity
    }

    fun to(opacity: Double) {
        toOpacity = opacity
    }

    fun build(node: Node): FadeTransition {
        return FadeTransition(getDuration(), node).apply {
            fromOpacity?.let { fromValue = it }
            toValue = toOpacity
            applyCommonSettings(this)
        }
    }
}

// 缩放动画
@FXMarker
class ScaleAnimationBuilder : AnimationBuilder() {

    private var fromX: Double = 0.0
    private var fromY: Double = 0.0
    private var toX: Double = 1.0
    private var toY: Double = 1.0

    fun from(x: Double, y: Double = x) {
        fromX = x
        fromY = y
    }

    fun to(x: Double, y: Double = x) {
        toX = x
        toY = y
    }

    fun build(node: Node): ScaleTransition {
        return ScaleTransition(getDuration(), node).apply {
            fromX = this@ScaleAnimationBuilder.fromX
            fromY = this@ScaleAnimationBuilder.fromY
            toX = this@ScaleAnimationBuilder.toX
            toY = this@ScaleAnimationBuilder.toY
            applyCommonSettings(this)
        }
    }
}

// 位移动画
@FXMarker
class TranslateAnimationBuilder : AnimationBuilder() {

    private var fromX: Double = 0.0
    private var fromY: Double = 0.0
    private var toX: Double = 1.0
    private var toY: Double = 1.0

    fun from(x: Double, y: Double = 0.0) {
        fromX = x
        fromY = y
    }

    fun to(x: Double, y: Double = 0.0) {
        toX = x
        toY = y
    }

    fun build(node: Node): TranslateTransition {
        return TranslateTransition(getDuration(), node).apply {
            fromX = this@TranslateAnimationBuilder.fromX
            fromY = this@TranslateAnimationBuilder.fromY
            toX = this@TranslateAnimationBuilder.toX
            toY = this@TranslateAnimationBuilder.toY
            applyCommonSettings(this)
        }
    }
}

// 旋转动画
@FXMarker
class RotateAnimationBuilder : AnimationBuilder() {

    private var fromAngle: Double = 0.0
    private var toAngle: Double = 0.0

    fun from(angle: Double) {
        fromAngle = angle
    }

    fun to(angle: Double) {
        toAngle = angle
    }

    fun build(node: Node): RotateTransition {
        return RotateTransition(getDuration(), node).apply {
            fromAngle = this@RotateAnimationBuilder.fromAngle
            toAngle = this@RotateAnimationBuilder.toAngle
            applyCommonSettings(this)
        }
    }
}

// 路径动画
@FXMarker
class PathAnimationBuilder : AnimationBuilder() {

    private var path: javafx.scene.shape.Shape? = null

    fun path(shape: javafx.scene.shape.Shape) {
        path = shape
    }

    fun build(node: Node): PathTransition {
        return PathTransition(getDuration(), path, node).apply {
            applyCommonSettings(this)
        }
    }
}

// 顺序动画
@FXMarker
class SequentialAnimationBuilder {

    private val animations = mutableListOf<Animation>()

    fun add(animation: Animation) {
        animations.add(animation)
    }

    fun fadeIn(node: Node, duration: Double = 300.0) {
        animations.add(
            FadeTransition(Duration.millis(duration), node).apply {
                fromValue = 0.0
                toValue = 1.0
            }
        )
    }

    fun fadeOut(node: Node, duration: Double = 300.0) {
        animations.add(
            FadeTransition(Duration.millis(duration), node).apply {
                fromValue = 1.0
                toValue = 0.0
            }
        )
    }

    fun build(): SequentialTransition {
        return SequentialTransition(*animations.toTypedArray())
    }
}

// 并行动画
@FXMarker
class ParallelAnimationBuilder {

    private val animations = mutableListOf<Animation>()

    fun add(animation: Animation) {
        animations.add(animation)
    }

    fun fadeIn(node: Node, duration: Double = 300.0) {
        animations.add(
            FadeTransition(Duration.millis(duration), node).apply {
                fromValue = 0.0
                toValue = 1.0
            }
        )
    }

    fun fadeOut(node: Node, duration: Double = 300.0) {
        animations.add(
            FadeTransition(Duration.millis(duration), node).apply {
                fromValue = 1.0
                toValue = 0.0
            }
        )
    }

    fun build(): ParallelTransition {
        return ParallelTransition(*animations.toTypedArray())
    }
}

// Node扩展函数
fun Node.fadeIn(duration: Double = 300.0, block: FadeAnimationBuilder.() -> Unit = {}): FadeTransition {
    return FadeAnimationBuilder().apply {
        duration(duration)
        from(0.0)
        to(1.0)
        block()
    }.build(this).apply { play() }
}

fun Node.fadeOut(duration: Double = 300.0, block: FadeAnimationBuilder.() -> Unit = {}): FadeTransition {
    return FadeAnimationBuilder().apply {
        duration(duration)
        from(1.0)
        to(0.0)
        block()
    }.build(this).apply { play() }
}

fun Node.fadeAnimation(block: FadeAnimationBuilder.() -> Unit): FadeTransition {
    return FadeAnimationBuilder().apply(block).build(this)
}

fun Node.scaleAnimation(block: ScaleAnimationBuilder.() -> Unit): ScaleTransition {
    return ScaleAnimationBuilder().apply(block).build(this)
}

fun Node.translateAnimation(block: TranslateAnimationBuilder.() -> Unit): TranslateTransition {
    return TranslateAnimationBuilder().apply(block).build(this)
}

fun Node.rotateAnimation(block: RotateAnimationBuilder.() -> Unit): RotateTransition {
    return RotateAnimationBuilder().apply(block).build(this)
}

fun Node.pathAnimation(block: PathAnimationBuilder.() -> Unit): PathTransition {
    return PathAnimationBuilder().apply(block).build(this)
}

fun sequentialAnimation(block: SequentialAnimationBuilder.() -> Unit): SequentialTransition {
    return SequentialAnimationBuilder().apply(block).build()
}

fun parallelAnimation(block: ParallelAnimationBuilder.() -> Unit): ParallelTransition {
    return ParallelAnimationBuilder().apply(block).build()
}

// 便捷动画效果
fun Node.shake(duration: Double = 500.0) {
    TranslateTransition(Duration.millis(duration / 8), this).apply {
        fromX = 0.0
        toX = 10.0
        cycleCount = 8
        isAutoReverse = true
        play()
    }
}

fun Node.pulse(duration: Double = 300.0) {
    ScaleTransition(Duration.millis(duration), this).apply {
        fromX = 1.0
        fromY = 1.0
        toX = 1.1
        toY = 1.1
        cycleCount = 2
        isAutoReverse = true
        play()
    }
}

fun Node.bounce(duration: Double = 500.0) {
    TranslateTransition(Duration.millis(duration), this).apply {
        fromY = 0.0
        toY = -20.0
        cycleCount = 2
        isAutoReverse = true
        play()
    }
}
