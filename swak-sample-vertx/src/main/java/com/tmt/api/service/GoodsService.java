package com.tmt.api.service;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.vertx.annotation.VertxService;
import com.tmt.api.dao.GoodsDao;
import com.tmt.api.entity.Goods;
import com.tmt.api.facade.GoodsNotServiceFacade;
import com.tmt.api.facade.GoodsServiceFacade;

/**
 * 商品服务, 只需要使用同步接口，代码写起来比较简单
 * 
 * @author lifeng
 */
@VertxService(use_pool = "goods", isAop = false, service = GoodsServiceFacade.class)
// @MotanService
public class GoodsService implements GoodsServiceFacade, GoodsNotServiceFacade {

	@Autowired
	private GoodsDao goodsDao;

	@Override
	public String sayHello() {
		// System.out.println("service:" + Thread.currentThread());
		// throw new GoodsException("商品错误！");
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "111";
	}

	/**
	 * 获取数据
	 */
	@Override
	public Goods get() {
		return goodsDao.get("1");
	}
}