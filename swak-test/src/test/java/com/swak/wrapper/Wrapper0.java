package com.swak.wrapper;

import com.swak.asm.ClassGenerator;
import com.swak.asm.Wrapper;
import com.swak.exception.NoSuchPropertyException;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * 通过javasist 动态生成的类的实现；
 * 
 * @author lifeng
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class Wrapper0 extends Wrapper implements ClassGenerator.Dc {
	public static String[] pns;

	public static Map pts;

	public static String[] mns;

	public static String[] dmns;

	public static Class[] mts0;

	public static Class[] mts1;

	@Override
    public String[] getPropertyNames() {
		return pns;
	}

	@Override
	public boolean hasProperty(String paramString) {
		return pts.containsKey(paramString);
	}

	@Override
	public Class getPropertyType(String paramString) {
		return (Class) pts.get(paramString);
	}

	@Override
	public String[] getMethodNames() {
		return mns;
	}

	@Override
	public String[] getDeclaredMethodNames() {
		return dmns;
	}

	@Override
	public void setPropertyValue(Object paramObject1, String paramString, Object paramObject2) {
		try {
			OrderService orderService = (OrderService) paramObject1;
		} catch (Throwable throwable) {
			throw new IllegalArgumentException(throwable);
		}
		throw new NoSuchPropertyException("Not found property \"" + paramString
				+ "\" filed or setter method in class com.swak.wrapper.OrderService.");
	}

	@Override
	public Object getPropertyValue(Object paramObject, String paramString) {
		try {
			OrderService orderService = (OrderService) paramObject;
		} catch (Throwable throwable) {
			throw new IllegalArgumentException(throwable);
		}
		throw new NoSuchPropertyException("Not found property \"" + paramString
				+ "\" filed or setter method in class com.swak.wrapper.OrderService.");
	}

	@Override
	public Object invokeMethod(Object paramObject, String paramString, Class[] paramArrayOfClass,
							   Object[] paramArrayOfObject) throws InvocationTargetException {
		OrderService orderService;
		try {
			orderService = (OrderService) paramObject;
		} catch (Throwable throwable) {
			throw new IllegalArgumentException(throwable);
		}
		try {
			if (!"doSomething".equals(paramString) || paramArrayOfClass.length != 0) {
				if (!"doSomething".equals(paramString) || paramArrayOfClass.length != 1
						|| !paramArrayOfClass[0].getName().equals("java.lang.String"))
					throw new NoSuchMethodException(
							"Not found method \"" + paramString + "\" in class com.swak.wrapper.OrderService.");
				return orderService.doSomething((String) paramArrayOfObject[0]);
			}
			return orderService.doSomething();
		} catch (Throwable throwable) {
			throw new InvocationTargetException(throwable);
		}
	}
}
