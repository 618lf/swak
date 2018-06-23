package com.swak.asm;

import java.lang.reflect.Method;
import java.util.UUID;

import com.swak.utils.ReflectUtils;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtField;
import javassist.CtMethod;
import javassist.CtNewMethod;
import javassist.Modifier;

public class MethodInvoker<T> {

	private final Object service;
	private final Method method;
	private final Class<?>[] parameterTypes;
	private Invoker<T> realInvoker;

	public MethodInvoker(Object service, Method method) {
		this.service = service;
		this.method = method;
		this.parameterTypes = method.getParameterTypes();

		try {
			this.realInvoker = generateRealInvoker();
		} catch (Exception e) {
		}
	}

	private Invoker<T> generateRealInvoker() throws Exception {
		final String invokerClassName = "com.tmt.asm.generate.Invoker_"//
				+ UUID.randomUUID().toString().replace("-", "");

		// 创建类
		ClassPool pool = ClassPool.getDefault();
		CtClass invokerCtClass = pool.makeClass(invokerClassName);
		try {

			invokerCtClass.setInterfaces(new CtClass[] { pool.getCtClass(Invoker.class.getName()) });

			// 添加私有成员service
			CtField serviceField = new CtField(pool.get(service.getClass().getName()), "service", invokerCtClass);
			serviceField.setModifiers(Modifier.PRIVATE | Modifier.FINAL);
			invokerCtClass.addField(serviceField);

			// 添加有参的构造函数
			CtConstructor constructor = new CtConstructor(new CtClass[] { pool.get(service.getClass().getName()) },
					invokerCtClass);
			constructor.setBody("{$0.service = $1;}");
			invokerCtClass.addConstructor(constructor);

			StringBuilder methodBuilder = new StringBuilder();
			StringBuilder resultBuilder = new StringBuilder();

			methodBuilder.append("public Object invoke(Object[] params) {\r\n");

			methodBuilder.append("  return ");

			resultBuilder.append("service.");
			resultBuilder.append(method.getName());
			resultBuilder.append("(");

			for (int i = 0; i < parameterTypes.length; i++) {
				Class<?> paramType = parameterTypes[i];

				resultBuilder.append("((");
				resultBuilder.append(ReflectUtils.forceCast(paramType));

				resultBuilder.append(")params[");
				resultBuilder.append(i);
				resultBuilder.append("])");

				resultBuilder.append(ReflectUtils.unbox(paramType));
			}

			resultBuilder.append(")");

			String resultStr = ReflectUtils.box(method.getReturnType(), resultBuilder.toString());

			methodBuilder.append(resultStr);
			methodBuilder.append(";\r\n}");

			CtMethod m = CtNewMethod.make(methodBuilder.toString(), invokerCtClass);
			invokerCtClass.addMethod(m);

			Class<?> invokerClass = invokerCtClass.toClass();

			// 通过反射创建有参的实例
			@SuppressWarnings("unchecked")
			Invoker<T> invoker = (Invoker<T>) invokerClass.getConstructor(service.getClass()).newInstance(service);

			return invoker;
		} finally {
			invokerCtClass.detach();
		}
	}

	/**
	 * 通过生成的类调用
	 */
	public T invoke(Object... params) {
		return this.realInvoker.invoke(params);
	}
}