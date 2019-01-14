package com.swak.fx.support;

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
		Display.getEventBus().post(Event.DOWNLOAD.message(url));
	}
}