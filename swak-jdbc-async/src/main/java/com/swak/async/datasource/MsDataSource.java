package com.swak.async.datasource;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;

import com.swak.loadbalance.LoadBalance;
import com.swak.loadbalance.impl.RoundRobinLoadBalance;
import com.swak.persistence.MS;
import com.swak.utils.Lists;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.sqlclient.SqlConnection;

/**
 * 主从数据源
 * 
 * @author lifeng
 * @date 2020年10月13日 下午9:05:48
 */
public class MsDataSource extends DataSource implements InitializingBean {

	DataSource master;
	List<DataSource> slaves = Lists.newArrayList();
	LoadBalance<DataSource> loadBalance = new RoundRobinLoadBalance<>();

	public MsDataSource(DataSource master, List<DataSource> slaves) {
		this.master = master;
		this.slaves = slaves;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.loadBalance.onRefresh(slaves);
	}

	@Override
	public void getConnection(MS type, Handler<AsyncResult<SqlConnection>> handler) {
		if (type == null || type == MS.Master) {
			this.master.getConnection(type, handler);
		} else {
			this.loadBalance.select().getConnection(type, handler);
		}
	}
}
