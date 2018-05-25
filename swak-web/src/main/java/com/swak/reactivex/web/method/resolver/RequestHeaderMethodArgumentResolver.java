package com.swak.reactivex.web.method.resolver;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.web.annotation.RequestHeader;

/**
 * 支持方法中获取RequestHeader中的内容
 * @author lifeng
 */
public class RequestHeaderMethodArgumentResolver extends AbstractMethodArgumentResolver {

	public RequestHeaderMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * @RequestHeader
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(RequestHeader.class);
	}

	/**
	 * 如果是map 则直接返回所有的 header
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest){
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			Map<String, String> result = new LinkedHashMap<String, String>();
			for (Iterator<String> iterator = webRequest.getRequestHeaderNames(); iterator.hasNext();) {
				String headerName = iterator.next();
				String headerValue = webRequest.getRequestHeader(headerName);
				if (headerValue != null) {
					result.put(headerName, headerValue);
				}
			}
			return result;
		} else {
			return webRequest.getRequestHeader(parameter.getParameterName());
		}
	}
}
