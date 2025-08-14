package club.xiaojiawei.kt.utils

import club.xiaojiawei.kt.bean.LRunnable
import javafx.application.Platform


/**
 * @author 肖嘉威
 * @date 2025/8/12 14:23
 */


/**
 * 确保在ui线程中执行
 */
inline fun runUI(crossinline block: () -> Unit) {
    if (Platform.isFxApplicationThread()) {
        block()
    } else {
        Platform.runLater(LRunnable { block() })
    }
}