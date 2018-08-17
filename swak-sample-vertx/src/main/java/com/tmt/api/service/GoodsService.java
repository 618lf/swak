package com.tmt.api.service;

import com.swak.vertx.annotation.ServiceMapping;
import com.tmt.api.facade.GoodsServiceFacade;

/**
 * 商品服务
 * @author lifeng
 */
@ServiceMapping
public class GoodsService implements GoodsServiceFacade {

	@Override
	public void sayHello() {
		System.out.println("I am a service");
	}
}
