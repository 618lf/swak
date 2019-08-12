package com.tmt.api.facade;

import com.swak.vertx.transport.async.VertxAsync;
import com.tmt.api.entity.Goods;

/**
 * 商品服务
 * 用异步接口来约束前端，同步接口来约束后端，之後可以自動身成异步接口
 * @author lifeng
 */
//@MotanAsync
@VertxAsync
public interface GoodsServiceFacade {

	/**
	 * 就这样执行
	 */
	String sayHello();
	
	Goods get();
}
