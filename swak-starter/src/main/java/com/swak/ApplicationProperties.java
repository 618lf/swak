package com.swak;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 系统配置
 *
 * @author: lifeng
 * @date: 2020/4/1 12:34
 */
@ConfigurationProperties(prefix = Constants.APPLICATION_PREFIX)
public class ApplicationProperties {

	/**
	 * 功能项
	 */
	private boolean enableRedis = true;
	private boolean enableEventBus = true;
	private boolean enableSecurity = true;
	private boolean enableSession = true;
	private boolean enableBooter = true;
	private boolean enableDataBase = true;
	private boolean enableMybatis = true;
	private boolean enableHttpClient = true;
	private boolean enableWorkers = true;
	private boolean enableActuator = true;

	private String serverSn = "server-1-1";
	private String serialization = "kryo_pool";

	public boolean isEnableActuator() {
		return enableActuator;
	}

	public void setEnableActuator(boolean enableActuator) {
		this.enableActuator = enableActuator;
	}

	public boolean isEnableHttpClient() {
		return enableHttpClient;
	}

	public void setEnableHttpClient(boolean enableHttpClient) {
		this.enableHttpClient = enableHttpClient;
	}

	public boolean isEnableWorkers() {
		return enableWorkers;
	}

	public void setEnableWorkers(boolean enableWorkers) {
		this.enableWorkers = enableWorkers;
	}

	public boolean isEnableSession() {
		return enableSession;
	}

	public void setEnableSession(boolean enableSession) {
		this.enableSession = enableSession;
	}

	public boolean isEnableRedis() {
		return enableRedis;
	}

	public void setEnableRedis(boolean enableRedis) {
		this.enableRedis = enableRedis;
	}

	public boolean isEnableEventBus() {
		return enableEventBus;
	}

	public void setEnableEventBus(boolean enableEventBus) {
		this.enableEventBus = enableEventBus;
	}

	public boolean isEnableSecurity() {
		return enableSecurity;
	}

	public void setEnableSecurity(boolean enableSecurity) {
		this.enableSecurity = enableSecurity;
	}

	public boolean isEnableBooter() {
		return enableBooter;
	}

	public void setEnableBooter(boolean enableBooter) {
		this.enableBooter = enableBooter;
	}

	public boolean isEnableDataBase() {
		return enableDataBase;
	}

	public void setEnableDataBase(boolean enableDataBase) {
		this.enableDataBase = enableDataBase;
	}

	public boolean isEnableMybatis() {
		return enableMybatis;
	}

	public void setEnableMybatis(boolean enableMybatis) {
		this.enableMybatis = enableMybatis;
	}

	public String getServerSn() {
		return serverSn;
	}

	public void setServerSn(String serverSn) {
		this.serverSn = serverSn;
	}

	public String getSerialization() {
		return serialization;
	}

	public void setSerialization(String serialization) {
		this.serialization = serialization;
	}
}