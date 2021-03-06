package com.swak.validator.process.impl;

import java.lang.annotation.Annotation;

import com.swak.Constants;
import com.swak.annotation.Email;
import com.swak.utils.RegexUtil;
import com.swak.utils.StringUtils;
import com.swak.validator.process.AbstractProcessor;

/**
 * Processer Email
 *
 * @author: lifeng
 * @date: 2020/3/29 15:25
 */
public class EmailProcessor extends AbstractProcessor {

    /**
     * 处理的类型
     */
    @Override
    protected Class<?> processBy() {
        return Email.class;
    }

    /**
     * 为 null 判断
     */
    @Override
    protected boolean doProcess(Annotation check, Object value) {
        if (value != null && value instanceof String) {
            return RegexUtil.isEmail((String) value);
        }
        return true;
    }

    /**
     * 处理为空的异常消息
     */
    @Override
    protected String processMessage(Annotation ann) {
        Email email = (Email) ann;
        return StringUtils.defaultIfEmpty(email.msg(), Constants.EMAIL_ERROR_MSG);
    }
}