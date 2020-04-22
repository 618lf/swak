package com.swak.vertx.handler;

import java.lang.reflect.Method;

import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.entity.Model;
import com.swak.entity.Result;
import com.swak.vertx.transport.VertxProxy;
import com.swak.vertx.transport.multipart.MultipartFile;
import com.swak.vertx.transport.multipart.PlainFile;

/**
 * InvokerHandler 和 MethodHandler 的结合
 *
 * @author: lifeng
 * @date: 2020/3/29 19:45
 */
public class FluxMethodInvoker extends MethodInvoker implements FluxInvoker {

	private final VertxProxy vertx;
	private final String address;
	private final Method method;
	private final ClassMeta classMeta;

	public FluxMethodInvoker(VertxProxy vertx, Object bean, Method method) {
		super(bean, method);
		this.method = method;
		this.vertx = vertx;
		this.address = this.getAddress(bean);
		this.classMeta = MethodCache.set(bean.getClass());
	}

	/**
	 * 初始化地址
	 */
	private String getAddress(Object bean) {

		// 优先使用接口，然后使用类
		Class<?> type = bean.getClass();
		Class<?>[] interfacesClasses = bean.getClass().getInterfaces();
		if (interfacesClasses != null && interfacesClasses.length > 0) {
			type = interfacesClasses[0];
		}

		// 返回接口地址
		return this.getAddress(type);
	}

	/**
	 * 通过消息发送给 服务
	 */
	@Override
	public Object doInvoke(Object[] args) {
		return this.invoke(vertx, address, classMeta.lookup(method), args).thenApply(this::wrapResult);
	}

	/**
	 * 包括固定的结构
	 */
	private Object wrapResult(Object result) {
		if (result == null || isJson(result.getClass())) {
			return Result.success(result);
		}
		return result;
	}

	/**
	 * 是否是Json 输出
	 */
	private boolean isJson(Class<?> type) {
		return !(type == Result.class || type == Model.class || type == PlainFile.class || type == MultipartFile.class
				|| type == String.class);
	}
}