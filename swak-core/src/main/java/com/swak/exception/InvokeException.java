package com.swak.exception;

/**
 * 方法调用异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:30
 */
public class InvokeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public InvokeException() {
        super();
    }

    public InvokeException(String message) {
        super(message);
    }

    public InvokeException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvokeException(String message, boolean writableStackTrace) {
        super(message, null, false, writableStackTrace);
    }

    public InvokeException(Throwable cause) {
        super(cause);
    }
}
