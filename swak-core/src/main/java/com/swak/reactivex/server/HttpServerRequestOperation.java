package com.swak.reactivex.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.swak.reactivex.Cookie;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.Subject;
import com.swak.utils.IOUtils;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;

public abstract class HttpServerRequestOperation implements HttpServerRequest {

	private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk
	
	private ByteBuf body;
	private String remoteAddress;
	private String uri;
	private String url;
	private HttpMethod method;
	private boolean keepAlive;

	private Map<String, String> headers = null;
	private Map<String, Object> attributes = null;
	private Map<String, List<String>> parameters;
	private Map<String, Cookie> cookies;
	private Map<String, String> pathVariables = null;
	private InputStream is;
	private Subject subject;

	
	/**
	 * 初始化
	 * @param channel
	 * @param fullHttpRequest
	 */
	protected void initRequest(Channel channel, FullHttpRequest request) {
		this.keepAlive = HttpUtil.isKeepAlive(request);
		String remoteAddress = channel.remoteAddress().toString();
		this.remoteAddress = remoteAddress;
		this.uri = request.uri();
		int pathEndPos = this.uri.indexOf('?');
		this.url = pathEndPos < 0 ? this.uri : this.uri.substring(0, pathEndPos);
		this.method = request.method();
		
		// 获取一些数据
		this.parseParameter(request);
		this.parseHeaders(request);
		this.parseBody(request);
	}
	
	/**
	 * 获得身份
	 * @return
	 */
	public Subject getSubject() {
		return this.subject;
	}
	
	/**
	 * 设置身份
	 * @param subject
	 */
    public void setSubject(Subject subject) {
		this.subject = subject;
	}
	
	/**
	 * 获得通道
	 * @return
	 */
	protected abstract Channel channel();
	
	/**
	 * 获得响应
	 * @return
	 */
	public abstract HttpServerResponse getResponse();
	
	/**
	 * 获取请求的地址
	 * 包含 请求的参数
	 * @return
	 */
	public String getRequestURI() {
		return uri;
	}

	/**
	 * 获取请求的地址
	 * 不包含请求的参数
	 * @return
	 */
	public String getRequestURL() {
		return url;
	}

	/**
	 * 获取请求的方法
	 * 
	 * @return
	 */
	public HttpMethod getRequestMethod() {
		return method;
	}

	/**
	 * 获取客户端的地址
	 * 
	 * @return
	 */
	public String getRemoteAddress() {
		return remoteAddress;
	}

	/**
	 * 是否长链接
	 * 
	 * @return
	 */
	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * 请求的参数
	 * 
	 * @param name
	 * @return
	 */
	public List<String> getParameterValues(String name) {
		return parameters.get(name);
	}

	/**
	 * 获取的请求的参数
	 * 
	 * @return
	 */
	public Map<String, List<String>> getParameterMap() {
		return parameters;
	}

	/**
	 * 解析出请求参数
	 */
	private void parseParameter(FullHttpRequest request) {
		Map<String, List<String>> parameters = new QueryStringDecoder(request.uri(), CharsetUtil.UTF_8).parameters();
		if (null != parameters) {
			this.parameters = new HashMap<>();
			this.parameters.putAll(parameters);
		}

		if (!HttpMethod.GET.name().equals(request.method().name())) {
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
			decoder.getBodyHttpDatas().forEach(this::parseData);
		}
	}

	/**
	 * 默认的都是UTF_8
	 * 
	 * @return
	 */
	public String getCharacterEncoding() {
		return CharsetUtil.UTF_8.name();
	}
	
	/**
	 * 设置属性
	 * 
	 * @param name
	 * @param value
	 */
	public void removeAttribute(String name) {
		if (attributes != null) {
			attributes.remove(name);
		}
	}

