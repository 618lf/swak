package com.swak.rocketmq.annotation;

public enum ConsumeMode {
	/**
	 * Receive asynchronously delivered messages concurrently
	 */
	CONCURRENTLY,

	/**
	 * Receive asynchronously delivered messages orderly. one queue, one thread
	 */
	ORDERLY
}