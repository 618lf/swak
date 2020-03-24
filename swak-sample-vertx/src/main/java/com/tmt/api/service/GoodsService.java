package com.tmt.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.swak.persistence.BaseDao;
import com.swak.service.BaseService;
import com.swak.vertx.annotation.VertxService;
import com.tmt.api.dao.GoodsDao;
import com.tmt.api.entity.Goods;
import com.tmt.api.facade.GoodsNotServiceFacade;
import com.tmt.api.facade.GoodsServiceFacade;
import com.weibo.api.motan.config.springsupport.annotation.MotanService;

import io.shardingsphere.api.HintManager;

/**
 * 商品服务, 只需要使用同步接口，代码写起来比较简单
 * 
 * @author lifeng
 */
@VertxService(use_pool = "goods", service = GoodsServiceFacade.class)
@MotanService
public class GoodsService extends BaseService<Goods, String> implements GoodsServiceFacade, GoodsNotServiceFacade {

	@Autowired
	private GoodsDao goodsDao;

	@Override
	protected BaseDao<Goods, String> getBaseDao() {
		return goodsDao;
	}

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

	/**
	 * 保存数据
	 */
	@Override
	@Transactional
	public Goods save() {
		Goods goods = new Goods();
		goods.setName("李锋");
		goods.setRemarks("还不错");
		this.insert(goods);
		return goods;
	}

	@Override
	@Transactional
	public Goods get_save() {
		Goods goods = this.queryForObject("getOne", "1");
		if (goods == null) {
			goods = new Goods();
		}
		goods.setName("李锋");
		goods.setRemarks("还不错");
		this.update(goods);
		return goods;
	}

	@Override
	@Transactional
	public Goods get_save_get() {
		Goods goods = this.queryForObject("getOne", "1");
		if (goods == null) {
			goods = new Goods();
		}
		goods.setName("李锋");
		goods.setRemarks("还不错");
		this.update(goods);
		return this.queryForObject("getOne", "1");
	}

	/**
	 * 强制走主库
	 */
	@Override
	@Transactional
	public Goods hint_get_save_get() {
		HintManager hintManager = HintManager.getInstance();
		hintManager.setMasterRouteOnly();
		Goods goods = this.queryForObject("getOne", "1");
		if (goods == null) {
			goods = new Goods();
		}
		goods.setName("李锋");
		goods.setRemarks("还不错");
		this.update(goods);
		return this.queryForObject("getOne", "1");
	}
}