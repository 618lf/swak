package com.swak.flux.web.method.resolver;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.swak.flux.transport.server.HttpServerRequest;
import com.swak.flux.web.annotation.CookieValue;
import com.swak.flux.web.method.MethodParameter;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 支持获取cookie 中的数据
 * @author lifeng
 */
public class HttpCookieValueMethodArgumentResolver extends AbstractMethodArgumentResolver{

	public HttpCookieValueMethodArgumentResolver(ConversionService conversionService) {
		super(conversionService);
	}

	/**
	 * @CookieValue
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CookieValue.class);
	}

	/**
	 * map 返回所有的cookie
	 */
	@Override
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServerRequest webRequest){
		if (Map.class.isAssignableFrom(parameter.getParameterType())) {
			Map<String, String> result = new LinkedHashMap<String, String>();
			for (Iterator<Cookie> iterator = webRequest.getCookies(); iterator.hasNext();) {
				Cookie cookie = iterator.next();
				String headerValue = cookie.value();
				if (headerValue != null) {
					result.put(cookie.name(), cookie.value());
				}
			}
			return result;
		}
		CookieValue cookieValue = parameter.getParameterAnnotation(CookieValue.class);
		Cookie cookie = webRequest.getCookie(StringUtils.defaultIfEmpty(cookieValue.value(), parameter.getParameterName()));
		return cookie != null? cookie.value(): StringUtils.EMPTY;
	}
}
