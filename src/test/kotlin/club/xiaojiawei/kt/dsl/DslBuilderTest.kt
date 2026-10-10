package club.xiaojiawei.kt.dsl

import club.xiaojiawei.kt.test.onFxThread
import javafx.scene.control.Label
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame

class DslBuilderTest {

    @Test
    fun immediateSettingsMutateCachedInstanceBeforeBuild() = onFxThread {
        val builder = LabelBuilder()
        val instance = builder.instance()
        builder.text("ready")
        builder.id("status")
        val expected = Label("ready").apply { id = "status" }

        assertSame(instance, builder.instance())
        assertEquals(expected.text, instance.text)
        assertEquals(expected.id, instance.id)
        assertSame(instance, builder.build())
        val next = builder.build()
        assertNotSame(instance, next)
        assertEquals(Label().text, next.text)
        assertEquals(Label().id, next.id)
    }

    @Test
    fun delayedSettingsExecuteInOrderAndCanBuildIndependentInstances() = onFxThread {
        val builder = LabelBuilder().apply {
            delayMode()
            text("first")
            settings { text += " second" }
            id("reusable")
        }
        val pending = builder.instance()
        assertEquals(Label().text, pending.text)
        val expected = Label("first second").apply { id = "reusable" }
        val first = builder.build()
        val second = builder.build()

        assertSame(pending, first)
        assertNotSame(first, second)
        listOf(first, second).forEach {
            assertEquals(expected.text, it.text)
            assertEquals(expected.id, it.id)
        }
        first.text = "changed"
        assertEquals(expected.text, second.text)
    }

    @Test
    fun delayedConfigMutatesExistingInstancesAndRetainsUnconfiguredProperties() = onFxThread {
        val builder = LabelBuilder().apply {
            delayMode()
            text("updated")
            settings { styleClass += "configured" }
        }
        val first = Label("old").apply { id = "existing" }
        val second = Label("other").apply { id = "other-id" }
        val expectedFirst = Label("updated").apply {
            id = "existing"
            styleClass += "configured"
        }
        val expectedSecond = Label("updated").apply {
            id = "other-id"
            styleClass += "configured"
        }

        builder.config(first)
        builder.config(second)

        listOf(expectedFirst to first, expectedSecond to second).forEach { (expected, actual) ->
            assertEquals(expected.text, actual.text)
            assertEquals(expected.id, actual.id)
            assertEquals(expected.styleClass, actual.styleClass)
        }
        assertEquals("updated", builder.build().text)
    }

    @Test
    fun immediateConfigDoesNotReplaySettingsOntoAnotherInstance() = onFxThread {
        val builder = LabelBuilder().apply { text("owned") }
        val target = Label("existing")
        builder.config(target)

        assertEquals(Label("existing").text, target.text)
        assertEquals(Label("owned").text, builder.build().text)
    }

    @Test
    fun clearDiscardsCachedInstanceAndPendingSettingsWithoutChangingMode() = onFxThread {
        DslBuilder.BuildMode.entries.forEach { mode ->
            val builder = LabelBuilder().apply {
                setMode(mode)
                text("discarded")
            }
            val oldInstance = builder.instance()
            builder.clear()
            val actual = builder.build()
            val expected = Label()

            assertEquals(mode, builder.buildMode)
            assertNotSame(oldInstance, actual)
            assertEquals(expected.text, actual.text)
            builder.text("fresh")
            assertEquals(Label("fresh").text, builder.build().text)
        }
    }

    @Test
    fun conditionalSettingsOnlyApplyForTrueConditionsInBothModes() = onFxThread {
        DslBuilder.BuildMode.entries.forEach { mode ->
            val builder = LabelBuilder().apply {
                setMode(mode)
                reserveSetting(8)
                settingsIf(true) { text = "applied" }
                settingsIf(false) { error("False settings must never execute") }
            }
            assertEquals(Label("applied").text, builder.build().text)
        }
    }
}
