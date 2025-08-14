package club.xiaojiawei.kt.bean

import kotlinx.coroutines.sync.Semaphore
import java.util.concurrent.ConcurrentHashMap

/**
 * 资源锁管理器
 * @author 肖嘉威
 * @date 2025/8/12 15:11
 */
object ResourceLockManager {
    private val locks = ConcurrentHashMap<String, Semaphore>()

    fun getLock(resource: String, permit: Int): Semaphore {
        return locks.computeIfAbsent(resource) { Semaphore(permit) }
    }
}