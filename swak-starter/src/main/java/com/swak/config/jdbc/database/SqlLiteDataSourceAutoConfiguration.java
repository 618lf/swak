package com.swak.config.jdbc.database;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.sqlite.SQLiteDataSource;

import com.swak.Constants;
import com.swak.exception.BaseRuntimeException;

/**
 * 配置 Druid
 * 
 * @author lifeng
 */
@ConditionalOnClass({ SQLiteDataSource.class })
@ConditionalOnMissingBean(DataSource.class)
@ConditionalOnProperty(prefix = Constants.DATASOURCE_PREFIX, name = "db", havingValue = "sqlite", matchIfMissing = false)
public class SqlLiteDataSourceAutoConfiguration {

	@Autowired
	private DataSourceProperties properties;
	@Autowired
	private ResourceLoader resourceLoader;

	/**
	 * 构建 sqlLiteDataSource 可配置在 resource: 在资源目录下获取db文件，这种方式有一定的问题 如果填写的是相对路径，则获取
	 * jar 中当前目录下的当前目录。 可以研究下spring 的 resource
	 * 
	 * URL配置总结：
	 * 1. 可以配置绝对路径，不建议
	 * 2. 配置resource:或 classpaht: 打包之后读取不到,开发环境可以使用，打包之后不能使用
	 * 3. 配置file: 打包之后在相对目录中获取，开发环境获取不到
	 */
	@Bean(destroyMethod = "")
	public DataSource sqlLiteDataSource() {
		SQLiteDataSource dataSource = new SQLiteDataSource();
		dataSource.setUrl("jdbc:sqlite:" + loadSqliteUrl());
		return dataSource;
	}

	/**
	 * 加载资源文件
	 * 
	 * @return
	 */
	private String loadSqliteUrl() {
		String location = properties.getUrl();
		if (location.startsWith("resource:")) {
			return location;
		}
		try {
			Resource resource = resourceLoader.getResource(location);
			return resource.getFile().getAbsolutePath();
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
	}

	@Bean
	public SqlLiteDataSourcePoolMetadata sqlLiteDataSourcePoolMetadata(SQLiteDataSource dataSource) {
		return new SqlLiteDataSourcePoolMetadata(dataSource);
	}
}
