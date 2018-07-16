package com.swak.config.storage;

import static com.swak.Application.APP_LOGGER;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import com.swak.storage.StorageProperties;
import com.swak.storage.Storager;
import com.swak.storage.local.LocalStorager;

/**
 * 存储配置
 * 
 * @author lifeng
 */
public class StorageConfigurationSupport {

	@Autowired
	private StorageProperties storageProperties;

	public StorageConfigurationSupport() {
		APP_LOGGER.debug("Loading Storage");
	}

	/**
	 * 创建一个存储服务
	 * @return
	 */
	@Bean
	public Storager localStorager() {
		LocalStorager storager = new LocalStorager();
		storager.setDomain(storageProperties.getDomain());
		storager.setStoragePath(storageProperties.getStoragePath());
		storager.setUrlPath(storageProperties.getUrlPath());
		return storager;
	}
}