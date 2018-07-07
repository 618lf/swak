package com.swak.security.web.token;

import java.util.UUID;

import com.swak.Constants;
import com.swak.cache.CacheManagers;
import com.swak.exception.BaseRuntimeException;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.web.cookie.CookieProvider;
import com.swak.utils.StringUtils;

/**
 * 通过 header 和redis 来存储值
 * @author lifeng
 */
public class TokenProvider {

	/**
	 * 设置属性
	 * 
	 * @param request
	 * @param response
	 * @param key
	 * @param value
	 */
	public static void setAttribute(HttpServerRequest request, HttpServerResponse response, String key, Object value) {
		String validateCodeKey = TokenProvider.getHeader(request, response, key);
		if (StringUtils.isBlank(validateCodeKey)) {
			validateCodeKey = UUID.randomUUID().toString();
			TokenProvider.setHeader(request, response, key, validateCodeKey);
		}
		CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times).putObject(validateCodeKey,
				value);
	}

	/**
	 * 获取属性
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAttribute(HttpServerRequest request, HttpServerResponse response, String key) {
		try {
			String validateCodeKey = TokenProvider.getHeader(request, response, key);
			if (StringUtils.isNotBlank(validateCodeKey)) {
				Object obj = CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times)
						.getObject(validateCodeKey);
				return (T) (obj);
			}
			return null;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 得到值，并清除相应值
	 * 
	 * @param request
	 * @param response
	 * @param key
	 * @return
	 */
	public static <T> T getAndClearAttribute(HttpServerRequest request, HttpServerResponse response, String key) {
		T t = TokenProvider.getAttribute(request, response, key);
		CookieProvider.removeAttribute(request, response, key);
		return t;
	}

	/**
	 * 删除属性
	 * 
	 * @param request
	 * @param response
	 * @param key
	 */
	public static void removeAttribute(HttpServerRequest request, HttpServerResponse response, String key) {
		String validateCodeKey = TokenProvider.getHeader(request, response, key);
		if (StringUtils.isNotBlank(validateCodeKey)) {
			CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times).delete(validateCodeKey);
		}
	}
	
	// ------------------ cookie ops ---------------------------
	/**
	 * 设置 Cookie
	 * 
	 * @param response
	 * @param name
	 * @param value
	 * @param maxAge
	 * @param path
	 * @param domain
	 * @param secure
	 */
	public static void setHeader(HttpServerRequest request, HttpServerResponse response, String name, String value) {
		if (StringUtils.isBlank(name)
				|| StringUtils.isBlank(value)) {
			throw new BaseRuntimeException("header name and value is not null");
		}
		response.header(name, value);
	}

	/**
	 * 获得指定Cookie的值
	 * 
	 * @param request
	 * @param response
	 * @param name
	 * @param isRemove
	 * @return
	 */
	public static String getHeader(HttpServerRequest request, HttpServerResponse response, String name) {
		return request.getRequestHeader(name);
	}
}