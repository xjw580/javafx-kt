package club.xiaojiawei.kt.dsl

import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.geometry.Rectangle2D
import javafx.scene.control.Button
import javafx.scene.image.Image
import javafx.scene.image.PixelFormat
import javafx.scene.input.MouseButton
import javafx.scene.input.MouseEvent
import javafx.scene.paint.Color
import javafx.scene.paint.Paint
import javafx.scene.robot.Robot
import javafx.stage.Stage
import javafx.util.Duration
import java.awt.image.BufferedImage
import java.nio.file.Files
import java.nio.file.Path
import javax.imageio.ImageIO
import kotlin.math.abs

private val screenshotDirectory = Path.of("qa-evidence", "style-pseudo-classes")
private val reportPath = Path.of(".omo", "evidence", "style-pseudo-classes-window-report.txt")

@Volatile
private var qaFailure: Throwable? = null

fun main(args: Array<String>) {
    val robotQa = "--robot" in args
    lateinit var submitButton: Button
    lateinit var siblingButton: Button
    val events = MouseEvents()

    launchApp {
        title("样式伪类验证")
        size(520.0, 340.0)
        resizable(false)
        alwaysOnTop(true)
        root {
            vbox {
                spacing(18.0)
                padding(36.0)
                alignCenter()
                +label("真实鼠标状态验证")
                submitButton = button("提交") {
                    style()
                    styled {
                        backgroundColor("#3498db")
                        textFill("white")
                        hover { backgroundColor("#2980b9") }
                        pressed { backgroundColor("#1f618d") }
                        disabled { opacity(0.5) }
                    }
                    onMouseEntered(EventHandler<MouseEvent> { events.entered++ })
                    onMouseExited(EventHandler<MouseEvent> { events.exited++ })
                    onMousePressed(EventHandler<MouseEvent> { events.pressed++ })
                    onMouseReleased(EventHandler<MouseEvent> { events.released++ })
                    onAction(EventHandler { events.actions++ })
                }
                siblingButton = button("对照") {
                    style()
                    settings { isFocusTraversable = false }
                }
                +submitButton
                +siblingButton
                +label("窗口将在验证完成后自动关闭")
            }
        }
        style()
        onShown { event ->
            if (robotQa) {
                Platform.runLater {
                    startNativeQa(event.source as Stage, submitButton, siblingButton, events)
                }
            }
        }
    }

    if (robotQa) qaFailure?.let { throw IllegalStateException("原生窗口 QA 失败", it) }
}

private fun startNativeQa(stage: Stage, submitButton: Button, siblingButton: Button, events: MouseEvents) {
    try {
        NativeQaSession(stage, submitButton, siblingButton, events).start()
    } catch (failure: Throwable) {
        var completionFailure = failure
        var stageClosed = false
        try {
            stage.close()
            stageClosed = true
        } catch (cleanupFailure: Throwable) {
            completionFailure = combine(completionFailure, cleanupFailure)
        }
        val report = listOf(
            "input=javafx.scene.robot.Robot",
            "window=${stage.title}",
            "result=FAIL",
            "failure=${failure::class.qualifiedName}: ${failure.message}",
            "cleanup=stageClosed=$stageClosed",
        )
        report.forEach(::println)
        try {
            Files.createDirectories(reportPath.parent)
            Files.writeString(reportPath, report.joinToString(System.lineSeparator(), postfix = System.lineSeparator()))
        } catch (reportFailure: Throwable) {
            completionFailure = combine(completionFailure, reportFailure)
        }
        qaFailure = completionFailure
    }
}

