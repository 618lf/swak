package com.swak.exception;

/**
 * 访问受限异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:25
 */
public class AccessDeniedException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    public AccessDeniedException(String msg) {
        super(msg);
    }
}