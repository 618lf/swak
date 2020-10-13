package com.swak.async.sharding;

import com.swak.async.persistence.SqlParam;

/**
 * 分库分表的策略
 * 
 * @author lifeng
 * @date 2020年10月13日 上午10:21:32
 */
public interface ShardingStrategy {

	/**
	 * 执行分片
	 * 
	 * @param <T>
	 * @param param
	 * @return
	 */
	<T> String sharding(SqlParam<T> param);

}