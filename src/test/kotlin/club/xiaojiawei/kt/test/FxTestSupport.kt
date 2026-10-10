package club.xiaojiawei.kt.test

import javafx.application.Platform
import java.util.concurrent.ExecutionException
import java.util.concurrent.FutureTask
import java.util.concurrent.TimeUnit

private val toolkitReady: Unit by lazy {
    val startup = FutureTask { Platform.setImplicitExit(false) }
    try {
        Platform.startup(startup)
    } catch (_: IllegalStateException) {
        // JavaFX may already have been initialized by a test launched from the IDE.
        Platform.runLater(startup)
    }
    startup.get(10, TimeUnit.SECONDS)
}

internal fun <T> onFxThread(action: () -> T): T {
    if (Platform.isFxApplicationThread()) return action()
    toolkitReady
    val task = FutureTask(action)
    Platform.runLater(task)
    return try {
        task.get(10, TimeUnit.SECONDS)
    } catch (failure: ExecutionException) {
        throw requireNotNull(failure.cause)
    }
}
