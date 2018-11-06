package com.swak.config.jdbc.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.sqlite.SQLiteDataSource;

import com.swak.Constants;

/**
 * 配置 Druid
 * 
 * @author lifeng
 */
@ConditionalOnClass({ SQLiteDataSource.class })
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(prefix = Constants.DATASOURCE_PREFIX, name = "db", havingValue="sqlite", matchIfMissing = false)
public class SqlLiteDataSourceAutoConfiguration {

	@Autowired
	private DataSourceProperties properties;

	/**
	 * 构建 sqlLiteDataSource
	 * 可配置在 resource:
	 * @return
	 */
	@Bean(destroyMethod = "")
	public DataSource sqlLiteDataSource() {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + properties.getUrl());
		return dataSource;
	}
	
	@Bean
	public SqlLiteDataSourcePoolMetadata sqlLiteDataSourcePoolMetadata(SQLiteDataSource dataSource) {
		return new SqlLiteDataSourcePoolMetadata(dataSource);
	}
}
