package com.tmt.coroutine

import kotlinx.coroutines.experimental.*

fun main(args: Array<String>) {

    runBlocking<Unit> {
        val jobs = arrayListOf<Job>()
        jobs += launch(Unconfined) { // not confined -- will work with main thread
            println("      'Unconfined': I'm working in thread ${Thread.currentThread().name} - ${coroutineContext[Job]}")
        }
        jobs += launch(coroutineContext) { // context of the parent, runBlocking coroutine
            println("'coroutineContext': I'm working in thread ${Thread.currentThread().name}")
        }
        jobs += launch(CommonPool) { // will get dispatched to ForkJoinPool.commonPool (or equivalent)
            println("      'CommonPool': I'm working in thread ${Thread.currentThread().name}")
        }
        jobs += launch(newSingleThreadContext("MyOwnThread")) { // will get its own new thread
            println("          'newSTC': I'm working in thread ${Thread.currentThread().name}")
        }
        jobs.forEach { println(it.hashCode()); it.join() }
    }
}

suspend fun doSomethingOne(): Int {
    println("当前线程：one - " + Thread.currentThread().name)
    delay(1000L)
    return 1
}

suspend fun doSomethingTwo(): Int {
    println("当前线程：two - " + Thread.currentThread().name)
    delay(2000L)
    return 2
}

