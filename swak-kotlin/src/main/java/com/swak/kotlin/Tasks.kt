package com.swak.kotlin

import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking

/**
 * 在主线程中顺序执行，属于顶级协程函数，一般用于最外层
 *
 * 注意：该函数会阻塞代码继续执行
 */
inline fun taskBlock(noinline job: suspend () -> Unit) = runBlocking {
    job()
}

/**
 * 并发执行，常用于最外层
 * 返回值 -- DeferredCoroutine, 通过 await 获取具体的值
 */
inline fun <T> taskAsync(noinline job: suspend () -> T) = async {
    job()
}

/**
 * 并发执行，常用于最外层
 * 返回值 --- StandaloneCoroutine
 */
inline fun taskLaunch(noinline job: suspend () -> Unit) = launch {
    job()
}

/**
 * 顺序执行函数，不能用于最外层
 */
suspend inline fun <T> taskOrder(crossinline job: () -> T) {
    job()
}

/**
 * 心跳执行 默认重复次数1次，不能用于最外层
 */
suspend inline fun <T> taskHeartbeat(times: Int = 1, delayTime: Long = 0, crossinline job: () -> T) = repeat(times) {
    delay(delayTime)
    job()
}