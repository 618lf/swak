package com.swak.vertx.handler.validate;

import java.io.Serializable;
import java.util.Map;

/**
 * 参数绑定异常
 * 
 * @author lifeng
 */
public class BindErrors implements Serializable {

	public static final BindErrors NONE = BindErrors.of(null);
	
	private static final long serialVersionUID = 1L;

	private Map<String, String> errors;

	public Map<String, String> getErrors() {
		return errors;
	}

	public void setErrors(Map<String, String> errors) {
		this.errors = errors;
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
