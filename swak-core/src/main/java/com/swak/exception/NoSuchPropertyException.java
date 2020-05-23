package com.swak.exception;

/**
 * 锁超时异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:31
 */
public class NoSuchPropertyException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NoSuchPropertyException() {
        super();
    }

    public NoSuchPropertyException(String msg) {
        super(msg);
    }
}