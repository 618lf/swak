package com.swak.vertx.protocol.im;

import com.swak.annotation.ImOps;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.impl.ContextInternal;

/**
 * WebSocket 山下文
 * 
 * @author lifeng
 * @date 2020年8月25日 下午2:34:08
 */
public interface ImContext {

	/**
	 * 获得处理类型
	 * 
	 * @return
	 */
	ImOps getOps();

	/**
	 * 下一个处理器
	 */
	void next();

	/**
	 * 获得属性
	 * 
	 * @param <T>
	 * @param key
	 * @return
	 */
	<T> T get(String key);

	/**
	 * 设置属性
	 * 
	 * @param <T>
	 * @param key
	 * @param t
	 * @return
	 */
	<T> ImContext put(String key, T t);

	/**
	 * 创建是所处的 Context
	 * 
	 * @return
	 */
	ContextInternal getContext();

	/**
	 * 是否已经关闭
	 * 
	 * @return
	 */
	boolean closed();

	/**
	 * 请求
	 * 
	 * @return
	 */
	ImRequest request();

	/**
	 * 响应
	 * 
	 * @return
	 */
	ImResponse response();

	/**
	 * 请求
	 * 
	 * @author lifeng
	 * @date 2020年8月26日 上午10:38:36
	 */
	interface ImRequest {

		/**
		 * 获得Body 数据 -- 不一定有
		 * 
		 * @return
		 */
		String getBodyAsString();

		/**
		 * 缓存
		 * 
		 * @return
		 */
		Buffer getBody();

		/**
		 * 获得参数
		 * 
		 * @param param
		 * @return
		 */
		String getParam(String param);

		/**
		 * 所有参数
		 * 
		 * @return
		 */
		MultiMap params();

		/**
		 * 所有头部信息
		 * 
		 * @return
		 */
		MultiMap headers();

		/**
		 * 指定头部
		 * 
		 * @param header
		 * @return
		 */
		String getHeader(String header);

		/**
		 * 处理的地址
		 * 
		 * @return
		 */
		String uri();

		/**
		 * 远端地址
		 * 
		 * @return
		 */
		String remoteAddress();
		
		/**
		 * 本地地址
		 * 
		 * @return
		 */
		String localAddress();
	}

	/**
	 * 响应
	 * 
	 * @author lifeng
	 * @date 2020年8月26日 上午10:38:11
	 */
	interface ImResponse {

		/**
		 * 设置 Header
		 * 
		 * @param name
		 * @param value
		 */
		ImResponse putHeader(CharSequence name, CharSequence value);

		/**
		 * 结束
		 * 
		 * @param chunk
		 */
		ImResponse out(String chunk);

		/**
		 * 结束
		 * 
		 * @param chunk
		 */
		ImResponse out(Buffer buffer);

		/**
		 * 输出文件
		 * 
		 * @param filename
		 * @param resultHandler
		 * @return
		 */
		ImResponse sendFile(String filename, Handler<AsyncResult<Void>> resultHandler);
	}
}
