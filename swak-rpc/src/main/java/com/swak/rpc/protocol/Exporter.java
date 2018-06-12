package com.swak.rpc.protocol;

import com.swak.rpc.api.URL;
import com.swak.rpc.invoker.Invoker;

/**
 * 对外暴露的服务
 * 
 * @author lifeng
 *
 * @param <T>
 */
public interface Exporter<T> {

	/**
	 * 服务地址
	 * 
	 * @return
	 */
	URL getURL();

	/**
	 * 服务执行类
	 * 
	 * @return
	 */
	Invoker<T> getInvoker();
}