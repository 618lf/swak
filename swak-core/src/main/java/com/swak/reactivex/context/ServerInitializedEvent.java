package com.swak.reactivex.context;

import org.springframework.context.ApplicationEvent;

public abstract class ServerInitializedEvent extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	protected ServerInitializedEvent(Server server) {
		super(server);
	}

	/**
	 * Access the {@link Server}.
	 * @return the embedded web server
	 */
	public Server getServer() {
		return getSource();
	}

	/**
	 * Access the application context that the server was created in. Sometimes it is
	 * prudent to check that this matches expectations (like being equal to the current
	 * context) before acting on the server itself.
	 * @return the applicationContext that the server was created from
	 */
	public abstract ReactiveServerApplicationContext getApplicationContext();

	/**
	 * Access the source of the event (an {@link Server}).
	 * @return the embedded web server
	 */
	@Override
	public Server getSource() {
		return (Server) super.getSource();
	}
}
