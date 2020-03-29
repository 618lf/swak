package com.swak.exception;

/**
 * 过期的数据
 *
 * @author: lifeng
 * @date: 2020/3/29 11:32
 */
public class StaleObjectStateException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    public StaleObjectStateException(String msg) {
        super(msg);
    }

    public StaleObjectStateException(Throwable cause) {
        super(cause);
    }

    public StaleObjectStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
