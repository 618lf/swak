package com.swak.hazelcast;

import com.hazelcast.config.Config;
import com.hazelcast.config.MulticastConfig;
import com.hazelcast.config.TcpIpConfig;
import com.hazelcast.config.XmlConfigBuilder;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * 分布式网格测试
 * 
 * @author lifeng
 * @date 2020年9月29日 下午12:12:22
 */
public class HazelcastTest2 {

	public static void main(String[] args) {

		// 日志
		System.setProperty("hazelcast.logging.type", "slf4j");

		Config config = new XmlConfigBuilder().build();
		MulticastConfig multicastConfig = config.getNetworkConfig().getJoin().getMulticastConfig();
		multicastConfig.setEnabled(false);
		TcpIpConfig tcpIpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
		tcpIpConfig.setEnabled(true);

		// 开启集群
		HazelcastInstance server1 = Hazelcast.newHazelcastInstance(config);
		System.out.println("启动~");
		server1.getTopic("log").addMessageListener((m) -> {
			System.out.println("消费消息：" + m);
		});
		server1.getTopic("log").publish("123");
	}
}
