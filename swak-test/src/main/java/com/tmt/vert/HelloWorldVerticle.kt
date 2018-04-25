package com.tmt.vert

import io.vertx.core.AbstractVerticle
import io.vertx.core.Handler
import io.vertx.core.eventbus.Message

class HelloWorldVerticle : AbstractVerticle() {
    override fun start() {
        val eb = vertx.eventBus()
        eb.consumer("com",  {
            message: Message<String>? ->
            println(message?.body())
            message?.reply("how interesting!")
        })

        eb.publish("com", "Yay! Someone kicked a ball")
    }
}