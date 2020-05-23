package com.swak.persistence.datasource;

import java.util.Map;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import com.swak.persistence.ms.MultiDataSource;

/**
 * 通过线程来切换动态的数据源，如果之前开启了事务则，切换无效 <br>
 * 不能在事务中切换数据源， 默认是没有使用的，如果需要简单的多数据源则可以使用
 * 
 * @author lifeng
 */
public class DynamicDataSource extends AbstractRoutingDataSource {

	public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources) {
		super.setDefaultTargetDataSource(defaultTargetDataSource);
		super.setTargetDataSources(targetDataSources);
	}

	/**
	 * 设置查找的KEY
	 */
	@Override
	protected Object determineCurrentLookupKey() {
		return DataSourceHolder.getDataSourceType();
	}

	/**
	 * 查找数据源: 如果内部是多数据源则使用负载均衡算法选择一个真实的数据源
	 */
	@Override
	protected DataSource determineTargetDataSource() {
		DataSource dataSource = super.determineTargetDataSource();
		if (dataSource instanceof MultiDataSource) {
			return ((MultiDataSource) dataSource).loadBalance();
		}
		return dataSource;
	}
}
