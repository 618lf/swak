package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;

import com.swak.Constants;
import com.swak.annotation.Phone;
import com.swak.utils.RegexUtil;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcesser;

public class EmailProcesser extends AbstractProcesser {

	/**
	 * 处理的类型
	 */
	@Override
	protected Class<?> processBy() {
		return Phone.class;
	}

	/**
	 * 为 null 判断
	 */
	@Override
	protected boolean doProcess(Annotation check, Object value) {
		return value != null && value instanceof String && RegexUtil.isEmail((String) value);
	}

	/**
	 * 处理为空的异常消息
	 */
	@Override
	protected String processMessage(Annotation ann) {
		Phone phone = (Phone) ann;
		return StringUtils.defaultIfEmpty(phone.msg(), Constants.EmailErrorMsg);
	}
}