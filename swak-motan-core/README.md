# 这个包的目的是定制化 motan 版本，基于 1.1.1
改动如下：
1. 将如下包合并为一个
motan-core 
motan-transport-netty4
motan-springsupport
motan-registry-zookeeper
2. 删除了一些不需要（用的极少）的jar common系列，guava。
3. 下一步测试可用性，将监控集成过来。
