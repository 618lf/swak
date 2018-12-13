package com.tmt.shop.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.entity.Result;
import com.swak.reactivex.web.annotation.Async;
import com.swak.reactivex.web.annotation.PathVariable;
import com.swak.reactivex.web.annotation.RequestMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.entity.Order;
import com.tmt.shop.service.OrderService;

/**
 * 需要用户才能访问
 * 
 * @author lifeng
 */
@RestController("/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@Async
	@RequestMapping("save")
	public Result save(Order order) {
		orderService.save(order);
		return Result.success();
	}

	@Async
	@RequestMapping("get/{id}")
	public Result save(@PathVariable Long id) {
		return Result.success(orderService.get(id));
	}
}
