package com.swak.fx.support;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * 应用 App
 * 
 * @author lifeng
 */
public interface App {

	/**
	 * 下载
	 */
	default void download(String url) {
		this.download(url, null, null);
	}

	/**
	 * 下载
	 */
	default void download(String url, String tokenName, String token) {
		Map<String, String> params = Maps.newHashMap();
		params.put("url", url);
		params.put("tokenName", tokenName);
		params.put("token", token);
		Display.getEventBus().post(Event.DOWNLOAD.message(params));
	}
}