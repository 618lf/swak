package com.swak.rocketmq.annotation;

public enum MessageModel {
	BROADCASTING("BROADCASTING"), CLUSTERING("CLUSTERING");

	private final String modeCN;

	MessageModel(String modeCN) {
		this.modeCN = modeCN;
	}

	public String getModeCN() {
		return this.modeCN;
	}
}