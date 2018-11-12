package com.swak.reactivex.web.method.resolver;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.annotation.RequestHeader;
import com.swak.utils.StringUtils;

/**
 * 支持方法中获取RequestHeader中的内容
 * 
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
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest) {
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			Map<CharSequence, String> result = new LinkedHashMap<CharSequence, String>();
			for (Iterator<String> iterator = webRequest.getRequestHeaderNames(); iterator.hasNext();) {
				String headerName = iterator.next();
				String headerValue = webRequest.getRequestHeader(headerName);
				if (headerValue != null) {
					result.put(headerName, headerValue);
				}
			}
			return result;
		} else {
			RequestHeader requestHeader = parameter.getParameterAnnotation(RequestHeader.class);
			return webRequest
					.getRequestHeader(StringUtils.defaultIfEmpty(requestHeader.value(), parameter.getParameterName()));
		}
	}
}
