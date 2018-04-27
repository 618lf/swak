package com.swak.http;

import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.springframework.util.StringUtils;

import com.swak.mvc.annotation.RequestMethod;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;

public class HttpServletRequest implements Closeable {

	private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE); // Disk

	private ChannelHandlerContext channel;
	private FullHttpRequest _request;
	private HttpServletResponse response;
	private ByteBuf body;
	private String remoteAddress;
	private String uri;
	private String url;
	private String method;
	private boolean keepAlive;

	private Map<String, String> headers = null;
	private Map<String, Object> attributes = null;
	private Map<String, List<String>> parameters;
	private Map<String, Cookie> cookies;
	private Map<String, String> pathVariables = null;
	private InputStream is;

	/**
	 * 获取响应
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse() {
		return response;
	}
	
	/**
	 * 得到通道
	 * @return
	 */
	public ChannelHandlerContext getChannel() {
		return channel;
	}

	/**
	 * 获取请求的地址
	 * 
	 * @return
	 */
	public String getRequestURI() {
		return uri;
	}

	/**
	 * 获取请求的地址
	 * 
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
	public String getRequestMethod() {
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
		if (parameters == null) {
			this.parseParameter();
		}
		return parameters.get(name);
	}

	/**
	 * 获取的请求的参数
	 * 
	 * @return
	 */
	public Map<String, List<String>> getParameterMap() {
		if (parameters == null) {
			this.parseParameter();
		}
		return parameters;
	}

	/**
	 * 解析出请求参数
	 */
	private void parseParameter() {
		Map<String, List<String>> parameters = new QueryStringDecoder(_request.uri(), CharsetUtil.UTF_8).parameters();
		if (null != parameters) {
			this.parameters = new HashMap<>();
			this.parameters.putAll(parameters);
		}

		if (!RequestMethod.GET.name().equals(_request.method().name())) {
			HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, _request);
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
	public Iterator<String> getHeaderNames() {
		if (headers == null) {
			HttpHeaders httpHeaders = _request.headers();
			if (httpHeaders.size() > 0) {
				this.headers = new HashMap<>(httpHeaders.size());
				httpHeaders.forEach((header) -> headers.put(header.getKey(), header.getValue()));
			} else {
				this.headers = new HashMap<>();
			}
		}
		return headers.keySet().iterator();
	}

	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	public String getHeader(String name) {
		if (headers == null) {
			HttpHeaders httpHeaders = _request.headers();
			if (httpHeaders.size() > 0) {
				this.headers = new HashMap<>(httpHeaders.size());
				httpHeaders.forEach((header) -> headers.put(header.getKey(), header.getValue()));
			} else {
				this.headers = new HashMap<>();
			}
		}
		return headers.get(name);
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
		if (body == null) {
			body = _request.content().copy();
		}
		return this.body;
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
	public void setPathVariables(Map<String, String> pathVariables) {
		this.pathVariables = pathVariables;
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
		
		// 必须释放这个请求
		ReferenceCountUtil.release(_request);
		
		this._request = null;
		this.response = null;
		if (this.body != null) {
			this.body.clear();
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

	/**
	 * 构建响应
	 * 
	 * @param ctx
	 * @param fullHttpRequest
	 * @param response
	 * @return
	 */
	public static HttpServletRequest build(ChannelHandlerContext channel, FullHttpRequest fullHttpRequest) {
		
		// 构建请求
		HttpServletRequest httpRequest = new HttpServletRequest();
				
		// 测试需要
		if(channel != null && fullHttpRequest != null) {
			httpRequest.channel = channel;
			httpRequest._request = fullHttpRequest;
			httpRequest.keepAlive = HttpUtil.isKeepAlive(fullHttpRequest);
			String remoteAddress = channel.channel().remoteAddress().toString();
			httpRequest.remoteAddress = remoteAddress;
			httpRequest.url = fullHttpRequest.uri();
			int pathEndPos = httpRequest.url.indexOf('?');
			httpRequest.uri = pathEndPos < 0 ? httpRequest.url : httpRequest.url.substring(0, pathEndPos);
			httpRequest.method = fullHttpRequest.method().name();
		}
		
		// 构建响应
		HttpServletResponse response = HttpServletResponse.build(httpRequest);
		httpRequest.response = response;
		return httpRequest;
	}
}
