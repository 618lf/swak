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

import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.swak.Constants;
import com.swak.config.jdbc.database.DataSourceProperties;
import com.swak.config.jdbc.database.HikariDataSourceAutoConfiguration;
import com.swak.utils.Maps;
import com.swak.utils.PropertyKit;

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

	@Autowired
	private ShardingJdbcShardingRuleConfigurationProperties shardingProperties;
	@Autowired
	private ShardingJdbcMasterSlaveRuleConfigurationProperties masterSlaveProperties;
	@Autowired
	private DataSourceProperties properties;

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
		try {
			DataSource dataSource = null == masterSlaveProperties.getMasterDataSourceName()
					? ShardingDataSourceFactory.createDataSource(dataSourceMap,
							shardingProperties.getShardingRuleConfiguration(), shardingProperties.getConfigMap(),
							shardingProperties.getProps())
					: MasterSlaveDataSourceFactory.createDataSource(dataSourceMap,
							masterSlaveProperties.getMasterSlaveRuleConfiguration(),
							masterSlaveProperties.getConfigMap(), masterSlaveProperties.getProps());
			return dataSource;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
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
				Map<String, Object> dataSourceProps = PropertyKit.handle(environment, prefix + each, Map.class);
				DataSource dataSource = newHikariDataSource(dataSourceProps);
				dataSourceMap.put(each, dataSource);
			} catch (Exception ex) {
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
		DataSourceProperties properties = new DataSourceProperties(this.properties);
		if (dataSourceProps != null) {
			properties = Maps.toBean(dataSourceProps, properties);
		}
		return new HikariDataSourceAutoConfiguration(properties).hikariDataSource();
	}
}