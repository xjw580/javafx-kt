package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.annotations.FXMarker
import javafx.animation.*
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.scene.Node
import javafx.scene.shape.Shape
import javafx.util.Duration

/**
 * 动画DSL
 * @author 肖嘉威
 */

@FXMarker
abstract class TransitionBuilder<T : Transition> : DslBuilder<T>() {

    fun delay(millis: Double) = settings {
        delay = Duration.millis(millis)
    }

    fun cycleCount(count: Int) = settings {
        cycleCount = count
    }

    fun infinite() = settings {
        cycleCount = Timeline.INDEFINITE
    }

    fun autoReverse(reverse: Boolean = true) = settings {
        isAutoReverse = reverse
    }

    fun onFinished(handler: (ActionEvent) -> Unit) = settings {
        onFinished = EventHandler(handler)
    }
}

// 淡入淡出动画
@FXMarker
class FadeTransitionBuilder : TransitionBuilder<FadeTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    fun from(opacity: Double) = settings {
        fromValue = opacity
    }

    fun to(opacity: Double) = settings {
        toValue = opacity
    }

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): FadeTransition = FadeTransition()

    fun build(node: Node?): FadeTransition = build().apply {
        this.node = node
    }

}

// 缩放动画
@FXMarker
class ScaleTransitionBuilder : TransitionBuilder<ScaleTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    fun from(x: Double, y: Double = x) = settings {
        fromX = x
        fromY = y
    }

    fun to(x: Double, y: Double = x) = settings {
        toX = x
        toY = y
    }

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): ScaleTransition = ScaleTransition()

    fun build(node: Node?): ScaleTransition = build().apply {
        this.node = node
    }
}

// 位移动画
@FXMarker
class TranslateTransitionBuilder : TransitionBuilder<TranslateTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    fun from(x: Double, y: Double = 0.0) = settings {
        fromX = x
        fromY = y
    }

    fun to(x: Double, y: Double = 0.0) = settings {
        toX = x
        toY = y
    }

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): TranslateTransition = TranslateTransition()

    fun build(node: Node?): TranslateTransition = build().apply {
        this.node = node
    }

}

// 旋转动画
@FXMarker
class RotateTransitionBuilder : TransitionBuilder<RotateTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    fun from(angle: Double) = settings {
        fromAngle = angle
    }

    fun to(angle: Double) = settings {
        toAngle = angle
    }

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): RotateTransition = RotateTransition()

    fun build(node: Node?): RotateTransition = build().apply {
        this.node = node
    }
}

// 路径动画
@FXMarker
class PathTransitionBuilder : TransitionBuilder<PathTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    fun path(shape: Shape) = settings {
        this.path = shape
    }

    fun path(builder: () -> Shape) = settings {
        path = builder()
    }

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): PathTransition = PathTransition()

    fun build(node: Node?): PathTransition = build().apply {
        this.node = node
    }
}

// 暂停动画
@FXMarker
class PauseTransitionBuilder : TransitionBuilder<PauseTransition>() {

    fun duration(millis: Double) = settings {
        duration = Duration.millis(millis)
    }

    override fun buildInstance(): PauseTransition = PauseTransition()
}

@FXMarker
abstract class CompositeTransitionBuilder<T : Transition> : TransitionBuilder<T>() {

    protected abstract fun innerAdd(animation: Animation)

    protected abstract fun innerAdd(builder: TransitionBuilder<*>)

    operator fun Animation?.unaryPlus() {
        add(this)
    }

    operator fun (() -> Animation?).unaryPlus() {
        add(this)
    }

    fun add(builder: () -> Animation?) = add(builder())

    fun add(animation: Animation?) {
        animation ?: return
        innerAdd(animation)
    }

    fun add(builder: TransitionBuilder<*>) {
        innerAdd(builder)
    }

    fun addAll(vararg animations: Animation) {
        for (animation in animations) {
            innerAdd(animation)
        }
    }

