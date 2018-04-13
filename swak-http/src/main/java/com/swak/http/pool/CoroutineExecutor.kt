package com.swak.http.pool

import com.swak.http.Executeable
import kotlinx.coroutines.experimental.launch

/**
 * 基于协程的处理方式
 * 如果没有协程变量，则很难在之前的模式下使用
 * 感觉如果整个框架是响应式的就可以使用，或者使用的数据直接放入 request 中
 * 不使用线程变量
 */
open class CoroutineExecutor : Executeable {

    override fun onExecute(lookupPath: String, run: Runnable) {
        launch {
            exec(run)
        }
    }

    suspend fun exec(run: Runnable) {
        run.run()
    }
}