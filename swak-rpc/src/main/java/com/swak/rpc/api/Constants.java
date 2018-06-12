package com.swak.rpc.api;

/**
 * RPC 相关的常量定义
 * @author lifeng
 */
public interface Constants {
	
    public static final String PROVIDER = "provider";
    public static final String CONSUMER = "consumer";
    public static final String REGISTER = "register";
    public static final String UNREGISTER = "unregister";
    public static final String PROTOCOL = "swak";
    public static final String GROUP_KEY = "group";
    public static final String PATH_KEY = "path";
    public static final String VERSION_KEY = "version";
    public static final String TOKEN_KEY = "token";
    public static final String METHOD_KEY = "method";
    public static final String PARAM_KEY_PREFIX = "param.";
    public static final String METHOD_PARAM_KEY_PREFIX = "method.";
    
    public static final String REGISTRY_FILESAVE_SYNC_KEY = "save.file";
    public static final String FILE_KEY = "file";
    public static final String APPLICATION_KEY = "application";
    public static final int DEFAULT_SESSION_TIMEOUT = 60 * 1000;
    public static final int DEFAULT_REGISTRY_RETRY_PERIOD = 5 * 1000;
    public static final String REDIS_SUBSCRIBE_TOPIC = "REDIS_SUBSCRIBE_TOPIC";
}
