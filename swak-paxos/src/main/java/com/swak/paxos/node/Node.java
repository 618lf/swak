package com.swak.paxos.node;

import com.swak.paxos.common.NodeId;
import com.swak.paxos.config.Config;
import com.swak.paxos.protol.Proposal;
import com.swak.paxos.protol.ProposePromise;

/**
 * 定义节点： 一个应用
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:26:07
 */
public interface Node {

	/**
	 * node节点信息
	 * 
	 * @return
	 */
	NodeId id();
	
	/**
	 * 是否是leader 节点
	 * 
	 * @return
	 */
	boolean isMaster();

	/**
	 * 开启节点
	 * 
	 * @param config
	 * @return
	 * @throws Exception
	 */
	void runPaxos(Config config) throws Exception;

	/**
	 * 发起一个提案
	 * 
	 * @param propose
	 * @return
	 */
	ProposePromise commit(Proposal proposal);

	/**
	 * 停止 Paxos
	 */
	void stopPaxos();
}