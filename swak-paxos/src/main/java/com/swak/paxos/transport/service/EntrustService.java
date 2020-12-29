package com.swak.paxos.transport.service;

import com.swak.paxos.config.Config;
import com.swak.paxos.transport.client.TcpClient;
import com.swak.paxos.transport.server.TcpServer;

/**
 * 委托服务： 将消息委托给Leader 发送
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:40:09
 */
public class EntrustService {

	private TcpServer tcpServer = null;
	private TcpClient tcpClient = null;
	private final Config config;

	public EntrustService(Config config) {
		this.config = config;
	}
}