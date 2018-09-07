package com.tmt.api.service;

import com.swak.vertx.annotation.ServiceMapping;
import com.tmt.api.facade.GoodsServiceFacade;

/**
 * 商品服务, 只需要使用同步接口，代码写起来比较简单
 * @author lifeng
 */
@ServiceMapping(use_pool="write_pool")
public class GoodsService implements GoodsServiceFacade {

	@Override
	public String sayHello() {
		System.out.println("service:" + Thread.currentThread());
		return "Hello World!";
	}
}