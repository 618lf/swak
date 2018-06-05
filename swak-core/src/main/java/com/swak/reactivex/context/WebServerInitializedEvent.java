package com.swak.reactivex.context;

import org.springframework.context.ApplicationEvent;

public abstract class WebServerInitializedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	protected WebServerInitializedEvent(WebServer webServer) {
		super(webServer);
	}

	/**
	 * Access the {@link WebServer}.
	 * @return the embedded web server
	 */
	public WebServer getWebServer() {
		return getSource();
	}

	/**
	 * Access the application context that the server was created in. Sometimes it is
	 * prudent to check that this matches expectations (like being equal to the current
	 * context) before acting on the server itself.
	 * @return the applicationContext that the server was created from
	 */
	public abstract ReactiveWebServerApplicationContext getApplicationContext();

	/**
	 * Access the source of the event (an {@link WebServer}).
	 * @return the embedded web server
	 */
	@Override
	public WebServer getSource() {
		return (WebServer) super.getSource();
	}
}
