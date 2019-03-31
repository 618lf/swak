package com.tmt.shop.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.entity.Result;
import com.swak.flux.web.annotation.PathVariable;
import com.swak.flux.web.annotation.RequestMapping;
import com.swak.flux.web.annotation.RestController;
import com.tmt.shop.entity.Order;
import com.tmt.shop.service.OrderService;

/**
 * 需要用户才能访问
 * 
 * @author lifeng
 */
@RestController(path = "/api/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	@RequestMapping("save")
	public Result save(Order order) {
		orderService.save(order);
		return Result.success();
	}

	@RequestMapping("get/{id}")
	public Result save(@PathVariable Long id) {
		return Result.success(orderService.get(id));
	}
}