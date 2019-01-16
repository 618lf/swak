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
	default void download(String url, String name, String tokenName, String token) {
		try {
			if (name == null) {
				name = url.substring(url.lastIndexOf('/') + 1);
			}
			Map<String, String> params = Maps.newHashMap();
			params.put("url", url);
			params.put("name", name);
			params.put("tokenName", tokenName);
			params.put("token", token);
			Display.getEventBus().post(Event.DOWNLOAD.message(params));
		} catch (Exception e) {
		}
	}
}