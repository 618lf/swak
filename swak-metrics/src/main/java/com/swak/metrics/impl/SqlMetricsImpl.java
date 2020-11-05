package com.swak.metrics.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.swak.codec.Digests;
import com.swak.meters.SqlMetrics;

/**
 * Sql 统计的指标
 * 
 * @author lifeng
 * @date 2020年11月5日 下午7:57:46
 */
public class SqlMetricsImpl extends AbstractMetrics implements SqlMetrics<Timer.Context> {

	private static ConcurrentMap<String, SqlMetrics<Timer.Context>> METRICS = new ConcurrentHashMap<>();

	private final String id;
	private final String sql;
	private final Timer time;
	private final Counter inuse;
	private final Counter count;

	/**
	 * 获得一个Sql统计项
	 * 
	 * @param registry 监控实体
	 * @param sql      需要监控的Sql
	 * @return 指标统计器
	 */
	public static SqlMetrics<Timer.Context> get(MetricRegistry registry, String sql) {
		String id = Digests.md5(sql);
		SqlMetrics<Timer.Context> metric = METRICS.get(Digests.md5(sql));
		if (metric == null) {
			metric = METRICS.computeIfAbsent(id, (key) -> {
				return new SqlMetricsImpl(registry, sql, id);
			});
		}
		return metric;
	}

	protected SqlMetricsImpl(MetricRegistry registry, String sql, String id) {
		super(registry, id);
		this.id = id;
		this.sql = sql;
		this.time = this.timer("usage");
		this.count = this.counter("count");
		this.inuse = this.counter("in-use");
	}

	@Override
	public Timer.Context begin() {
		if (inuse != null) {
			inuse.inc();
		}
		if (time != null) {
			return time.time();
		}
		return null;
	}

	@Override
	public void end(Context t, boolean succeeded) {
		if (t != null) {
			t.stop();
		}
		if (inuse != null) {
			inuse.dec();
		}
		if (count != null) {
			count.inc();
		}
	}

	@Override
	public String id() {
		return id;
	}

	@Override
	public String sql() {
		return sql;
	}
}