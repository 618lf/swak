package com.swak.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Controller;

import com.swak.utils.StringUtils;

/**
 * 用于配置请求
 *
 * @author: lifeng
 * @date: 2020/3/28 17:22
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Controller
@RequestMapping
public @interface RestApi {

    /**
     * 设置 bean name， 不能用其他的替换，识别不到
     */
    @AliasFor(annotation = Controller.class, attribute = "value")
    String value() default StringUtils.EMPTY;

    /**
     * 支持的 path
     */
    @AliasFor(annotation = RequestMapping.class, value = "value")
    String[] path() default {};

    /**
     * 支持的 method
     */
    @AliasFor(annotation = RequestMapping.class)
    RequestMethod method() default RequestMethod.ALL;
}
