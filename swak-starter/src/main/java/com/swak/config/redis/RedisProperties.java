package com.swak.config.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportProperties;

/**
 * 缓存属性配置
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.REDIS_PREFIX)
public class RedisProperties extends TransportProperties{

	private String hosts;
	private String password;
	
	// 本地缓存
	private int localPoolSize = 3;
	private String localName = "LOCAL";
	private int localHeadSize = 200; // 个
	private int localOffHeadMB = 5; // MB
	private int localDiskMB = 10;// MB
	private int localLiveSeconds = 60;
	private String localDiskPath = ".cache";
	
	public int getLocalPoolSize() {
		return localPoolSize;
	}
	public void setLocalPoolSize(int localPoolSize) {
		this.localPoolSize = localPoolSize;
	}
	public String getLocalDiskPath() {
		return localDiskPath;
	}
	public void setLocalDiskPath(String localDiskPath) {
		this.localDiskPath = localDiskPath;
	}
	public int getLocalLiveSeconds() {
		return localLiveSeconds;
	}
	public void setLocalLiveSeconds(int localLiveSeconds) {
		this.localLiveSeconds = localLiveSeconds;
	}
	public String getLocalName() {
		return localName;
	}
	public void setLocalName(String localName) {
		this.localName = localName;
	}
	public int getLocalHeadSize() {
		return localHeadSize;
	}
	public int getLocalOffHeadMB() {
		return localOffHeadMB;
	}
	public int getLocalDiskMB() {
		return localDiskMB;
	}
	public void setLocalHeadSize(int localHeadSize) {
		this.localHeadSize = localHeadSize;
	}
	public void setLocalOffHeadMB(int localOffHeadMB) {
		this.localOffHeadMB = localOffHeadMB;
	}
	public void setLocalDiskMB(int localDiskMB) {
		this.localDiskMB = localDiskMB;
	}
	public String getHosts() {
		return hosts;
	}
	public void setHosts(String hosts) {
		this.hosts = hosts;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
