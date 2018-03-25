package com.swak.http.pool

import com.swak.http.Executeable
import kotlinx.coroutines.experimental.launch

/**
 * 基于协程的处理方式
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