/**
 * 代码全部取至 motan 的源码
 * https://github.com/weibocom/motan 1.1.6
 * 
 * 引入：
 * com.weibo.api.motan.core
 * com.weibo.api.motan.registry.zookeeper
 * com.weibo.api.motan.config.springsupport
 * com.weibo.api.motan.transport.netty4
 * 
 * 引入：
 * motan-core/META-INF/src/main/resources/services
 * motan-registry-zookeeper/META-INF/src/main/resources/services
 * motan-transport-netty4/META-INF/src/main/resources/services
 * 修改：
 * MotanConstants  :改了下最小线程数
 * 修改：
 * URLParamType  : 修改了序列化的方式 serialize("serialization", "hessian2")
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
 * 
 * 改进： 改变生成的接口目录
 * MotanAsyncProcessor
 * "src/main/generated-sources/"
 * 
 * java8 下的问题：
 * AccessStatisticFilter 需要修改  long bizProcessTime; 的位置，移动第56行即可。
 * 
 * 特別改進: StandardThreadExecutor
 */
package com.weibo.api.motan;
