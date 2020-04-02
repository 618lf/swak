//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import com.swak.asm.ClassGenerator.Dc;
import com.swak.asm.Wrapper;
import com.swak.exception.NoSuchPropertyException;

/**
 * 通过 Wrapper 自动生成的代码： 需要注意的点： get，set，is，can，has开头的方式默认认为是属性的获取和填充。
 * 不能使用get，is，can，has开头的方法但返回值是void的方法！
 * 
 * @author lifeng
 * @date 2020年4月2日 上午11:24:58
 */
@SuppressWarnings({ "unused", "rawtypes", "unchecked" })
public class Wrapper0 extends Wrapper implements Dc {
	public static String[] pns;
	public static Map pts;
	public static String[] mns;
	public static String[] dmns;
	public static Class[] mts0;
	public static Class[] mts1;
	public static Class[] mts2;
	public static Class[] mts3;

	public String[] getPropertyNames() {
		return pns;
	}

	public boolean hasProperty(String var1) {
		return pts.containsKey(var1);
	}

	public Class getPropertyType(String var1) {
		return (Class) pts.get(var1);
	}

	public String[] getMethodNames() {
		return mns;
	}

	public String[] getDeclaredMethodNames() {
		return dmns;
	}

	public void setPropertyValue(Object var1, String var2, Object var3) {
		try {
			OrderService var4 = (OrderService) var1;
		} catch (Throwable var6) {
			throw new IllegalArgumentException(var6);
		}

		throw new NoSuchPropertyException(
				"Not found property \"" + var2 + "\" filed or setter method in class com.swak.wrapper.OrderService.");
	}

	public Object getPropertyValue(Object var1, String var2) {
		OrderService var3;
		try {
			var3 = (OrderService) var1;
		} catch (Throwable var5) {
			throw new IllegalArgumentException(var5);
		}

		if (var2.equals("string")) {
			return var3.getString();
		} else if (var2.equals("void")) {
			return var3.getVoid();
		} else {
			throw new NoSuchPropertyException("Not found property \"" + var2
					+ "\" filed or setter method in class com.swak.wrapper.OrderService.");
		}
	}

	public Object invokeMethod(Object var1, String var2, Class[] var3, Object[] var4)
			throws InvocationTargetException, NoSuchMethodException {
		OrderService var5;
		try {
			var5 = (OrderService) var1;
		} catch (Throwable var8) {
			throw new IllegalArgumentException(var8);
		}

		try {
			if ("getString".equals(var2) && var3.length == 0) {
				return var5.getString();
			}

			if ("getVoid".equals(var2) && var3.length == 0) {
				return var5.getVoid();
			}

			if ("doSomething".equals(var2) && var3.length == 0) {
				return var5.doSomething();
			}

			if ("doSomething".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.String")) {
				return var5.doSomething((String) var4[0]);
			}
		} catch (Throwable var9) {
			throw new InvocationTargetException(var9);
		}

		throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.swak.wrapper.OrderService.");
	}

	public Wrapper0() {
	}
}
