package com.tmt.shop.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ShopService {

	/**
	 * 看是否有事务
	 * @return
	 */
	@Transactional
	public String say() {
		return "say hello to lifeng";
	}
}
