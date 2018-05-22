package com.tmt.coroutine.kotlin

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.runBlocking
import java.util.concurrent.CountDownLatch

fun main(args: Array<String>) = runBlocking{

    val latch = CountDownLatch(1)

    for (i in 1..10) {
        var data = async {
            println("${i}号任务，获取数据的线程 开始：${Thread.currentThread().name}")
            val client = OkHttpClient()
            val request = Request.Builder().url("http://127.0.0.1:8080/admin/hello/say/void").build()
            val call = client.newCall(request).execute()
            println("${i}号任务，获取数据的线程 结束：${Thread.currentThread().name}")
            call
        }
    }

    latch.await()
}