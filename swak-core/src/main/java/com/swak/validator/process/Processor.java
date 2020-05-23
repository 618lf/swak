package com.swak.validator.process;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 处理器
 *
 * @author lifeng
 */
public interface Processor {

    /**
     * 下一个处理器
     *
     * @param processor 下一个处理器
     * @return 下一个处理器
     */
    default Processor next(Processor processor) {
        return processor;
    }

    /**
     * 处理属性
     *
     * @param field 属性
     * @param value 值
     * @return 处理结果
     */
    String process(FieldMeta field, Object value);
}