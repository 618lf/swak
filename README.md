# swak
一款參照 SPRINGBOOT 和 WEBFLUX 實現的响应式 http 服务器，主要功能如下：
swak-code : reactivex netty server
swak-web  : mvc、 router、 security
swak-jdbc : datasource、jdbc、mybatis
swak-redis: redis、eventbus
swak-actuator : actuator
swak-sample : 样例

# 启动线程
系统启动之后，如果开启 redis 的pub/sub 的功能则开始5个线程，不知道干吗的.
其他模块的都是基于 nio 的io线程。所以一般不会阻塞线程，除了数据库连接外。

# 异步分析
整个系统通过 mono 进行事件推送，其他模块可以通过 future 和 mono 接入到服务器
处理链上。
mono.create 或 mono.fromFuture 进行异步事件推送。

# 耗时处理
WEB 服务器只需要启动4个线程就能提供高性能高并发服务，如果面对IO，则需要进行异步处理，
例如异步http、异步redis、异步数据库操作。当然数据库本身是不支持异步的，只能靠线程池
来模拟，然后通过mono.fromFuture来进行异步推送。

同理，如果耗时的操作，也需要通过线程池来模拟异步，然后推送到mono 执行链上。
@see Workers