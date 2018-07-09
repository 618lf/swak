package com.swak.reactivex.web.method.resolver;

import java.util.Collections;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.annotation.PathVariable;
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