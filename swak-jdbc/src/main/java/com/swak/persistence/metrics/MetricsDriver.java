package com.swak.persistence.metrics;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import com.alibaba.druid.util.Utils;
import com.swak.persistence.JDBCDrivers;
import com.swak.persistence.metrics.wrapper.ConnectionWrapper;

public class MetricsDriver implements Driver {

	private static MetricsDriver instance = new MetricsDriver();
	private AtomicReference<Driver> rawDriver = new AtomicReference<Driver>();

	static {
		try {
			DriverManager.registerDriver(MetricsDriver.instance);
		} catch (SQLException e) {
			throw new IllegalStateException("Could not register MetricsDriver with DriverManager", e);
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) {
			return null;
		}
		return ConnectionWrapper.wrap(getDriver(url, info).connect(url, info), MetricsCollector.getMetricsFactory());
	}

	private Driver getDriver(String url, Properties info) throws SQLException {

		if (rawDriver.get() != null) {
			return rawDriver.get();
		}

		Driver rawDriver = this.createDriver(extractRealUrl(url));

		this.rawDriver.compareAndSet(null, rawDriver);

		return rawDriver;
	}

	private Driver createDriver(String rawUrl) throws SQLException {

		String className = JDBCDrivers.getDriverClassName(rawUrl);

		Class<?> rawDriverClass = Utils.loadClass(className);

		if (rawDriverClass == null) {
			throw new SQLException("jdbc-driver's class not found. '" + className + "'");
		}

		Driver rawDriver;
		try {
			rawDriver = (Driver) rawDriverClass.newInstance();
		} catch (InstantiationException e) {
			throw new SQLException("create driver instance error, driver className '" + className + "'", e);
		} catch (IllegalAccessException e) {
			throw new SQLException("create driver instance error, driver className '" + className + "'", e);
		}

		return rawDriver;
	}

	@Override
	public boolean acceptsURL(String url) throws SQLException {
		return url != null && url.startsWith("Metrics:");
	}

	/**
	 * Parses out the real JDBC connection URL by removing "p6spy:".
	 *
	 * @param url the connection URL
	 * @return the parsed URL
	 * @throws SQLException
	 */
	private String extractRealUrl(String url) throws SQLException {
		return acceptsURL(url) ? url.replace("Metrics:", "") : url;
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		return getDriver(url, info).getPropertyInfo(url, info);
	}

	@Override
	public int getMajorVersion() {
		return rawDriver.get().getMajorVersion();
	}

	@Override
	public int getMinorVersion() {
		return rawDriver.get().getMinorVersion();
	}

	@Override
	public boolean jdbcCompliant() {
		return true;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException("Feature not supported");
	}
}