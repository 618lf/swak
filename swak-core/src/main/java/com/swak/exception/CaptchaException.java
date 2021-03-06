package com.swak.exception;


/**
 * 验证码异常处理类
 *
 * @author: lifeng
 * @date: 2020/3/29 11:27
 */
public class CaptchaException extends BaseRuntimeException {

    private static final long serialVersionUID = 1L;

    public CaptchaException(String message, Throwable cause) {
        super(message, cause);
    }

    public CaptchaException(String message) {
        super(message);
    }

    public CaptchaException(Throwable cause) {
        super(cause);
    }

}
