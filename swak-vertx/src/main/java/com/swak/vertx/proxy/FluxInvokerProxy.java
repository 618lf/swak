package com.swak.vertx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.swak.asm.MethodCache;
import com.swak.exception.InvokeException;
import com.swak.vertx.handler.FluxInvoker;
import com.swak.vertx.transport.VertxProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 调用执行器 -- 适用JDK 的动态代理，也适用CGlib 的子类代理
 * 
 * @author lifeng
 */
public class FluxInvokerProxy implements InvocationHandler, MethodInterceptor, FluxInvoker {

	private final VertxProxy vertx;
	private final Class<?> type;
	private final String address;

	/**
	 * 创建反应式执行的代理
	 * 
	 * @param vertx
	 * @param type
	 */
	public FluxInvokerProxy(VertxProxy vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
		this.address = this.getAddress(type);
		this.initMethods();
	}

	/**
	 * 缓存方法
	 */
	private void initMethods() {
		Method[] methods = type.getMethods();
		for (Method method : methods) {
			MethodCache.set(method);
		}
	}

	/**
	 * JDK 动态代理走此分支
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return this.filter(method, args);
	}

	/**
	 * CGLIB 代理走此分支
	 */
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return this.filter(method, args);
	}

	/**
	 * 本地的方法不进行远程调用
	 * 
	 * @param method
	 * @param args
	 * @return
	 * @throws Throwable
	 */
	private Object filter(Method method, Object[] args) throws Throwable {
		if (this.isLocalMethod(method)) {
			if ("toString".equals(method.getName())) {
				return type.toString();
			}
			throw new InvokeException("can not invoke local method:" + method.getName());
		}
		return this.invoke(vertx, address, method, args);
	}

	/**
	 * tostring,equals,hashCode,finalize
	 *
	 * @param method
	 * @return
	 */
	private boolean isLocalMethod(Method method) {
		if (method.getDeclaringClass().equals(Object.class)) {
			return true;
		}
		return false;
	}
}