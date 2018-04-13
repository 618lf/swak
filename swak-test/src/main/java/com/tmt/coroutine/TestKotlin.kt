package com.tmt.coroutine

import kotlinx.coroutines.experimental.*
import java.util.concurrent.atomic.AtomicLong

fun main(args: Array<String>) {

    var count = AtomicLong()

    var jobs = List(100_0000) {
        launch {
            doWorld(count)
        }
    }

    runBlocking {
        jobs.forEach { it.join() }
    }

    println("count: ${count.get()}")

}

suspend fun doWorld(count: AtomicLong):Int {
    delay(1000L)
    count.getAndAdd(1)
    // println("World!")
    return 1
}