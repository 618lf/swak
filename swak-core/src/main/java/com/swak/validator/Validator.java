package com.swak.validator;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 基础的验证服务
 *
 * @author lifeng
 */
public interface Validator {

    /**
     * 简单的验证
     *
     * @param field 字段数据
     * @param value 验证值
     * @return 验证结果
     */
    String validate(FieldMeta field, Object value);
}