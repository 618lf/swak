package com.swak.config.jdbc.database;

import static com.swak.Application.APP_LOGGER;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;

import com.swak.config.customizer.DataSourceOptionsCustomizer;
import com.swak.persistence.datasource.DataSourceHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 配置 Hikari
 * 
 * @author lifeng
 */
@ConditionalOnClass({ HikariDataSource.class })
@ConditionalOnMissingBean(DataSource.class)
public class HikariDataSourceAutoConfiguration {

	private List<DataSourceOptionsCustomizer> customizers;

	public HikariDataSourceAutoConfiguration(ObjectProvider<List<DataSourceOptionsCustomizer>> customizersProvider) {
		APP_LOGGER.debug("Loading Hikari DataSource");
		this.customizers = customizersProvider != null ? customizersProvider.getIfAvailable() : null;
	}

	/**
	 * 构建 HikariDataSource
	 * 
	 * @return
	 */
	@Bean
	public DataSource hikariDataSource(DataSourceProperties properties) {
		HikariConfig config = new HikariConfig();
		config.setPoolName(properties.getName());
		config.setJdbcUrl(properties.getUrl());
		config.setUsername(properties.getUsername());
		config.setPassword(properties.getPassword());
		config.setDriverClassName(properties.getDriverClassName());

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", properties.getPrepStmtCacheSize());
		config.addDataSourceProperty("prepStmtCacheSqlLimit", properties.getPrepStmtCacheSqlLimit());
		config.addDataSourceProperty("useServerPrepStmts", "true");
		config.addDataSourceProperty("useLocalSessionState", "true");
		config.addDataSourceProperty("rewriteBatchedStatements", "true");
		config.addDataSourceProperty("cacheResultSetMetadata", "true");
		config.addDataSourceProperty("cacheServerConfiguration", "true");
		config.addDataSourceProperty("elideSetAutoCommits", "true");
		config.addDataSourceProperty("maintainTimeStats", "false");
		config.setMinimumIdle(properties.getMinIdle()); // 最小空闲连接数量
		config.setIdleTimeout(properties.getMinEvictableIdleTimeMillis()); // 空闲连接存活最大时间，默认600000（10分钟）
		config.setMaximumPoolSize(properties.getMaxActive()); // 连接池最大连接数，默认是10
		config.setConnectionTimeout(properties.getMaxWait()); // 数据库连接超时时间,默认30秒，即30000
		config.setMaxLifetime(properties.getMaxLifetime()); // 连接的最长生命周期，值0表示无限生命周期，默认1800000即30分钟

		// 创建连接
		HikariDataSource dataSource = new HikariDataSource(config);
		DataSourceHolder.setDataSource(dataSource);

		if (this.customizers != null && !this.customizers.isEmpty()) {
			for (DataSourceOptionsCustomizer customizer : this.customizers) {
				customizer.customize(dataSource);
			}
		}
		return dataSource;
	}

	/**
	 * 提供 HikariDataSourcePool 指标查询
	 * 
	 * @return
	 */
	@Bean
	public HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata(HikariDataSource dataSource) {
		return new HikariDataSourcePoolMetadata(dataSource);
	}
}
