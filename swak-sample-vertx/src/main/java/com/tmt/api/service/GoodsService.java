package com.tmt.api.service;

import com.swak.vertx.annotation.VertxService;
import com.tmt.api.facade.GoodsServiceFacade;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

/**
 * 商品服务, 只需要使用同步接口，代码写起来比较简单
 * 
 * @author lifeng
 */
@VertxService(use_pool = "goods")
@MotanService
public class GoodsService implements GoodsServiceFacade {

	@Override
	public String sayHello() {
		//System.out.println("service:" + Thread.currentThread());
		return "Hello World!";
	}
}