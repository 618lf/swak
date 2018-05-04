package com.swak.kotlin

import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.experimental.rx2.rxObservable

fun main(args: Array<String>) {
    val o: Observable<String> = rxObservable {
        this.send("123")
        println("${Thread.currentThread().name}")
    }
    o.subscribeOn(Schedulers.computation()).subscribe {
        println("$it : ${Thread.currentThread().name}")
    }
    Thread.sleep(2000L)
}