package com.swak.config.jdbc;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 数据库的配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.DATASOURCE_PREFIX)
public class AsyncDataSourceProperties {

	private String host;
	private Integer port;
	private String username;
	private String password;
	private String database;
	private Integer maxActive = 20;

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Integer getMaxActive() {
		return maxActive;
	}

	public void setMaxActive(Integer maxActive) {
		this.maxActive = maxActive;
	}
}
