/*package com.tmt.vert

import io.vertx.core.AbstractVerticle

*//**
 * manifest {
    attributes "Main-Class": "io.vertx.core.Launcher",//改为Launcher
    "Main-Verticle": "io.example.MainVerticle"//新增Main Verticle属性，对应MainVerticle类
   }
   通过这种方式来启动 -- 官方推荐
 *//*
open class MainVerticle : AbstractVerticle() {

    *//**
     * 原来可以这样获取一个类的名称
     *//*
    override fun start() {
        vertx.deployVerticle(HelloWorldVerticle::class.java!!.getName())
    }
}*/