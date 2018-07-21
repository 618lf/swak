package com.swak.motan;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * motan rpc 的配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.MOTAN_PREFIX)
public class MotanProperties {

	// 扫描的包
	private String annotationPackage;
	private boolean protocolDefault = true;
	private String protocolName = "motan";
	private int protocolMaxContentLength = 1048576;
	
	private String registryProtocol = "direct";
	private String registryAddress;

	private String refererProtocol = "motan";
	private String refererGroup = "motan-rpc";
	private String refererModule = "motan-rpc";
	private String refererApplication = "motan-rpc";
	private String refererRegistry = "registry";
	private boolean refererCheck = true;
	private boolean refererAccessLog = true;
	private int refererRetries = 2;
	private boolean refererThrowException = true;
	public String getAnnotationPackage() {
		return annotationPackage;
	}
	public void setAnnotationPackage(String annotationPackage) {
		this.annotationPackage = annotationPackage;
	}
	public boolean isProtocolDefault() {
		return protocolDefault;
	}
	public void setProtocolDefault(boolean protocolDefault) {
		this.protocolDefault = protocolDefault;
	}
	public String getProtocolName() {
		return protocolName;
	}
	public void setProtocolName(String protocolName) {
		this.protocolName = protocolName;
	}
	public int getProtocolMaxContentLength() {
		return protocolMaxContentLength;
	}
	public void setProtocolMaxContentLength(int protocolMaxContentLength) {
		this.protocolMaxContentLength = protocolMaxContentLength;
	}
	public String getRegistryProtocol() {
		return registryProtocol;
	}
	public void setRegistryProtocol(String registryProtocol) {
		this.registryProtocol = registryProtocol;
	}
	public String getRegistryAddress() {
		return registryAddress;
	}
	public void setRegistryAddress(String registryAddress) {
		this.registryAddress = registryAddress;
	}
	public String getRefererProtocol() {
		return refererProtocol;
	}
	public void setRefererProtocol(String refererProtocol) {
		this.refererProtocol = refererProtocol;
	}
	public String getRefererGroup() {
		return refererGroup;
	}
	public void setRefererGroup(String refererGroup) {
		this.refererGroup = refererGroup;
	}
	public String getRefererModule() {
		return refererModule;
	}
	public void setRefererModule(String refererModule) {
		this.refererModule = refererModule;
	}
	public String getRefererApplication() {
		return refererApplication;
	}
	public void setRefererApplication(String refererApplication) {
		this.refererApplication = refererApplication;
	}
	public String getRefererRegistry() {
		return refererRegistry;
	}
	public void setRefererRegistry(String refererRegistry) {
		this.refererRegistry = refererRegistry;
	}
	public boolean isRefererCheck() {
		return refererCheck;
	}
	public void setRefererCheck(boolean refererCheck) {
		this.refererCheck = refererCheck;
	}
	public boolean isRefererAccessLog() {
		return refererAccessLog;
	}
	public void setRefererAccessLog(boolean refererAccessLog) {
		this.refererAccessLog = refererAccessLog;
	}
	public int getRefererRetries() {
		return refererRetries;
	}
	public void setRefererRetries(int refererRetries) {
		this.refererRetries = refererRetries;
	}
	public boolean isRefererThrowException() {
		return refererThrowException;
	}
	public void setRefererThrowException(boolean refererThrowException) {
		this.refererThrowException = refererThrowException;
	}
}