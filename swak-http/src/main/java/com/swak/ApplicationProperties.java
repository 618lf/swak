package com.swak;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.common.Constants;

/**
 * 系统配置
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.APPLICATION_PREFIX)
public class ApplicationProperties {

	// 功能项
	private boolean enableRedis = true;
	private boolean enableEventBus = true;
	private boolean enableSecurity = true;
	private boolean enableBooter = true;
	private boolean enableDataBase = true;
	private boolean enableMybatis = true;
	
	// 系统配置
	private String serverSn;
	private String serialization;
	
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