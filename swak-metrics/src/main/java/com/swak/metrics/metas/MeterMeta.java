package com.swak.metrics.metas;

public class MeterMeta extends ServerMeta {
	private String type;
	private String name;
	private Long count;
	private Double m1_rate;
	private Double m5_rate;
	private Double m15_rate;
	private Double mean_rate;
	private String rate_unit;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCount() {
		return count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public Double getM1_rate() {
		return m1_rate;
	}

	public void setM1_rate(Double m1_rate) {
		this.m1_rate = m1_rate;
	}

	public Double getM5_rate() {
		return m5_rate;
	}

	public void setM5_rate(Double m5_rate) {
		this.m5_rate = m5_rate;
	}

	public Double getM15_rate() {
		return m15_rate;
	}

	public void setM15_rate(Double m15_rate) {
		this.m15_rate = m15_rate;
	}

	public Double getMean_rate() {
		return mean_rate;
	}

	public void setMean_rate(Double mean_rate) {
		this.mean_rate = mean_rate;
	}

	public String getRate_unit() {
		return rate_unit;
	}

	public void setRate_unit(String rate_unit) {
		this.rate_unit = rate_unit;
	}
}
