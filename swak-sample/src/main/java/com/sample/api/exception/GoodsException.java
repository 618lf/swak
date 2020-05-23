package com.sample.api.exception;

/**
 * 自定义的异常
 * 
 * @author lifeng
 */
public class GoodsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public GoodsException(String message) {
        super(message);
	}
}