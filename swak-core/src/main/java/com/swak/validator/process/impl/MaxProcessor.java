package com.swak.validator.process.impl;

import com.swak.Constants;
import com.swak.annotation.Max;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcessor;

import java.lang.annotation.Annotation;

/**
 * 最大值处理
 *
 * @author lifeng
 */
public class MaxProcessor extends AbstractProcessor {

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
        if (value instanceof Integer) {
            Integer intValue = (Integer) value;
            return intValue <= max.value();
        } else if (value instanceof Long) {
            Long longValue = (Long) value;
            return longValue <= max.value();
        } else if (value instanceof Double) {
            Double doubleValue = (Double) value;
			return doubleValue <= max.value();
		} else if (value instanceof Float) {
            Float floatValue = (Float) value;
			return floatValue <= max.value();
		}
        return true;
    }

    /**
     * 处理为空的异常消息
     */
    @Override
    protected String processMessage(Annotation check) {
        Max max = (Max) check;
        return StringUtils.defaultIfEmpty(max.msg(), Constants.MAX_ERROR_MSG);
    }
}