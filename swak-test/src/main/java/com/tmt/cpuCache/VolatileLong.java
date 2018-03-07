package com.tmt.cpuCache;

public class VolatileLong {
	private volatile long value;
	private long p1, p2, p3, p4, p5, p6, p7;
	public void set(long value) {
		this.value = value;
		p1 = p2 = p3 = p4 = p5 = p6 = p7 = value;
	}
	public long getValue() {
		return value;
	}
	public void setValue(long value) {
		this.value = value;
	}
	public long getP1() {
		return p1;
	}
	public void setP1(long p1) {
		this.p1 = p1;
	}
	public long getP2() {
		return p2;
	}
	public void setP2(long p2) {
		this.p2 = p2;
	}
	public long getP3() {
		return p3;
	}
	public void setP3(long p3) {
		this.p3 = p3;
	}
	public long getP4() {
		return p4;
	}
	public void setP4(long p4) {
		this.p4 = p4;
	}
	public long getP5() {
		return p5;
	}
	public void setP5(long p5) {
		this.p5 = p5;
	}
	public long getP6() {
		return p6;
	}
	public void setP6(long p6) {
		this.p6 = p6;
	}
	public long getP7() {
		return p7;
	}
	public void setP7(long p7) {
		this.p7 = p7;
	}
}
