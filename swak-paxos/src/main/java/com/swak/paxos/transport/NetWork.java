package com.swak.paxos.transport;

import com.swak.paxos.node.Node;

/**
 * 定义通讯层
 * 
 * @author lifeng
 * @date 2020年12月26日 下午7:25:34
 */
public interface NetWork {

	/**
	 * 开启网络： 启动服务器和客户端
	 * 
	 * @throws Exception
	 */
	void start() throws Exception;

	/**
	 * 停止服务器和客户端连接
	 */
	void stop();

	/**
	 * 发送消息
	 */
	void sendMessage(Request request);

	/**
	 * 处理消息接收的入口
	 * 
	 * @param message
	 */
	void onReceiveMessage(Response message);

	/**
	 * 获得节点
	 * 
	 * @return
	 */
	Node getNode();

}