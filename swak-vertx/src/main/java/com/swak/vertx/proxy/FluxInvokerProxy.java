package com.swak.vertx.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.swak.App;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.exception.InvokeException;
import com.swak.vertx.invoker.FluxInvoker;
import com.swak.vertx.transport.VertxProxy;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 调用执行器 -- 适用JDK 的动态代理，也适用CGlib 的子类代理
 *
 * @author: lifeng
 * @date: 2020/3/29 20:28
 */
public class FluxInvokerProxy implements InvocationHandler, MethodInterceptor, FluxInvoker {

	private final VertxProxy vertx;
	private final Class<?> type;
	private final String address;
	private final ClassMeta classMeta;

	/**
	 * 如果使用的非异步接口，则直接使用本地调用，会导致当前方法阻塞
	 */
	private Object realService;

	/**
	 * 创建反应式执行的代理
	 *
	 * @param vertx vertx代理
	 * @param type  类型
	 */
	public FluxInvokerProxy(VertxProxy vertx, Class<?> type) {
		this.vertx = vertx;
		this.type = type;
		this.address = this.getAddress(type);
		this.classMeta = MethodCache.set(this.type);
	}

	/**
	 * JDK 动态代理走此分支 -- 直接发起异步调用
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		return this.filter(method, args, null);
	}

	/**
	 * CGLIB 代理走此分支
	 */
	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		return this.filter(method, args, proxy);
	}

	/**
	 * 本地的方法不进行远程调用
	 */
	private Object filter(Method method, Object[] args, MethodProxy proxy) throws Throwable {

		// 方法元
		MethodMeta meta = classMeta.lookup(method);

		// 是否本地方法
		if (meta == null || meta.isLocal()) {
			String toString = "toString";
			if (toString.equals(method.getName())) {
				return type.toString();
			}
			throw new InvokeException("can not invoke local method:" + method.getName());
		}

		// 非异步方法， 获取实现类来执行
		if (!meta.isAsync()) {
			if (realService == null) {
				realService = App.getBean(type);
			}
			return proxy == null ? method.invoke(realService, args) : proxy.invoke(realService, args);
		}

		// 发起异步调用
		return this.invoke(vertx, address, meta, args);
	}
}