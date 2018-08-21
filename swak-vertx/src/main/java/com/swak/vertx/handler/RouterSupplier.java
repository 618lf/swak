package com.swak.vertx.handler;

import io.vertx.ext.web.Router;

/**
 * 提供子router
 * @author lifeng
 */
public interface RouterSupplier {

	/**
	 * 提供 Router
	 * @return
	 */
	Router get();
}