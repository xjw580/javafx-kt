package club.xiaojiawei.kt.i18n

import javafx.beans.binding.Bindings
import javafx.beans.binding.StringBinding
import javafx.beans.property.ReadOnlyObjectProperty
import javafx.beans.property.ReadOnlyObjectWrapper
import java.util.Locale
import java.util.ResourceBundle

class I18nContext(
    val baseName: String,
    supportedLocales: Set<Locale>,
    initialLocale: Locale = Locale.getDefault(),
    private val classLoader: ClassLoader = Thread.currentThread().contextClassLoader,
) {

    val supportedLocales: Set<Locale> = supportedLocales.toSet()

    private val localeWrapper = ReadOnlyObjectWrapper(this, "locale", initialLocale)

    val localeProperty: ReadOnlyObjectProperty<Locale> = localeWrapper.readOnlyProperty

    var locale: Locale
        get() = localeWrapper.value
        set(value) {
            require(value in supportedLocales) { "不支持的语言: $value" }
            loadBundle(value)
            localeWrapper.value = value
        }

    val bundle: ResourceBundle
        get() = loadBundle(locale)

    init {
        require(baseName.isNotBlank()) { "资源包名称不能为空" }
        require(this.supportedLocales.isNotEmpty()) { "支持的语言不能为空" }
        require(initialLocale in this.supportedLocales) { "初始语言必须包含在支持的语言中: $initialLocale" }
        loadBundle(initialLocale)
    }

    operator fun get(key: String): String {
        require(key.isNotBlank()) { "国际化键不能为空" }
        return bundle.getString(key)
    }

    fun binding(key: String): StringBinding {
        this[key]
        return Bindings.createStringBinding({ this[key] }, localeProperty)
    }

    fun localized(key: String): LocalizedText {
        this[key]
        return LocalizedText(this, key)
    }

    private fun loadBundle(locale: Locale): ResourceBundle = ResourceBundle.getBundle(
        baseName,
        locale,
        classLoader,
        NO_DEFAULT_LOCALE_FALLBACK,
    )

    private companion object {
        val NO_DEFAULT_LOCALE_FALLBACK: ResourceBundle.Control =
            ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES)
    }
}
