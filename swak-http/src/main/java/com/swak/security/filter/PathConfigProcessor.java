package com.swak.security.filter;

import com.swak.http.Filter;

public interface PathConfigProcessor {

	/**
	 * 填充配置信息
	 * @param path
	 * @param config
	 * @return
	 */
	Filter processPathConfig(String path, String config);
}
