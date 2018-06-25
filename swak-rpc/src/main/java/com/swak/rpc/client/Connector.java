package com.swak.rpc.client;

import com.swak.rpc.api.RpcRequest;

/**
 * 一个服务器的连接
 * @author lifeng
 */
public interface Connector {

	/**
	 * 同步的方式连接
	 */
	void connect();
	
	/**
	 * 断开连接
	 */
	void disConnect();
	
	/**
	 * 发送数据
	 * @param t
	 */
	void sent(RpcRequest request);
}