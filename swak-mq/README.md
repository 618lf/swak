# swak-mq
https://bintray.com/rabbitmq/rpm/erlang/20.3-1（使用的是rabbitmq定制的erl，和第二种方式使用的来源一致，只是版本不同）
yum -y install erlang socat
rpm --import https://www.rabbitmq.com/rabbitmq-release-signing-key.asc
rpm -Uvh rabbitmq-server-3.7.13-1.el7.noarch.rpm

/usr/sbin/rabbitmq-server
rabbitmq-server start
su rabbitmq-server -detached #后台运行(这个可行)
rabbitmq-server stop
rabbitmqctl status
rabbitmqctl start_app

还有就是开启管理界面可以通过命令：rabbitmq-plugins enable rabbitmq_management
http://127.0.0.1:15672/


之后可以研究：
wireshark 抓包


rabbitmqctl stop_app：关闭应用

rabbitmqctl stop_app：启动应用

rabbitmqctl status：节点状态

rabbitmqctl add_user username password：添加用户

rabbitmqctl list_users：列出所有用户

rabbitmqctl delete_user username：删除用户

rabbitmqctl clear_permissions -p vhostpath username ：清楚用户权限

rabbitmqctl add_vhost vhostpath：创建虚拟主机

rabbitmqctl list_vhost：列出所有虚拟主机

rabbitmqctl list_permissions -p vhostpath：列出虚拟主机所有权限

rabbitmqctl delete_vhost vhostpath：删除虚拟主机

rabbitmqctl list_queues：查看所有队列信息

rabbitmqctl -p vhostpath purge_queue blue：清除队列里的消息

rabbitmqctl reset：移除所有数据，要在rabbitmqctl stop_app之后使用

rabbitmqctl join_cluster<clusternode> [--ram]：组成集群命令

rabbitmqctl cluster_status：查看集群状态

rabbitmqctl change_cluster_node_type disc  | ram：修改集群节点的存储模式

rabbitmqctl forget_cluster_node_ [--offline] ：忘记节点（摘除节点）


#重点（如下操作能保证成功，而且简单）
https://github.com/rabbitmq：有相应的jar
https://www.cnblogs.com/liaojie970/p/6138278.html 指导文档
https://www.rabbitmq.com/cli.html 服务器端的管理监控工具

wget https://github.com/rabbitmq/rabbitmq-server/releases/download/v3.7.14/rabbitmq-server-3.7.14-1.el7.noarch.rpm
wget https://github.com/rabbitmq/erlang-rpm/releases/download/v21.3.3/erlang-21.3.3-1.el7.x86_64.rpm
scp -P34872 catax_admin@10.170.11.226:/home/catax_admin/rabbitmq-server-3.7.14-1.el7.noarch.rpm   /mnt/soft
scp -P34872 catax_admin@10.170.11.226:/home/catax_admin/erlang-21.3.3-1.el7.x86_64.rpm   /mnt/soft
rpm -ivh erlang-21.3.3-1.el7.x86_64.rpm
yum install socat
rpm -ivh rabbitmq-server-3.7.14-1.el7.noarch.rpm

mkdir -p /usr/local/rabbitmq/mnesia
mkdir -p /usr/local/rabbitmq/log
chmod -R 777 /usr/local/rabbitmq
vi /etc/rabbitmq/rabbitmq-env.conf
RABBITMQ_MNESIA_BASE=/usr/local/rabbitmq/mnesia
RABBITMQ_LOG_BASE=/usr/local/rabbitmq/log

/sbin/service rabbitmq-server stop #关闭
/sbin/service rabbitmq-server start #启动
/sbin/service rabbitmq-server status #状态

cd /sbin(命令全部放在sbin目录下)
./rabbitmqctl list_users
./rabbitmqctl add_user admin admin 
./rabbitmqctl set_user_tags admin administraotr