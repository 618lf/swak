package com.swak.rpc.remote

import com.swak.common.coroutines.taskBlock
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 * 通过 kotlin 来实现
 */
class RemoteHandler : InvocationHandler {

    /**
     * 通过协程来实现
     */
    override fun invoke(proxy: Any?, method: Method?, args: Array<out Any>?): Any? {
        taskBlock(100L) {
            // println("协程")
        }
        return null
    }
}