package club.xiaojiawei.kt.i18n

import javafx.beans.binding.StringBinding
import java.util.Locale
import java.util.MissingResourceException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class I18nContextTest {

    @Test
    fun bindingTracksLocaleWithoutAffectingIndependentContexts() {
        val chineseContext = testContext(Locale.SIMPLIFIED_CHINESE)
        val englishContext = testContext(Locale.ENGLISH)
        val binding: StringBinding = chineseContext.binding("greeting")

        assertEquals("你好", binding.value)
        assertEquals("Hello", englishContext["greeting"])

        chineseContext.locale = Locale.ENGLISH

        assertEquals("Hello", binding.value)
        assertEquals(Locale.ENGLISH, chineseContext.localeProperty.value)
        assertEquals(Locale.ENGLISH, englishContext.locale)
    }

    @Test
    fun invalidLocaleAndMissingKeyFailWithoutChangingState() {
        val context = testContext(Locale.SIMPLIFIED_CHINESE)

        assertFailsWith<IllegalArgumentException> {
            context.locale = Locale.FRENCH
        }
        assertEquals(Locale.SIMPLIFIED_CHINESE, context.locale)
        assertFailsWith<MissingResourceException> {
            context["missing.key"]
        }
        assertEquals("{0} 个项目", context["literal.placeholder"])
    }

    @Test
    fun contextRequiresDeclaredLocaleAndExistingBundle() {
        assertFailsWith<IllegalArgumentException> {
            I18nContext(
                baseName = "i18n.messages",
                supportedLocales = setOf(Locale.ENGLISH),
                initialLocale = Locale.SIMPLIFIED_CHINESE,
                classLoader = javaClass.classLoader,
            )
        }
        assertFailsWith<MissingResourceException> {
            I18nContext(
                baseName = "i18n.does-not-exist",
                supportedLocales = setOf(Locale.ENGLISH),
                initialLocale = Locale.ENGLISH,
                classLoader = javaClass.classLoader,
            )
        }
    }

    @Test
    fun defaultContextMustBeConfiguredExactlyOnce() {
        assertFalse(I18n.isConfigured)
        assertFailsWith<IllegalStateException> {
            I18n["greeting"]
        }

        val context = testContext(Locale.SIMPLIFIED_CHINESE)
        I18n.configure(context)

        assertTrue(I18n.isConfigured)
        assertEquals("你好", I18n["greeting"])
        assertEquals("你好", i18n("greeting").value)
        assertEquals(context.locale, I18n.locale)
        assertEquals(context.localeProperty.value, I18n.localeProperty.value)
        assertEquals(context.supportedLocales, I18n.supportedLocales)
        assertEquals(context.bundle, I18n.bundle)

        I18n.locale = Locale.ENGLISH

        assertEquals("Hello", I18n.binding("greeting").value)
        assertFailsWith<IllegalStateException> {
            I18n.configure(testContext(Locale.ENGLISH))
        }
    }

    private fun testContext(initialLocale: Locale) = I18nContext(
        baseName = "i18n.messages",
        supportedLocales = setOf(Locale.SIMPLIFIED_CHINESE, Locale.ENGLISH),
        initialLocale = initialLocale,
        classLoader = javaClass.classLoader,
    )
}
