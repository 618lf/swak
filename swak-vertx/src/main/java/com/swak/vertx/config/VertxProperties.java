package com.swak.vertx.config;

import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportProperties;
import com.swak.utils.Maps;

import io.netty.handler.logging.LogLevel;
import io.netty.util.ResourceLeakDetector.Level;
import io.vertx.core.VertxOptions;
import io.vertx.core.file.FileSystemOptions;
import io.vertx.core.http.ClientAuth;

/**
 * Vertx 的属性配置
 *
 * @author: lifeng
 * @date: 2020/3/29 19:15
 */
@ConfigurationProperties(prefix = Constants.VERTX_SERVER_PREFIX)
public class VertxProperties extends TransportProperties {

	private String host = null;
	private int port = 8888;
	private int webSocketPort = 8889;

	/**
	 * 配置是否开启：WebSocket， 如果配置了@ImApi则会开启支持
	 * 
	 */
	private boolean enableWebsocket = false;

	/**
	 * http2 需要 配置ssl ，服务的 openssl版本在1.0.2+ 以上才支持 Http2
	 */
	private boolean enableHttp2 = false;
	private LogLevel serverLogLevel = null;
	private Level leakDetectionLevel = Level.DISABLED;
	private boolean threadCache = true;
	private int eventLoopPoolSize = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;
	private int internalBlockingThreads = 2;
	private int workerThreads = 10;
	private Map<String, Integer> workers = Maps.newHashMap();
	private boolean metricAble = true;
	private long maxEventLoopExecuteTime = VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME;
	private long maxWorkerExecuteTime = VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;
	private int sendTimeout = 5 * 60 * 1000;
	private String uploadDirectory = "uploads";
	private int bodyLimit = -1;
	private boolean deleteUploadedFilesOnEnd = false;
	private String rootPath = "io.vertx";
	private int sessionTimeout = 20000;
	private int connectTimeout = 3000;
	private int retryInitialSleepTime = 1000;
	private int retryIntervalTimes = 10000;
	private int retryMaxTimes = 5;

	/**
	 * 权限验证
	 */
	private String keyStorePath;
	private String keyStorePass = "secret";
	private String keyStoreAlgorithm = "HS256";
	private String jwtTokenName = Constants.TOKEN_NAME;

	/**
	 * 开启压缩
	 */
	private boolean compressionSupported;
	private int compressionLevel = -1;

	/**
	 * 设置服务器证书
	 */
	private boolean useSsl;
	private boolean useAlpn;
	private List<String> keyPaths;
	private List<String> certPaths;
	private ClientAuth clientAuth = ClientAuth.NONE;

	/**
	 * 文件处理
	 */
	private boolean classPathResolvingEnabled = false; // FileSystemOptions.DEFAULT_CLASS_PATH_RESOLVING_ENABLED;
	private boolean fileCachingEnabled = false; // FileSystemOptions.DEFAULT_FILE_CACHING_ENABLED;
	private String fileCacheDir = FileSystemOptions.DEFAULT_FILE_CACHING_DIR;

	public boolean isClassPathResolvingEnabled() {
		return classPathResolvingEnabled;
	}

	public void setClassPathResolvingEnabled(boolean classPathResolvingEnabled) {
		this.classPathResolvingEnabled = classPathResolvingEnabled;
	}

	public boolean isFileCachingEnabled() {
		return fileCachingEnabled;
	}

	public void setFileCachingEnabled(boolean fileCachingEnabled) {
		this.fileCachingEnabled = fileCachingEnabled;
	}

	public String getFileCacheDir() {
		return fileCacheDir;
	}

	public void setFileCacheDir(String fileCacheDir) {
		this.fileCacheDir = fileCacheDir;
	}

	public ClientAuth getClientAuth() {
		return clientAuth;
	}

	public void setClientAuth(ClientAuth clientAuth) {
		this.clientAuth = clientAuth;
	}

	public boolean isUseSsl() {
		return useSsl;
	}

	public void setUseSsl(boolean useSsl) {
		this.useSsl = useSsl;
	}

	public boolean isUseAlpn() {
		return useAlpn;
	}

	public void setUseAlpn(boolean useAlpn) {
		this.useAlpn = useAlpn;
	}

	public List<String> getKeyPaths() {
		return keyPaths;
	}

	public void setKeyPaths(List<String> keyPaths) {
		this.keyPaths = keyPaths;
	}

	public List<String> getCertPaths() {
		return certPaths;
	}

	public void setCertPaths(List<String> certPaths) {
		this.certPaths = certPaths;
	}

	public boolean isCompressionSupported() {
		return compressionSupported;
	}

	public void setCompressionSupported(boolean compressionSupported) {
		this.compressionSupported = compressionSupported;
	}

	public int getCompressionLevel() {
		return compressionLevel;
	}

