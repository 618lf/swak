package com.swak.hazelcast;

import java.util.Set;

import com.hazelcast.cluster.Member;
import com.hazelcast.cluster.MembershipEvent;
import com.hazelcast.cluster.MembershipListener;
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
		HazelcastInstance server1 = Hazelcast.newHazelcastInstance();
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
		Set<Member> members = server1.getCluster().getMembers();
		System.out.println(members);

		// 开启集群
		HazelcastInstance server2 = Hazelcast.newHazelcastInstance();
		server2.shutdown();
	}
}
