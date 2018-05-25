package com.swak.exception;


/**
 * 数据访问异常
 * @author liliang
 *
 */
@SuppressWarnings("serial")
public class DataAccessException extends BaseRuntimeException {

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
