package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;

import com.swak.asm.Wrapper;

public class WrapperDaoTest {

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException {
		OrderDao orderDao = new OrderDao();
		Wrapper wrapper = Wrapper.getWrapper(OrderDao.class);
		wrapper.invokeMethod(orderDao, "insert", new Class[] { Order.class }, new Object[] { new Order() });
	}
}
