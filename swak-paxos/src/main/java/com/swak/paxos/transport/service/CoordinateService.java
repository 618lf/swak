package com.swak.paxos.transport.service;

import com.swak.paxos.config.Config;
import com.swak.paxos.transport.server.TcpServer;
import com.swak.paxos.transport.server.UdpServer;

/**
 * 协调服务:
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:39:26
 */
public class CoordinateService {

	private TcpServer tcpServer = null;
	private UdpServer udpServer = null;
	private final Config config;

	public CoordinateService(Config config) {
		this.config = config;
	}
}