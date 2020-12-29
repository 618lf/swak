package com.swak.paxos.node;

import com.swak.paxos.config.Config;
import com.swak.paxos.protol.ProposeParam;
import com.swak.paxos.protol.ProposeResult;

/**
 * 定义节点： 一个应用
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:26:07
 */
public interface Node {

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
	ProposeResult propose(ProposeParam propose);

	/**
	 * 停止 Paxos
	 */
	void stopPaxos();
}