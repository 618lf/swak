package com.swak.security.web.cookie;

import java.util.UUID;

import com.swak.Constants;
import com.swak.cache.CacheManagers;
import com.swak.codec.Encodes;
import com.swak.reactivex.transport.http.SimpleCookie;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.web.token.TokenProvider;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.cookie.Cookie;

/**
 * cookie cache
 * 
 * key 放入 cookie ，value 放入缓存 cookie 是与用户相关的信息
 * 
 * @author root
 */
public class CookieProvider {

	/**
	 * 设置属性
	 * 
	 * @param request
	 * @param response
	 * @param key
	 * @param value
	 */
	public static void setAttribute(HttpServerRequest request, HttpServerResponse response, String key, Object value) {
		String validateCodeKey = CookieProvider.getCookie(request, response, key, false);
		if (StringUtils.isBlank(validateCodeKey)) {
			validateCodeKey = UUID.randomUUID().toString();
			CookieProvider.setCookie(request, response, key, validateCodeKey, -1, null, null, true);
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
			String _key = CookieProvider.getCookie(request, response, key, false);
			if (StringUtils.isBlank(_key)) {
				_key = TokenProvider.getHeader(request, response, key);
			}
			if (StringUtils.isNotBlank(_key)) {
				Object obj = CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times)
						.getObject(_key);
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
		T t = CookieProvider.getAttribute(request, response, key);
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
		String _key = CookieProvider.getCookie(request, response, key, true);
		if (StringUtils.isNotBlank(_key)) {
			CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times).delete(_key);
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
	public static void setCookie(HttpServerRequest request, HttpServerResponse response, String name, String value,
			Integer maxAge, String path, String domain, Boolean secure) {
		SimpleCookie cookie = new SimpleCookie(name, Encodes.urlEncode(value));
		if (maxAge != null)
			cookie.setMaxAge(maxAge.intValue());
		if (StringUtils.isNotEmpty(path))
			cookie.setPath(path);
		if (StringUtils.isNotEmpty(domain))
			cookie.setDomain(domain);
		if (secure != null)
			cookie.setSecure(secure);
		cookie.saveTo(request, response);
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
	public static String getCookie(HttpServerRequest request, HttpServerResponse response, String name,
			boolean isRemove) {
		String value = null;
		Cookie cookie = request.getCookie(name);
		if (cookie != null) {
			value = Encodes.urlDecode(cookie.value());
		}
		if (isRemove) {
			cookie.setMaxAge(0);
			response.cookie(cookie);
		}
		return value;
	}

	/**
	 * 移除
	 * 
	 * @param request
	 * @param response
	 * @param name
	 */
	public static void remove(HttpServerRequest request, HttpServerResponse response, String name, String path,
			String domain) {
		SimpleCookie cookie = new SimpleCookie(name, null);
		if (StringUtils.isNotEmpty(path))
			cookie.setPath(path);
		if (StringUtils.isNotEmpty(domain))
			cookie.setDomain(domain);
		cookie.removeFrom(request, response);
	}
}