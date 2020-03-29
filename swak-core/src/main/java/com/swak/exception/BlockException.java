package com.swak.exception;

/**
 * 线程阻塞异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:27
 */
public class BlockException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public BlockException() {
        super();
    }

    public BlockException(String message) {
        super(message);
    }

    public BlockException(String message, Throwable cause) {
        super(message, cause);
    }

    public BlockException(String message, boolean writableStackTrace) {
        super(message, null, false, writableStackTrace);
    }

    public BlockException(Throwable cause) {
        super(cause);
    }
}
