package com.tmt.database.datasource;

import javax.sql.DataSource;

import com.swak.config.database.DataSourceProperties;
import com.tmt.database.DataSourceProvider;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class HikariDataSourceProvider implements DataSourceProvider{

	@Override
	public DataSource getDataSource() {
		DataSourceProperties properties = this.getProperties();
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
		return dataSource;
	}
}
