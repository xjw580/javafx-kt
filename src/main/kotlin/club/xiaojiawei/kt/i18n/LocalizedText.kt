package club.xiaojiawei.kt.i18n

import javafx.beans.binding.StringBinding

class LocalizedText internal constructor(
    private val context: I18nContext,
    val key: String,
) {

    val value: String
        get() = context[key]

    fun binding(): StringBinding = context.binding(key)
}
