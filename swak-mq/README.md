# swak-mq
https://bintray.com/rabbitmq/rpm/erlang/20.3-1
yum -y install erlang socat
rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
rpm -Uvh rabbitmq-server-3.7.13-1.el7.noarch.rpm

/usr/sbin/rabbitmq-server
rabbitmq-server start
rabbitmq-server -detached #后台运行
rabbitmq-server stop
rabbitmqctl status

还有就是开启管理界面可以通过命令：rabbitmq-plugins enable rabbitmq_management
http://127.0.0.1:15672/


之后可以研究：
wireshark 抓包