package com.swak.reactivex.transport;

/**
 * 还有一些属性每配置，在研究
 * @author lifeng
 */
public class ReactiveWebServerFactory {

	private HttpServerProperties properties;
	
	public ReactiveWebServerFactory(HttpServerProperties properties) {
		this.properties = properties;
	}
	
	/**
	 * 获得webServer
	 * @param handler
	 * @return
	 */
	public WebServer getWebServer() {
		return ReactiveWebServer.build(properties);
	}
}