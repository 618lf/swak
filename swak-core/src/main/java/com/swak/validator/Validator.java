package com.swak.validator;

import com.swak.validator.errors.BindErrors;

/**
 * 基础的验证服务
 * 
 * @author lifeng
 */
public interface Validator {
	
	/**
	 * 简单的验证
	 * 
	 * @param object
	 * @return
	 */
	BindErrors validate(Object object);
}