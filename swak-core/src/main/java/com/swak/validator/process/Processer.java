package com.swak.validator.process;

import com.swak.asm.FieldCache.FieldMeta;

/**
 * 处理器
 * 
 * @author lifeng
 */
public interface Processer {

	/**
	 * 下一个处理器
	 * 
	 * @param processer
	 */
	default Processer next(Processer processer) {
		return processer;
	}

	/**
	 * 处理
	 * 
	 * @return
	 */
	String process(FieldMeta field, Object value);
}