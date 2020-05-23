package com.swak.exception;


/**
 * 缓存操作异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:27
 */
public class CacheException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    public CacheException(String message, Throwable cause) {
        super(message, cause);
    }

    public CacheException(String message) {
        super(message);
    }

    public CacheException(Throwable cause) {
        super(cause);
    }
}
