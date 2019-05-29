package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;

import com.swak.Constants;
import com.swak.annotation.Length;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcesser;

/**
 * 长度的判断
 * 
 * @author lifeng
 */
public class LengthProcesser extends AbstractProcesser {

	/**
	 * 处理的类型
	 */
	@Override
	protected Class<?> processBy() {
		return Length.class;
	}

	/**
	 * 为 null 判断
	 */
	@Override
	protected boolean doProcess(Annotation check, Object value) {
		Length length = (Length) check;
		if (value != null && value instanceof String) {
			String _value = (String) value;
			if (_value.length() > length.max() || _value.length() < length.min()) {
				return false;
			}
			return true;
		}
		return true;
	}

	/**
	 * 处理为空的异常消息
	 */
	@Override
	protected String processMessage(Annotation check) {
		Length length = (Length) check;
		return StringUtils.defaultIfEmpty(length.msg(), Constants.LengthErrorMsg);
	}
}