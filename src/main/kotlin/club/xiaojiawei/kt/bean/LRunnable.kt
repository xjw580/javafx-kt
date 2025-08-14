package club.xiaojiawei.kt.bean

import club.xiaojiawei.kt.config.log

/**
 * @author 肖嘉威
 * @date 2024/9/8 17:02
 */
class LRunnable(private var task: Runnable?) : Runnable {

    override fun run() {
        try {
            task?.run()
        } catch (e: Exception) {
            log.error(e) { "发生错误" }
        }
    }

}