package com.swak.validator;

import com.swak.asm.FieldCache.FieldMeta;

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
	String validate(FieldMeta field, Object value);
}