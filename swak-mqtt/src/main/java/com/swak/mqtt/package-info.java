/**
 * @see 可以参考 -- jmqtt 来实现 mqtt 服务器，也可以是 rabbitMQ 或其他一些实现了 mqtt 协议的产品
 * @see 可以是 emqttd http://www.emqtt.com/
 * @see 客户端可以是 org.eclipse.paho.client.mqttv3，不需要自己来开发
 * @see 我所理解的 mqtt，服务器来说有很完整的实现，类似消息队列的这种模式，不需要自己实现。当然可以实现一个玩玩也可以。
 * @see 使用起来类似消息队列。设备是生产者或消费者，包括服务器也是生产者或消费者。
 */
package com.swak.mqtt;