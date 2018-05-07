package com.swak.reactivex.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务器的默认配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = HttpServerProperties.HTTP_SERVER_PREFIX)
public class HttpServerProperties {

	public static final String HTTP_SERVER_PREFIX = "spring.http-server";

	private String name = "SWAK-REACTIVE-SERVER";
	private String mode = "NIO";
	private int port = 8888;
	private int connectTimeout = 30000;
	private String host = "localhost";
	private boolean tcpNoDelay = true;
	private boolean soKeepAlive = true;
	private boolean startReport = false;
	private boolean enableGzip = false; // 暂时不支持
	private boolean enableCors = false; // 暂时不支持

	// 支持ssl
	private boolean sslOn = false;
	private String certFilePath;
	private String privateKeyPath;
	private String privateKeyPassword;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean isTcpNoDelay() {
		return tcpNoDelay;
	}

	public void setTcpNoDelay(boolean tcpNoDelay) {
		this.tcpNoDelay = tcpNoDelay;
	}

	public boolean isSoKeepAlive() {
		return soKeepAlive;
	}

	public void setSoKeepAlive(boolean soKeepAlive) {
		this.soKeepAlive = soKeepAlive;
	}

	public boolean isStartReport() {
		return startReport;
	}

	public void setStartReport(boolean startReport) {
		this.startReport = startReport;
	}

	public boolean isEnableGzip() {
		return enableGzip;
	}

	public void setEnableGzip(boolean enableGzip) {
		this.enableGzip = enableGzip;
	}

	public boolean isEnableCors() {
		return enableCors;
	}

	public void setEnableCors(boolean enableCors) {
		this.enableCors = enableCors;
	}

	public boolean isSslOn() {
		return sslOn;
	}

	public void setSslOn(boolean sslOn) {
		this.sslOn = sslOn;
	}

	public String getCertFilePath() {
		return certFilePath;
	}

	public void setCertFilePath(String certFilePath) {
		this.certFilePath = certFilePath;
	}

	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public String getPrivateKeyPassword() {
		return privateKeyPassword;
	}

	public void setPrivateKeyPassword(String privateKeyPassword) {
		this.privateKeyPassword = privateKeyPassword;
	}
}
