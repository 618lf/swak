package com.tmt.api.facade;

import java.util.concurrent.CompletableFuture;

/**
 * 商品服务
 * @author lifeng
 */
public interface GoodsServiceFacade {

	/**
	 * 就这样执行
	 */
	CompletableFuture<String> sayHello();
}
