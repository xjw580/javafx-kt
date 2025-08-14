package club.xiaojiawei.kt.bean

/**
 * 仿cpp的std::expected
 * @author 肖嘉威
 * @date 2025/7/24 10:46
 */

sealed class Expected<out T, out E> {
    data class Success<T>(val value: T?) : Expected<T, Nothing>()
    data class Error<E>(val error: E?) : Expected<Nothing, E>()

    fun isSuccess() = this is Success
    fun isError() = this is Error

    fun takeValue(): T? {
        if (this is Success) {
            return value
        }
        return null
    }

    fun takeError(): E? {
        if (this is Error) {
            return error
        }
        return null
    }

    inline fun onSuccess(action: (T?) -> Unit): Expected<T, E> {
        if (this is Success) action(value)
        return this
    }

    inline fun onFail(action: (E?) -> Unit): Expected<T, E> {
        if (this is Error) action(error)
        return this
    }

}

