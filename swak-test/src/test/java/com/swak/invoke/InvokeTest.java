package com.swak.invoke;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;

/**
 * Invoke 测试
 * 
 * @author lifeng
 * @date 2020年7月20日 下午8:24:44
 */
public class InvokeTest {

	public static void main(String[] args) throws Throwable {
		/**
		 * Exception in thread "main" java.lang.invoke.WrongMethodTypeException: cannot
		 * convert MethodHandle(String,char,char)String to (String,Void,char)String at
		 * java.lang.invoke.MethodHandle.asTypeUncached(MethodHandle.java:775) at
		 * java.lang.invoke.MethodHandle.asType(MethodHandle.java:761) at
		 * java.lang.invoke.Invokers.checkGenericType(Invokers.java:321) at
		 * com.swak.invoke.InvokeTest.main(InvokeTest.java:20)
		 * 
		 */
		MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
		MethodType mt = MethodType.methodType(String.class, char.class, char.class);
		MethodHandle replaceMH = publicLookup.findVirtual(String.class, "replace", mt);
		String output = (String) replaceMH.invoke("1", null, 'a');
		System.out.println(output);

		/**
		 * Exception in thread "main" java.lang.IllegalArgumentException at
		 * sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method) at
		 * sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
		 * at
		 * sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
		 * at java.lang.reflect.Method.invoke(Method.java:498) at
		 * com.swak.invoke.InvokeTest.main(InvokeTest.java:33)
		 */
		Method method = String.class.getMethod("replace", char.class, char.class);
		method.invoke("1", null, 'a');
	}
}
