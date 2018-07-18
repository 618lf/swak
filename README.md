# swak
一款參照 SPRINGBOOT 和 WEBFLUX 實現的响应式 http 服务器，主要功能如下：
swak-code : reactivex netty server
swak-web  : mvc、 router、 security
swak-jdbc : datasource、jdbc、mybatis
swak-redis: redis、eventbus
swak-actuator : actuator
swak-sample : 样例

# 启动线程
系统启动之后，如果开启 redis 的pub/sub 的功能则开始5个线程，不知道干吗的. 其中有一个thread 
named： thread-9（or 12）
其他模块的都是基于 nio 的io线程。所以一般不会阻塞线程，除了数据库连接外。

如果启动了 micrometer 则会启动一个线程，named： thread-4 

到此为止： 系统中所有启动的线程都知道来源了。


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


# 版本0.0.9
为什么会有这一个版本：
加入了拦截器的代码

在写业务代码的过程中，发现业务的接口用ComplableFuture 来做可能更好一点。
业务代码中一般不需要切换线程，除非需要访问IO。

所以这个决定升级一个版本来将接口改为 ComplableFuture 看是否好用一点

# 版本0.1.0
准备修改接口中的 mono -> ComplableFuture
一点点心得，不知道正不正确：
mono             用作事件驱动，不用于耗时操作，不切线程，可能在不同的线程中执行后续代码
构建事件执行链，通过fromFuture 将数据应用于执行链上，从而驱动 请求 -> 响应
ComplableFuture  用作数据驱动，应用于耗时操作，切换线程，mono 通过 fromFuture 来和 ComplableFuture 对接上
所有业务接口需要调用的地方，返回值是 ComplableFuture， 这样方便业务代码接入到 执行链中，如果返回的是mono，则后续需要
执行耗时操作还是需要使用 ComplableFuture，同时需要多次转换到 mono。
尽量保证整个执行链中只需要一次 fromFuture 来转换
