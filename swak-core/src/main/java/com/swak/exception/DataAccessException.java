package com.swak.exception;


/**
 * 数据访问异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:27
 */
public class DataAccessException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    public DataAccessException(String msg) {
        super(msg);
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }

    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
