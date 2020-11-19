package com.swak.metrics.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;

import com.swak.App;
import com.swak.meters.Metrics;

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

	@Override
	public void trace(Marker marker, String msg) {
		delegate().trace(marker, this.format(msg));
	}

	@Override
	public void debug(Marker marker, String msg) {
		delegate().trace(marker, this.format(msg));
	}

	@Override
	public void info(Marker marker, String msg) {
		delegate().info(marker, this.format(msg));
	}

	@Override
	public void warn(Marker marker, String msg) {
		delegate().warn(marker, this.format(msg));
	}

	@Override
	public void error(Marker marker, String msg) {
		delegate().error(marker, this.format(msg));
	}

	/**
	 * 格式化输出
	 * 
	 * @param msg
	 * @return
	 */
	protected String format(String msg) {
		return new StringBuilder("server=").append(App.me().getServerSn()).append(", ").append(msg).toString();
	}

	/**
	 * 用于指标输出
	 * 
	 * @return
	 */
	public static MetricsLogger me() {
		return new MetricsLogger(LoggerFactory.getLogger(Metrics.class));
	}
}