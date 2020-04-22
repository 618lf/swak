package com.swak.method;

import java.util.concurrent.CompletableFuture;

/**
 * 测试的订单服务
 * 
 * @author lifeng
 * @date 2020年4月2日 下午4:09:23
 */
public interface OrderService extends BaseService<Order> {

	/**
	 * 同步获取
	 * 
	 * @return Order
	 */
	Order get();

	/**
	 * 异步获取
	 * 
	 * @return CompletableFuture<Order>
	 */
	CompletableFuture<Order> getAsync();
}