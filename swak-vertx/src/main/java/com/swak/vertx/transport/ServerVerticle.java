package com.swak.vertx.transport;

import com.swak.vertx.config.VertxProperties;

import io.vertx.core.AsyncResult;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.PemKeyCertOptions;

/**
 * 服务器通用配置
 * 
 * @author lifeng
 * @date 2020年5月14日 下午3:17:55
 */
public interface ServerVerticle {

	/**
	 * 服务的配置
	 * 
	 * @param properties
	 * @return
	 */
	default HttpServerOptions serverOptions(VertxProperties properties) {

		HttpServerOptions options = new HttpServerOptions();

		if (properties.isCompressionSupported()) {
			options.setCompressionSupported(properties.isCompressionSupported());
		}
		if (properties.getCompressionLevel() != -1) {
			options.setCompressionLevel(properties.getCompressionLevel());
		}
		if (properties.getKeyPaths() != null && properties.getCertPaths() != null && properties.getKeyPaths().size() > 0
				&& properties.getCertPaths().size() == properties.getKeyPaths().size()) {
			if (properties.getKeyPaths().get(0).endsWith(".pem")) {
				PemKeyCertOptions pemKeys = new PemKeyCertOptions().setKeyPaths(properties.getKeyPaths())
						.setCertPaths(properties.getCertPaths());
				options.setKeyCertOptions(pemKeys);
			} else if (properties.getKeyPaths().get(0).endsWith(".jks")) {
				options.setKeyStoreOptions(new JksOptions().setPath(properties.getKeyPaths().get(0))
						.setPassword(properties.getCertPaths().get(0)));
			}
		}
		options.setSsl(properties.isUseSsl());
		options.setUseAlpn(properties.isUseAlpn());
		options.setClientAuth(properties.getClientAuth());
		return options;
	}

	/**
	 * 启动异常处理
	 * 
	 * @param startPromise
	 * @param result
	 */
	default void startResult(Promise<Void> startPromise, AsyncResult<HttpServer> result) {
		if (result.failed()) {
			startPromise.fail(result.cause());
		} else {
			startPromise.complete();
		}
	}
}
