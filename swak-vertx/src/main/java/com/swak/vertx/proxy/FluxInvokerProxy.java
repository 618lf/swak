package com.swak.vertx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.swak.App;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.exception.InvokeException;
import com.swak.vertx.handler.FluxInvoker;
import com.swak.vertx.transport.VertxProxy;

import io.vertx.core.Future;
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
	private Object $realService; // 如果使用的非异步接口，则直接使用本地调用，会导致当前方法阻塞

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

		/**
		 * 是否本地方法
		 */
		if (this.isLocalMethod(method)) {
			if ("toString".equals(method.getName())) {
				return type.toString();
			}
			throw new InvokeException("can not invoke local method:" + method.getName());
		}

		/**
		 * 非异步方法
		 */
		if (!isAsyncMethod(method)) {
			return method.invoke($realService, args);
		}

		/**
		 * 发起异步调用
		 */
		return this.invoke(vertx, address, method, args);
	}

	/**
	 * 是否返回异步接口，只有异步接口的才能远程调用
	 *
	 * @param method
	 * @return
	 */
	private boolean isAsyncMethod(Method method) {

		/**
		 * 方法元
		 */
		MethodMeta meta = MethodCache.get(method);

		/**
		 * 是否是异步接口
		 */
		if (Future.class.isAssignableFrom(meta.getReturnType())) {
			return true;
		}

		/**
		 * 使用本地调用
		 */
		if ($realService == null) {
			$realService = App.getBean(type);
		}
		return false;
	}

	/**
	 * tostring, equals, hashCode, finalize
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