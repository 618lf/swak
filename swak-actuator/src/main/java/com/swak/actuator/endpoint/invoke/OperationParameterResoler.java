package com.swak.actuator.endpoint.invoke;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

/**
 * 方法参数转换
 * @author lifeng
 */
public class OperationParameterResoler {

	private final ConversionService conversionService;
	
	public OperationParameterResoler(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	
	/**
	 * 执行转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T> T doConvert(Object value, Class<T> targetType) {
		TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(value);
		TypeDescriptor targetDescriptor = TypeDescriptor.valueOf(targetType);
		if (conversionService.canConvert(sourceTypeDesc, targetDescriptor)) {
			return (T) conversionService.convert(value, sourceTypeDesc, targetDescriptor); 
		}
		return null;
	}
}