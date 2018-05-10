package com.tmt.database.datasource;

import java.sql.SQLException;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;
import com.swak.config.database.DataSourceProperties;
import com.tmt.database.DataSourceProvider;

/**
 * 基于 Druid 的数据库连接池
 * @author lifeng
 */
public class DruidProvider implements DataSourceProvider{

	public DataSource getDataSource() {
		DataSourceProperties properties = this.getProperties();
		DruidDataSource dataSource = new DruidDataSource();
		dataSource.setUrl(properties.getUrl());
		dataSource.setUsername(properties.getUsername());
		dataSource.setPassword(properties.getPassword());

		dataSource.setDriverClassName(properties.getDriverClassName());
		dataSource.setInitialSize(properties.getInitialSize()); // 定义初始连接数
		dataSource.setMinIdle(properties.getMinIdle()); // 最小空闲
		dataSource.setMaxActive(properties.getMaxActive()); // 定义最大连接数
		dataSource.setMaxWait(properties.getMaxWait()); // 最长等待时间

		// 配置间隔多久才进行一次检测，检测需要关闭的空闲连接，单位是毫秒
		dataSource.setTimeBetweenEvictionRunsMillis(properties.getTimeBetweenEvictionRunsMillis());

		// 配置一个连接在池中最小生存的时间，单位是毫秒
		dataSource.setMinEvictableIdleTimeMillis(properties.getMinEvictableIdleTimeMillis());
		dataSource.setValidationQuery(properties.getValidationQuery());
		dataSource.setTestWhileIdle(properties.getTestWhileIdle());
		dataSource.setTestOnBorrow(properties.getTestOnBorrow());
		dataSource.setTestOnReturn(properties.getTestOnReturn());

		// 打开PSCache，并且指定每个连接上PSCache的大小
		dataSource.setPoolPreparedStatements(properties.getPoolPreparedStatements());
		dataSource.setMaxPoolPreparedStatementPerConnectionSize(
				properties.getMaxPoolPreparedStatementPerConnectionSize());

		try {
			dataSource.setFilters(properties.getFilters());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataSource;
	}
}