    fun addAll(vararg builders: TransitionBuilder<*>) {
        for (builder in builders) {
            innerAdd(builder)
        }
    }

    fun addFadeIn(node: Node?, duration: Double = 300.0) {
        innerAdd(FadeTransition(Duration.millis(duration), node).apply {
            fromValue = 0.0
            toValue = 1.0
        })
    }

    fun addFadeOut(node: Node?, duration: Double = 300.0) {
        innerAdd(FadeTransition(Duration.millis(duration), node).apply {
            fromValue = 1.0
            toValue = 0.0
        })
    }

    fun addFadeTransition(config: FadeTransitionBuilder.() -> Unit) {
        innerAdd(fadeTransitionBuilder(config))
    }

    fun addScaleTransition(config: ScaleTransitionBuilder.() -> Unit) {
        innerAdd(scaleTransitionBuilder(config))
    }

    fun addTranslateTransition(config: TranslateTransitionBuilder.() -> Unit) {
        innerAdd(translateTransitionBuilder(config))
    }

    fun addRotateTransition(config: RotateTransitionBuilder.() -> Unit) {
        innerAdd(rotateTransitionBuilder(config))
    }

    fun addPathTransition(config: PathTransitionBuilder.() -> Unit) {
        innerAdd(pathTransitionBuilder(config))
    }

    fun addPauseTransition(config: PauseTransitionBuilder.() -> Unit) {
        innerAdd(pauseTransitionBuilder(config))
    }

    fun addSequentialTransition(config: SequentialTransitionBuilder.() -> Unit) {
        innerAdd(sequentialTransitionBuilder(config))
    }

    fun addParallelTransition(config: ParallelTransitionBuilder.() -> Unit) {
        innerAdd(parallelTransitionBuilder(config))
    }

    fun addPause(millis: Double) {
        innerAdd(pauseTransitionBuilder {
            duration(millis)
        })
    }

}

// 顺序动画
@FXMarker
class SequentialTransitionBuilder : CompositeTransitionBuilder<SequentialTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    override fun buildInstance(): SequentialTransition = SequentialTransition()

    override fun innerAdd(animation: Animation) = settings {
        children.add(animation)
    }

    override fun innerAdd(builder: TransitionBuilder<*>) = settings {
        children.add(builder.build())
    }

    fun build(node: Node?): SequentialTransition = build().apply {
        this.node = node
    }

}

// 并行动画
@FXMarker
class ParallelTransitionBuilder : CompositeTransitionBuilder<ParallelTransition>() {

    fun node(node: Node?) = settings {
        this.node = node
    }

    override fun buildInstance(): ParallelTransition = ParallelTransition()

    override fun innerAdd(animation: Animation) = settings {
        children.add(animation)
    }

    override fun innerAdd(builder: TransitionBuilder<*>) = settings {
        children.add(builder.build())
    }

    fun build(node: Node?): ParallelTransition = build().apply {
        this.node = node
    }

}

// Node扩展动画函数
fun Node.playFadeInTransition(duration: Double = 300.0, block: FadeTransitionBuilder.() -> Unit = {}) {
    fadeInTransition(duration, block).apply { node = this@playFadeInTransition }.play()
}

fun Node.playFadeOutTransition(duration: Double = 300.0, block: FadeTransitionBuilder.() -> Unit = {}) {
    fadeOutTransition(duration, block).apply { node = this@playFadeOutTransition }.play()
}

fun Node.playFadeTransition(block: FadeTransitionBuilder.() -> Unit) {
    FadeTransitionBuilder().apply(block).build(this).play()
}

fun Node.playScaleTransition(block: ScaleTransitionBuilder.() -> Unit) {
    ScaleTransitionBuilder().apply(block).build(this).play()
}

fun Node.playTranslateTransition(block: TranslateTransitionBuilder.() -> Unit) {
    TranslateTransitionBuilder().apply(block).build(this).play()
}

