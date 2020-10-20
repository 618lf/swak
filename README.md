# 已发布到maven中央仓库 http://repo1.maven.org/maven2/

可以通过如下坐标在系统中引入   
```
&lt;dependency&gt;  
	&lt;groupId&gt;com.github.618lf&lt;/groupId&gt; 
	&lt;artifactId&gt;swak-starter&lt;/artifactId&gt;  
	&lt;version&gt;1.0.0_final&lt;/version&gt;  
&lt;/dependency&gt; 
```
# swak
目前这个配置下Eclipse 下能lombok正常的编译、打包   
swak分为很多单独的模块，每个模块都是单独的功能，相互之间没有强依赖，可以单独使用。  

swak 中所有io都是基于netty 的。  

每个模块说明如下：  
swak  --- 父模块，定义基本的依赖，打包配置等，详见pom.xml  
swak-actuator -- 仿springboot 的actuator 模块（线上没启用）  
swak-app      -- 基础的 andriod、vue 框架（线上没启用）  
swak-core     -- 基础模块，提供基础依赖（spring，cglib，fastjson，netty）等，提供一些工具类。  
swak-excel    -- excel 的快捷导入和下载。  
swak-flux     -- 本人手写的响应式http服务器，响应式的权限验证框架（shiro的响应式版本），注意：代码  
                 没有在线上使用，可以一起交流学习。  
swak-fx       -- java桌面版，嵌入了webkit浏览器，可以和springboot web项目结合起来用  
swak-gen      -- 代码生成工具，未完成  
swak-groovy   -- 用来做表达式额解析执行。   
swak-http     -- 基于async-http-client（基于netty）的客户端，本模块只是做了一层封装简化json，xml等的解析。  
swak-jdbc     -- 数据库处理模块，基于spring，mybatis，HikariCP。  
swak-kotlin   -- 本来想研究协程的处理，一直没成功过，可以一起学习交流。  
swak-metrics  -- 监控，基于dropwizard metrics。除了基本的cpu，内存还有对线程池的监控方法执行的监控等，比较完整。  
swak-mongodb  -- mongodb的客户端，在自己的博客系统中使用mongodb实现。  
swak-motan    -- 所有代码来至motan。  
swak-mq       -- rabbitmq 的java客户端，通过研究spring data 中的代码，自己实现的rabbitmq客户端，有消费者重试，  
                 发送重试，重试队列等实现，又兴趣的可以一起交流  
swak-mqtt     -- 只是记录  
swak-package  -- maven打包的配置  
swak-qrcode   -- 二维码生成的工具包  
swak-quartz   -- 定时任务的实现  
swak-redis    -- 基于lettuce 的redis操作客户端，简单封装，更好使用。  
swak-rocketmq -- 只是记录  
swak-starter  -- 启动项目，基于并改造至springboot。提供最基本模板的启动依赖。  
swak-template -- 居于freemarker 的模板技术  
swak-test     -- 测试项目  
swak-vertx    -- 封装vertx，可以像使用springmvc一样使用vertx,简单高效。并提供jwt的token技术，类shiro 的权限校验技术。  
                 基于注解的api映射技术。（线上使用中，持续更新）  
swak-wechat   -- 微信的基本操作：登录、发送消息，接收消息等。  

