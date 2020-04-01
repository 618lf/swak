package com.swak.vertx.config;

import com.swak.Constants;
import com.swak.reactivex.transport.TransportProperties;
import com.swak.utils.Maps;
import io.netty.handler.logging.LogLevel;
import io.netty.util.ResourceLeakDetector.Level;
import io.vertx.core.VertxOptions;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Map;

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
    private LogLevel serverLogLevel = null;
    private Level leakDetectionLevel = Level.DISABLED;
    private boolean threadCache = true;
    private int eventLoopPoolSize = VertxOptions.DEFAULT_EVENT_LOOP_POOL_SIZE;

    /**
     * vertx Blocking 线程
     */
    private int internalBlockingThreads = 10;

    /**
     * vertx Verticle worker 线程
     */
    private int workerThreads = 10;

    /**
     * 自定义的线程池
     */
    private Map<String, Integer> workers = Maps.newHashMap();
    private boolean metricAble = true;
    private long maxEventLoopExecuteTime = VertxOptions.DEFAULT_MAX_EVENT_LOOP_EXECUTE_TIME;
    private long maxWorkerExecuteTime = VertxOptions.DEFAULT_MAX_WORKER_EXECUTE_TIME;


    /**
     * Event Bus 的超时时间默认, 代码都是提交到 Event Bus来协调运行的，
     * Event Bus 中持有 方法提交和执行的引用
     */
    private int sendTimeout = 5 * 60 * 1000;

    /**
     * 上传文件的设置
     */
    private String uploadDirectory = "upload-files";
    private int bodyLimit = -1;
    private boolean deleteUploadedFilesOnEnd = false;

    private String rootPath = "io.vertx";
    private int sessionTimeout = 20000;
    private int connectTimeout = 3000;

    /**
     * 重试
     */
    private int retryInitialSleepTime = 1000;
    private int retryIntervalTimes = 10000;
    private int retryMaxTimes = 5;

    /**
     * 授权: keyStore 的路径 、keyStore 的密码
     */
    private String keyStorePath;
    private String keyStorePass = "secret";
    private String jwtTokenName = Constants.TOKEN_NAME;

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
}