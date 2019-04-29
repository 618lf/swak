#swak
发布当前版本：
cd /home/lifeng/git/swak
mvn deploy

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

最近注意力不是很集中，也遇到了不少没碰到过的问题：

问题1：
遇到一个问题： 如果自动配置中有bean 实现了 BeanFactoryPostProcessor, BeanPostProcessor 等接口，那么这个自动配置的优先级
就会很高。高度在内中定义的如下代码都不能正常实例化。所以记住这个，花了好长的时间才调试出来
@Autowired
private MotanProperties properties;

问题2：
javac 的 process，这个对我来说是一个比较新的概念，如果不是motan中用到了这种技术，估计还没了解过，其实也很简单，因为是javac 的标准功能，所以不会有问题。但在配置eclipse，idea等使用时不好看到效果。但配置maven 使用就很简单，
基本上执行mvn compile 就可以执行process，生成的代码会放到 target/generated-sources/annotations 这个目录，如果需要使用到这部分代码，需要使用一个maven 插件将 这个目录作为源码包，或者手动添加。
之前一致卡在 mvn compile 这个地方。 @see swak-sample-api
如果 mvn compile  也不自动生成代码，把 target 目录删除后再次执行就好了

问题2：
maven-resources-plugin 问题，如果war 项目一直提示这个插件文件，则需要在pom 文件中加入这个插件war 的pom 

# 版本0.1.1
升级一个新的版本,这个版本支持了 vertx。

待改进1：
应该可以想motan一样通过注解自动生成异步接口，只需要定义同步接口，这样通用性好一点。因为后端实现都是同步的，只有前端才需要异步。
所以这块是一个改进的点

获取返回类型有问题，暂时无法实现，只能统一是 Msg, 以后再研究

待改进2：
前端传入的参数包含子对象的改进，可以支持到第二层级

问题1：
使用新版本的mysql驱动导致时区问题。再驱动链接字符串中添加如下设置
?characterEncoding=utf8&useSSL=true&serverTimezone=UTC&nullNamePatternMatchesAll=true
serverTimezone=GMT%2B8

已经改进： 
1. 参考 motan 自动生成异步接口，但只能支持生成 Msg 的范型，到这一步就可以了。

2. 加入了 motan 的源码，定制化的motan

这个版本将待改进2和motan定制版实现且用起来之后就可以升级为新版本， 基本可以使用了

# 版本0.1.2
这个版本中基本的稳定版本： vertx + motan
打包之后，如何将配置文件放在 jar 包之外。相对jar 的目录
application.properties 会查找 config 目录下的文件
可以在 application.properties 指定 logback.xml的路径 logging.config=config/logback.xml
问题1： 6.0.6 版本的 mysql 驱动有bug 读取TEXT字段出错
将此版本的 mysql 驱动改为 5.1.47 后面版本将改为最新版8.0.15

# 版本0.1.3
升级 netty 和 springboot 和相关的jar

# 版本0.1.3.1
简化配置， 二级缓存，redis订阅的自动恢复，MQ 客户端。MQ 客户端和缓存的结合，发送时将消息存储在缓存中，应答超时或者应答失败，将消息重新发送。消费者需要做消息的冥等校验。

备注下Workers，此类已删除
用来执行耗时的操作。例如：数据库、文件IO、耗时计算（生成图片）
https://yq.aliyun.com/articles/591627
通过线城池 executor来异步执行任务，提供不同的获取结果的方式
延迟： stream 、mono 延迟执行，只有最后终端操作时才会触发整个执行链 future 、optional 立即执行
可重用： future、optional、mono 可重用 stream 不可重用
异步： future、mono 异步执行 stream、optional 不可异步
推模式还是拉模式： Stream 、Optional 是拉模式的 Future、mono 推模式
重要性： Mono、Optional、future 是可以重用的 意味着：可以多次获取结果，而不会重复计算
注意： sink 中返回的值不能为 null， 不然事件发送不出去

指标库：
http://throwable.coding.me/2018/11/17/jvm-micrometer-prometheus/

核心服务不能设置为后台进程，需要能关闭
关于 javapoet 自动生成异步接口，先maven clean。然后mvn compile来生成代码， scope 至少为 provided。

maven 内置属性:
${basedir}表示项目根目录，即包含pom.xml文件的目录;  
${version}表示项目版本。
${project.basedir}同${basedir};
${project.build.sourceDirectory}:项目的主源码目录，默认为src/main/java/.
${project.build.testSourceDirectory}:项目的测试源码目录，默认为/src/test/java/.
${project.build.directory}:项目构建输出目录，默认为target/.
${project.outputDirectory}:项目主代码编译输出目录，默认为target/classes/.
${project.testOutputDirectory}:项目测试代码编译输出目录，默认为target/testclasses/.
${project.groupId}:项目的groupId.
${project.artifactId}:项目的artifactId.
${project.version}:项目的version,于${version}等价 
${project.build.finalName}:项目打包输出文件的名称，默认 为${project.artifactId}${project.version}.

# 版本0.1.3.2
简化 baseEntity， 升级核心jar