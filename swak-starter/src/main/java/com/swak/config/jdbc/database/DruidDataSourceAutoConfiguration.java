package com.swak.config.jdbc.database;

import static com.swak.Application.APP_LOGGER;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

import com.alibaba.druid.pool.DruidDataSource;
import com.swak.persistence.datasource.DataSourceHolder;

/**
 * 配置 Druid
 * 
 * @author lifeng
 */
@ConditionalOnClass({ DruidDataSource.class })
@ConditionalOnMissingBean(DataSource.class)
public class DruidDataSourceAutoConfiguration extends MetricsConfiguration {

	@Autowired
	private DataSourceProperties properties;

	/**
	 * 构建 DruidDataSource
	 * 
	 * @return
	 */
	@Bean
	public DataSource druidDataSource() {
		APP_LOGGER.debug("Loading Druid DataSource");
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

		// 设置链接
		DataSourceHolder.setDataSource(dataSource);
		return dataSource;
	}

	/**
	 * 提供 HikariDataSourcePool 指标查询
	 * 
	 * @return
	 */
	@Bean
	public DruidDataSourcePoolMetadata druidDataSourcePoolMetadata(DruidDataSource dataSource) {
		return new DruidDataSourcePoolMetadata(dataSource);
	}
}
