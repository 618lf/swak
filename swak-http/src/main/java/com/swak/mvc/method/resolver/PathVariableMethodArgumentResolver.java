package com.swak.mvc.method.resolver;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.http.HttpServletRequest;
import com.swak.mvc.annotation.PathVariable;

/**
 * PathVariable 的处理
 * @author lifeng
 */
public class PathVariableMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public PathVariableMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * @PathVariable
	 * 例如 /get/{name} == > @PathVariable String name
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterAnnotation(PathVariable.class) != null;
	}

	/**
	 * 解析出变量的值
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServletRequest request) throws Exception {
		Map<String, String> uriTemplateVars = request.getPathVariables();
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			return uriTemplateVars != null? uriTemplateVars: Collections.emptyMap();
		}
		return uriTemplateVars != null ? uriTemplateVars.get(parameter.getParameterName()) : Collections.emptyMap();
	}
}