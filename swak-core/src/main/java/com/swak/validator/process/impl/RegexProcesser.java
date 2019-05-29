package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.swak.Constants;
import com.swak.annotation.Regex;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcesser;

/**
 * 正则表达式处理
 * 
 * @author lifeng
 */
public class RegexProcesser extends AbstractProcesser {

	/**
	 * 处理的类型
	 */
	@Override
	protected Class<?> processBy() {
		return Regex.class;
	}

	/**
	 * 为 null 判断
	 */
	@Override
	protected boolean doProcess(Annotation check, Object value) {
		Regex regex = (Regex) check;
		return value != null && value instanceof String && this.checkRegex(regex.value(), (String) value);
	}

	/**
	 * 处理为空的异常消息
	 */
	@Override
	protected String processMessage(Annotation ann) {
		Regex regex = (Regex) ann;
		return StringUtils.defaultIfEmpty(regex.msg(), Constants.EmailErrorMsg);
	}

	/**
	 * 正则的校验
	 * 
	 * @param regex
	 * @param value
	 * @return
	 */
	protected boolean checkRegex(String regex, String value) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.matches();
	}
}