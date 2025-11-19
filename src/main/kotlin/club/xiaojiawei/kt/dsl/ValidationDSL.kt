package club.xiaojiawei.kt.dsl

import javafx.beans.property.BooleanProperty
import javafx.beans.property.SimpleBooleanProperty
import javafx.beans.property.StringProperty
import javafx.scene.control.Control
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl

/**
 * 表单验证DSL
 * @author 肖嘉威
 */

@FXMarker
class ValidationRule<T>(
    val validator: (T) -> Boolean,
    val errorMessage: String
)

@FXMarker
open class Validator<T> {
    private val rules = mutableListOf<ValidationRule<T>>()
    val isValid: BooleanProperty = SimpleBooleanProperty(true)
    var errorMessage: String = ""
        private set

    fun rule(message: String, validator: (T) -> Boolean) {
        rules.add(ValidationRule(validator, message))
    }

    fun validate(value: T): Boolean {
        for (rule in rules) {
            if (!rule.validator(value)) {
                errorMessage = rule.errorMessage
                isValid.set(false)
                return false
            }
        }
        errorMessage = ""
        isValid.set(true)
        return true
    }

    fun reset() {
        errorMessage = ""
        isValid.set(true)
    }
}

// 文本验证器
@FXMarker
class TextValidator : Validator<String>() {
    
    fun required(message: String = "此字段不能为空") {
        rule(message) { it.isNotBlank() }
    }

    fun minLength(length: Int, message: String = "长度不能少于${length}个字符") {
        rule(message) { it.length >= length }
    }

    fun maxLength(length: Int, message: String = "长度不能超过${length}个字符") {
        rule(message) { it.length <= length }
    }

    fun pattern(regex: Regex, message: String = "格式不正确") {
        rule(message) { it.matches(regex) }
    }

    fun email(message: String = "邮箱格式不正确") {
        pattern(
            Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"),
            message
        )
    }

    fun phone(message: String = "手机号格式不正确") {
        pattern(
            Regex("^1[3-9]\\d{9}$"),
            message
        )
    }

    fun url(message: String = "URL格式不正确") {
        pattern(
            Regex("^https?://[\\w.-]+(?:\\.[\\w.-]+)+[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=]+$"),
            message
        )
    }

    fun numeric(message: String = "必须是数字") {
        rule(message) { it.toDoubleOrNull() != null }
    }

    fun integer(message: String = "必须是整数") {
        rule(message) { it.toIntOrNull() != null }
    }

    fun range(min: Int, max: Int, message: String = "数值必须在${min}到${max}之间") {
        rule(message) {
            val num = it.toIntOrNull()
            num != null && num in min..max
        }
    }

    fun alphanumeric(message: String = "只能包含字母和数字") {
        pattern(Regex("^[a-zA-Z0-9]+$"), message)
    }

    fun custom(message: String, validator: (String) -> Boolean) {
        rule(message, validator)
    }
}

// 数字验证器
@FXMarker
class NumberValidator : Validator<Number>() {
    
    fun min(value: Number, message: String = "不能小于${value}") {
        rule(message) { it.toDouble() >= value.toDouble() }
    }

    fun max(value: Number, message: String = "不能大于${value}") {
        rule(message) { it.toDouble() <= value.toDouble() }
    }

    fun range(min: Number, max: Number, message: String = "必须在${min}到${max}之间") {
        rule(message) { 
            val v = it.toDouble()
            v >= min.toDouble() && v <= max.toDouble()
        }
    }

    fun positive(message: String = "必须是正数") {
        rule(message) { it.toDouble() > 0 }
    }

    fun negative(message: String = "必须是负数") {
        rule(message) { it.toDouble() < 0 }
    }

    fun custom(message: String, validator: (Number) -> Boolean) {
        rule(message, validator)
    }
}

// 为TextField添加验证
fun TextField.validator(block: TextValidator.() -> Unit): TextValidator {
    val validator = TextValidator().apply(block)
    
    // 实时验证
    textProperty().addListener { _, _, newValue ->
        validator.validate(newValue)
    }
    
    // 添加错误样式
    validator.isValid.addListener { _, _, isValid ->
        if (isValid) {
            style = ""
        } else {
            style = "-fx-border-color: red; -fx-border-width: 2px;"
        }
    }
    
    return validator
}

// 表单验证器
@FXMarker
class FormValidator {
    private val validators = mutableListOf<Validator<*>>()
    val isValid: BooleanProperty = SimpleBooleanProperty(true)

    fun <T> add(validator: Validator<T>) {
        validators.add(validator)
        validator.isValid.addListener { _, _, _ ->
            updateFormValidation()
        }
    }

    fun validateAll(): Boolean {
        var allValid = true
        validators.forEach { validator ->
            if (!validator.isValid.get()) {
                allValid = false
            }
        }
        isValid.set(allValid)
        return allValid
    }

    private fun updateFormValidation() {
        val allValid = validators.all { it.isValid.get() }
        isValid.set(allValid)
    }

    fun reset() {
        validators.forEach { it.reset() }
        isValid.set(true)
    }

    fun getErrors(): List<String> {
        return validators
            .filter { !it.isValid.get() }
            .map { it.errorMessage }
    }
}

// 表单DSL
inline fun form(block: FormValidator.() -> Unit): FormValidator {
    return FormValidator().apply(block)
}

// 预定义验证规则
object ValidationRules {
    val USERNAME = { validator: TextValidator ->
        validator.apply {
            required()
            minLength(3, "用户名至少3个字符")
            maxLength(20, "用户名最多20个字符")
            pattern(Regex("^[a-zA-Z0-9_]+$"), "用户名只能包含字母、数字和下划线")
        }
    }

    val PASSWORD = { validator: TextValidator ->
        validator.apply {
            required()
            minLength(6, "密码至少6个字符")
            pattern(
                Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$"),
                "密码必须包含大小写字母和数字"
            )
        }
    }

    val EMAIL = { validator: TextValidator ->
        validator.apply {
            required()
            email()
        }
    }

    val PHONE = { validator: TextValidator ->
        validator.apply {
            required()
            phone()
        }
    }

    val AGE = { validator: NumberValidator ->
        validator.apply {
            range(0, 150, "年龄必须在0到150之间")
        }
    }
}

// 使用示例的扩展函数
fun TextField.usernameValidator() = validator {
    ValidationRules.USERNAME(this)
}

fun TextField.passwordValidator() = validator {
    ValidationRules.PASSWORD(this)
}

fun TextField.emailValidator() = validator {
    ValidationRules.EMAIL(this)
}

fun TextField.phoneValidator() = validator {
    ValidationRules.PHONE(this)
}
