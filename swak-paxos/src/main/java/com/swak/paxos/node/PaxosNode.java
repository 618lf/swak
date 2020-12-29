package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.NodeId;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.PaxosNodeFunctionRet;
import com.swak.paxos.protol.Propoal;
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

	/**
	 * 发送消息
	 */
	@Override
	public ProposeResult propose(Propoal propoal) {

		// 校验分组信息
		if (!groups.check(propoal.getGroup())) {
			logger.error("message groupid {} wrong, groupsize {}.", groupIdx, groupList.size());
			return new ProposeResult(PaxosNodeFunctionRet.Paxos_GroupIdxWrong.getRet(), 0);
		}
		
		// 通过分组发送数据
		groups.getGroup(propoal.getGroup()).getInstance().getCommitter().newValueGetIDNoRetry(propose)
		
		return null;
	}

	@Override
	public void stopPaxos() {

	}
}
