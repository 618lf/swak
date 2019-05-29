package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;

import com.swak.Constants;
import com.swak.annotation.NotNull;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcesser;

/**
 * 空处理
 * 
 * @author lifeng
 */
public class NotNullProcesser extends AbstractProcesser {

	/**
	 * 处理的类型
	 */
	@Override
	protected Class<?> processBy() {
		return NotNull.class;
	}

	/**
	 * 为 null 判断
	 */
	@Override
	protected boolean doProcess(Annotation check, Object value) {
		return value != null;
	}

	/**
	 * 处理为空的异常消息
	 */
	@Override
	protected String processMessage(Annotation ann) {
		NotNull notNull = (NotNull) ann;
		return StringUtils.defaultIfEmpty(notNull.msg(), Constants.NotNullErrorMsg);
	}
}