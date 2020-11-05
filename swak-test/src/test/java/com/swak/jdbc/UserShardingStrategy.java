package com.swak.jdbc;

import com.swak.async.persistence.SqlParam;
import com.swak.async.persistence.sharding.ShardingStrategy;

/**
 * 用户分片
 * 
 * @author lifeng
 * @date 2020年10月13日 下午11:21:36
 */
public class UserShardingStrategy implements ShardingStrategy {

	@Override
	public <T> String sharding(SqlParam<T> param) {
		Long id = param.getFieldValue("id");
		return param.table.name + "_" + (id % 24);
	}
}
