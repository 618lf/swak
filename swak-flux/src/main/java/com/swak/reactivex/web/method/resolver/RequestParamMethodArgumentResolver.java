package com.swak.reactivex.web.method.resolver;

import java.util.List;
import java.util.Map;

import org.springframework.beans.BeanUtils;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.multipart.MultipartFile;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.method.MethodParameter;
import com.swak.utils.Maps;

/**
 * 处理基本的参数 支持最基本的类型和Map类型
 * 
 * @author lifeng
 */
public class RequestParamMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public RequestParamMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * 支持最基本的类型和Map类型
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return Map.class.isAssignableFrom(parameter.getParameterType())
				|| List.class.isAssignableFrom(parameter.getParameterType())
				|| MultipartFile.class.isAssignableFrom(parameter.getParameterType())
				|| BeanUtils.isSimpleProperty(parameter.getParameterType());
	}

	/**
	 * 返回对应的对象
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest request) {
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			return this.getArguments(request);
		} else if (List.class.isAssignableFrom(parameter.getParameterType())) {
			String resolvedName = parameter.getParameterName();
			return request.getParameterValues(resolvedName);
		} else if (MultipartFile.class.isAssignableFrom(parameter.getParameterType())) {
			return Maps.getFirst(request.getMultipartFiles());
		} else {
			return request.getParameter(parameter.getParameterName());
		}
	}
}