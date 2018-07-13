package com.swak.security.web.cookie;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

import com.swak.Constants;
import com.swak.cache.AsyncCache;
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
 * cookie 的时间的误解： maxage 不能设置为 -1，必须大于等于 0。
 * 
 * Secure ： 这说明这个cookie 必须通过https 发送给服务器，通过http 则不能发送
 * @author root
 */
public class CookieProvider {
	
	
	// 异步操作
	private static AsyncCache<Object> COOKIES_CACHE;
	private static AsyncCache<Object> getCache() {
	   if (COOKIES_CACHE == null) {
		   COOKIES_CACHE = CacheManagers.getCache(Constants.token_cache_name, Constants.cookie_cache_times).async();
	   }
	   return COOKIES_CACHE;
	}

	/**
	 * 设置属性
	 * 
	 * @param request
	 * @param response
	 * @param key
	 * @param value
	 */
	public static CompletionStage<Void> setAttribute(HttpServerRequest request, HttpServerResponse response, String key, Object value) {
		String validateCodeKey = CookieProvider.getCookie(request, response, key, false);
		if (StringUtils.isBlank(validateCodeKey)) {
			validateCodeKey = UUID.randomUUID().toString();
		}
		final String cacheKey = validateCodeKey;
		return getCache().putObject(cacheKey, value).thenAccept(s -> {
			TokenProvider.setHeader(request, response, key, cacheKey);
			CookieProvider.setCookie(request, response, key, cacheKey, Long.MIN_VALUE, null, null, false);
		});
	}

	/**
	 * 获取属性
	 * 
	 * @param request
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> CompletionStage<T> getAttribute(HttpServerRequest request, HttpServerResponse response, String key) {
		try {
			String _key = CookieProvider.getCookie(request, response, key, false);
			if (StringUtils.isBlank(_key)) {
				_key = TokenProvider.getHeader(request, response, key);
			}
			if (StringUtils.isNotBlank(_key)) {
				return (CompletionStage<T>)getCache().getObject(_key);
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
	public static <T> CompletionStage<T> getAndClearAttribute(HttpServerRequest request, HttpServerResponse response, String key) {
		CompletionStage<T> t = CookieProvider.getAttribute(request, response, key);
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
			getCache().delete(_key);
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
			Long maxAge, String path, String domain, Boolean secure) {
		SimpleCookie cookie = new SimpleCookie(name, Encodes.urlEncode(value));
		if (maxAge != null)
			cookie.setMaxAge(maxAge);
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
		SimpleCookie cookie = new SimpleCookie(name, Constants.deleted_cookie_value);
		if (StringUtils.isNotEmpty(path))
			cookie.setPath(path);
		if (StringUtils.isNotEmpty(domain))
			cookie.setDomain(domain);
		cookie.removeFrom(request, response);
	}
}