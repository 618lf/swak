package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.Wrapper;

public class WrapperDaoTest {

	public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException {
		ClassMeta classMeta = MethodCache.set(OrderDao.class);
		classMeta.getMethods().forEach(meta -> {
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
		});

		OrderDao orderDao = new OrderDao();
		Wrapper wrapper = Wrapper.getWrapper(OrderDao.class);
		wrapper.invokeMethod(orderDao, "insert", new Object[] { new Order() });
	}
}
