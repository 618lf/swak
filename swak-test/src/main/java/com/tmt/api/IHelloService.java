package com.tmt.api;

import com.swak.rpc.annotation.RpcService;

/**
 * 测试的服务
 * @author lifeng
 *
 */
@RpcService
public interface IHelloService {

	/**
	 * 测试的服务
	 */
	default void sayHello() {
		System.out.println("hello lifeng!");
	}
}