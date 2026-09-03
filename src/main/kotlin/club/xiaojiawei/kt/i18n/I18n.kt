package club.xiaojiawei.kt.i18n

import javafx.beans.binding.StringBinding
import javafx.beans.property.ReadOnlyObjectProperty
import java.util.Locale
import java.util.ResourceBundle
import java.util.concurrent.atomic.AtomicReference

object I18n {

    private val contextReference = AtomicReference<I18nContext?>()

    val isConfigured: Boolean
        get() = contextReference.get() != null

    val context: I18nContext
        get() = contextReference.get() ?: error("I18n 尚未配置")

    val localeProperty: ReadOnlyObjectProperty<Locale>
        get() = context.localeProperty

    var locale: Locale
        get() = context.locale
        set(value) {
            context.locale = value
        }

    val supportedLocales: Set<Locale>
        get() = context.supportedLocales

    val bundle: ResourceBundle
        get() = context.bundle

    fun configure(context: I18nContext) {
        check(contextReference.compareAndSet(null, context)) { "I18n 只能配置一次" }
    }

    operator fun get(key: String): String = context[key]

    fun binding(key: String): StringBinding = context.binding(key)

    fun localized(key: String): LocalizedText = context.localized(key)
}

fun i18n(key: String): LocalizedText = I18n.localized(key)
