package com.swak.flux.web.method.resolver;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.web.annotation.PathVariable;
import com.swak.flux.web.method.MethodParameter;
import com.swak.utils.StringUtils;

/**
 * PathVariable 的处理
 * 
 * @author lifeng
 */
public class PathVariableMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public PathVariableMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * @PathVariable 例如 /get/{name} == > @PathVariable String name
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(PathVariable.class) != null;
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
	 * 解析出变量的值
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest request) {
		Map<String, String> uriTemplateVars = request.getPathVariables();
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			return uriTemplateVars != null ? uriTemplateVars : Collections.emptyMap();
		}
		PathVariable pathVariable = parameter.getParameterAnnotation(PathVariable.class);
		return uriTemplateVars != null
				? uriTemplateVars.get(StringUtils.defaultIfEmpty(pathVariable.value(), parameter.getParameterName()))
				: Collections.emptyMap();
	}
}