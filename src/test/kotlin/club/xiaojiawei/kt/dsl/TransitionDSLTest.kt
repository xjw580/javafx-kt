package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.dsl.DslBuilder.BuildMode
import club.xiaojiawei.kt.test.onFxThread
import javafx.animation.Animation
import javafx.animation.FadeTransition
import javafx.animation.ParallelTransition
import javafx.animation.PathTransition
import javafx.animation.PauseTransition
import javafx.animation.RotateTransition
import javafx.animation.ScaleTransition
import javafx.animation.SequentialTransition
import javafx.animation.TranslateTransition
import javafx.event.ActionEvent
import javafx.scene.shape.Circle
import javafx.scene.shape.Line
import javafx.util.Duration
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertSame

class TransitionDSLTest {
    @Test
    fun fadeMatchesNativeInBothModes() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = FadeTransition(Duration.millis(240.0), target).apply {
                fromValue = 0.2
                toValue = 0.9
            }
            val actual = fadeTransition {
                setMode(mode)
                node(target)
                duration(240.0)
                from(0.2)
                to(0.9)
            }
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun scaleMatchesNativeInBothModes() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = ScaleTransition(Duration.millis(360.0), target).apply {
                fromX = 0.5
                fromY = 0.8
                toX = 1.2
                toY = 1.6
            }
            val actual = scaleTransition {
                setMode(mode)
                node(target)
                duration(360.0)
                from(0.5, 0.8)
                to(1.2, 1.6)
            }
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun translateMatchesNativeInBothModes() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = TranslateTransition(Duration.millis(480.0), target).apply {
                fromX = -12.0
                fromY = -8.0
                toX = 20.0
                toY = 30.0
            }
            val actual = translateTransition {
                setMode(mode)
                node(target)
                duration(480.0)
                from(-12.0, -8.0)
                to(20.0, 30.0)
            }
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun rotateMatchesNativeAndBuildNodeOverridesConfiguredNode() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = RotateTransition(Duration.millis(600.0), target).apply {
                fromAngle = -45.0
                toAngle = 180.0
            }
            val actual = rotateTransitionBuilder {
                setMode(mode)
                node(Circle())
                duration(600.0)
                from(-45.0)
                to(180.0)
            }.build(target)
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun pathMatchesNativeWithShapeAndProvider() = onFxThread {
        val target = Circle(10.0)
        val route = Line(0.0, 0.0, 120.0, 80.0)
        for (mode in BuildMode.entries) {
            val expected = PathTransition(Duration.millis(700.0), route, target)
            val actual = pathTransition {
                setMode(mode)
                node(target)
                duration(700.0)
                path { route }
            }
            assertAnimationEquals(expected, actual)
            val configured = PathTransition()
            assertSame(configured, configured.config {
                node(target)
                duration(700.0)
                path(route)
            })
            assertAnimationEquals(expected, configured)
        }
    }

    @Test
    fun pauseMatchesNativeAndConvenienceOverload() = onFxThread {
        val expected = PauseTransition(Duration.millis(125.0))
        for (mode in BuildMode.entries) {
            assertAnimationEquals(expected, pauseTransition {
                setMode(mode)
                duration(125.0)
            })
        }
        assertAnimationEquals(expected, pauseTransition(125.0))
    }

    @Test
    fun sequentialMatchesNativeNestedOrderInBothModes() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = SequentialTransition(
                target,
                FadeTransition(Duration.millis(100.0)).apply { fromValue = 0.0; toValue = 1.0 },
                PauseTransition(Duration.millis(50.0)),
                ParallelTransition(
                    ScaleTransition(Duration.millis(200.0)).apply { toX = 2.0; toY = 2.0 },
                    RotateTransition(Duration.millis(200.0)).apply { toAngle = 90.0 }
                )
            )
            val actual = sequentialTransition {
                setMode(mode)
                node(target)
                addFadeIn(null, 100.0)
                addPause(50.0)
                addParallelTransition {
                    addScaleTransition { duration(200.0); to(2.0) }
                    addRotateTransition { duration(200.0); to(90.0) }
                }
            }
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun parallelMatchesNativeBuilderAndAnimationAdditionsInBothModes() = onFxThread {
        val target = Circle(10.0)
        for (mode in BuildMode.entries) {
            val expected = ParallelTransition(
                target,
                FadeTransition(Duration.millis(150.0)).apply { fromValue = 1.0; toValue = 0.0 },
                TranslateTransition(Duration.millis(250.0)).apply { toX = 25.0; toY = 0.0 },
                PauseTransition(Duration.millis(40.0)),
                SequentialTransition(PauseTransition(Duration.millis(20.0)))
            )
            val actual = parallelTransition {
                setMode(mode)
                node(target)
                addFadeOut(null, 150.0)
                add(translateTransitionBuilder { duration(250.0); to(25.0) })
                +PauseTransition(Duration.millis(40.0))
                addSequentialTransition { addPauseTransition { duration(20.0) } }
                add(null as Animation?)
                add { null }
            }
            assertAnimationEquals(expected, actual)
        }
    }

