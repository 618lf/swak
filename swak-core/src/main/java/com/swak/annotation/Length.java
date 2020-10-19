package com.swak.annotation;

import com.swak.utils.StringUtils;

import java.lang.annotation.*;

/**
 * 长度校验
 *
 * @author: lifeng
 * @date: 2020/3/28 17:17
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Documented
public @interface Length {

    /**
     * @return 最小值
     */
    int min() default 0;

    /**
     * @return 最大值
     */
    int max() default Integer.MAX_VALUE;

    /**
     * @return 错误描述
     */
    String msg() default StringUtils.EMPTY;
}
