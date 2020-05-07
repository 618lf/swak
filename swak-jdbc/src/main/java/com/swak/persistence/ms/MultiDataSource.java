package com.swak.persistence.ms;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.InitializingBean;

import com.swak.loadbalance.LoadBalance;
import com.swak.loadbalance.impl.RoundRobinLoadBalance;
import com.swak.utils.Lists;

/**
 * 支持多数据源
 * 
 * @author lifeng
 * @date 2020年4月29日 下午5:34:53
 */
public class MultiDataSource extends DataSourceWraper implements InitializingBean {

	/**
	 * 数据源
	 */
	List<DataSource> dataSources;

	/**
	 * 负载均衡算法 -- 默认使用轮询
	 */
	LoadBalance<DataSource> loadBalance = new RoundRobinLoadBalance<>();

	/**
	 * 添加数据源
	 * 
	 * @param dataSource
	 * @return
	 */
	public MultiDataSource addDataSource(DataSource dataSource) {
		if (this.dataSources == null) {
			this.dataSources = Lists.newArrayList();
		}
		this.dataSources.add(dataSource);
		return this;
	}

	/**
	 * 设置数据源
	 * 
	 * @param dataSources
	 */
	public void setDataSources(List<DataSource> dataSources) {
		this.dataSources = dataSources;
	}

	/**
	 * 设置负载均衡算法
	 * 
	 * @param loadBalance
	 */
	public void setLoadBalance(LoadBalance<DataSource> loadBalance) {
		this.loadBalance = loadBalance;
	}

	/**
	 * 初始化负载均衡算法
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		loadBalance.onRefresh(dataSources);
	}

	/**
	 * 选择的数据源
	 */
	public DataSource loadBalance() {
		return loadBalance.select();
	}
}