    @Test
    fun transitionCommonPropertiesAndFinishedHandlerMatchNative() = onFxThread {
        var expectedCalls = 0
        var actualCalls = 0
        val expected = FadeTransition().apply {
            delay = Duration.millis(80.0)
            cycleCount = 3
            isAutoReverse = true
            setOnFinished { expectedCalls++ }
        }
        val actual = fadeTransition {
            delay(80.0)
            cycleCount(3)
            autoReverse()
            onFinished { actualCalls++ }
        }
        assertAnimationEquals(expected, actual)
        expected.onFinished.handle(ActionEvent())
        actual.onFinished.handle(ActionEvent())
        assertEquals(1, expectedCalls)
        assertEquals(expectedCalls, actualCalls)
        expected.cycleCount = Animation.INDEFINITE
        expected.isAutoReverse = false
        assertSame(actual, actual.config { infinite(); autoReverse(false) })
        assertAnimationEquals(expected, actual)
    }

    @Test
    fun compositeConfigAppendsChildrenInOrderAndPreservesIdentity() = onFxThread {
        val actual = ParallelTransition(PauseTransition(Duration.millis(10.0)))
        val expected = ParallelTransition(
            PauseTransition(Duration.millis(10.0)),
            PauseTransition(Duration.millis(20.0)),
            PauseTransition(Duration.millis(30.0)),
            PauseTransition(Duration.millis(40.0))
        )
        assertSame(actual, actual.config {
            addAll(PauseTransition(Duration.millis(20.0)), PauseTransition(Duration.millis(30.0)))
            addAll(pauseTransitionBuilder { duration(40.0) })
        })
        assertAnimationEquals(expected, actual)
    }

    @Test
    fun conveniencePresetsMatchNativeAnimations() = onFxThread {
        assertAnimationEquals(FadeTransition(Duration.millis(160.0)).apply {
            fromValue = 0.0; toValue = 1.0
        }, fadeInTransition(160.0))
        assertAnimationEquals(FadeTransition(Duration.millis(180.0)).apply {
            fromValue = 1.0; toValue = 0.0
        }, fadeOutTransition(180.0))
        assertAnimationEquals(TranslateTransition(Duration.millis(100.0)).apply {
            fromX = 0.0; fromY = 0.0; toX = 10.0; toY = 0.0
            cycleCount = 8; isAutoReverse = true
        }, shakeTransition(800.0))
        assertAnimationEquals(ScaleTransition(Duration.millis(250.0)).apply {
            fromX = 1.0; fromY = 1.0; toX = 1.1; toY = 1.1
            cycleCount = 2; isAutoReverse = true
        }, pulseTransition(250.0))
        assertAnimationEquals(TranslateTransition(Duration.millis(450.0)).apply {
            fromX = 0.0; fromY = 0.0; toX = 0.0; toY = -20.0
            cycleCount = 2; isAutoReverse = true
        }, bounceTransition(450.0))
    }

    @Test
    fun presetConfigurationCanOverridePresetValues() = onFxThread {
        val expected = TranslateTransition(Duration.millis(90.0)).apply {
            fromX = 0.0; fromY = 0.0; toX = 4.0; toY = 5.0
            cycleCount = 4
            isAutoReverse = false
        }
        val actual = shakeTransition {
            duration(90.0)
            to(4.0, 5.0)
            cycleCount(4)
            autoReverse(false)
        }
        assertAnimationEquals(expected, actual)
    }

    private fun assertAnimationEquals(expected: Animation, actual: Animation) {
        assertEquals(expected.javaClass, actual.javaClass)
        assertEquals(expected.delay, actual.delay)
        assertEquals(expected.cycleCount, actual.cycleCount)
        assertEquals(expected.isAutoReverse, actual.isAutoReverse)
        assertEquals(expected.status, actual.status)
        when (expected) {
            is FadeTransition -> {
                assertIs<FadeTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.duration, actual.duration)
                assertEquals(expected.fromValue, actual.fromValue)
                assertEquals(expected.toValue, actual.toValue)
            }

            is ScaleTransition -> {
                assertIs<ScaleTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.duration, actual.duration)
                assertEquals(expected.fromX, actual.fromX)
                assertEquals(expected.fromY, actual.fromY)
                assertEquals(expected.toX, actual.toX)
                assertEquals(expected.toY, actual.toY)
            }

            is TranslateTransition -> {
                assertIs<TranslateTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.duration, actual.duration)
                assertEquals(expected.fromX, actual.fromX)
                assertEquals(expected.fromY, actual.fromY)
                assertEquals(expected.toX, actual.toX)
                assertEquals(expected.toY, actual.toY)
            }

            is RotateTransition -> {
                assertIs<RotateTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.duration, actual.duration)
                assertEquals(expected.fromAngle, actual.fromAngle)
                assertEquals(expected.toAngle, actual.toAngle)
            }

            is PathTransition -> {
                assertIs<PathTransition>(actual)
                assertSame(expected.node, actual.node)
                assertSame(expected.path, actual.path)
                assertEquals(expected.duration, actual.duration)
            }

            is PauseTransition -> {
                assertIs<PauseTransition>(actual)
                assertEquals(expected.duration, actual.duration)
            }

            is SequentialTransition -> {
                assertIs<SequentialTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.children.size, actual.children.size)
                expected.children.zip(actual.children).forEach { (e, a) -> assertAnimationEquals(e, a) }
            }

            is ParallelTransition -> {
                assertIs<ParallelTransition>(actual)
                assertSame(expected.node, actual.node)
                assertEquals(expected.children.size, actual.children.size)
                expected.children.zip(actual.children).forEach { (e, a) -> assertAnimationEquals(e, a) }
            }
        }
    }
}
