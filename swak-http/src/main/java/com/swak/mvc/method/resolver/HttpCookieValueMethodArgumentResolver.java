package com.swak.mvc.method.resolver;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.http.Cookie;
import com.swak.http.HttpServletRequest;
import com.swak.mvc.annotation.CookieValue;

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
	public Object resolveArgumentInternal(MethodParameter parameter, HttpServletRequest webRequest) throws Exception {
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
		return webRequest.getCookie(parameter.getParameterName()).value();
	}
}
