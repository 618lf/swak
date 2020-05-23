package com.swak.utils;

/**
 * 异常操作
 *
 * @author: lifeng
 * @date: 2020/3/29 14:02
 */
public class ExceptionUtils {

    /**
     * 获得错误原因
     *
     * @param ex 异常
     * @return 错误原因
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