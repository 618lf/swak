package com.swak.config.zookeeper;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.utils.StringUtils;

/**
 * zookeeper 的配置
 * 
 * @author lifeng
 * @date 2020年9月15日 下午8:36:09
 */
@ConfigurationProperties(prefix = Constants.ZOOKEEPER_PREFIX)
public class ZookeeperProperties {
	private String username;
	private String password;
	private int timeout = 5 * 1000;
	private int sessionExpireMs = 60 * 1000;
	private String address;

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public int getSessionExpireMs() {
		return sessionExpireMs;
	}

	public void setSessionExpireMs(int sessionExpireMs) {
		this.sessionExpireMs = sessionExpireMs;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getAuthority() {
		if (StringUtils.isEmpty(username) && StringUtils.isEmpty(password)) {
			return null;
		}
		return (username == null ? "" : username) + ":" + (password == null ? "" : password);
	}
}
