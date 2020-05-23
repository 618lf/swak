package com.swak.exception;

/**
 * 缓存序列化异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:31
 */
public class SerializeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public SerializeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializeException(String message) {
        super(message);
    }

    public SerializeException(Throwable cause) {
        super(cause);
    }
}
