package com.swak.validator;

import com.swak.asm.FieldCache.FieldMeta;
import com.swak.validator.process.Processer;
import com.swak.validator.process.impl.EmailProcesser;
import com.swak.validator.process.impl.LengthProcesser;
import com.swak.validator.process.impl.MaxProcesser;
import com.swak.validator.process.impl.MinProcesser;
import com.swak.validator.process.impl.NotNullProcesser;
import com.swak.validator.process.impl.PhoneProcesser;
import com.swak.validator.process.impl.RegexProcesser;

/**
 * 简单的验证器
 * 
 * @author lifeng
 */
public class SmartValidator implements Validator {

	/**
	 * 链式 - 处理器
	 */
	private Processer processer;

	public SmartValidator() {
		processer = new NotNullProcesser();
		LengthProcesser lengthProcesser = new LengthProcesser();
		MaxProcesser maxProcesser = new MaxProcesser();
		MinProcesser minProcesser = new MinProcesser();
		EmailProcesser emailProcesser = new EmailProcesser();
		PhoneProcesser phoneProcesser = new PhoneProcesser();
		RegexProcesser regexProcesser = new RegexProcesser();
		processer.next(lengthProcesser).next(maxProcesser).next(minProcesser).next(emailProcesser).next(phoneProcesser)
				.next(regexProcesser);
	}

	/**
	 * 链式处理校验
	 */
	@Override
	public String validate(FieldMeta field, Object value) {
		return processer.process(field, value);
	}
}