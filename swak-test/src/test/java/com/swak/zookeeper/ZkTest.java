package com.swak.zookeeper;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import org.apache.zookeeper.CreateMode;

import com.swak.config.zookeeper.ZookeeperAutoConfiguration;
import com.swak.config.zookeeper.ZookeeperProperties;

public class ZkTest {

	public static void main(String[] args) throws InterruptedException, BrokenBarrierException {
		CyclicBarrier latch = new CyclicBarrier(2);
		ZookeeperProperties properties = new ZookeeperProperties();
		properties.setAddress("192.168.137.100:2181");
		properties.setUsername("admin");
		properties.setPassword("admin");
		ZookeeperAutoConfiguration configuration = new ZookeeperAutoConfiguration(properties);
		ZookeeperService zookeeperService = configuration.zookeeperService();
		zookeeperService.addStateListener(stat -> {
			System.out.println("状态变化：" + stat);
			if (stat == 1) {
				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BrokenBarrierException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		latch.await();
		String path = "/hanqian/lock";
		System.out.println("创建前");
		zookeeperService.asyncCreate(path, CreateMode.PERSISTENT).whenComplete((r, e) -> {
			if (e != null) {
				System.out.println("创建失败！" + e.getMessage());
			} else {
				System.out.println("创建成功");
			}
		});
		System.out.println("创建后2");
		System.out.println(zookeeperService.getChildren("/"));
		latch.await();
	}

}