	/**
	 * 设置属性
	 * 
	 * @param name
	 * @param value
	 */
	public void setAttribute(String name, Object value) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		attributes.put(name, value);
	}

	/**
	 * 获取参数
	 * 
	 * @param name
	 * @return
	 */
	public Object getAttribute(String name) {
		if (attributes == null) {
			attributes = new HashMap<String, Object>();
		}
		return attributes.get(name);
	}

	/**
	 * heanders
	 * 
	 * @return
	 */
	public Iterator<String> getRequestHeaderNames() {
		return headers.keySet().iterator();
	}

	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	public String getRequestHeader(String name) {
		return headers.get(name);
	}
	
	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	public Map<String, String> getRequestHeaders() {
		return headers;
	}
	
	/**
	 * 解析 headers
	 * 最新的协议都是小写的
	 * @param request
	 */
	private void parseHeaders(FullHttpRequest request) {
		HttpHeaders httpHeaders = request.headers();
		if (httpHeaders.size() > 0) {
			this.headers = new HashMap<>(httpHeaders.size());
			httpHeaders.forEach((header) -> headers.put(header.getKey().toLowerCase(), header.getValue()));
		} else {
			this.headers = new HashMap<>();
		}
	}

	/**
	 * 说的所有的cookie
	 * 
	 * @return
	 */
	public Iterator<Cookie> getCookies() {
		if (this.cookies == null) {
			String cookie = headers.getOrDefault(HttpHeaderNames.COOKIE, "");
			cookie = cookie.length() > 0 ? cookie : headers.getOrDefault(HttpHeaderNames.COOKIE, "");
			if (!StringUtils.isEmpty(cookie)) {
				ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
			}
		}
		return this.cookies.values().iterator();
	}

	/**
	 * 获得指定的cookie
	 * 
	 * @param name
	 * @return
	 */
	public Cookie getCookie(String name) {
		if (this.cookies == null) {
			String cookie = headers.getOrDefault(HttpHeaderNames.COOKIE, "");
			cookie = cookie.length() > 0 ? cookie : headers.getOrDefault(HttpHeaderNames.COOKIE, "");
			if (!StringUtils.isEmpty(cookie)) {
				ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
			}
		}
		return this.cookies.get(name);
	}

	/**
	 * 请求体
	 * 
	 * @return
	 */
	public ByteBuf body() {
		return this.body;
	}
	
	/**
	 * 解析 body 数据
	 * @param request
	 */
	private void parseBody(FullHttpRequest request) {
		body = request.content();
	}

	/**
	 * 请求的输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		is =  new ByteArrayInputStream(this.body().array());
		return is;
	}
	
	/**
	 * 路径的变量
	 * @return
	 */
	public Map<String, String> getPathVariables() {
		return pathVariables;
	}
	
	/**
	 * 路径的变量
	 * @return
	 */
	public void addPathVariables(Map<String, String> pathVariables) {
		if (this.pathVariables != null) {
			this.pathVariables.putAll(pathVariables);
		} else {
			this.pathVariables = pathVariables;
		}
	}

	private void parseData(InterfaceHttpData data) {
		try {
			switch (data.getHttpDataType()) {
			case Attribute:
				Attribute attribute = (Attribute) data;
				String name = attribute.getName();
				String value = attribute.getValue();
				this.parameters.put(name, Collections.singletonList(value));
				break;
			default:
				break;
			}
		} catch (IOException e) {
		} finally {
			data.release();
		}
	}

	private void parseCookie(io.netty.handler.codec.http.cookie.Cookie nettyCookie) {
		Cookie cookie = new Cookie();
		cookie.name(nettyCookie.name());
		cookie.value(nettyCookie.value());
		cookie.httpOnly(nettyCookie.isHttpOnly());
		cookie.path(nettyCookie.path());
		cookie.domain(nettyCookie.domain());
		cookie.maxAge(nettyCookie.maxAge());
		this.cookies.put(cookie.name(), cookie);
	}

	@Override
	public void close() throws IOException {
		if (this.body != null) {
			this.body = null;
		}
		this.remoteAddress = null;
		this.uri = null;
		this.url = null;
		this.method = null;
		if (this.headers != null) {
			this.headers.clear();
			this.headers = null;
		}
		if (this.attributes != null) {
			this.attributes.clear();
			this.attributes = null;
		}
		if (this.parameters != null) {
			this.parameters.clear();
			this.parameters = null;
		}
		if (this.cookies != null) {
			this.cookies.clear();
			this.cookies = null;
		}
		if (this.pathVariables != null) {
			this.pathVariables.clear();
			this.pathVariables = null;
		}
		if (is != null) {
			IOUtils.closeQuietly(is);
		}
	}
}