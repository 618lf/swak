package com.swak.service;

import com.swak.utils.SpringContextHolder;

/**
 * 持有代理对象
 * 
 * @author lifeng
 */
public class ProxyService {

	/**
	 * 代理类
	 */
	protected Object proxy;
	

	/**
	 * 获得代理类
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <U> U getProxy() {
		if (this.proxy == null) {
			Class<?>[] interfacess = this.getClass().getInterfaces();
			if (interfacess != null && interfacess.length > 0) {
				this.proxy = SpringContextHolder.getBean(interfacess[0]);
			}
		}
		return (U) this.proxy;
	}
}
