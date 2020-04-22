package com.swak.method;

import java.util.Arrays;
import java.util.Collection;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.method.classes.OrderService;

public class ClassMethodTest {

	public static void main(String[] args) {

		// 方法
		Collection<MethodMeta> metas = MethodCache.set(OrderService.class).getMethods();

		// 打印识别到的所有方法
		for (MethodMeta meta : metas) {
			System.out.println("方法签名：" + meta.getMethodDesc());
			System.out.println("方法所属：" + meta.getMethod().getDeclaringClass());
			System.out.println("方法HASH：" + meta.getMethod().hashCode());
			StringBuilder params = new StringBuilder();
			Arrays.asList(meta.getParameterTypes()).forEach((p) -> {
				params.append(p.getName() + ",");
			});
			System.out.println("方法参数：" + params);
			StringBuilder nestParams = new StringBuilder();
			Arrays.asList(meta.getNestedParameterTypes()).forEach((p) -> {
				nestParams.append(p.getName() + ",");
			});
			System.out.println("内部参数：" + nestParams);
			System.out.println("返回值：" + meta.getReturnType().getName());
			System.out.println("实际返回值：" + meta.getNestedReturnType().getName());
			System.out.println("-------------------");
		}
	}
}
