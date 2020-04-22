package com.swak.method;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;

import org.junit.Test;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.method.classes.OrderService;

public class MethodTest {

	@Test
	public void testDeclaredMethods() {
		ClassMeta classMeta = MethodCache.set(OrderService.class);
		Collection<MethodMeta> methods = classMeta.getMethods();
		for (MethodMeta meta : methods) {
			// 打印结果
			System.out.println(meta.getMethodDesc() + ":是否本地[" + meta.isLocal() + "]:是否同步[" + meta.isAsync() + "]");
		}
	}

	@Test
	public void testSuperMethods() {
		Method[] methods = OrderService.class.getMethods();
		for (Method method : methods) {
			if ("testGeneric".equals(method.getName())) {
				Type mtype = method.getGenericParameterTypes()[0];
				System.out.println(mtype);
			}
		}

	}
}
