package com.swak.validator.process;

import java.lang.annotation.Annotation;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 实现基本的处理器
 * 
 * @author lifeng
 */
public abstract class AbstractProcesser implements Processer {

	/**
	 * 处理器
	 */
	private Processer processer;

	/**
	 * 处理消息
	 */
	@Override
	public String process(FieldMeta field, Object value) {

		/**
		 * 处理结果
		 */
		Annotation checkAn = null;
		if ((checkAn = field.getAnnotation(this.processBy())) != null && !this.doProcess(checkAn, value)) {
			return this.processMessage(checkAn);
		}

		/**
		 * 下一个处理器
		 */
		if (processer != null) {
			return processer.process(field, value);
		}

		/**
		 * 返回结果
		 */
		return null;
	}

	/**
	 * 返回处理的类型
	 * 
	 * @return
	 */
	protected abstract Class<?> processBy();

	/**
	 * 子类需要实现如何处理
	 * 
	 * @return 错误的
	 */
	protected abstract boolean doProcess(Annotation check, Object value);

	/**
	 * 处理错误消息, 可以自定义错误消息
	 * 
	 * @param ann
	 * @return
	 */
	protected abstract String processMessage(Annotation ann);

	/**
	 * 设置下一个处理器
	 */
	@Override
	public Processer next(Processer processer) {
		this.processer = processer;
		return processer;
	}
}
