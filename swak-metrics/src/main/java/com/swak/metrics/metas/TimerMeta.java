package com.swak.metrics.metas;

public class TimerMeta {
	private String type;
	private String name;
	private Long count;
	private Double min;
	private Double max;
	private Double mean;
	private Double stddev;
	private Double p50;
	private Double p75;
	private Double p95;
	private Double p98;
	private Double p99;
	private Double p999;
	private Double m1_rate;
	private Double m5_rate;
	private Double m15_rate;
	private Double mean_rate;
	private Double rate_unit;
	private Double duration_unit;

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

	public Double getMin() {
		return min;
	}

	public void setMin(Double min) {
		this.min = min;
	}

	public Double getMax() {
		return max;
	}

	public void setMax(Double max) {
		this.max = max;
	}

	public Double getMean() {
		return mean;
	}

	public void setMean(Double mean) {
		this.mean = mean;
	}

	public Double getStddev() {
		return stddev;
	}

	public void setStddev(Double stddev) {
		this.stddev = stddev;
	}

	public Double getP50() {
		return p50;
	}

	public void setP50(Double p50) {
		this.p50 = p50;
	}

	public Double getP75() {
		return p75;
	}

	public void setP75(Double p75) {
		this.p75 = p75;
	}

	public Double getP95() {
		return p95;
	}

	public void setP95(Double p95) {
		this.p95 = p95;
	}

	public Double getP98() {
		return p98;
	}

	public void setP98(Double p98) {
		this.p98 = p98;
	}

	public Double getP99() {
		return p99;
	}

	public void setP99(Double p99) {
		this.p99 = p99;
	}

	public Double getP999() {
		return p999;
	}

	public void setP999(Double p999) {
		this.p999 = p999;
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

	public Double getRate_unit() {
		return rate_unit;
	}

	public void setRate_unit(Double rate_unit) {
		this.rate_unit = rate_unit;
	}

	public Double getDuration_unit() {
		return duration_unit;
	}

	public void setDuration_unit(Double duration_unit) {
		this.duration_unit = duration_unit;
	}
}
