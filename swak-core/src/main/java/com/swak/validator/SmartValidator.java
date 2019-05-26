package com.swak.validator;

import com.swak.asm.FieldCache;
import com.swak.asm.FieldCache.ClassMeta;
import com.swak.asm.FieldCache.FieldMeta;
import com.swak.validator.errors.BindErrors;

/**
 * 简单的验证器
 * 
 * @author lifeng
 */
public class SmartValidator implements Validator {

	@Override
	public BindErrors validate(Object object) {
		ClassMeta classMeta = FieldCache.get(object.getClass());
		if (classMeta == null) {
			return null;
		}
		this.processClass(classMeta);
		return null;
	}

	/**
	 * 处理 类
	 * 
	 * @param classMeta
	 */
	private void processClass(ClassMeta classMeta) {
		classMeta.getFields().values().forEach(this::processField);
	}

	/**
	 * 处理 字段
	 * 
	 * @param fieldMeta
	 */
	private void processField(FieldMeta fieldMeta) {
	}
}