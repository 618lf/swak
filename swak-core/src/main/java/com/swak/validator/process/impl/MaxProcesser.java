package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;

import com.swak.Constants;
import com.swak.annotation.Max;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcesser;

/**
 * 最大值处理
 * 
 * @author lifeng
 */
public class MaxProcesser extends AbstractProcesser {

	/**
	 * 处理的类型
	 */
	@Override
	protected Class<?> processBy() {
		return Max.class;
	}

	/**
	 * 为 null 判断
	 */
	@Override
	protected boolean doProcess(Annotation check, Object value) {
		Max max = (Max) check;
		if (value != null && value instanceof Integer) {
			Integer _value = (Integer) value;
			if (_value > max.value()) {
				return false;
			}
			return true;
		} else if(value != null && value instanceof Long) {
			Long _value = (Long) value;
			if (_value > max.value()) {
				return false;
			}
			return true;
		} else if(value != null && value instanceof Double) {
			Double _value = (Double) value;
			if (_value > max.value()) {
				return false;
			}
			return true;
		} else if(value != null && value instanceof Float) {
			Float _value = (Float) value;
			if (_value > max.value()) {
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
		Max max = (Max) check;
		return StringUtils.defaultIfEmpty(max.msg(), Constants.MaxErrorMsg);
	}
}