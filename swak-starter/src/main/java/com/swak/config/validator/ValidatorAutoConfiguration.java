package com.swak.config.validator;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.Constants;
import com.swak.validator.SmartValidator;
import com.swak.validator.Validator;

/**
 * 系统验证器
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnProperty(prefix = Constants.APPLICATION_PREFIX, name = "validator", matchIfMissing = true)
public class ValidatorAutoConfiguration {
	
	/**
	 * 简单验证器
	 * 
	 * @return
	 */
	@Bean
	@ConditionalOnMissingBean(Validator.class)
	public Validator validator() {
		return new SmartValidator();
	}
}