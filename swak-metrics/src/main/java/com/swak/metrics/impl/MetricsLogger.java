package com.swak.metrics.impl;

import org.slf4j.Logger;

/**
 * 自动在统计信息中添加服务器信息
 * 
 * @author lifeng
 * @date 2020年11月19日 下午5:53:55
 */
public class MetricsLogger extends LoggerDelegate {

	public MetricsLogger(Logger delegate) {
		super(delegate);
	}
}