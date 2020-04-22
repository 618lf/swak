package com.swak.method.interfaces;

import java.util.concurrent.CompletableFuture;

import com.swak.method.entity.Order;

/**
 * 测试的订单服务
 * 
 * @author lifeng
 * @date 2020年4月2日 下午4:09:23
 */
public interface OrderService extends BaseService<Order, Long>, NoGenericsService {

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