private class NativeQaSession(
    private val stage: Stage,
    private val submitButton: Button,
    private val siblingButton: Button,
    private val events: MouseEvents,
) {
    private val robot = Robot()
    private val initialMousePosition = robot.mousePosition
    private val report = mutableListOf("input=javafx.scene.robot.Robot", "window=${stage.title}")
    private var pendingPause: PauseTransition? = null
    private lateinit var siblingBackground: Paint
    private var mouseDown = false
    private var finished = false

    fun start() = guarded(::startGuarded)

    private fun startGuarded() {
        Files.createDirectories(screenshotDirectory)
        Files.createDirectories(reportPath.parent)
        moveToBlankArea()
        after(200) {
            siblingBackground = backgroundOf(siblingButton)
            verify("base", hover = false, pressed = false, disabled = false, color = "#3498db", opacity = 1.0)
            events.reset()
            moveToSubmitButton()
            after(100) {
                verify("hover", hover = true, pressed = false, disabled = false, color = "#2980b9", opacity = 1.0)
                check(events.entered == 1) { "hover 未触发现有 onMouseEntered 处理器: $events" }
                robot.mousePress(MouseButton.PRIMARY)
                mouseDown = true
                after(100) {
                    verify(
                        "pressed",
                        hover = true,
                        pressed = true,
                        disabled = false,
                        color = "#1f618d",
                        opacity = 1.0
                    )
                    check(events.pressed == 1) { "pressed 未触发现有 onMousePressed 处理器: $events" }
                    robot.mouseRelease(MouseButton.PRIMARY)
                    mouseDown = false
                    after(100) {
                        verify(
                            "released",
                            hover = true,
                            pressed = false,
                            disabled = false,
                            color = "#2980b9",
                            opacity = 1.0
                        )
                        check(events.released == 1 && events.actions == 1) { "release/action 处理器计数异常: $events" }
                        moveToBlankArea()
                        after(100) {
                            verify(
                                "restored",
                                hover = false,
                                pressed = false,
                                disabled = false,
                                color = "#3498db",
                                opacity = 1.0
                            )
                            check(events.exited == 1) { "移出后未触发现有 onMouseExited 处理器: $events" }
                            stage.scene.root.isFocusTraversable = true
                            stage.scene.root.requestFocus()
                            after(200) {
                                check(stage.scene.root.isFocused) { "禁用前无法将焦点转移到根容器" }
                                submitButton.isDisable = true
                                after(100) {
                                    verify(
                                        "disabled",
                                        hover = false,
                                        pressed = false,
                                        disabled = true,
                                        color = "#3498db",
                                        opacity = 0.5
                                    )
                                    succeed()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun after(millis: Int, action: () -> Unit) {
        pendingPause = PauseTransition(Duration.millis(millis.toDouble())).apply {
            setOnFinished { guarded(action) }
            play()
        }
    }

    private fun guarded(action: () -> Unit) {
        if (finished) return
        try {
            action()
        } catch (failure: Throwable) {
            complete(failure)
        }
    }

    private fun verify(
        name: String,
        hover: Boolean,
        pressed: Boolean,
        disabled: Boolean,
        color: String,
        opacity: Double,
    ) {
        stage.scene.root.applyCss()
        stage.scene.root.layout()
        val actualBackground = backgroundOf(submitButton)
        val actualSiblingBackground = backgroundOf(siblingButton)
        report +=
            "$name hover=${submitButton.isHover} pressed=${submitButton.isPressed} " +
                    "disabled=${submitButton.isDisabled} background=$actualBackground opacity=${submitButton.opacity} " +
                    "sibling=$actualSiblingBackground siblingHover=${siblingButton.isHover} " +
                    "siblingFocused=${siblingButton.isFocused} siblingFocusTraversable=${siblingButton.isFocusTraversable} " +
                    "focusOwner=${stage.scene.focusOwner?.javaClass?.simpleName} events=$events"
        check(submitButton.isHover == hover) { "$name hover=${submitButton.isHover}, expected=$hover" }
        check(submitButton.isPressed == pressed) { "$name pressed=${submitButton.isPressed}, expected=$pressed" }
        check(submitButton.isDisabled == disabled) { "$name disabled=${submitButton.isDisabled}, expected=$disabled" }
        check(colorsMatch(actualBackground, color)) { "$name background=$actualBackground, expected=$color" }
        check(abs(submitButton.opacity - opacity) < 0.001) {
            "$name opacity=${submitButton.opacity}, expected=$opacity"
        }
        check(actualSiblingBackground == siblingBackground) {
            "$name 影响了兄弟按钮: $actualSiblingBackground, expected=$siblingBackground"
        }
        capture(name)
    }

    private fun moveToSubmitButton() {
        val bounds = submitButton.localToScreen(submitButton.boundsInLocal)
        checkNotNull(bounds) { "提交按钮没有屏幕坐标" }
        robot.mouseMove(bounds.minX + bounds.width / 2, bounds.minY + bounds.height / 2)
    }

    private fun moveToBlankArea() {
        val point = stage.scene.root.localToScreen(12.0, 12.0)
        checkNotNull(point) { "根节点没有屏幕坐标" }
        robot.mouseMove(point.x, point.y)
    }

    private fun capture(name: String) {
        val region = Rectangle2D(stage.x, stage.y, stage.width, stage.height)
        val image = robot.getScreenCapture(null, region, false)
        writePng(image, screenshotDirectory.resolve("$name.png"))
    }

    private fun succeed() {
        complete(null)
    }

    private fun complete(failure: Throwable?) {
        if (finished) return
        finished = true
        pendingPause?.stop()
        var completionFailure = failure
        var mouseRestored = false
        var stageClosed = false
        try {
            if (mouseDown) {
                robot.mouseRelease(MouseButton.PRIMARY)
                mouseDown = false
            }
        } catch (cleanupFailure: Throwable) {
            completionFailure = combine(completionFailure, cleanupFailure)
        } finally {
            try {
                robot.mouseMove(initialMousePosition.x, initialMousePosition.y)
                mouseRestored = true
            } catch (cleanupFailure: Throwable) {
                completionFailure = combine(completionFailure, cleanupFailure)
            } finally {
                try {
                    stage.close()
                    stageClosed = true
                } catch (cleanupFailure: Throwable) {
                    completionFailure = combine(completionFailure, cleanupFailure)
                }
            }
        }
        qaFailure = completionFailure
        report += "cleanup=mouseReleased=${!mouseDown} mouseRestored=$mouseRestored stageClosed=$stageClosed"
        report += if (completionFailure == null) "result=PASS" else "result=FAIL"
        completionFailure?.let { report += "failure=${it::class.qualifiedName}: ${it.message}" }
        try {
            writeReport()
        } catch (reportFailure: Throwable) {
            qaFailure = combine(completionFailure, reportFailure)
            report.forEach(::println)
            System.err.println("reportFailure=${reportFailure::class.qualifiedName}: ${reportFailure.message}")
        }
    }

    private fun writeReport() {
        Files.writeString(reportPath, report.joinToString(System.lineSeparator(), postfix = System.lineSeparator()))
        report.forEach(::println)
    }
}

private data class MouseEvents(
    var entered: Int = 0,
    var exited: Int = 0,
    var pressed: Int = 0,
    var released: Int = 0,
    var actions: Int = 0,
) {
    fun reset() {
        entered = 0
        exited = 0
        pressed = 0
        released = 0
        actions = 0
    }
}

private fun combine(current: Throwable?, next: Throwable): Throwable = current?.apply { addSuppressed(next) } ?: next

private fun backgroundOf(button: Button): Paint = button.background.fills.first().fill

private fun colorsMatch(actual: Paint, expected: String): Boolean = actual == Color.web(expected)

private fun writePng(image: Image, path: Path) {
    val width = image.width.toInt()
    val height = image.height.toInt()
    val pixels = IntArray(width * height)
    image.pixelReader.getPixels(0, 0, width, height, PixelFormat.getIntArgbInstance(), pixels, 0, width)
    val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB)
    bufferedImage.setRGB(0, 0, width, height, pixels, 0, width)
    check(ImageIO.write(bufferedImage, "png", path.toFile())) { "无法写入截图: $path" }
}
