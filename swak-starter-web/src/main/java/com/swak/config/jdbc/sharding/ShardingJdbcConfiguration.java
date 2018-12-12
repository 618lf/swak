/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.swak.config.jdbc.sharding;

import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.google.common.base.CaseFormat;
import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.swak.Constants;
import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;

import io.shardingsphere.core.exception.ShardingException;
import io.shardingsphere.core.yaml.sharding.YamlShardingRuleConfiguration;
import io.shardingsphere.shardingjdbc.api.MasterSlaveDataSourceFactory;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;

/**
 * Spring boot sharding and master-slave configuration.
 *
 * @author caohao
 */
@Configuration
@ConditionalOnClass(YamlShardingRuleConfiguration.class)
@EnableConfigurationProperties({ ShardingJdbcShardingRuleConfigurationProperties.class,
		ShardingJdbcMasterSlaveRuleConfigurationProperties.class })
@ConditionalOnProperty(prefix = Constants.DATASOURCE_PREFIX, name = "db", havingValue = "sharding", matchIfMissing = false)
public class ShardingJdbcConfiguration implements EnvironmentAware {

	// 初始化数据
	private static final String SET_METHOD_PREFIX = "set";
	private static Collection<Class<?>> generalClassType = Sets.<Class<?>>newHashSet(boolean.class, Boolean.class,
			int.class, Integer.class, long.class, Long.class, String.class);

	// 通用配置
	@Autowired
	private DataSourceProperties properties;

	@Autowired
	private ShardingJdbcShardingRuleConfigurationProperties shardingProperties;

	@Autowired
	private ShardingJdbcMasterSlaveRuleConfigurationProperties masterSlaveProperties;

	private final Map<String, DataSource> dataSourceMap = new LinkedHashMap<>();

	/**
	 * Get data source bean.
	 * 
	 * @return data source bean
	 * @throws SQLException
	 *             SQL exception
	 */
	@Bean
	public DataSource dataSource() throws SQLException {
		return null == masterSlaveProperties.getMasterDataSourceName()
				? ShardingDataSourceFactory.createDataSource(dataSourceMap,
						shardingProperties.getShardingRuleConfiguration(), shardingProperties.getConfigMap(),
						shardingProperties.getProps())
				: MasterSlaveDataSourceFactory.createDataSource(dataSourceMap,
						masterSlaveProperties.getMasterSlaveRuleConfiguration(), masterSlaveProperties.getConfigMap(),
						masterSlaveProperties.getProps());
	}

	/**
	 * 初始化多数据源
	 */
	@Override
	public final void setEnvironment(final Environment environment) {
		setDataSourceMap(environment);
	}

	@SuppressWarnings("unchecked")
	private void setDataSourceMap(final Environment environment) {
		String prefix = "sharding.jdbc.datasource.";
		String dataSources = environment.getProperty(prefix + "names");
		for (String each : dataSources.split(",")) {
			try {
				Map<String, Object> dataSourceProps = PropertyUtil.handle(environment, prefix + each, Map.class);
				Preconditions.checkState(!dataSourceProps.isEmpty(), "Wrong datasource properties!");
				DataSource dataSource = newHikariDataSource(dataSourceProps);
				dataSourceMap.put(each, dataSource);
			} catch (final ReflectiveOperationException ex) {
				throw new ShardingException("Can't find datasource type!", ex);
			}
		}
	}

	/**
	 * 限制只能使用 Hikari
	 * 
	 * @param dataSourceProps
	 * @return
	 */
	private DataSource newHikariDataSource(Map<String, Object> dataSourceProps) {
		DataSource dataSource = new HikariDataSourceAutoConfiguration(properties).hikariDataSource();
		for (Entry<String, Object> entry : dataSourceProps.entrySet()) {
			callSetterMethod(dataSource, getSetterMethodName(entry.getKey()),
					null == entry.getValue() ? null : entry.getValue().toString());
		}
		return dataSource;
	}

	private String getSetterMethodName(final String propertyName) {
		if (propertyName.contains("-")) {
			return CaseFormat.LOWER_HYPHEN.to(CaseFormat.LOWER_CAMEL, SET_METHOD_PREFIX + "-" + propertyName);
		}
		return SET_METHOD_PREFIX + String.valueOf(propertyName.charAt(0)).toUpperCase()
				+ propertyName.substring(1, propertyName.length());
	}

	private void callSetterMethod(final DataSource dataSource, final String methodName, final String setterValue) {
		for (Class<?> each : generalClassType) {
			try {
				Method method = dataSource.getClass().getMethod(methodName, each);
				if (boolean.class == each || Boolean.class == each) {
					method.invoke(dataSource, Boolean.valueOf(setterValue));
				} else if (int.class == each || Integer.class == each) {
					method.invoke(dataSource, Integer.parseInt(setterValue));
				} else if (long.class == each || Long.class == each) {
					method.invoke(dataSource, Long.parseLong(setterValue));
				} else {
					method.invoke(dataSource, setterValue);
				}
				return;
			} catch (final ReflectiveOperationException ignore) {
			}
		}
	}
}