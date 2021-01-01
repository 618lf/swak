package com.swak.paxos.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.swak.paxos.common.NodeId;
import com.swak.paxos.config.Config;
import com.swak.paxos.enums.ResultCode;
import com.swak.paxos.event.Event;
import com.swak.paxos.protol.Propoal;
import com.swak.paxos.protol.ProposePromise;
import com.swak.paxos.transport.NetWork;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 本端节点
 * 
 * @author lifeng
 * @date 2020年12月28日 上午10:47:56
 */
@Getter
@Setter
@Accessors(chain = true)
public class PaxosNode implements Node {

	private final Logger logger = LoggerFactory.getLogger(PaxosNode.class);

	private NodeId nodeId;
	private NetWork netWork;
	private Groups groups;
	private Config config;
	private Master master;

	@Override
	public NodeId id() {
		return nodeId;
	}

	@Override
	public boolean isMaster() {
		return this.master.id().equals(this.id());
	}

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
	public ProposePromise commit(Propoal propoal) {

		// 校验分组信息
		if (!groups.check(propoal.getGroup())) {
			return new ProposePromise().setRet(ResultCode.GROUP_INDEX_ERROR);
		}

		// 通过分组发送数据
		return groups.getGroup(propoal.getGroup()).getInstance().commit(propoal);
	}

	@Override
	public void stopPaxos() {

	}
}
