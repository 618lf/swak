package com.swak.hazelcast;

import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
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
public class HazelcastTest {

	public static void main(String[] args) {

		// 日志
		System.setProperty("hazelcast.logging.type", "slf4j");

		Config config = new XmlConfigBuilder().build();
		MulticastConfig multicastConfig = config.getNetworkConfig().getJoin().getMulticastConfig();
		multicastConfig.setEnabled(false);
		TcpIpConfig tcpIpConfig = config.getNetworkConfig().getJoin().getTcpIpConfig();
		tcpIpConfig.setEnabled(true);

		HazelcastInstance server1 = Hazelcast.newHazelcastInstance(config);
		server1.getCluster().addMembershipListener(new MembershipListener() {

			@Override
			public void memberAdded(MembershipEvent membershipEvent) {
				System.out.println("集群添加");
			}

			@Override
			public void memberRemoved(MembershipEvent membershipEvent) {
				System.out.println("集群删除");
			}
		});

		System.out.println("启动~");
		
		// 消费消息
		server1.getTopic("log").addMessageListener((m) -> {
			System.out.println("消费消息：" + m);
		});
	}
}
