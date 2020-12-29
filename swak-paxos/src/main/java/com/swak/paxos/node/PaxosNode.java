package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.NodeId;
import com.swak.paxos.config.Config;
import com.swak.paxos.protol.ProposeParam;
import com.swak.paxos.protol.ProposeResult;
import com.swak.paxos.transport.NetWork;

/**
 * 本端节点
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:47:56
 */
public class PaxosNode implements Node {

	private final Logger logger = LoggerFactory.getLogger(PaxosNode.class);

	private NodeId nodeId;
	private NetWork netWork;
	private Groups groups;

	@Override
	public void runPaxos(Config config) throws Exception {

		// 校验参数
		this.checkConfig(config);

		// 开启网络
		this.startNetWork(config);

		// 开启多组支持
		this.initGroups(config);
	}

	private void checkConfig(Config config) throws Exception {

	}

	private void startNetWork(Config config) throws Exception {

	}

	private void initGroups(Config config) throws Exception {

	}

	@Override
	public ProposeResult propose(ProposeParam propose) {
		return null;
	}

	@Override
	public void stopPaxos() {

	}
}
