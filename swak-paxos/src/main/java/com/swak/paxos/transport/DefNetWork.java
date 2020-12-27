package com.swak.paxos.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;
import com.swak.paxos.node.Node;
import com.swak.paxos.transport.server.TcpServer;
import com.swak.paxos.transport.server.UdpServer;

/**
 * 默认的网络
 * 
 * @author DELL
 */
public class DefNetWork implements NetWork {

	private final Logger logger = LoggerFactory.getLogger(NetWork.class);
	private final Config config;
	private TcpServer tcpServer = null;
	private UdpServer udpServer = null;

	public DefNetWork(Config config) {
		this.config = config;
	}

	@Override
	public void start() throws Exception {

	}

	@Override
	public void stop() {

	}

	@Override
	public void sendMessage(Message request) {

	}

	@Override
	public void onReceiveMessage(Message message) {

	}

	@Override
	public Node getNode() {
		return null;
	}
}