# 线上项目一：校园自助取餐项目  
【项目介绍】为学校建立无人食堂，家长在线点餐，学生通过取餐码取餐。  
【后台技术】vertx Java、Vue.js、Spring Boot、Netty、MySql、Redis、MQ、mqtt  
【前台技术】vue    
【界面预览】以下截图来至测试环境    
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txpmAaA-mAAh1wPUx16s926%20%281%29.PNG)  
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txpmAaA-mAAh1wPUx16s926.PNG)    
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txpyALR13AAO8emZq5oM202.PNG)           
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txq-AWaQfAAEECixmopg907%20%281%29.PNG)          
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txqCANccHAAa0RdxT2Gg844.PNG)                
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txqeAc621AAXxA01myZ0728.PNG)        
![Image text](https://wushiji.oss-cn-shenzhen.aliyuncs.com/introduce/-wKgBDV1txqqAKpqHAAk6aKkfZrA043.PNG)        

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

vertx 的 internalBlockingThreads， workerThreads
如果当前线程是vertx线程，则使用 executeBlocking 执行阻塞代码时会使用workerThreads 来执行
否则会使用 internalBlockingThreads 来执行代码。

例如，api 中执行executeBlocking 使用 workerThreads 来执行代码

vertx：
RouteImpl --  具体的某个路由
RouterImpl -- 所有的路由集合
RoutingContextImpl -- 请求对应的路由执行链
Http1xServerConnection -- 一次请求的处理

问题1： 找遍了spring aop 中的 exposeProxy，之后测试是否使用另一种方式的aop。发现默认aop没有设置的地方。
不知道以后会不会提供。也可以重新从spring中获取代理类
-- 目前再baseService 中可以 getProxy 来获取当前的代理对象

问题2： http 的简单处理方式可以参考hutool的处理方式（太过于复杂，每必要）

-- 还没研究他的处理方式

问题3： 验证框架，感觉使用简单点的方式就够了。
-- 已经实现了一版简单的，适合当前系统的版本

问题3： 发现循环依赖会导致aop失效，@lazy能避免这个问题。

-- 并非循环依赖的问题，二是postBeanprocess 中获取的bean 不一定是spring加工完成的bean。
所以vertx再发布服务时需要再次获取此bean。包括motan 的处理也一样需要再次获取，才能保证获取
的bean时proxy的。

# 版本0.1.3.3
目的：简化，删除不需要的jar，添加监控。

问题1： 对系统全局链路跟踪的想法，异步系统如果想做链路跟踪，在发起异步请求时获取或生成traceId，放入请求的参数中，
服务执行时，需要将traceId 返回给调用方，所以redis，http异步客户端做不到这一点，因为无法决定服务端代码的参数。
只能在服务端和客户端都是我们的系统中来做traceId。那么对于redis，http 的访问，要么移动到service中执行。
不可能在 EventLoop 中使用同步的方式。可以考虑把redis，http放入service中执行，需要在服务端中支持异步处理。
例如motan的服务端异步处理，或vertx服务端异步处理。

为redis、http来配置单独的服务业务不错的想法。只要服务器端支持异步处理即可，异步处理也是很好实现的。
让前端更简单，仅仅将不同的服务器串起来。不需要真实的访问其他服务器端（例如redis服务器，http服务器等）。

--- 升级到此版本的系统，按照此想法来修改。不然不做 trace。

dubbo 中的trace 客户端和服务器配合，将traceId存储在参数中。在客户端和服务器端都使用线程变量来保证业务无关。
问题3： 发现循环依赖会导致aop失效，@lazy能避免这个问题。
问题4： 数据库事务问题，又发现数据库事务问题，而且表现很奇怪，只回滚最后操作的一张表的数据，很奇葩的处理方式。
       发现是在配置数据库链接池时设置了useLocalTransactionState = true。导致的。
       在 druid 中不能配置此参数，使用数据库链接池默认的。
       在 Hikari 中可以配置此参数，如果配置为true就会导致很奇葩的问题。配置成false即可。
       结论： 数据库链接池的两个参数比较重要，AutoCommit 要和数据库保持一致。
       useLocalTransactionState 要设置为false。
       Hikari DriverDataSource 获取链接时，将所有的参数设置进去了。
       
问题5： 看到我重试队列的处理方式之后，本义为可以优化，但发现现在这样是最优的方式。
       重试队列都是延时队列，不能直接设置消费者，如果要加消费者，则必须在设置一个消费队列，将消费队列的死信队列设置为
       下一个重试队列。这样需要定义多个消费者，和消费队列，不过此方法也值的一试。暂时使用主动发送到下一重试队列的方式。
       
问题6： 锁的基本目的是保证共享数据的安全，所以同一时刻只能一个线程执行修改数据的代码。互斥锁的执行性能还是很高的。比单线程
       执行性能要高，但最大的问题是，会阻塞线程。
       如果执行的代码需要点时间，则互斥锁性能会急剧下降，不仅仅会阻塞线程。
       
# 版本1.0.0
第一个正式的版本，在多个项目实际使用中修改、测试。这个版本的目的是完善mongodb，完善读写分离，测试sharding jdbc 的读写分离对
事务带来的影响。
读写分离的方式： 单纯的读只读库，只要有写都需要走写库。不管是先发生读、还是先发生写。如果先写，后面再读，可以先走读库，后面的事务
部分走写库。

问题1： 读写分离的测试使用的是sharding-jdbc 满足预期，后面添加分库分表的测试例子；  
问题2： 分库分表之后最重要的是分布式事务，先使用最简单的分布式事务方式，基于消息队列的最终一致性。
改进3： vertx 权限框架的改进：支持异步获取权限，支持方法级别的鉴权方式。
问题4： java8下发布motan服务，时抛出 Inconsistent stackmap frames at branch target 这类错误。网上提示加上啥编译参数，很奇葩的修正方式（或者不是同一个问题）。解决方式是 AccessStatisticFilter 的问题。 把 bizProcessTime 的问题移动到56行即可。  

优化权限的验证：  

url 配置方式：  
和权限验证：role[a,b]  
或权限验证：role[a|b]  

注解 配置方式：  
RequiresPermissions(value={"a","b"}, logical = Logical.AND)  
RequiresPermissions(value={"a","b"}, logical = Logical.OR)  
RequiresRoles(value={"a","b"}, logical = Logical.AND)  
RequiresRoles(value={"a","b"}, logical = Logical.OR)  

后期如果有需要可以支持user:admin:* 这类权限的配置

问题5： spting 和 vertx 的启动顺序
ContextRefreshedEvent > startReactiveServer > ReactiveServerInitializedEvent
如果需要使用 vertx 组件中的内容则需要监听 ReactiveServerInitializedEvent 事件



# 版本1.0.0 合并到 master 上
以后在 master 版本上做新开发，具体的版本发布稳定版本

新功能1： api doc 的自动生成 -- 开发中(目前遇到的问题是，使用的jar无法获取 classes 的类)  
新功能2： 代码生成工具 -- 待开发   
新功能2： 支持 websocket  

# 版本1.0.0  之 websocket认证
1.open时发参数  
var  ws = new WebSocket("ws://....");  
ws.onopen=function(){  
    ws.send(token)  
}  
2.url里带参数  
var  ws = new WebSocket("ws://url/1/3/9");  
var  ws = new WebSocket("ws://url?userid=1"); 
3.替换子协议   
var  ws = new WebSocket("ws://url"，【token】);

# 版本1.0.0 功能
1、程序的启动   
使用springboot的启动方式，可以通过maven打成可执行的jar，也可以通过springbootmaven插件打成fatjar  

2、程序的停止 
第一种：可以通过创建一个管理员api，通过他来发送停止命令    
第二种： 通过信号量的处理 ， 系统中默认注册的信号处理器  
org.springframework.context.support.ApplicationContext 系统关闭处理事件
io.vertx.core.file.impl.FileResolver 的关闭处理事件（默认设置不启用文件缓存）
java.util.logging.LogManager.Cleaner 
org.h2.engine.OnExitDatabaseCloser （如果使用了h2数据库）


# 版本1.0.2  （预留的版本）
此版本是未来的版本，对标vertx 的 4.0 系列，等vertx的4.0出可用版本之后启用此版本


# 演示项目
具体的演示项目参见：
[地址](https://gitee.com/618lf/springboot-vertx)

springboot-vertx 是基于swak 的一个演示项目，主要演示了如下部分：  
1. 基本的启动框架  
2. api的配置方式，例如AdminApi、AnnoApi、ParamApi、UserApi。  
3. api权限的校验，简单的权限校验方式，配置方式AppConfiguration 第 48 行代码到第 54 行代码。  
4. 参数的传递方式，详细见测试代码 ParamTest.java  
5. 返回值的使用方式，详细见测试代码 ParamTest.java   
6. 项目如何测试继承AppRunnerTest，详细见测试代码 ParamTest.java  
7. service 的配置 UserServiceImpl  

# 演示项目:自定义的项目启动  
大家都很熟悉springboot的四大特性项目依赖，自动化配置。本人也非常喜欢，但不太喜欢引入过多的start.jar来引入组件。  
所以本人对spring做了一些简单的定制。将他的启动属性文件spring.factories 改为 swak.factories.  
改变的方法也很简单中有详细的源码：swak-starter。所有我们熟悉的组件都放在 com.swak.config 中来自启动。  
AppConfiguration.java -- 项目级别的启动配置  
AppRunner.java        -- 启动入口，执行main方法  
执行注解com.swak.ApplicationBoot所引入的com.swak.selector.AutoConfigurationImportSelector，此类将会执行swak.factories中引入的启动配置。  

如果是web环境（依赖的jar有vertx的相关类）则会实例化 ReactiveServerApplicationContext，否则会实例化     AnnotationConfigApplicationContext。  
ReactiveServerApplicationContext 实例化之后会启动 ReactiveServer，vertx 开始初始化。    
vertx 的启动依赖如下：（swak-vertx）  
ReactiveServer -> MainVerticle -> ServiceVerticle(多个) -> HttpVerticle(一个多实例)  
如上是简单的解释，后续在完善。  

# 演示项目:API的配置方式、参数处理、权限校验、返回值处理
通过注解的方式来配置，类似springmvc 的方式.注解只有：PageController、RestController、PostMapping、GetMapping简单明了。  
PageController 和 RestController 没有过多的差别，只是说明此Controller是来做展示页面的。  
API 中最重要的类是 HandlerAdapter, 将api的path绑定到vertx中的 Router，调用api的method，处理参数，处理返回值等。

# 演示项目:响应式HTTP服务器的简单介绍
何为响应式，说白了就是回调，IO处理好之后回调注册的方法。这样线程就不需要等待IO完成才继续执行后续代码.  
vertx 被称为java中的node.js，多线程版node.js。    
swak是基于vertx做了简单的封装，让我们能像使用springmvc一样使用vertx。  
响应式开发并不难，难的是需要弄清楚回调之后的代码在哪个线程中执行。以前我们开发ssm项目时，一个请求对应一个线程，从头走到尾，我们基本上不用理会当前线程是谁，执行是否需要花费很长时间。  
我觉得响应式最大的特点是在请求响应这条事务中可以任意（只是比如而已）的切换线程。  
例如，本例子中，Controller的执行在netty的eventloop线程中，UserServiceImpl 的执行在 vertx的work线程中。 

下面简单解释下这个执行过程。vertx 的关键点其内部的 Eventbus。可以简单的把他想象成为一个map。map 的key是接口类型，value是实现类对象的封装。  
Controller 调用 service 时，实际上是发送一个消息到 Eventbus，消息中包含service的接口类型，Eventbus获取相应的实现类对象并在相应线程中执行，将返回值通过Eventbus通过消息的形式返回。

具体参见 InvokerHandler 第 73 行 发送消息代码。

service 对象执行 参见 ServiceVerticle handle 部分


# 演示项目:项目中为啥会出现这两类接口
UserService、UserServiceAsync     
UserService -- 和我们做ssm时后的service接口一样，用于同步返回，一帮用于jdbc 的操作接口。  
UserServiceAsync -- 异步返回版本，这个可以自动生成，但自动生成的代码返回值中的范型无法动态设置，所以现在都手动生成此异步接口。  


# 演示项目:关于开发响应式系统的一些经验
以前我们开发ssm时喜欢将系统分为三层的开发模式，Controller、service、dao。现在也一样也是三层。不同点在于Controller在eventloop线程中执行，service、dao和之前一样在work线程中执行,service、dao以前怎么开发现在也怎么开发，没变。  
Controller 调用 service 通过异步发消息的方式来调用。

Controller 的 代码  

@RestController(path = "/api/user", value = "userApi")  
public class UserApi {  
  
	@VertxReferer  
	private UserServiceAsync userService;  
  
	/**  
	 * 获取用户  
	 *   
	 * @param subject  
	 * @return  
	 */  
	@GetMapping("/get")  
	public CompletableFuture<Result> get(Subject subject) {  
		return userService.get(subject.getIdAsLong()).thenApply(res -> Result.success(res));  
	}  
}  

service 的代码，和之前一样使用spring声明式事务等。

@VertxService  
public class UserServiceImpl implements UserService {  
  
	@Override  
	public User get(Long id) {  
		return new User().setId(id);  
	}  
}  

java的jdbc是同步执行的，spring的事务业务依赖当前线程的，所以不要在service中切换线程。  
其他一些io，例如redis，http 客户端，我都是用的基于netty的异步客户端。  

相关说明后续补充...