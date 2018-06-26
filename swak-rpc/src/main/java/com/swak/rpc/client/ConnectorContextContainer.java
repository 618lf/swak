package com.swak.rpc.client;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import com.swak.reactivex.transport.resources.LoopResources;
import com.swak.rpc.api.URL;
import com.swak.utils.Lists;

/**
 * 连接管理的容器
 * 
 * @author lifeng
 */
public class ConnectorContextContainer {

	final RpcClientProperties properties;
	final LoopResources loopResources;
	Map<String, List<String>> services;
	Map<String, Connector> connectors;

	public ConnectorContextContainer(RpcClientProperties properties) {
		this.properties = properties;
		this.loopResources = LoopResources.create(properties.getMode(), -1, -1, "RPC-");
	}
	
	/**
	 * 连接这几个地址
	 * @param urls
	 */
	public void connect(List<URL> urls) {
		List<String> servers = Lists.newArrayList();
		for(URL url: urls) {
			String serverKey = url.getServerKey();
			servers.add(serverKey);
			if (connectors.get(serverKey) == null) {
				Connector connector = this.connect(url);
				connectors.put(serverKey, connector);
			}
		}
		if (servers.size() != 0) {
			String serviceKey = urls.get(0).getServiceKey();
			services.put(serviceKey, servers);
		}
	}
	
	/**
	 * 连接道地址
	 * @param url
	 * @return
	 */
	private Connector connect(URL url) {
		InetSocketAddress address = new InetSocketAddress(url.getHost(), url.getPort());
		Connector connector = new ConnectorContext(loopResources, properties, address, 1);
		connector.connect();
		return connector;
	}
	
	/**
	 * 获取一个连接,
	 * 在这里写一些算法，负载均衡的算法
	 * @param url
	 * @return
	 */
	public Connector select(URL url) {
		String serviceKey = url.getServiceKey();
		List<String> servers = services.get(serviceKey);
		return connectors.get(servers.get(0));
	}
}