package com.swak.reactivex.transport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.swak.reactivex.HttpConst;
import com.swak.reactivex.HttpServerRequest;
import com.swak.reactivex.HttpServerResponse;
import com.swak.reactivex.transport.channel.ServerContextHandler;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

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
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;

/**
 * 响应
 * @author lifeng
 */
public abstract class HttpServerResponseOperation extends HttpServerRequestOperation implements HttpServerResponse{

	private ByteArrayOutputStream os;
	private ByteBuffer buffer = null;
	private HttpHeaders headers = new DefaultHttpHeaders(false);
	private Set<Cookie> cookies = new HashSet<>(4);
	private HttpResponseStatus status = HttpResponseStatus.OK;
	private CharSequence contentType = null;
	private int contentSize;
	private boolean closed;
	
	/**
	 * 请求
	 * @return
	 */
	public abstract HttpServerRequest getRequest();
	
    /**
     * 状态码
     * @return
     */
	public HttpResponseStatus getStatus() {
		return status;
	}

	/**
	 * 设置状态码
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponseOperation status(HttpResponseStatus status) {
		this.status = status;
		return this;
	}
	
	/**
	 * 500
	 * @return
	 */
	public HttpServerResponseOperation error() {
		return this.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}
	
	/**
	 * 404
	 * @return
	 */
	public HttpServerResponseOperation notFound() {
		return this.status(HttpResponseStatus.NOT_FOUND);
	}
	
	/**
	 * 401
	 * @return
	 */
	public HttpServerResponseOperation unauthorized() {
		return this.status(HttpResponseStatus.UNAUTHORIZED);
	}
	
	/**
	 * 301
	 * @return
	 */
	public HttpServerResponseOperation redirect() {
		return this.status(HttpResponseStatus.MOVED_PERMANENTLY);
	}
	
	/**
	 * 301
	 * @return
	 */
	public HttpServerResponseOperation ok() {
		return this.status(HttpResponseStatus.OK);
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponseOperation json() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponseOperation text() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponseOperation html() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_HTML);
		return this;
	}
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponseOperation xml() {
		headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_XML);
		return this;
	}
	
	/**
	 * 自动判断类型
	 * 只判断这两种类型
	 */
	@Override
	public HttpServerResponse accept() {
		String acceptType = this.getRequestHeader(HttpHeaderNames.ACCEPT.toString());
		if (!StringUtils.isBlank(acceptType) && StringUtils.contains(acceptType, "text/html")) {
			return this.html();
		} else if (!StringUtils.isBlank(acceptType) && StringUtils.contains(acceptType, "text/plain")) {
			return this.text();
		}
		return this;
	}

	/**
	 * 设置浏览器缓存，默认是无缓存
	 * @return
	 */
	public HttpServerResponseOperation cache(int maxAge) {
		headers.set(HttpHeaderNames.CACHE_CONTROL,StringUtils.format("%s:%s", HttpHeaderValues.MAX_AGE, maxAge));
		return this;
	}

	/**
	 * 设置内容类型
	 * 
	 * @param contentType
	 * @return
	 */
	public HttpServerResponseOperation contentType(CharSequence contentType) {
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
	public HttpServerResponseOperation header(CharSequence name, CharSequence value) {
		this.headers.set(name, value);
		return this;
	}

	/**
	 * 设置cookie
	 * 
	 * @param cookie
	 * @return
	 */
	public HttpServerResponseOperation cookie(Cookie cookie) {
		this.cookies.add(cookie);
		return this;
	}

	/**
	 * 删除cookie
	 * 
	 * @param name
	 * @return
	 */
	public HttpServerResponseOperation removeCookie(String name) {
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
	public <T> HttpServerResponseOperation buffer(T content) {
		if (buffer != null) {
			buffer.clear();
		}
		byte[] bytes = String.valueOf(content).getBytes(HttpConst.DEFAULT_CHARSET);
		buffer = ByteBuffer.wrap(bytes);
		return this;
	}
	
	/**
	 * 输出数据
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	public <T> HttpServerResponseOperation orJsonBuffer(T content) {
		if (headers.contains(HttpHeaderNames.CONTENT_TYPE)
				&& headers.get(HttpHeaderNames.CONTENT_TYPE).equals(HttpConst.APPLICATION_JSON.toString())) {
			this.buffer(content);
		}
		return this;
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
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, buffer);
		
		if (!headers.contains(HttpHeaderNames.CONTENT_TYPE)) {
			headers.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		}
		if (!headers.contains(HttpHeaderNames.CACHE_CONTROL)) {
			headers.set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
		}
		contentSize = response.content().readableBytes();
		headers.set(HttpHeaderNames.CONTENT_LENGTH, contentSize);
		headers.set(HttpHeaderNames.DATE, ServerContextHandler.date);
		headers.set(HttpConst.X_POWER_BY, HttpConst.VERSION);
		if (!headers.contains(HttpHeaderNames.SERVER)) {
			headers.set(HttpHeaderNames.SERVER, HttpConst.VERSION);
		}
		if (this.cookies.size() > 0) {
			this.cookies.forEach(cookie -> headers.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
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
		super.close();
		this.closed = true;
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
	}
	
	/**
	 * 输出
	 * 只能执行一次
	 */
	protected void out() {
		
		// 只能执行一次
		if (this.closed) {
			return;
		}
		
		// 通道已经关闭
		if (!channel().isActive()) {
			channel().close();
		}
		
		HttpResponse _response = this.render();
		boolean keepAlive = isKeepAlive();
		if (!keepAlive) {
			channel().writeAndFlush(_response);
			channel().close();
		} else {
			_response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			channel().writeAndFlush(_response);
		}
	}
	
	/**
	 * 输出错误信息
	 * @param code
	 * @throws UnsupportedEncodingException
	 */
	protected void out(String msg) {
		this.buffer(msg);
		this.out();
	}
}
