package com.swak.meters;

/**
 * Sql 指标统计
 * 
 * @author lifeng
 * @date 2020年11月5日 下午7:53:29
 */
public interface SqlMetrics<T> extends Metrics {

	String id();

	String sql();

	/**
	 * 开始记录
	 *
	 * @return 统计对象
	 */
	default T begin() {
		return null;
	}

	/**
	 * 结束
	 *
	 * @param t         结束对象
	 * @param succeeded 结果
	 */
	default void end(T t, boolean succeeded) {
	}
}