package com.swak.config.database;

import static com.swak.Application.APP_LOGGER;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.jdbc.metadata.HikariDataSourcePoolMetadata;
import org.springframework.context.annotation.Bean;

import com.swak.common.persistence.DataSourceHolder;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 配置 Hikari
 * @author lifeng
 */
@ConditionalOnClass({HikariDataSource.class})
@ConditionalOnMissingBean(DataSource.class)
public class HikariDataSourceAutoConfiguration {

	@Autowired
	private DataSourceProperties properties;
	
	/**
	 * 构建 HikariDataSource
	 * @return
	 */
	@Bean
	public DataSource hikariDataSource() {
		APP_LOGGER.debug("Loading Hikari DataSource");
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
        config.addDataSourceProperty("useLocalTransactionState", "true");
        config.addDataSourceProperty("rewriteBatchedStatements", "true");
        config.addDataSourceProperty("cacheResultSetMetadata", "true");
        config.addDataSourceProperty("cacheServerConfiguration", "true");
        config.addDataSourceProperty("elideSetAutoCommits", "true");
        config.addDataSourceProperty("maintainTimeStats", "false");
        config.setMinimumIdle(properties.getMinIdle());
        config.setMaximumPoolSize(properties.getMaxActive());
        config.setConnectionTimeout(properties.getMaxWait());
        config.setIdleTimeout(properties.getMinEvictableIdleTimeMillis());
        config.setMaxLifetime(properties.getMaxLifetime());
        
        // 创建连接
        HikariDataSource dataSource = new HikariDataSource(config);
		DataSourceHolder.setDataSource(dataSource);
		return dataSource;
	}
	
	/**
	 * 提供 HikariDataSourcePool 指标查询
	 * @return
	 */
	@Bean
	public HikariDataSourcePoolMetadata hikariDataSourcePoolMetadata(HikariDataSource dataSource) {
		return new HikariDataSourcePoolMetadata(dataSource);
	}
}
