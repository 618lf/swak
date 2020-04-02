package com.swak.method;

import java.lang.reflect.Method;

import org.junit.Test;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;

public class MethodTest {

	@Test
	public void test() {
		Method[] methods = OrderService.class.getMethods();
		for (Method method : methods) {

			// 缓存method
			MethodCache.set(method);

			// 获取元数据
			MethodMeta meta = MethodCache.get(method);

			// 打印结果
			System.out.println(meta.getMethodDesc() + ":是否本地[" + meta.isLocal() + "]:是否同步[" + meta.isAsync() + "]");
		}
	}
}
