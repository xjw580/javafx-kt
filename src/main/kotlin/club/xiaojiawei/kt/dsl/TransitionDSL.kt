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
inline fun fadeTransition(config: FadeTransitionBuilder.() -> Unit): FadeTransition {
    return fadeTransitionBuilder(config).build()
}

inline fun fadeTransitionBuilder(config: FadeTransitionBuilder.() -> Unit): FadeTransitionBuilder {
    return FadeTransitionBuilder().apply(config)
}

inline fun FadeTransition.config(config: FadeTransitionBuilder.() -> Unit): FadeTransition {
    FadeTransitionBuilder().apply(config).config(this)
    return this
}

fun fadeInTransition(duration: Double = 300.0, block: FadeTransitionBuilder.() -> Unit = {}): FadeTransition {
    return fadeTransitionBuilder {
        duration(duration)
        from(0.0)
        to(1.0)
        block()
    }.build()
}

fun fadeOutTransition(duration: Double = 300.0, block: FadeTransitionBuilder.() -> Unit = {}): FadeTransition {
    return fadeTransitionBuilder {
        duration(duration)
        from(1.0)
        to(0.0)
        block()
    }.build()
}

// ScaleTransition 衍生
inline fun scaleTransition(config: ScaleTransitionBuilder.() -> Unit): ScaleTransition {
    return scaleTransitionBuilder(config).build()
}

inline fun scaleTransitionBuilder(config: ScaleTransitionBuilder.() -> Unit): ScaleTransitionBuilder {
    return ScaleTransitionBuilder().apply(config)
}

inline fun ScaleTransition.config(config: ScaleTransitionBuilder.() -> Unit): ScaleTransition {
    ScaleTransitionBuilder().apply(config).config(this)
    return this
}

// TranslateTransition 衍生
inline fun translateTransition(config: TranslateTransitionBuilder.() -> Unit): TranslateTransition {
    return translateTransitionBuilder(config).build()
}

inline fun translateTransitionBuilder(config: TranslateTransitionBuilder.() -> Unit): TranslateTransitionBuilder {
    return TranslateTransitionBuilder().apply(config)
}

inline fun TranslateTransition.config(config: TranslateTransitionBuilder.() -> Unit): TranslateTransition {
    TranslateTransitionBuilder().apply(config).config(this)
    return this
}

// RotateTransition 衍生
inline fun rotateTransition(config: RotateTransitionBuilder.() -> Unit): RotateTransition {
    return rotateTransitionBuilder(config).build()
}

inline fun rotateTransitionBuilder(config: RotateTransitionBuilder.() -> Unit): RotateTransitionBuilder {
    return RotateTransitionBuilder().apply(config)
}

inline fun RotateTransition.config(config: RotateTransitionBuilder.() -> Unit): RotateTransition {
    RotateTransitionBuilder().apply(config).config(this)
    return this
}

// PathTransition 衍生
inline fun pathTransition(config: PathTransitionBuilder.() -> Unit): PathTransition {
    return pathTransitionBuilder(config).build()
}

inline fun pathTransitionBuilder(config: PathTransitionBuilder.() -> Unit): PathTransitionBuilder {
    return PathTransitionBuilder().apply(config)
}

inline fun PathTransition.config(config: PathTransitionBuilder.() -> Unit): PathTransition {
    PathTransitionBuilder().apply(config).config(this)
    return this
}

// PauseTransition 衍生
inline fun pauseTransition(config: PauseTransitionBuilder.() -> Unit): PauseTransition {
    return pauseTransitionBuilder(config).build()
}

inline fun pauseTransitionBuilder(config: PauseTransitionBuilder.() -> Unit): PauseTransitionBuilder {
    return PauseTransitionBuilder().apply(config)
}

inline fun PauseTransition.config(config: PauseTransitionBuilder.() -> Unit): PauseTransition {
    PauseTransitionBuilder().apply(config).config(this)
    return this
}

fun pauseTransition(duration: Double, block: PauseTransitionBuilder.() -> Unit = {}): PauseTransition {
    return pauseTransitionBuilder {
        duration(duration)
        block()
    }.build()
}

// 便捷 Transition 衍生
fun shakeTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
): TranslateTransition {
    return translateTransition {
        duration(duration / 8)
        from(0.0, 0.0)
        to(10.0, 0.0)
        cycleCount(8)
        autoReverse()
        block()
    }
}

fun pulseTransition(
    duration: Double = 300.0,
    block: ScaleTransitionBuilder.() -> Unit = {}
): ScaleTransition {
    return scaleTransition {
        duration(duration)
        from(1.0)
        to(1.1)
        cycleCount(2)
        autoReverse()
        block()
    }
}

fun bounceTransition(
    duration: Double = 500.0,
    block: TranslateTransitionBuilder.() -> Unit = {}
): TranslateTransition {
    return translateTransition {
        duration(duration)
        from(0.0, 0.0)
        to(0.0, -20.0)
        cycleCount(2)
        autoReverse()
        block()
    }
}

// SequentialTransition 衍生
inline fun sequentialTransition(config: SequentialTransitionBuilder.() -> Unit): SequentialTransition {
    return sequentialTransitionBuilder(config).build()
}

inline fun sequentialTransitionBuilder(config: SequentialTransitionBuilder.() -> Unit): SequentialTransitionBuilder {
    return SequentialTransitionBuilder().apply(config)
}

inline fun SequentialTransition.config(config: SequentialTransitionBuilder.() -> Unit): SequentialTransition {
    SequentialTransitionBuilder().apply(config).config(this)
    return this
}

// ParallelTransition 衍生
inline fun parallelTransition(config: ParallelTransitionBuilder.() -> Unit): ParallelTransition {
    return parallelTransitionBuilder(config).build()
}

inline fun parallelTransitionBuilder(config: ParallelTransitionBuilder.() -> Unit): ParallelTransitionBuilder {
    return ParallelTransitionBuilder().apply(config)
}

inline fun ParallelTransition.config(config: ParallelTransitionBuilder.() -> Unit): ParallelTransition {
    ParallelTransitionBuilder().apply(config).config(this)
    return this
}
