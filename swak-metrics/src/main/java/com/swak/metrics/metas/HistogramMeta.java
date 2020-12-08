package com.swak.metrics.metas;

public class HistogramMeta extends ServerMeta{

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

}
