package com.swak.motan.manager;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.I0Itec.zkclient.ZkClient;

import com.weibo.api.motan.exception.MotanFrameworkException;

public class ZkClientWrapper {

	private final String registryUrl;
	private ZkClient zkClient;

	public ZkClientWrapper(String registryUrl) {
		this.registryUrl = registryUrl;
	}

	@PostConstruct
	void init() {
		try {
			zkClient = new ZkClient(registryUrl, 10000);
		} catch (Exception e) {
			throw new MotanFrameworkException("Fail to connect zookeeper, cause: " + e.getMessage());
		}
	}

	@PreDestroy
	void destory() {
		zkClient = null;
	}

	public ZkClient getZkClient() {
		return zkClient;
	}
}