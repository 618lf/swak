package com.swak.paxos.transport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.config.Config;
import com.swak.paxos.node.Node;
import com.swak.paxos.transport.service.CoordinateService;
import com.swak.paxos.transport.service.EntrustService;

/**
 * 默认的网络
 * 
 * @author DELL
 */
public class DefNetWork implements NetWork {

	private final Logger logger = LoggerFactory.getLogger(NetWork.class);
	private final Config config;

	/**
	 * 协调服务
	 */
	private final CoordinateService coordinateService;

	/**
	 * 委托服务
	 */
	private final EntrustService entrustService;

	public DefNetWork(Config config) {
		this.config = config;
		this.coordinateService = new CoordinateService(config);
		this.entrustService = new EntrustService(config);
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
