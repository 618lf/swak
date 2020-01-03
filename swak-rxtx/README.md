# 简单的串口操作

## 如何使用
- spring boot 项目请使用 [spring-boot-starter-rxtx](https://github.com/han1396735592/spring-boot-starter-rxtx) 

1. 引入依赖

```xml
<dependency>
    <groupId>cn.qqhxj.common</groupId>
    <artifactId>rxtx</artifactId>
    <version>1.3.1-RELEASE</version>
<dependency>
```
2. 设置串口

portName such as COM1 或者使用 `SerialUtils.getCommNames();`

```
 SerialPort connect = SerialUtils.connect(portName, 9600);
```
3. 设置串口读写器
```
SerialContext.setSerialReader(new VariableLengthSerialReader('{', '}'));
```
4. 设置串口数据解析器
```
SerialContext.getSerialDataParserSet().add(new StringSerialDataParser());
```
5. 设置串口事件监听器
```
SerialContext.setSerialPortEventListener(new DefaultSerialDataListener());
```
6. 设置串口byte数据处理器(可选)
```
SerialContext.setSerialByteDataProcessor(new SerialByteDataProcessor() {
    @Override
    public void process(byte[] bytes) {
        System.out.println(bytes);
    }
});
```
7. 设置自定义的串口数据解析器

需要实现 ` interface SerialDataParser<T>`
```
 SerialContext.getSerialDataParserSet().add(new SerialDataParser<Object>() {
    @Override
    public Object parse(byte[] bytes) {
        return null;
    }
});
```


8. 设置串口对象处理器(可选,需要有对应的串口数据解析器)
```
SerialContext.getSerialDataProcessorSet().add(new SerialDataProcessor<T>() {
    @Override
    public void process(T t) {
        System.out.println(t);
    }
});
```


问题1： How can I use Lock Files with rxtx? in INSTALL

解决方法：
Windows

RXTXcomm.jar goes in \jre\lib\ext (under java)
rxtxSerial.dll goes in \jre\bin

Mac OS X (x86 and ppc) (there is an Installer with the source)

RXTXcomm.jar goes in  /Library/Java/Extensions
librxtxSerial.jnilib goes in /Library/Java/Extensions
Run fixperm.sh thats in the directory.  Fix perms is in the Mac_OS_X
subdirectory.

Linux (only x86, x86_64, ia64 here but more in the ToyBox)

RXTXcomm.jar goes in /jre/lib/ext (under java)
librxtxSerial.so goes in /jre/lib/[machine type] (i386 for instance)
Make sure the user is in group lock or uucp so lockfiles work.

Solaris (sparc only so far)

RXTXcomm.jar goes in /jre/lib/ext (under java)
librxtxSerial.so goes in /jre/lib/[machine type]
Make sure the user is in group uucp so lockfiles work.


A person is added to group lock or uucp by editing /etc/groups.  Distributions
have various tools but this works:

lock:x:54:   becomes:
lock:x:53:jarvi,taj 

Now jarvi and taj are in group lock.

Also make sure jarvi and taj have read and write permissions on the port.


很遗憾一直提示这个错误，后面换一个库试一下：（http://fizzed.com/oss/rxtx-for-java）
please see: How can I use Lock Files with rxtx? in INSTALL