	public void setCompressionLevel(int compressionLevel) {
		this.compressionLevel = compressionLevel;
	}

	public boolean isEnableHttp2() {
		return enableHttp2;
	}

	public void setEnableHttp2(boolean enableHttp2) {
		this.enableHttp2 = enableHttp2;
	}

	public boolean isEnableWebsocket() {
		return enableWebsocket;
	}

	public void setEnableWebsocket(boolean enableWebsocket) {
		this.enableWebsocket = enableWebsocket;
	}

	public int getWebSocketPort() {
		return webSocketPort;
	}

	public void setWebSocketPort(int webSocketPort) {
		this.webSocketPort = webSocketPort;
	}

	public LogLevel getServerLogLevel() {
		return serverLogLevel;
	}

	public Level getLeakDetectionLevel() {
		return leakDetectionLevel;
	}

	public boolean isThreadCache() {
		return threadCache;
	}

	public void setServerLogLevel(LogLevel serverLogLevel) {
		this.serverLogLevel = serverLogLevel;
	}

	public void setLeakDetectionLevel(Level leakDetectionLevel) {
		this.leakDetectionLevel = leakDetectionLevel;
	}

	public void setThreadCache(boolean threadCache) {
		this.threadCache = threadCache;
	}

	public long getMaxEventLoopExecuteTime() {
		return maxEventLoopExecuteTime;
	}

	public long getMaxWorkerExecuteTime() {
		return maxWorkerExecuteTime;
	}

	public void setMaxEventLoopExecuteTime(long maxEventLoopExecuteTime) {
		this.maxEventLoopExecuteTime = maxEventLoopExecuteTime;
	}

	public void setMaxWorkerExecuteTime(long maxWorkerExecuteTime) {
		this.maxWorkerExecuteTime = maxWorkerExecuteTime;
	}

	public String getUploadDirectory() {
		return uploadDirectory;
	}

	public void setUploadDirectory(String uploadDirectory) {
		this.uploadDirectory = uploadDirectory;
	}

	public int getBodyLimit() {
		return bodyLimit;
	}

	public void setBodyLimit(int bodyLimit) {
		this.bodyLimit = bodyLimit;
	}

	public boolean isDeleteUploadedFilesOnEnd() {
		return deleteUploadedFilesOnEnd;
	}

	public void setDeleteUploadedFilesOnEnd(boolean deleteUploadedFilesOnEnd) {
		this.deleteUploadedFilesOnEnd = deleteUploadedFilesOnEnd;
	}

	public String getJwtTokenName() {
		return jwtTokenName;
	}

	public void setJwtTokenName(String jwtTokenName) {
		this.jwtTokenName = jwtTokenName;
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

	public int getSendTimeout() {
		return sendTimeout;
	}

	public void setSendTimeout(int sendTimeout) {
		this.sendTimeout = sendTimeout;
	}

	public String getRootPath() {
		return rootPath;
	}

	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}

	public int getSessionTimeout() {
		return sessionTimeout;
	}

	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}

	public int getConnectTimeout() {
		return connectTimeout;
	}

	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public int getRetryInitialSleepTime() {
		return retryInitialSleepTime;
	}

	public void setRetryInitialSleepTime(int retryInitialSleepTime) {
		this.retryInitialSleepTime = retryInitialSleepTime;
	}

	public int getRetryIntervalTimes() {
		return retryIntervalTimes;
	}

	public void setRetryIntervalTimes(int retryIntervalTimes) {
		this.retryIntervalTimes = retryIntervalTimes;
	}

	public int getRetryMaxTimes() {
		return retryMaxTimes;
	}

	public void setRetryMaxTimes(int retryMaxTimes) {
		this.retryMaxTimes = retryMaxTimes;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public boolean isMetricAble() {
		return metricAble;
	}

	public void setMetricAble(boolean metricAble) {
		this.metricAble = metricAble;
	}

	public Map<String, Integer> getWorkers() {
		return workers;
	}

	public void setWorkers(Map<String, Integer> workers) {
		this.workers = workers;
	}

	public int getEventLoopPoolSize() {
		return eventLoopPoolSize;
	}

	public void setEventLoopPoolSize(int eventLoopPoolSize) {
		this.eventLoopPoolSize = eventLoopPoolSize;
	}

	public int getWorkerThreads() {
		return workerThreads;
	}

	public void setWorkerThreads(int workerThreads) {
		this.workerThreads = workerThreads;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public int getInternalBlockingThreads() {
		return internalBlockingThreads;
	}

	public void setInternalBlockingThreads(int internalBlockingThreads) {
		this.internalBlockingThreads = internalBlockingThreads;
	}

	public String getKeyStoreAlgorithm() {
		return keyStoreAlgorithm;
	}

	public void setKeyStoreAlgorithm(String keyStoreAlgorithm) {
		this.keyStoreAlgorithm = keyStoreAlgorithm;
	}
}