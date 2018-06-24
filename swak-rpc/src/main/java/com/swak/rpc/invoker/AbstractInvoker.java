package com.swak.rpc.invoker;

import java.util.Map;

import com.swak.rpc.api.Constants;
import com.swak.rpc.api.URL;
import com.swak.utils.Maps;

/**
 * 设置 URL 的 一般格式
 * @author lifeng
 * @param <T>
 */
public abstract class AbstractInvoker<T> implements Invoker<T>{
	
	private final Invocation invocation;
	public AbstractInvoker(Invocation invocation) {
		this.invocation = invocation;
	}

	/**
	 * 地址
	 */
	@Override
	public URL getURL() {
		Map<String, String> parameters  = Maps.newHashMap();
		parameters.put(Constants.VERSION_KEY, invocation.getVersion());
		parameters.put(Constants.GROUP_KEY, invocation.getGroup());
		return new URL(null, null, 0, invocation.getServiceType().getName(), parameters);
	}
}
