package com.swak.metrics.metas;

import java.util.HashMap;

import com.codahale.metrics.MetricAttribute;
import com.swak.App;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

/**
 * 万能的指标收集
 * 
 * @author lifeng
 * @date 2020年12月8日 下午4:01:23
 */
public class MapMeta extends HashMap<String, Object> {

	private static final long serialVersionUID = 1L;
	public static final String SERVER = "server";
	public static final String TYPE = "type";
	public static final String NAME = "name";
	public static final String RATE_UNIT = "rate_unit";
	public static final String DURATION_UNIT = "duration_unit";
	public static final String VALUE = "value";
	public static final String MAX = MetricAttribute.MAX.getCode();
	public static final String MEAN = MetricAttribute.MEAN.getCode();
	public static final String MIN = MetricAttribute.MIN.getCode();
	public static final String STDDEV = MetricAttribute.STDDEV.getCode();
	public static final String P50 = MetricAttribute.P50.getCode();
	public static final String P75 = MetricAttribute.P75.getCode();
	public static final String P95 = MetricAttribute.P95.getCode();
	public static final String P98 = MetricAttribute.P98.getCode();
	public static final String P99 = MetricAttribute.P99.getCode();
	public static final String P999 = MetricAttribute.P999.getCode();
	public static final String COUNT = MetricAttribute.COUNT.getCode();
	public static final String M1_RATE = MetricAttribute.M1_RATE.getCode();
	public static final String M5_RATE = MetricAttribute.M5_RATE.getCode();
	public static final String M15_RATE = MetricAttribute.M15_RATE.getCode();
	public static final String MEAN_RATE = MetricAttribute.MEAN_RATE.getCode();

	@Override
	public String toString() {
		return JsonMapper.toJson(this);
	}

	public CounterMeta toCounterMeta() {
		return Maps.toBean(this, new CounterMeta());
	}

	public GaugeMeta toGaugeMeta() {
		return Maps.toBean(this, new GaugeMeta());
	}

	public HistogramMeta toHistogramMeta() {
		return Maps.toBean(this, new HistogramMeta());
	}

	public MeterMeta toMeterMeta() {
		return Maps.toBean(this, new MeterMeta());
	}

	public TimerMeta toTimerMeta() {
		return Maps.toBean(this, new TimerMeta());
	}

	/**
	 * 转换为 MapMeta
	 * 
	 * @param json
	 * @return
	 */
	public static MapMeta from(String json) {
		return JsonMapper.fromJson(json, MapMeta.class);
	}

	/**
	 * 添加默认的属性
	 * 
	 * @return
	 */
	public static MapMeta of() {
		MapMeta meta = new MapMeta();
		meta.put("server", App.me().getServerSn());
		return meta;
	}
}