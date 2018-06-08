package com.swak.reactivex.context;

/**
 * Simple interface that represents a fully configured web server (for example Tomcat,
 * Jetty, Netty). Allows the server to be {@link #start() started} and {@link #stop()
 * stopped}.
 *
 * @author Phillip Webb
 * @author Dave Syer
 * @since 2.0.0
 */
public interface Server {

	/**
	 * Starts the web server. Calling this method on an already started server has no
	 * effect.
	 * @throws ServerException
	 */
	void start() throws ServerException;

	/**
	 * Stops the web server. Calling this method on an already stopped server has no
	 * effect.
	 * @throws ServerException
	 */
	void stop() throws ServerException;

	/**
	 * Return the port this server is listening on.
	 * @return
	 */
	int getPort();
}
