package com.tmt.coroutine

import kotlinx.coroutines.experimental.*

fun main(args: Array<String>) {

    var jobs = List(100_0000) {
        launch {
            doWorld()
        }
    }

    runBlocking {
        jobs.forEach { it.join() }
    }
}

suspend fun doWorld() {
    delay(1000L)
    // println("World!")
}