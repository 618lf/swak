package com.swak.reactivex.transport.http.server;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportProperties;

import io.netty.handler.codec.http.multipart.DiskAttribute;
import io.netty.handler.codec.http.multipart.DiskFileUpload;
import io.netty.handler.logging.LogLevel;
import io.netty.util.ResourceLeakDetector.Level;

/**
 * 服务器的默认配置
 * 
 * @author lifeng
 */
@ConfigurationProperties(prefix = Constants.HTTP_SERVER_PREFIX)
public class HttpServerProperties extends TransportProperties {

	private String name = "SWAK-HTTP-SERVER";
	private LogLevel serverLogLevel = null;
	private Level leakDetectionLevel = Level.DISABLED;
	private boolean threadCache = true;
	private int port = 8888;
	private int connectTimeout = 30000;
	private int readTimeout = -1;
	private int writeTimeout = -1;
	private String host = null; // 设置这个回导致只能通过网卡IP或本机IP访问 或 0.0.0.0
	private boolean tcpNoDelay = true;
	private boolean soKeepAlive = true;
	private boolean startReport = false;
	private boolean enableGzip = false;
	private boolean enableCors = false;

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

	// 静态资源
	private String[] statics;

	// 授权
	private String keyStorePath; // keyStore 的路径
	private String keyStorePass = "secret"; // keyStore 的密码
	private String jwtTokenName = "X-Token";

	public int getReadTimeout() {
		return readTimeout;
	}

	public void setReadTimeout(int readTimeout) {
		this.readTimeout = readTimeout;
	}

	public int getWriteTimeout() {
		return writeTimeout;
	}

	public void setWriteTimeout(int writeTimeout) {
		this.writeTimeout = writeTimeout;
	}

	public String[] getStatics() {
		return statics;
	}

	public void setStatics(String[] statics) {
		this.statics = statics;
	}

	public String getKeyStorePath() {
		return keyStorePath;
	}

	public void setKeyStorePath(String keyStorePath) {
		this.keyStorePath = keyStorePath;
	}

	public String getKeyStorePass() {
		return keyStorePass;
	}

	public void setKeyStorePass(String keyStorePass) {
		this.keyStorePass = keyStorePass;
	}

	public String getJwtTokenName() {
		return jwtTokenName;
	}

	public void setJwtTokenName(String jwtTokenName) {
		this.jwtTokenName = jwtTokenName;
	}

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

	public LogLevel getServerLogLevel() {
		return serverLogLevel;
	}

	public void setServerLogLevel(LogLevel serverLogLevel) {
		this.serverLogLevel = serverLogLevel;
	}
	
	public Level getLeakDetectionLevel() { 
		return leakDetectionLevel;
	}

	public void setLeakDetectionLevel(Level leakDetectionLevel) {
		this.leakDetectionLevel = leakDetectionLevel;
	}

	public boolean isThreadCache() {
		return threadCache;
	}

	public void setThreadCache(boolean threadCache) {
		this.threadCache = threadCache;
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
