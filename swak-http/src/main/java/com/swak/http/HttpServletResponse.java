package com.swak.http;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.io.IOUtils;

import com.swak.http.metric.MetricCenter;
import com.swak.http.server.HttpServerChannelInitializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 响应
 * 
 * @author lifeng
 */
public class HttpServletResponse implements Closeable {

	private HttpServletRequest request;
	private ByteArrayOutputStream os;
	private ByteBuffer buffer = null;
	private HttpHeaders headers = new DefaultHttpHeaders(false);
	private Set<Cookie> cookies = new HashSet<>(4);
	private int statusCode = 200;
	private CharSequence contentType = null;
	private int contentSize;
	
	public HttpServletResponse(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * 返回状态吗
	 * 
	 * @return
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * 设置状态码
	 * 
	 * @param status
	 * @return
	 */
	public HttpServletResponse status(HttpResponseStatus status) {
		this.statusCode = status.code();
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServletResponse json() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServletResponse text() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServletResponse xml() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_XML);
		return this;
	}

	/**
	 * 设置内容类型
	 * 
	 * @param contentType
	 * @return
	 */
	public HttpServletResponse contentType(CharSequence contentType) {
		this.contentType = contentType;
		return this;
	}

	/**
	 * 获得内容类型
	 * 
	 * @return
	 */
	public String getContentType() {
		return null == this.contentType ? null : String.valueOf(this.contentType);
	}
	
	/**
	 * 获得内容的大小
	 * @return
	 */
	public int getContentSize() {
		return contentSize;
	}

	/**
	 * 返回所有headers
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		Map<String, String> map = new HashMap<>(this.headers.size());
		this.headers.forEach(header -> map.put(header.getKey(), header.getValue()));
		return map;
	}

	/**
	 * 设置header
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpServletResponse header(CharSequence name, CharSequence value) {
		this.headers.set(name, value);
		return this;
	}

	/**
	 * 设置cookie
	 * 
	 * @param cookie
	 * @return
	 */
	public HttpServletResponse cookie(com.swak.http.Cookie cookie) {
		Cookie nettyCookie = new io.netty.handler.codec.http.cookie.DefaultCookie(cookie.name(), cookie.value());
		if (cookie.domain() != null) {
			nettyCookie.setDomain(cookie.domain());
		}
		if (cookie.maxAge() > 0) {
			nettyCookie.setMaxAge(cookie.maxAge());
		}
		nettyCookie.setPath(cookie.path());
		nettyCookie.setHttpOnly(cookie.httpOnly());
		nettyCookie.setSecure(cookie.secure());
		this.cookies.add(nettyCookie);
		return this;
	}

	/**
	 * 删除cookie
	 * 
	 * @param name
	 * @return
	 */
	public HttpServletResponse removeCookie(String name) {
		Optional<Cookie> cookieOpt = this.cookies.stream().filter(cookie -> cookie.name().equals(name)).findFirst();
		cookieOpt.ifPresent(cookie -> {
			cookie.setValue("");
			cookie.setMaxAge(-1);
		});
		Cookie nettyCookie = new io.netty.handler.codec.http.cookie.DefaultCookie(name, "");
		nettyCookie.setMaxAge(-1);
		this.cookies.add(nettyCookie);
		return this;
	}

	/**
	 * 输出流
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		os = new ByteArrayOutputStream();
		return os;
	}

	/**
	 * 输出数据
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	public <T> void buffer(T content) {
		byte[] bytes = String.valueOf(content).getBytes(HttpConst.DEFAULT_CHARSET);
		buffer = ByteBuffer.wrap(bytes);
	}

	/**
	 * 只能调用一次
	 * 
	 * @return
	 * @throws IOException
	 */
	private HttpResponse render()  {
		byte[] _content = this.getContent();
		ByteBuf buffer = _content== null? Unpooled.EMPTY_BUFFER : Unpooled.wrappedBuffer(_content);
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.valueOf(statusCode), buffer);
		
		if (!headers.contains(HttpHeaderNames.CONTENT_TYPE)) {
			headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		}
		contentSize = response.content().readableBytes();
		headers.set(HttpHeaderNames.CONTENT_LENGTH, contentSize);
		headers.set(HttpHeaderNames.DATE, HttpServerChannelInitializer.date);
		headers.set(HttpConst.X_POWER_BY, HttpConst.VERSION);
		if (!headers.contains(HttpHeaderNames.SERVER)) {
			headers.set(HttpHeaderNames.SERVER, HttpConst.VERSION);
		}
		if (this.cookies.size() > 0) {
			this.cookies.forEach(cookie -> headers.add(HttpHeaderNames.SET_COOKIE,
					io.netty.handler.codec.http.cookie.ServerCookieEncoder.LAX.encode(cookie)));
		}
		response.headers().set(headers);
		return response;
	}

	/**
	 * 优先获取输出流中的数据
	 * 
	 * @return
	 * @throws IOException
	 */
	private byte[] getContent() {
		if (this.os != null) {
			return this.os.toByteArray();
		}
		if (this.buffer != null) {
			return this.buffer.array();
		}
		return null;
	}
	
	@Override
	public void close() throws IOException {
		if (os != null) {
			IOUtils.closeQuietly(os);
			os = null;
		}
		if (buffer != null) {
			buffer.clear();
			buffer = null;
		}
		if (headers != null) {
			headers.clear();
			headers = null;
		}
		if (cookies != null) {
			cookies.clear();
			cookies = null;
		}
		contentType = null;
		request = null;
	}
	
	/**
	 * 输出
	 * 只能执行一次
	 */
	public void out() {
		try {
			HttpResponse _response = this.render();
			boolean keepAlive = request.isKeepAlive();
			if (!keepAlive) {
				request.getChannel().writeAndFlush(_response);
				request.getChannel().close();
			} else {
				_response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
				request.getChannel().writeAndFlush(_response);
			}
		} finally {
			MetricCenter.responseSize(this != null ? this.getContentSize() : 0);
			IOUtils.closeQuietly(request);
			IOUtils.closeQuietly(this);
		}
	}
	
	/**
	 * 输出错误信息
	 * @param code
	 * @throws UnsupportedEncodingException
	 */
	public void out(String msg) {
		this.buffer(msg);
		this.out();
	}

	/**
	 * 构建一个响应
	 * 
	 * @param dateString
	 * @return
	 */
	public static HttpServletResponse build(HttpServletRequest request) {
		return new HttpServletResponse(request);
	}
}
