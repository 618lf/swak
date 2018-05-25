package com.tmt.shop.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.swak.common.persistence.BaseDao;
import com.swak.common.service.BaseService;
import com.tmt.shop.dao.ShopDao;
import com.tmt.shop.entity.Shop;

@Service
public class ShopService extends BaseService<Shop, Long>{

	@Autowired
	private ShopDao shopDao;
	
	@Override
	protected BaseDao<Shop, Long> getBaseDao() {
		return shopDao;
	}
	
	/**
	 * 看是否有事务
	 * @return
	 */
	public String say() {
		Shop shop = new Shop();
		shop.setName("丸子世家");
		this.insert(shop);
		return "say hello to lifeng";
	}
	
	/**
	 * 我是有事务的
	 * @return
	 */
	@Transactional
	public String sayTransactional() {
		return "say hello to hanqian";
	}

	/**
	 * 保存数据
	 */
	@Override
	@Transactional
	public CompletableFuture<Shop> save(Shop entity) {
		return this.doSave(entity);
	}
	
	/**
	 * 保存数据
	 */
	@Transactional
	public CompletableFuture<Shop> saveAndGet(Shop entity) {
		return this.doSave(entity).thenApply((shop) -> {
			return this.shopDao.get(shop.getId());
		});
	}

	/**
	 * 删除数据
	 */
	@Override
	public CompletableFuture<Void> delete(List<Shop> entities) {
		return this.doDelete(entities);
	}
}
