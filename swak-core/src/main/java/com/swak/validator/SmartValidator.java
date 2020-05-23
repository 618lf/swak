package com.swak.validator;

import com.swak.asm.FieldCache.FieldMeta;
import com.swak.validator.process.Processor;
import com.swak.validator.process.impl.EmailProcessor;
import com.swak.validator.process.impl.LengthProcessor;
import com.swak.validator.process.impl.MaxProcessor;
import com.swak.validator.process.impl.MinProcessor;
import com.swak.validator.process.impl.NotNullProcessor;
import com.swak.validator.process.impl.PhoneProcessor;
import com.swak.validator.process.impl.RegexProcessor;

/**
 * 简单的验证器
 *
 * @author: lifeng
 * @date: 2020/3/29 15:37
 */
public class SmartValidator implements Validator {

    /**
     * 链式 - 处理器
     */
    private Processor processor;

    public SmartValidator() {
        processor = new NotNullProcessor();
        LengthProcessor lengthProcessor = new LengthProcessor();
        MaxProcessor maxProcessor = new MaxProcessor();
        MinProcessor minProcessor = new MinProcessor();
        EmailProcessor emailProcessor = new EmailProcessor();
        PhoneProcessor phoneProcessor = new PhoneProcessor();
        RegexProcessor regexProcessor = new RegexProcessor();
        processor.next(lengthProcessor).next(maxProcessor).next(minProcessor).next(emailProcessor).next(phoneProcessor)
                .next(regexProcessor);
    }

    /**
     * 链式处理校验
     */
    @Override
    public String validate(FieldMeta field, Object value) {
        return processor.process(field, value);
    }
}