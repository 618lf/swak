package com.swak.flux.web.statics;

import java.io.IOException;

import org.springframework.core.Ordered;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.web.Handler;
import com.swak.flux.web.HandlerMapping;

/**
 * 静态资源的处理
 * 
 * 约定处理如下路径下的资源（这些资源在jar 包中）：
 * 
 * /static、/files、/META-INF/resources/ 会查找这样几个目录，暂时不能自定义
 * 
 * 或者可以自定义路径，支持相对路径（这些资源在jar 包外）：
 * 
 * 应该放在最后，可以查找所有的请求
 * 
 * @author lifeng
 */
public class StaticHandlerMapping implements HandlerMapping, Ordered {

	// 默认支持的静态资源
	private StaticHandler staticHandler;

	/**
	 * 设置处理器
	 */
	public void setStaticHandler(StaticHandler staticHandler) {
		this.staticHandler = staticHandler;
	}

	/**
	 * 处理静态资源
	 */
	@Override
	public Handler getHandler(HttpServerRequest request) {
		return staticHandler;
	}

	/**
	 * 清空资源
	 */
	@Override
	public void close() throws IOException {
		staticHandler.close();
	}

	/**
	 * 处理的顺序, 最后处理
	 */
	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}
}