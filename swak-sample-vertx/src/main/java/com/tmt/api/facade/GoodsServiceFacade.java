package com.tmt.api.facade;

import com.swak.annotation.FluxAsync;
import com.tmt.api.entity.Goods;
import com.weibo.api.motan.transport.async.MotanAsync;

/**
 * 商品服务 用异步接口来约束前端，同步接口来约束后端，之後可以自動身成异步接口
 * 
 * @author lifeng
 */
@MotanAsync
@FluxAsync
public interface GoodsServiceFacade {

	/**
	 * 就这样执行
	 */
	String sayHello();

	/**
	 * 读数据
	 * 
	 * @return
	 */
	Goods get();

	/**
	 * 保存数据
	 * 
	 * @return
	 */
	Goods save();

	/**
	 * 保存数据
	 * 
	 * @return
	 */
	Goods get_save();

	/**
	 * 保存数据
	 * 
	 * @return
	 */
	Goods get_save_get();

	/**
	 * 强制走主库
	 * 
	 * @return
	 */
	Goods hint_get_save_get();
}
