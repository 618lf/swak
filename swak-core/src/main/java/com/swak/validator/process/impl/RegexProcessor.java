package com.swak.validator.process.impl;

import com.swak.Constants;
import com.swak.annotation.Regex;
import com.swak.utils.RegexUtil;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcessor;

import java.lang.annotation.Annotation;

/**
 * 正则表达式处理
 *
 * @author lifeng
 */
public class RegexProcessor extends AbstractProcessor {

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
        if (value instanceof String) {
            return RegexUtil.checkRegex(regex.value(), (String) value);
        }
        return true;
    }

    /**
     * 处理为空的异常消息
     */
    @Override
    protected String processMessage(Annotation ann) {
        Regex regex = (Regex) ann;
        return StringUtils.defaultIfEmpty(regex.msg(), Constants.REGEX_ERROR_MSG);
    }
}