//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.swak.wrapper;

import java.lang.reflect.InvocationTargetException;

import com.swak.asm.ClassGenerator.Dc;
import com.swak.asm.Wrapper;

/**
 * 通过 Wrapper 自动生成的代码： 需要注意的点： get，set，is，can，has开头的方式默认认为是属性的获取和填充。
 * 不能使用get，is，can，has开头的方法但返回值是void的方法！
 * 
 * @author lifeng
 * @date 2020年4月2日 上午11:24:58
 */
public class Wrapper0 extends Wrapper implements Dc {
	@Override
	public Object invokeMethod(Object var1, String var2, Class<?>[] var3, Object[] var4)
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

			if ("doSomething".equals(var2) && var3.length == 0) {
				return var5.doSomething();
			}

			if ("doSomething".equals(var2) && var3.length == 1 && var3[0].getName().equals("java.lang.String")) {
				return var5.doSomething((String) var4[0]);
			}

			if ("getVoid".equals(var2) && var3.length == 0) {
				return var5.getVoid();
			}
		} catch (Throwable var9) {
			throw new InvocationTargetException(var9);
		}

		throw new NoSuchMethodException("Not found method \"" + var2 + "\" in class com.swak.wrapper.OrderService.");
	}

	public Wrapper0() {
	}
}
