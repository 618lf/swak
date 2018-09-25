package com.swak.utils;

/**
 * 异常操作
 * 
 * @author lifeng
 */
public class ExceptionUtils {

	/**
	 * 获得错误原因
	 * @param ex
	 * @return
	 */
	public static String causedMessage(Exception ex) {
		StringBuilder messages = new StringBuilder();
		Throwable cause = ex.getCause();
		while (cause != null) {
			messages.append(cause.getMessage());
			cause = cause.getCause();
		}
		return messages.toString();
	}

}