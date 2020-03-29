package com.swak.validator.errors;

import java.io.Serializable;
import java.util.Map;

/**
 * 参数绑定异常
 *
 * @author: lifeng
 * @date: 2020/3/29 15:24
 */
public class BindErrors implements Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> errors;

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }

    public BindErrors addError(String object, String field, String value) {
        this.errors.put(object + "." + field, value);
        return this;
    }

    public boolean hasError() {
        return errors != null && !errors.isEmpty();
    }

    public static BindErrors of(Map<String, String> errors) {
        BindErrors bindErrors = new BindErrors();
        bindErrors.setErrors(errors);
        return bindErrors;
    }
}
