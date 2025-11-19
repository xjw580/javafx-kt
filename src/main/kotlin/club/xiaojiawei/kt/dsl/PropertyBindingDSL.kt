package club.xiaojiawei.kt.dsl

import javafx.beans.binding.*
import javafx.beans.property.*
import javafx.beans.value.ObservableNumberValue
import javafx.beans.value.ObservableValue
import javafx.collections.ObservableList

/**
 * 属性绑定DSL
 * @author 肖嘉威
 */

@FXMarker
class PropertyBindings {

    // 单向绑定
    infix fun <T> Property<T>.bind(observable: ObservableValue<out T>) {
        this.bind(observable)
    }

    // 双向绑定
    infix fun <T> Property<T>.bindBidirectional(other: Property<T>) {
        this.bindBidirectional(other)
    }

    // 解除绑定
    fun <T> Property<T>.unbind() {
        this.unbind()
    }

    // 解除双向绑定
    fun <T> Property<T>.unbindBidirectional(other: Property<T>) {
        this.unbindBidirectional(other)
    }
}

// String属性绑定扩展
@FXMarker
class StringBindings {

    infix fun StringProperty.concat(other: String): StringExpression {
        return Bindings.concat(this, other)
    }

    infix fun StringProperty.concat(other: ObservableValue<String>): StringExpression {
        return Bindings.concat(this, other)
    }

    fun StringProperty.isEmpty(): BooleanBinding {
        return Bindings.isEmpty(this)
    }

    fun StringProperty.isNotEmpty(): BooleanBinding {
        return Bindings.isNotEmpty(this)
    }

    fun StringProperty.length(): IntegerBinding {
        return Bindings.length(this)
    }
}

// Number属性绑定扩展
@FXMarker
class NumberBindings {

    infix fun NumberExpression.plus(other: ObservableNumberValue): NumberBinding {
        return this.add(other)
    }

    infix fun NumberExpression.minus(other: ObservableNumberValue): NumberBinding {
        return this.subtract(other)
    }

    infix fun NumberExpression.times(other: ObservableNumberValue): NumberBinding {
        return this.multiply(other)
    }

    infix fun NumberExpression.div(other: ObservableNumberValue): NumberBinding {
        return this.divide(other)
    }

    infix fun NumberExpression.eq(other: ObservableNumberValue): BooleanBinding {
        return this.isEqualTo(other)
    }

    infix fun NumberExpression.neq(other: ObservableNumberValue): BooleanBinding {
        return this.isNotEqualTo(other)
    }

    infix fun NumberExpression.gt(other: ObservableNumberValue): BooleanBinding {
        return this.greaterThan(other)
    }

    infix fun NumberExpression.gte(other: ObservableNumberValue): BooleanBinding {
        return this.greaterThanOrEqualTo(other)
    }

    infix fun NumberExpression.lt(other: ObservableNumberValue): BooleanBinding {
        return this.lessThan(other)
    }

    infix fun NumberExpression.lte(other: ObservableNumberValue): BooleanBinding {
        return this.lessThanOrEqualTo(other)
    }

    operator fun NumberExpression.unaryMinus(): NumberBinding {
        return this.negate()
    }

    fun NumberExpression.abs(): NumberBinding {
        return Bindings.createDoubleBinding(
            { Math.abs(this.doubleValue()) },
            this
        )
    }

    fun max(vararg values: ObservableValue<out Number>): NumberBinding {
        return Bindings.createDoubleBinding(
            { values.maxOf { it.value.toDouble() } },
            *values
        )
    }

    fun min(vararg values: ObservableValue<out Number>): NumberBinding {
        return Bindings.createDoubleBinding(
            { values.minOf { it.value.toDouble() } },
            *values
        )
    }
}

// Boolean属性绑定扩展
@FXMarker
class BooleanBindings {

    infix fun BooleanExpression.and(other: ObservableValue<Boolean>): BooleanBinding {
        return this.and(other)
    }

    infix fun BooleanExpression.or(other: ObservableValue<Boolean>): BooleanBinding {
        return this.or(other)
    }

    operator fun BooleanExpression.not(): BooleanBinding {
        return this.not()
    }
}

// 条件绑定
@FXMarker
class ConditionalBindings {

    fun <T> BooleanExpression.then(trueValue: T): WhenBuilder<T> {
        return WhenBuilder(this, trueValue)
    }

    class WhenBuilder<T>(
        private val condition: BooleanExpression,
        private val trueValue: T
    ) {
        infix fun otherwise(falseValue: T): ObjectBinding<T> {
            return Bindings.createObjectBinding(
                { if (condition.get()) trueValue else falseValue },
                condition
            )
        }

        infix fun otherwise(falseValue: ObservableValue<T>): ObjectBinding<T> {
            return Bindings.createObjectBinding(
                { if (condition.get()) trueValue else falseValue.value },
                condition, falseValue
            )
        }
    }
}

// 列表绑定
@FXMarker
class ListBindings {

    fun <T> ObservableList<T>.size(): IntegerBinding {
        return Bindings.size(this)
    }

    fun <T> ObservableList<T>.isEmpty(): BooleanBinding {
        return Bindings.isEmpty(this)
    }

    fun <T> ObservableList<T>.isNotEmpty(): BooleanBinding {
        return Bindings.isNotEmpty(this)
    }
}

// DSL入口
inline fun bindings(block: PropertyBindings.() -> Unit) {
    PropertyBindings().apply(block)
}

inline fun stringBindings(block: StringBindings.() -> Unit): StringBindings {
    return StringBindings().apply(block)
}

inline fun numberBindings(block: NumberBindings.() -> Unit): NumberBindings {
    return NumberBindings().apply(block)
}

inline fun booleanBindings(block: BooleanBindings.() -> Unit): BooleanBindings {
    return BooleanBindings().apply(block)
}

inline fun conditionalBindings(block: ConditionalBindings.() -> Unit): ConditionalBindings {
    return ConditionalBindings().apply(block)
}

inline fun listBindings(block: ListBindings.() -> Unit): ListBindings {
    return ListBindings().apply(block)
}