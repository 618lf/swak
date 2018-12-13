package com.tmt.shop.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.swak.persistence.BaseDao;
import com.swak.service.BaseService;
import com.tmt.shop.dao.OrderDao;
import com.tmt.shop.entity.Order;

@Service
public class OrderService extends BaseService<Order, Long> {

	@Autowired
	private OrderDao orderDao;

	@Override
	protected BaseDao<Order, Long> getBaseDao() {
		return orderDao;
	}

	/**
	 * 存储订单
	 * 
	 * @param order
	 */
	public void save(Order order) {
		this.insert(order);
	}
}
