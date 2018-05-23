package com.tmt.reactor.b;

public class PriceTick {
	public PriceTick(String name, boolean last) {
		this.name = name;
		this.last = last;
	}

	private String name;
	private boolean last;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isLast() {
		return last;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public String toString() {
		return name;
	}
}
