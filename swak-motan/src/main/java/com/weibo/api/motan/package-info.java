/**
 * 代码全部取至 motan 的源码
 * https://github.com/weibocom/motan 1.1.3
 * 
 * 引入：
 * com.weibo.api.motan.core
 * com.weibo.api.motan.registry.zookeeper
 * com.weibo.api.motan.config.springsupport
 * 
 * 引入：
 * motan-core/META-INF/src/main/resources/services
 * motan-registry-zookeeper/META-INF/src/main/resources/services
 * motan-transport-netty4/META-INF/src/main/resources/services
 * 
 * 修改：
 * com.weibo.api.motan.codec.Serialization
 * 修改：
 * MotanService @Service
 * 修改：
 * NettyServer
 * 修改：
 * ResponseFuture
 * 修改：
 * 统一命名线程池 参考vert.x的线程池设计
 * FailbackRegistry.java
 * RefererSupports.java
 * RpcStats.java
 * NettyChannelFactory.java
 * NettyClient.java
 * HeartbeatClientEndpointManager.java
 * StatsUtil.java
 */
package com.weibo.api.motan;
