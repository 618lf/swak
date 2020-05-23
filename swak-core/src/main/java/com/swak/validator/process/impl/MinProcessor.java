package com.swak.validator.process.impl;

import com.swak.Constants;
import com.swak.annotation.Min;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcessor;

import java.lang.annotation.Annotation;

/**
 * 最小值处理
 *
 * @author lifeng
 */
public class MinProcessor extends AbstractProcessor {

    /**
     * 处理的类型
     */
    @Override
    protected Class<?> processBy() {
        return Min.class;
    }

    /**
     * 为 null 判断
     */
    @Override
    protected boolean doProcess(Annotation check, Object value) {
        Min min = (Min) check;
        if (value instanceof Integer) {
            Integer intValue = (Integer) value;
            return intValue >= min.value();
        } else if (value instanceof Long) {
            Long longValue = (Long) value;
            return longValue >= min.value();
        } else if (value instanceof Double) {
            Double doubleValue = (Double) value;
            return doubleValue >= min.value();
        } else if (value instanceof Float) {
            Float floatValue = (Float) value;
            return floatValue >= min.value();
        }
        return true;
    }

    /**
     * 处理为空的异常消息
     */
    @Override
    protected String processMessage(Annotation check) {
        Min min = (Min) check;
        return StringUtils.defaultIfEmpty(min.msg(), Constants.MIN_ERROR_MSG);
    }
}