fun Node.playRotateTransition(block: RotateTransitionBuilder.() -> Unit) {
    RotateTransitionBuilder().apply(block).build(this).play()
}

fun Node.playPathTransition(block: PathTransitionBuilder.() -> Unit) {
    PathTransitionBuilder().apply(block).build(this).play()
}

fun Node.playShakeTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
) {
    shakeTransition(duration, block).apply { node = this@playShakeTransition }.play()
}

fun Node.playPulseTransition(
    duration: Double = 300.0,
    block: ScaleTransitionBuilder.() -> Unit = {}
) {
    pulseTransition(duration, block).apply { node = this@playPulseTransition }.play()
}

fun Node.playBounceTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
) {
    bounceTransition(duration, block).apply { node = this@playBounceTransition }.play()
}

fun Node.playSequentialTransition(block: SequentialTransitionBuilder.() -> Unit) {
    SequentialTransitionBuilder().apply(block).build(this).play()
}

fun Node.playParallelTransition(block: ParallelTransitionBuilder.() -> Unit) {
    ParallelTransitionBuilder().apply(block).build(this).play()
}

// FadeTransition 衍生
inline fun fadeTransition(config: FadeTransitionBuilder.() -> Unit): FadeTransition =
    fadeTransitionBuilder(config).build()

inline fun fadeTransitionBuilder(config: FadeTransitionBuilder.() -> Unit): FadeTransitionBuilder =
    FadeTransitionBuilder().apply(config)

fun fadeTransitionConfig(config: FadeTransitionBuilder.() -> Unit): FadeTransitionBuilder.() -> Unit =
    config

inline fun FadeTransition.config(config: FadeTransitionBuilder.() -> Unit): FadeTransition =
    apply {
        FadeTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }

fun fadeInTransition(
    duration: Double = 300.0,
    block: FadeTransitionBuilder.() -> Unit = {}
): FadeTransition =
    fadeTransitionBuilder {
        duration(duration)
        from(0.0)
        to(1.0)
        block()
    }.build()

fun fadeOutTransition(
    duration: Double = 300.0,
    block: FadeTransitionBuilder.() -> Unit = {}
): FadeTransition =
    fadeTransitionBuilder {
        duration(duration)
        from(1.0)
        to(0.0)
        block()
    }.build()


// ScaleTransition 衍生
inline fun scaleTransition(config: ScaleTransitionBuilder.() -> Unit): ScaleTransition =
    scaleTransitionBuilder(config).build()

inline fun scaleTransitionBuilder(config: ScaleTransitionBuilder.() -> Unit): ScaleTransitionBuilder =
    ScaleTransitionBuilder().apply(config)

fun scaleTransitionConfig(config: ScaleTransitionBuilder.() -> Unit): ScaleTransitionBuilder.() -> Unit =
    config

