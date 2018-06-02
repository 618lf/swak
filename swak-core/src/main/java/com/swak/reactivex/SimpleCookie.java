package com.swak.reactivex;

import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/**
 * Cookie
 *
 * @author biezhi 2017/6/1	
 */
public class SimpleCookie extends DefaultCookie {

	public static final String DELETED_COOKIE_VALUE = "deleteMe";

	public SimpleCookie(String name, String value) {
		super(name, value);
		this.setHttpOnly(true);
	}

	public void saveTo(HttpServerRequest request, HttpServerResponse response) {
		String cookieValue = ServerCookieEncoder.LAX.encode(this);
		addCookieHeader(response, cookieValue);
	}

	public void removeFrom(HttpServerRequest request, HttpServerResponse response) {
		this.setValue(DELETED_COOKIE_VALUE);
		String cookieValue = ServerCookieEncoder.LAX.encode(this);
		addCookieHeader(response, cookieValue);
	}

	private void addCookieHeader(HttpServerResponse response, String cookieValue) {
		response.header(HttpHeaderNames.SET_COOKIE, cookieValue);
	}
}