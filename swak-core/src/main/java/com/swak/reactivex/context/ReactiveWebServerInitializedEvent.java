package com.swak.reactivex.context;

public class ReactiveWebServerInitializedEvent extends WebServerInitializedEvent {

	private static final long serialVersionUID = 1L;
	
	private final ReactiveWebServerApplicationContext applicationContext;

	public ReactiveWebServerInitializedEvent(WebServer webServer,
			ReactiveWebServerApplicationContext reactiveWebServerApplicationContext) {
		super(webServer);
		this.applicationContext = reactiveWebServerApplicationContext;
	}

	@Override
	public ReactiveWebServerApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
}