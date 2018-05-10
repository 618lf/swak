package com.tmt.database;

import javax.sql.DataSource;

import com.swak.config.database.DataSourceProperties;

public interface DataSourceProvider {

	/**
	 * 得到数据源
	 * @return
	 */
	DataSource getDataSource();
	
	/**
	 * 默认的属性配置
	 * @return
	 */
	default DataSourceProperties getProperties() {
		DataSourceProperties properties = new DataSourceProperties();
		properties.setUrl("jdbc:mysql://localhost:3306/shop?useUnicode=true&characterEncoding=utf-8");
		properties.setMaxActive(200);
		properties.setUsername("root");
		properties.setPassword("rootadmin");
		return properties;
	}
}
