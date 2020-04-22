package com.swak.method;

import java.util.Collection;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.method.interfaces.OrderService;

public class InterfaceMethodTest {

	public static void main(String[] args) {

		// 方法
		Collection<MethodMeta> metas = MethodCache.set(OrderService.class).getMethods();

		// 打印识别到的所有方法
		System.out.println(metas);
	}
}
