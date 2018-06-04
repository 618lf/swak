package com.swak.reactivex.context;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.util.StringUtils;

import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.transport.http.ReactiveWebServerFactory;
import com.swak.reactivex.transport.http.WebServer;

/**
 * 只是一个基本的web服务
 * 但提供了服务的启动任务
 * @author lifeng
 */
public class ReactiveWebServerApplicationContext extends AnnotationConfigApplicationContext{

	private volatile WebServer webServer;
	
	@Override
	public final void refresh() throws BeansException, IllegalStateException {
		try {
			super.refresh();
		}
		catch (RuntimeException ex) {
			stopAndReleaseReactiveWebServer();
			throw ex;
		}
	}

	@Override
	protected void onRefresh() {
		super.onRefresh();
		try {
			createWebServer();
		}
		catch (Throwable ex) {
			throw new ApplicationContextException("Unable to start reactive web server",
					ex);
		}
	}

	@Override
	protected void finishRefresh() {
		super.finishRefresh();
		WebServer localServer = startReactiveWebServer();
		if (localServer != null) {
			publishEvent(new ReactiveWebServerInitializedEvent(localServer, this));
		}
	}

	@Override
	protected void onClose() {
		super.onClose();
		stopAndReleaseReactiveWebServer();
	}

	private void createWebServer() {
		WebServer localServer = this.webServer;
		if (localServer == null) {
			this.webServer = getWebServerFactory().getWebServer();
		}
		initPropertySources();
	}

	/**
	 * Returns the {@link WebServer} that was created by the context or {@code null} if
	 * the server has not yet been created.
	 * @return the web server
	 */
	public WebServer getWebServer() {
		return this.webServer;
	}

	/**
	 * Return the {@link ReactiveWebServerFactory} that should be used to create the
	 * reactive web server. By default this method searches for a suitable bean in the
	 * context itself.
	 * @return a {@link ReactiveWebServerFactory} (never {@code null})
	 */
	protected ReactiveWebServerFactory getWebServerFactory() {
		// Use bean names so that we don't consider the hierarchy
		String[] beanNames = getBeanFactory()
				.getBeanNamesForType(ReactiveWebServerFactory.class);
		if (beanNames.length == 0) {
			throw new ApplicationContextException(
					"Unable to start ReactiveWebApplicationContext due to missing "
							+ "ReactiveWebServerFactory bean.");
		}
		if (beanNames.length > 1) {
			throw new ApplicationContextException(
					"Unable to start ReactiveWebApplicationContext due to multiple "
							+ "ReactiveWebServerFactory beans : "
							+ StringUtils.arrayToCommaDelimitedString(beanNames));
		}
		return getBeanFactory().getBean(beanNames[0], ReactiveWebServerFactory.class);
	}

	/**
	 * Return the {@link HttpHandler} that should be used to process the reactive web
	 * server. By default this method searches for a suitable bean in the context itself.
	 * @return a {@link HttpHandler} (never {@code null}
	 */
	protected HttpHandler getHttpHandler() {
		// Use bean names so that we don't consider the hierarchy
		String[] beanNames = getBeanFactory().getBeanNamesForType(HttpHandler.class);
		if (beanNames.length == 0) {
			throw new ApplicationContextException(
					"Unable to start ReactiveWebApplicationContext due to missing HttpHandler bean.");
		}
		if (beanNames.length > 1) {
			throw new ApplicationContextException(
					"Unable to start ReactiveWebApplicationContext due to multiple HttpHandler beans : "
							+ StringUtils.arrayToCommaDelimitedString(beanNames));
		}
		return getBeanFactory().getBean(beanNames[0], HttpHandler.class);
	}

	private WebServer startReactiveWebServer() {
		WebServer localServer = this.webServer;
		if (localServer != null) {
			localServer.start(this.getHttpHandler());
		}
		return localServer;
	}

	private void stopAndReleaseReactiveWebServer() {
		WebServer localServer = this.webServer;
		if (localServer != null) {
			try {
				localServer.stop();
				this.webServer = null;
			}
			catch (Exception ex) {
				throw new IllegalStateException(ex);
			}
		}
	}
}