inline fun ScaleTransition.config(config: ScaleTransitionBuilder.() -> Unit): ScaleTransition =
    apply {
        ScaleTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// TranslateTransition 衍生
inline fun translateTransition(config: TranslateTransitionBuilder.() -> Unit): TranslateTransition =
    translateTransitionBuilder(config).build()

inline fun translateTransitionBuilder(config: TranslateTransitionBuilder.() -> Unit): TranslateTransitionBuilder =
    TranslateTransitionBuilder().apply(config)

fun translateTransitionConfig(config: TranslateTransitionBuilder.() -> Unit): TranslateTransitionBuilder.() -> Unit =
    config

inline fun TranslateTransition.config(config: TranslateTransitionBuilder.() -> Unit): TranslateTransition =
    apply {
        TranslateTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// RotateTransition 衍生
inline fun rotateTransition(config: RotateTransitionBuilder.() -> Unit): RotateTransition =
    rotateTransitionBuilder(config).build()

inline fun rotateTransitionBuilder(config: RotateTransitionBuilder.() -> Unit): RotateTransitionBuilder =
    RotateTransitionBuilder().apply(config)

fun rotateTransitionConfig(config: RotateTransitionBuilder.() -> Unit): RotateTransitionBuilder.() -> Unit =
    config

inline fun RotateTransition.config(config: RotateTransitionBuilder.() -> Unit): RotateTransition =
    apply {
        RotateTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// PathTransition 衍生
inline fun pathTransition(config: PathTransitionBuilder.() -> Unit): PathTransition =
    pathTransitionBuilder(config).build()

inline fun pathTransitionBuilder(config: PathTransitionBuilder.() -> Unit): PathTransitionBuilder =
    PathTransitionBuilder().apply(config)

fun pathTransitionConfig(config: PathTransitionBuilder.() -> Unit): PathTransitionBuilder.() -> Unit =
    config

inline fun PathTransition.config(config: PathTransitionBuilder.() -> Unit): PathTransition =
    apply {
        PathTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// PauseTransition 衍生
inline fun pauseTransition(config: PauseTransitionBuilder.() -> Unit): PauseTransition =
    pauseTransitionBuilder(config).build()

inline fun pauseTransitionBuilder(config: PauseTransitionBuilder.() -> Unit): PauseTransitionBuilder =
    PauseTransitionBuilder().apply(config)

fun pauseTransitionConfig(config: PauseTransitionBuilder.() -> Unit): PauseTransitionBuilder.() -> Unit =
    config

inline fun PauseTransition.config(config: PauseTransitionBuilder.() -> Unit): PauseTransition =
    apply {
        PauseTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }

fun pauseTransition(
    duration: Double,
    block: PauseTransitionBuilder.() -> Unit = {}
): PauseTransition =
    pauseTransitionBuilder {
        duration(duration)
        block()
    }.build()


// 便捷 Transition 衍生
fun shakeTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
): TranslateTransition =
    translateTransition {
        duration(duration / 8)
        from(0.0, 0.0)
        to(10.0, 0.0)
        cycleCount(8)
        autoReverse()
        block()
    }

fun pulseTransition(
    duration: Double = 300.0,
    block: ScaleTransitionBuilder.() -> Unit = {}
): ScaleTransition =
    scaleTransition {
        duration(duration)
        from(1.0)
        to(1.1)
        cycleCount(2)
        autoReverse()
        block()
    }

fun bounceTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
): TranslateTransition =
    translateTransition {
        duration(duration)
        from(0.0, 0.0)
        to(0.0, -20.0)
        cycleCount(2)
        autoReverse()
        block()
    }


// SequentialTransition 衍生
inline fun sequentialTransition(config: SequentialTransitionBuilder.() -> Unit): SequentialTransition =
    sequentialTransitionBuilder(config).build()

inline fun sequentialTransitionBuilder(config: SequentialTransitionBuilder.() -> Unit): SequentialTransitionBuilder =
    SequentialTransitionBuilder().apply(config)

fun sequentialTransitionConfig(config: SequentialTransitionBuilder.() -> Unit): SequentialTransitionBuilder.() -> Unit =
    config

inline fun SequentialTransition.config(config: SequentialTransitionBuilder.() -> Unit): SequentialTransition =
    apply {
        SequentialTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }


// ParallelTransition 衍生
inline fun parallelTransition(config: ParallelTransitionBuilder.() -> Unit): ParallelTransition =
    parallelTransitionBuilder(config).build()

inline fun parallelTransitionBuilder(config: ParallelTransitionBuilder.() -> Unit): ParallelTransitionBuilder =
    ParallelTransitionBuilder().apply(config)

fun parallelTransitionConfig(config: ParallelTransitionBuilder.() -> Unit): ParallelTransitionBuilder.() -> Unit =
    config

inline fun ParallelTransition.config(config: ParallelTransitionBuilder.() -> Unit): ParallelTransition =
    apply {
        ParallelTransitionBuilder().apply {
            delayMode()
            config()
        }.config(this@config)
    }