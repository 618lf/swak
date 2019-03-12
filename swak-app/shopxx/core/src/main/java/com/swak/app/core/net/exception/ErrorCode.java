package com.swak.app.core.net.exception;

public class ErrorCode {

    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    public static final int REQUEST_TIMEOUT = 408;
    public static final int INTERNAL_SERVER_ERROR = 500;
    public static final int BAD_GATEWAY = 502;
    public static final int SERVICE_UNAVAILABLE = 503;
    public static final int GATEWAY_TIMEOUT = 504;

    /* 自约定的响应码 */
    // 未知错误
    public static final int UNKNOWN_ERROR = 1000;
    // 解析错误
    public static final int PARSE_ERROR = 1001;
    // 网络错误
    public static final int NET_ERROR = 1002;
    // 协议出错
    public static final int HTTP_ERROR = 1003;
    // 证书出错
    public static final int SSL_ERROR = 1005;
    // 连接超时
    public static final int TIMEOUT_ERROR = 1006;
}
