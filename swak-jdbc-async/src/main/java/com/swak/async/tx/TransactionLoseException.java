package com.swak.async.tx;

/**
 * 事务丢失
 * 
 * @author lifeng
 * @date 2020年10月9日 上午11:01:44
 */
public class TransactionLoseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TransactionLoseException() {
		super("事务丢失！");
	}
}