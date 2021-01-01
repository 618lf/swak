package com.swak.paxos.node;

import com.swak.paxos.common.NodeId;
import com.swak.paxos.config.Config;
import com.swak.paxos.protol.Propoal;
import com.swak.paxos.protol.ProposePromise;
import com.swak.paxos.transport.NetWork;

/**
 * Leader 节点信息
 * 
 * @author DELL
 */
public class Master implements Node {

	private NodeId nodeId;
	private NetWork netWork;
	private PaxosNode paxosNode;

	public Master(PaxosNode paxosNode) {
		this.paxosNode = paxosNode;
	}

	@Override
	public NodeId id() {
		return nodeId;
	}

	@Override
	public boolean isMaster() {
		return this.paxosNode.id().equals(this.id());
	}

	@Override
	public void runPaxos(Config config) throws Exception {

	}

	/**
	 * 向 master 提交数据
	 * 
	 * @param promise
	 * @return
	 */
	@Override
	public ProposePromise commit(Propoal propoal) {
		// 如果是leader,需要把当前事务处理完成了才处理新提交的事务
		if (this.isMaster()) {
			paxosNode.getGroups().getGroup(propoal.getGroup()).getInstance().startNewRound();
			return null;
		}

		// 如果不是leader 则可以通过网络将数据传递给leader
		// 指定节点发送数据 -- 后面写
		this.netWork.sendMessage(null);
		return null;
	}

	@Override
	public void stopPaxos() {

	}
}
