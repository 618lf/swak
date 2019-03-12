package com.swak.app.core.net.exception;

import android.net.ParseException;

import com.google.gson.JsonParseException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.net.ConnectException;

import retrofit2.HttpException;

import static com.swak.app.core.net.exception.ErrorCode.BAD_GATEWAY;
import static com.swak.app.core.net.exception.ErrorCode.FORBIDDEN;
import static com.swak.app.core.net.exception.ErrorCode.GATEWAY_TIMEOUT;
import static com.swak.app.core.net.exception.ErrorCode.HTTP_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.NET_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.NOT_FOUND;
import static com.swak.app.core.net.exception.ErrorCode.PARSE_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.REQUEST_TIMEOUT;
import static com.swak.app.core.net.exception.ErrorCode.SERVICE_UNAVAILABLE;
import static com.swak.app.core.net.exception.ErrorCode.SSL_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.TIMEOUT_ERROR;
import static com.swak.app.core.net.exception.ErrorCode.UNAUTHORIZED;
import static com.swak.app.core.net.exception.ErrorCode.UNKNOWN_ERROR;

public class ExceptionHandler {

    public static ResponseException handle(Throwable e) {
        ResponseException responseException;
        if (e instanceof ApiException) {
            ApiException apiException = (ApiException) e;
            responseException = new ResponseException(apiException, Integer.valueOf(apiException.getErrorCode()), apiException.getMessage());
        } else if (e instanceof HttpException) {
            HttpException httpException = (HttpException) e;
            switch (httpException.code()) {
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    responseException = new ResponseException(e, HTTP_ERROR + ":" + httpException.code(), "网络连接错误");
                    break;
            }
        } else if (e instanceof JsonParseException
                || e instanceof JSONException
                || e instanceof ParseException) {
            responseException = new ResponseException(e, PARSE_ERROR, "解析错误");
        } else if (e instanceof ConnectException) {
            responseException = new ResponseException(e, NET_ERROR, "连接失败");
        } else if (e instanceof ConnectTimeoutException || e instanceof java.net.SocketTimeoutException) {
            responseException = new ResponseException(e, TIMEOUT_ERROR, "网络连接超时");
        } else if (e instanceof javax.net.ssl.SSLHandshakeException) {
            responseException = new ResponseException(e, SSL_ERROR, "证书验证失败");
        } else {
            responseException = new ResponseException(e, UNKNOWN_ERROR, "未知错误");
        }
        return responseException;
    }
}
