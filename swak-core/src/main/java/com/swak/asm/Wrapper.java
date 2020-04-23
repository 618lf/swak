/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.swak.asm;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.swak.asm.MethodCache.MethodMeta;
import com.swak.utils.ClassHelper;
import com.swak.utils.ReflectUtils;

/**
 * Wrapper.
 */
public abstract class Wrapper {

	private static AtomicLong WRAPPER_CLASS_COUNTER = new AtomicLong(0);
	private static final Map<Class<?>, Wrapper> WRAPPER_MAP = new ConcurrentHashMap<Class<?>, Wrapper>(); // class
	private static final Wrapper OBJECT_WRAPPER = new Wrapper() {

		@Override
		public Object invokeMethod(Object instance, String mn, Object[] args) throws NoSuchMethodException {
			if ("getClass".equals(mn)) {
				return instance.getClass();
			}
			if ("hashCode".equals(mn)) {
				return instance.hashCode();
			}
			if ("toString".equals(mn)) {
				return instance.toString();
			}
			if ("equals".equals(mn)) {
				if (args.length == 1) {
					return instance.equals(args[0]);
				}
				throw new IllegalArgumentException("Invoke method [" + mn + "] argument number error.");
			}
			throw new NoSuchMethodException("Method [" + mn + "] not found.");
		}
	};

	/**
	 * Get wrapper.
	 * 
	 * @param c 类
	 * @return 类执行代理
	 */
	public static Wrapper getWrapper(Class<?> c) {

		while (ClassGenerator.isDynamicClass(c)) {
			c = c.getSuperclass();
		}

		if (c == Object.class) {
			return OBJECT_WRAPPER;
		}

		return WRAPPER_MAP.computeIfAbsent(c, key -> makeWrapper(key));
	}

	/**
	 * 创建代理类
	 * 
	 * @param c 类
	 * @return 类执行代理
	 */
	private static Wrapper makeWrapper(Class<?> c) {
		if (c.isPrimitive()) {
			throw new IllegalArgumentException("Can not create wrapper for primitive type: " + c);
		}

		String name = c.getName();
		ClassLoader cl = ClassHelper.getClassLoader(c);

		StringBuilder c3 = new StringBuilder("public Object invokeMethod(Object o, String n, Object[] v) throws "
				+ InvocationTargetException.class.getName() + "," + NoSuchMethodException.class.getName() + "{ ");

		c3.append(name).append(" w; try{ w = ((").append(name)
				.append(")$1); }catch(Throwable e){ throw new IllegalArgumentException(e); }");

		// build all public method meta
		Collection<MethodMeta> methods = MethodCache.set(c).getMethods();

		// get all public method.
		if (!methods.isEmpty()) {
			c3.append(" try{");

			for (MethodMeta m : methods) {

				// ignore Object's method.
				if (m.getMethod().getDeclaringClass() == Object.class) {
					continue;
				}

				String md = m.getMethodDesc();
				String mn = m.getMethodName();
				c3.append(" if( \"").append(md).append("\".equals( $2 ) ) { ");

				if (m.getReturnType() == Void.TYPE) {
					c3.append(" w.").append(mn).append('(').append(args(m.getParameterTypes(), "$3")).append(");")
							.append(" return null;");
				} else {
					c3.append(" return ($w)w.").append(mn).append('(').append(args(m.getParameterTypes(), "$3"))
							.append(");");
				}

				c3.append(" }");
			}
			c3.append(" } catch(Throwable e) { ");
			c3.append("     throw new java.lang.reflect.InvocationTargetException(e); ");
			c3.append(" }");
		}

		c3.append(" throw new " + NoSuchMethodException.class.getName()
				+ "(\"Not found method \\\"\"+$2+\"\\\" in class " + c.getName() + ".\"); }");

		// make class
		long id = WRAPPER_CLASS_COUNTER.getAndIncrement();
		ClassGenerator cc = ClassGenerator.newInstance(cl);
		cc.setClassName((Modifier.isPublic(c.getModifiers()) ? Wrapper.class.getName() : c.getName() + "$sw") + id);
		cc.setSuperClass(Wrapper.class);
		cc.addDefaultConstructor();
		cc.addMethod(c3.toString());

		try {
			Class<?> wc = cc.toClass();
			return (Wrapper) wc.newInstance();
		} catch (RuntimeException e) {
			throw e;
		} catch (Throwable e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			cc.release();
		}
	}

	private static String args(Class<?>[] cs, String name) {
		int len = cs.length;
		if (len == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			if (i > 0) {
				sb.append(',');
			}
			sb.append(arg(cs[i], name + "[" + i + "]"));
		}
		return sb.toString();
	}

	private static String arg(Class<?> cl, String name) {
		if (cl.isPrimitive()) {
			if (cl == Boolean.TYPE) {
				return "((Boolean)" + name + ").booleanValue()";
			}
			if (cl == Byte.TYPE) {
				return "((Byte)" + name + ").byteValue()";
			}
			if (cl == Character.TYPE) {
				return "((Character)" + name + ").charValue()";
			}
			if (cl == Double.TYPE) {
				return "((Number)" + name + ").doubleValue()";
			}
			if (cl == Float.TYPE) {
				return "((Number)" + name + ").floatValue()";
			}
			if (cl == Integer.TYPE) {
				return "((Number)" + name + ").intValue()";
			}
			if (cl == Long.TYPE) {
				return "((Number)" + name + ").longValue()";
			}
			if (cl == Short.TYPE) {
				return "((Number)" + name + ").shortValue()";
			}
			throw new RuntimeException("Unknown primitive type: " + cl.getName());
		}
		return "(" + ReflectUtils.getName(cl) + ")" + name;
	}

	/**
	 * 执行方法
	 *
	 * @param instance 实例.
	 * @param mn       方法名称.
	 * @param types    方法类型
	 * @param args     方法参数.
	 * @return 执行结果.
	 */
	public abstract Object invokeMethod(Object instance, String mn, Object[] args)
			throws NoSuchMethodException, InvocationTargetException;
}
