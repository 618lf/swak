package com.swak.flux.verticle;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.swak.Constants;
import com.swak.asm.MethodCache;
import com.swak.asm.MethodCache.ClassMeta;
import com.swak.asm.MethodCache.MethodMeta;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.StringUtils;

/**
 * 调用执行器
 * 
 * @author lifeng
 */
public class InvokerHandler implements InvocationHandler {
	private final Class<?> type;
	private final String address;
	private final Flux flux;
	private final ClassMeta classMeta;

	public InvokerHandler(Flux flux, Class<?> type) {
		this.flux = flux;
		this.type = type;
		this.address = this.initAddress();
		this.classMeta = MethodCache.set(type);
	}

	private String initAddress() {

		// 访问的地址
		String address = StringUtils.EMPTY;

		// 默认使用接口的全额限定名称
		if (StringUtils.isBlank(address)) {
			address = type.getName();
		}

		// 约定去掉后面的 Asyncx
		return StringUtils.substringBeforeLast(address, Constants.ASYNC_SUFFIX);
	}

	/**
	 * 只支持异步接口的调用
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		MethodMeta meta = classMeta.lookup(method);

		// 构建请求消息
		Msg request = new Msg(meta, args);

		// 发送消息，处理相应结果
		return flux.sendMessage(this.address, request).thenApply(res -> {
			// 约定的通讯协议
			Msg result = (Msg) res;

			// 错误处理 - 结果返回
			String result_error = result.getError();
			Object result_result = result.getResult();

			// 自动生成异步接口返回值
			if (meta.getNestedReturnType() == Msg.class) {
				result_result = result;
			}

			// 优先错误处理
			if (StringUtils.isNotBlank(result_error)) {
				throw new BaseRuntimeException(result_error);
			}
			return result_result;
		});
	}
}
