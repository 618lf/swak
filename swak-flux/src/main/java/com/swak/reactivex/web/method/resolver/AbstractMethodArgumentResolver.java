package com.swak.reactivex.web.method.resolver;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;
import com.swak.reactivex.web.method.MethodParameter;

/**
 * 基本的参数处理器
 * @author lifeng
 */
public abstract class AbstractMethodArgumentResolver implements HandlerMethodArgumentResolver {
	
	// 属性转换服务
	protected ConversionService conversionService;
	
	public AbstractMethodArgumentResolver(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	/**
	 * 1. 解析出参数
	 * 2. 通过属性转换器转换成对应的类型
	 */
	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest){
		// 获取子类的参数
		Object arg = this.resolveArgumentInternal(parameter, webRequest);
		// 转换类型描述
		return this.doConvert(arg, parameter.getParameterType());
	}
	
	/**
	 * 执行转换
	 * @param value
	 * @param targetType
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doConvert(Object value, Class<T> targetType) {
		TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(value);
		TypeDescriptor targetDescriptor = TypeDescriptor.valueOf(targetType);
		if (conversionService.canConvert(sourceTypeDesc, targetDescriptor)) {
			return (T) conversionService.convert(value, sourceTypeDesc, targetDescriptor); 
		}
		return null;
	}
	
	/**
	 * 获得参数
	 * 
	 * @param request
	 * @return
	 */
	protected Map<String, String> getArguments(HttpServerRequest request) {
		Map<String, List<String>> parameterMap = request.getParameterMap();
		Map<String, String> result = new LinkedHashMap<String, String>(parameterMap.size());
		for (Map.Entry<String, List<String>> entry : parameterMap.entrySet()) {
			if (entry.getValue().size() > 0) {
				result.put(entry.getKey(), entry.getValue().get(0));
			}
		}
		return result;
	}
	
	/**
	 * 子类需要实现如何获取参数
	 * @param parameter
	 * @param webRequest
	 * @return
	 */
	protected abstract Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest);

}
