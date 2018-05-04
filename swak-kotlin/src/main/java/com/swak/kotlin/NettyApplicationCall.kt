package com.swak.kotlin

import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.*


fun fibProducer() = produce(CommonPool) {
    //建立生产者
    var terms = Pair(0L, 1L)
    while (true) {
        send(terms.first) //把资源送到channel
        terms = Pair(terms.second, terms.first + terms.second)
    }
}

fun fibConsumer(id: Int, channel: ReceiveChannel<Long>) = launch(CommonPool) {
    //建立消费者
    channel.consumeEach {
        println("#$id consumed $it")
    }
}

//假设我们要找出数列中的素数...
fun fibPrimeProducer(fibQueue: ReceiveChannel<Long>) = produce(CommonPool) {
    //假设函数isPrime()能判断传进去的正整数是否素数
    fibQueue.consumeEach { if (it / 2 === 0L) send(it) }
}

fun main(args: Array<String>) = runBlocking<Unit> {
    val producer = fibProducer()
    (1..10).forEach {
        val fibPrime = fibPrimeProducer(producer).receive()
        println(fibPrime)
    }
    producer.cancel()
}