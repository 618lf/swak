package com.swak.method.classess;

import java.util.List;
import java.util.Map;

import com.swak.method.entity.Fix;
import com.swak.method.entity.Order;

public class OrderService extends BaseService<Order, Long, List<Order>, Fix, List<Fix>> {

	@Override
	public List<Order> testGeneric(Order entity, Map<String, Object> params, Long id) {
		return null;
	}
}
