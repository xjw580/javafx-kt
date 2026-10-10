package club.xiaojiawei.kt.test

import javafx.application.Platform
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import kotlin.test.assertTrue

class FxTestSupportTest {
    @Test
    fun executesOnFxThreadAndSupportsNestedCalls() = onFxThread {
        assertTrue(Platform.isFxApplicationThread())
        val current = Thread.currentThread()
        onFxThread { assertSame(current, Thread.currentThread()) }
    }

    @Test
    fun propagatesOriginalFailureWithoutBreakingSubsequentTests() {
        val failure = AssertionError("test failure")
        assertSame(failure, assertFailsWith<AssertionError> { onFxThread { throw failure } })
        onFxThread { assertTrue(Platform.isFxApplicationThread()) }
    }
}
