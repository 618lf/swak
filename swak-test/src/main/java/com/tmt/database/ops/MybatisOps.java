package com.tmt.database.ops;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ObjectUtils;

import com.google.common.collect.Maps;
import com.swak.common.persistence.QueryCondition;
import com.swak.common.persistence.dialect.MySQLDialect;
import com.swak.common.persistence.incrementer.IdGen;
import com.swak.common.persistence.mybatis.ExecutorInterceptor;
import com.swak.common.utils.StringUtils;
import com.swak.config.database.MybatisProperties;
import com.swak.config.database.SpringBootVFS;
import com.tmt.database.InsertOps;
import com.tmt.database.QueryOps;

public class MybatisOps implements InsertOps, QueryOps {

	private final MybatisProperties properties;
	private final Interceptor[] interceptors;
	private final ResourceLoader resourceLoader;
	private final DatabaseIdProvider databaseIdProvider;
	SqlSessionTemplate sqlSessionTemplate;

	public MybatisOps(DataSource dataSource) {
		this.properties = new MybatisProperties();
		this.interceptors = null;
		this.resourceLoader = null;
		this.databaseIdProvider = null;
		sqlSessionTemplate = sqlSessionTemplate(sqlSessionFactory(dataSource));
	}

	public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
		SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
		factory.setDataSource(dataSource);
		factory.setVfs(SpringBootVFS.class);
		if (StringUtils.hasText(this.properties.getConfigLocation())) {
			factory.setConfigLocation(this.resourceLoader.getResource(this.properties.getConfigLocation()));
		}
		Configuration configuration = this.properties.getConfiguration();
		if (configuration == null && !StringUtils.hasText(this.properties.getConfigLocation())) {
			configuration = new Configuration();
		}
		factory.setConfiguration(configuration);
		if (this.properties.getConfigurationProperties() != null) {
			factory.setConfigurationProperties(this.properties.getConfigurationProperties());
		}
		if (!ObjectUtils.isEmpty(this.interceptors)) {
			factory.setPlugins(this.interceptors);
		}
		if (this.databaseIdProvider != null) {
			factory.setDatabaseIdProvider(this.databaseIdProvider);
		}
		if (StringUtils.hasLength(this.properties.getTypeAliasesPackage())) {
			factory.setTypeAliasesPackage(this.properties.getTypeAliasesPackage());
		}
		if (StringUtils.hasLength(this.properties.getTypeHandlersPackage())) {
			factory.setTypeHandlersPackage(this.properties.getTypeHandlersPackage());
		}
		if (!ObjectUtils.isEmpty(this.properties.resolveMapperLocations())) {
			factory.setMapperLocations(this.properties.resolveMapperLocations());
		}

		// 默认配置
		this.defaultConfiguration(configuration);
		try {
			return factory.getObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void defaultConfiguration(Configuration configuration) {

		// 默认的拦截器
		ExecutorInterceptor interceptor = new ExecutorInterceptor();
		interceptor.setDialect(new MySQLDialect());
		configuration.addInterceptor(interceptor);

		// 默认的别名
		configuration.getTypeAliasRegistry().registerAlias("queryCondition", QueryCondition.class);
	}

	public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
		ExecutorType executorType = this.properties.getExecutorType();
		if (executorType != null) {
			return new SqlSessionTemplate(sqlSessionFactory, executorType);
		} else {
			return new SqlSessionTemplate(sqlSessionFactory);
		}
	}

	@Override
	public void insert() {
		Map<String, Object> param = Maps.newHashMap();
		param.put("ID", IdGen.id());
		param.put("NAME", "lifeng");
		sqlSessionTemplate.insert("com.tmt.shop.dao.ShopDao.insert", param);
	}

	@Override
	public void query() {
		sqlSessionTemplate.selectList("com.tmt.shop.dao.ShopDao.get");
	}
}
