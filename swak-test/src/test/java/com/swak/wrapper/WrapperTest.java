package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;

import com.swak.asm.Wrapper;

public class WrapperTest {

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException {
		OrderService orderService = new OrderService();
		Wrapper wrapper = Wrapper.getWrapper(OrderService.class);
		wrapper.invokeMethod(orderService, "doSomething", new Object[] { "123" });
	}
}
