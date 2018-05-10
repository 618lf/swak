package com.tmt.shop.service;

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
	@Transactional
	public String say() {
		return "say hello to lifeng";
	}
	
}
