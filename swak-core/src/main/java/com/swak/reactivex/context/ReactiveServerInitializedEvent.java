package com.swak.reactivex.context;

public class ReactiveServerInitializedEvent extends ServerInitializedEvent {

	private static final long serialVersionUID = 1L;
	
	private final ReactiveServerApplicationContext applicationContext;

	public ReactiveServerInitializedEvent(Server server,
			ReactiveServerApplicationContext reactiveWebServerApplicationContext) {
		super(server);
		this.applicationContext = reactiveWebServerApplicationContext;
	}

	@Override
	public ReactiveServerApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
}