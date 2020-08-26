package com.swak.vertx.security;

import com.swak.vertx.protocol.im.ImContext;

import io.vertx.ext.web.RoutingContext;

/**
 * 请求的上下文，屏蔽 RoutingContext 和 ImContext 等需要安全处理的请求
 * 
 * 
 * @author lifeng
 * @date 2020年8月26日 下午11:45:54
 */
public interface Context {

	/**
	 * 进一步处理
	 * 
	 * @return
	 */
	void next();

	/**
	 * 请求地址
	 * 
	 * @return
	 */
	String uri();

	/**
	 * 设置属性
	 * 
	 * @param <T>
	 * @param name
	 * @param value
	 * @return
	 */
	<T> Context put(String name, T value);

	/**
	 * 获得属性
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	<T> T get(String key);

	/**
	 * 设置头部
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	Context header(CharSequence name, CharSequence value);

	/**
	 * 获取头部
	 * 
	 * @param name
	 * @return
	 */
	String header(String name);

	/**
	 * 返回Cookie
	 * 
	 * @param <T>
	 * @param name
	 * @return
	 */
	<T> T cookie(String name);

	/**
	 * 输出数据
	 * 
	 * @param value
	 * @return
	 */
	Context end(String value);

	class SecurityImContext implements Context {

		ImContext context;

		SecurityImContext(ImContext context) {
			this.context = context;
		}

		@Override
		public void next() {
			context.next();
		}

		@Override
		public String uri() {
			return context.request().uri();
		}

		@Override
		public <T> Context put(String name, T value) {
			context.put(name, value);
			return this;
		}

		@Override
		public <T> T get(String key) {
			return context.get(key);
		}

		@Override
		public Context header(CharSequence name, CharSequence value) {
			context.response().putHeader(name, value);
			return this;
		}

		@Override
		public String header(String name) {
			return context.request().getHeader(name);
		}

		@Override
		public <T> T cookie(String name) {
			return null;
		}

		@Override
		public Context end(String value) {
			context.response().out(value);
			return this;
		}
	}

	class SecurityRoutingContext implements Context {

		RoutingContext context;

		SecurityRoutingContext(RoutingContext context) {
			this.context = context;
		}

		@Override
		public void next() {
			context.next();
		}

		@Override
		public String uri() {
			return context.request().uri();
		}

		@Override
		public <T> Context put(String name, T value) {
			context.put(name, value);
			return this;
		}

		@Override
		public <T> T get(String key) {
			return context.get(key);
		}

		@Override
		public Context header(CharSequence name, CharSequence value) {
			context.response().putHeader(name, value);
			return this;
		}

		@Override
		public String header(String name) {
			return context.request().getHeader(name);
		}

		@Override
		@SuppressWarnings("unchecked")
		public <T> T cookie(String name) {
			return (T) context.request().getCookie(name);
		}

		@Override
		public Context end(String value) {
			context.response().end(value);
			return this;
		}
	}

	/**
	 * 创建 ImContext 的上下文
	 * 
	 * @param context
	 * @return
	 */
	static Context of(ImContext context) {
		return new SecurityImContext(context);
	}

	/**
	 * 创建 RoutingContext 的上下文
	 * 
	 * @param context
	 * @return
	 */
	static Context of(RoutingContext context) {
		return new SecurityRoutingContext(context);
	}

	/**
	 * 创建 上下文
	 * 
	 * @param context
	 * @return
	 */
	static Context of(Object context) {
		Context innerContext = null;
		if (context instanceof Context) {
			innerContext = (Context) context;
		} else if (context instanceof RoutingContext) {
			innerContext = Context.of((RoutingContext) context);
		} else if (context instanceof ImContext) {
			innerContext = Context.of((ImContext) context);
		}
		return innerContext;
	}
}
