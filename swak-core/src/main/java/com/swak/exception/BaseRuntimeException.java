package com.swak.exception;

/**
 * 基础运行时异常
 *
 * @author: lifeng
 * @date: 2020/3/29 11:26
 */
public class BaseRuntimeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 具体异常码
     */
    protected int code;

    public BaseRuntimeException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public BaseRuntimeException(String msg) {
        super(msg);
    }

    public BaseRuntimeException(Throwable cause) {
        super(cause);
    }

    public BaseRuntimeException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Return the detail message, including the message from the nested
     * exception if there is one.
     */
    @Override
    public String getMessage() {
        return this.buildMessage(super.getMessage(), getCause());
    }

    /**
     * @param message 错误消息
     * @param cause   错误
     * @return 错误消息
     */
    public String buildMessage(String message, Throwable cause) {
        if (cause != null) {
            StringBuilder buf = new StringBuilder();
            if (message != null) {
                buf.append(message).append("; ");
            }
            buf.append("nested exception is ").append(cause);
            return buf.toString();
        } else {
            return message;
        }
    }

    /**
     * Retrieve the innermost cause of this exception, if any.
     *
     * @return the innermost exception, or <code>null</code> if none
     * @since 2.0
     */
    public Throwable getRootCause() {
        Throwable rootCause = null;
        Throwable cause = getCause();
        while (cause != null) {
            rootCause = cause;
            cause = cause.getCause();
        }
        return rootCause;
    }

    /**
     * Retrieve the most specific cause of this exception, that is, either the
     * innermost cause (root cause) or this exception itself.
     * <p>
     * Differs from {@link #getRootCause()} in that it falls back to the present
     * exception if there is no root cause.
     *
     * @return the most specific cause (never <code>null</code>)
     * @since 2.0.3
     */
    public Throwable getMostSpecificCause() {
        Throwable rootCause = getRootCause();
        return rootCause != null ? rootCause : this;
    }

    /**
     * Check whether this exception contains an exception of the given type:
     * either it is of the given class itself or it contains a nested cause of
     * the given type.
     *
     * @param exType the exception type to look for
     * @return whether there is a nested exception of the specified type
     */
    public <T> boolean contains(Class<T> exType) {
        if (exType == null) {
            return false;
        }
        if (exType.isInstance(this)) {
            return true;
        }
        Throwable cause = getCause();
        if (cause.equals(this)) {
            return false;
        }
        if (cause instanceof BaseRuntimeException) {
            return ((BaseRuntimeException) cause).contains(exType);
        } else {
            while (cause != null) {
                if (exType.isInstance(cause)) {
                    return true;
                }
                if (cause.getCause().equals(cause)) {
                    break;
                }
                cause = cause.getCause();
            }
            return false;
        }
    }
}