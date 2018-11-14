# 暂时打包的方式，方便以后使用
1. 打springboot 的 fatjar， 不知道有什麼好处
   spring-boot-maven-plugin
2. 打普通的可执行jar
   maven-jar-plugin
3. 在前面两个的基础上，使用来自定义打包的结构
   maven-assembly-plugin