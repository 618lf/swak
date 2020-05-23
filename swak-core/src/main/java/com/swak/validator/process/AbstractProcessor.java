package com.swak.validator.process;

import java.lang.annotation.Annotation;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 实现基本的处理器
 *
 * @author lifeng
 */
public abstract class AbstractProcessor implements Processor {

    /**
     * 处理器
     */
    private Processor processor;

    /**
     * 处理消息
     */
    @Override
    public String process(FieldMeta field, Object value) {

        // 处理结果
        Annotation checkAn;
        if ((checkAn = field.getAnnotation(this.processBy())) != null && !this.doProcess(checkAn, value)) {
            return this.processMessage(checkAn);
        }

        // 下一个处理器
        if (processor != null) {
            return processor.process(field, value);
        }

        // 返回结果
        return null;
    }

    /**
     * 返回处理的类型
     *
     * @return 可以处理的类
     */
    protected abstract Class<?> processBy();

    /**
     * 子类需要实现如何处理
     *
     * @param check 注解
     * @param value 默认值
     * @return 处理结果
     */
    protected abstract boolean doProcess(Annotation check, Object value);

    /**
     * 处理错误消息, 可以自定义错误消息
     *
     * @param ann 注解
     * @return 错误消息
     */
    protected abstract String processMessage(Annotation ann);

    /**
     * 设置下一个处理器
     */
    @Override
    public Processor next(Processor processor) {
        this.processor = processor;
        return processor;
    }
}
