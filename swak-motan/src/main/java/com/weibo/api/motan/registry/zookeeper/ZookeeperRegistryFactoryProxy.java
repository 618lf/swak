package com.weibo.api.motan.registry.zookeeper;

import com.weibo.api.motan.core.extension.SpiMeta;
import com.weibo.api.motan.registry.Registry;
import com.weibo.api.motan.registry.RegistryFactory;
import com.weibo.api.motan.rpc.URL;

/**
 * 代理 ZookeeperRegistryFactory 会判断需要的依赖是否存在
 * 
 * @author lifeng
 */
@SpiMeta(name = "zookeeper")
public class ZookeeperRegistryFactoryProxy implements RegistryFactory {
	private static Boolean availabled;
	static {
		try {
			Class.forName("org.I0Itec.zkclient.ZkClient");
			availabled = true;
		} catch (ClassNotFoundException e) {
			availabled = false;
		}
	}
	private RegistryFactory registryFactory;

	public ZookeeperRegistryFactoryProxy() {
		if (availabled) {
			registryFactory = new ZookeeperRegistryFactory();
		}
	}

	/**
	 * 如果不可用直接抛出异常
	 * 
	 * @param url
	 * @return
	 */
	@Override
	public Registry getRegistry(URL url) {
		if (!availabled) {
			throw new RuntimeException("java.lang.NoClassDefFoundError: org/I0Itec/zkclient/exception/ZkException");
		}
		return registryFactory.getRegistry(url);
	}
}