package com.swak.reactivex.transport.http.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportMode;

import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;

/**
 * 服务器的默认配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.HTTP_SERVER_PREFIX)
public class HttpServerProperties {

	private String name = "SWAK-HTTP-SERVER";
	private TransportMode mode = TransportMode.NIO;
	private int port = 8888;
	private int connectTimeout = 30000;
	private String host = "localhost";
	private boolean tcpNoDelay = true;
	private boolean soKeepAlive = true;
	private boolean startReport = false;
	private boolean enableGzip = false; // 暂时不支持
	private boolean enableCors = false; // 暂时不支持

	// 线程数量
	private int serverSelect = -1; // 自动计算
	private int serverWorker = -1; // 自动计算
	private int workerThreads = 20; // 默认 20 和数据库连接池一样

	// 支持ssl
	private boolean sslOn = false;
	private String certFilePath;
	private String privateKeyPath;
	private String privateKeyPassword;

	// 文件上传
	private boolean deleteOnExitTemporaryFile = true;
	private String baseDirectory = null;

	public int getServerSelect() {
		return serverSelect;
	}

	public void setServerSelect(int serverSelect) {
		this.serverSelect = serverSelect;
	}

	public int getServerWorker() {
		return serverWorker;
	}

	public void setServerWorker(int serverWorker) {
		this.serverWorker = serverWorker;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TransportMode getMode() {
		return mode;
	}

	public void setMode(TransportMode mode) {
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

	public boolean isDeleteOnExitTemporaryFile() {
		return deleteOnExitTemporaryFile;
	}

	public void setDeleteOnExitTemporaryFile(boolean deleteOnExitTemporaryFile) {
		this.deleteOnExitTemporaryFile = deleteOnExitTemporaryFile;
		DiskFileUpload.deleteOnExitTemporaryFile = true;
		DiskAttribute.deleteOnExitTemporaryFile = true;
	}

	public String getBaseDirectory() {
		return baseDirectory;
	}

	public void setBaseDirectory(String baseDirectory) {
		this.baseDirectory = baseDirectory;
		DiskFileUpload.baseDirectory = null;
		DiskAttribute.baseDirectory = null;
	}
}
