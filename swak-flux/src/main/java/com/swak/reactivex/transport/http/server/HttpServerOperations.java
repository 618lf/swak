package com.swak.reactivex.transport.http.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.channel.ServerContextHandler;
import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.multipart.MultipartFile;
import com.swak.utils.IOUtils;
import com.swak.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.QueryStringDecoder;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.ServerCookieDecoder;
import io.netty.handler.codec.http.cookie.ServerCookieEncoder;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.util.CharsetUtil;
import io.netty.util.internal.ObjectUtil;
import reactor.core.publisher.Mono;

/**
 * HttpServer http 操作
 * 
 * 关于资源释放这块还有些疑问
 * 
 * @author lifeng
 */
public class HttpServerOperations extends ChannelOperations<HttpServerRequest, HttpServerResponse>
		implements HttpServerRequest, HttpServerResponse {

	// Disk if size exceed
	private static final HttpDataFactory HTTP_DATA_FACTORY = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

	// 初始化
	final String serverName;
	final FullHttpRequest request;

	private HttpServerOperations(Channel channel,
			BiFunction<? super HttpServerRequest, ? super HttpServerResponse, Mono<Void>> handler,
			ContextHandler context, String serverName, FullHttpRequest request) {
		super(channel, handler, context);
		this.serverName = serverName;
		this.request = request;
	}

	// 通用
	private ByteBuf content;

	// -------- 请求 --------------------
	private ByteBufInputStream is;
	private String remoteAddress;
	private String uri;
	private String url;
	private HttpMethod method;
	private boolean keepAlive;

	private Map<String, String> requestHeaders = null;
	private Map<String, Object> attributes = null;
	private Map<String, List<String>> parameters;
	private Map<String, Cookie> requestCookies;
	private Map<String, String> pathVariables = null;
	private Map<String, MultipartFile> files = null;
	private Subject subject;

	/**
	 * 初始化
	 * 
	 * @param channel
	 * @param fullHttpRequest
	 */
	protected void initRequest(Channel channel, FullHttpRequest request) {
		this.keepAlive = HttpUtil.isKeepAlive(request);
		String remoteAddress = channel.remoteAddress().toString();
		this.remoteAddress = remoteAddress;
		this.uri = StringUtils.removeStart(request.uri(), this.getServerName());
		int pathEndPos = this.uri.indexOf('?');
		this.url = pathEndPos < 0 ? this.uri : this.uri.substring(0, pathEndPos);
		this.method = request.method();

		// 获取一些数据
		this.parseParameter(request);
		this.parseHeaders(request);
		this.parseCookies();
		this.parseBody(request);

		// 释放引用
		request = null;
	}

	/**
	 * 获得身份
	 * 
	 * @return
	 */
	public Subject getSubject() {
		return this.subject;
	}

	/**
	 * 设置身份
	 * 
	 * @param subject
	 */
	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	/**
	 * 服务地址
	 */
	public String getServerName() {
		return serverName;
	}

	/**
	 * 获得响应
	 * 
	 * @return
	 */
	public HttpServerResponse getResponse() {
		return this;
	}

	/**
	 * 获取请求的地址 包含 请求的参数
	 * 
	 * @return
	 */
	public String getRequestURI() {
		return uri;
	}

	/**
	 * 获取请求的地址 不包含请求的参数
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
		return requestHeaders.keySet().iterator();
	}

	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	public String getRequestHeader(String name) {
		return requestHeaders.get(name);
	}

	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	public Map<String, String> getRequestHeaders() {
		return requestHeaders;
	}

	/**
	 * 解析 headers 最新的协议都是小写的
	 * 
	 * @param request
	 */
	private void parseHeaders(FullHttpRequest request) {
		HttpHeaders httpHeaders = request.headers();
		if (httpHeaders.size() > 0) {
			this.requestHeaders = new HashMap<>(httpHeaders.size());
			httpHeaders.forEach((header) -> requestHeaders.put(header.getKey().toLowerCase(), header.getValue()));
		} else {
			this.requestHeaders = new HashMap<>();
		}
	}

	/**
	 * 解析 Cookie
	 */
	private void parseCookies() {
		String cookie = requestHeaders.getOrDefault(HttpHeaderNames.COOKIE.toString(), "");
		if (!StringUtils.isEmpty(cookie)) {
			this.requestCookies = new HashMap<>();
			ServerCookieDecoder.LAX.decode(cookie).forEach(this::parseCookie);
		}
		if (this.requestCookies == null) {
			this.requestCookies = new HashMap<>();
		}
	}

	/**
	 * 说的所有的cookie
	 * 
	 * @return
	 */
	public Iterator<Cookie> getCookies() {
		return this.requestCookies.values().iterator();
	}

	/**
	 * 获得指定的cookie
	 * 
	 * @param name
	 * @return
	 */
	public Cookie getCookie(String name) {
		return this.requestCookies.get(name);
	}

	/**
	 * 解析 body 数据 先这样处理
	 * 
	 * @param request
	 */
	private void parseBody(FullHttpRequest request) {
		this.content = request.content();
	}

	/**
	 * 请求的输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		if (is == null) {
			is = new ByteBufInputStream(content);
		}
		return is;
	}
	
	/**
	 * 上传的文件
	 * 
	 */
	@Override
	public Map<String, MultipartFile> getMultipartFiles() {
		return this.files;
	}

	/**
	 * 路径的变量
	 * 
	 * @return
	 */
	public Map<String, String> getPathVariables() {
		return pathVariables;
	}

	/**
	 * 路径的变量
	 * 
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
			case FileUpload:
				FileUpload fileUpload = (FileUpload) data;
				parseFileUpload(fileUpload);
				break;
			default:
				break;
			}
		} catch (IOException e) {
			logger.error("parse request parameter error", e);
		} finally {
			data.release();
		}
	}

	private void parseFileUpload(FileUpload fileUpload) throws IOException {
		if (this.files == null) {
			this.files = new HashMap<>();
		}
		if (fileUpload.isCompleted()) {
			CharSequence contentType = HttpConst.getMimeType(fileUpload.getFilename());
			if (null == contentType) {
				contentType = URLConnection.guessContentTypeFromName(fileUpload.getFilename());
			}
			if (fileUpload.isInMemory()) {
				MultipartFile fileItem = new MultipartFile(fileUpload.getName(), fileUpload.getFilename(), contentType,
						fileUpload.length());

				ByteBuf byteBuf = fileUpload.getByteBuf();
				fileItem.setData(ByteBufUtil.getBytes(byteBuf));
				files.put(fileItem.getName(), fileItem);
			} else {
				MultipartFile fileItem = new MultipartFile(fileUpload.getName(), fileUpload.getFilename(), contentType,
						fileUpload.length());
				fileItem.setFile(fileUpload.getFile());
				files.put(fileItem.getName(), fileItem);
			}
		}
	}

	private void parseCookie(Cookie cookie) {
		this.requestCookies.put(cookie.name(), cookie);
	}

	// -------------- 响应 -------------------
	private ByteBufOutputStream os;
	private File file = null;
	private HttpHeaders responseHeaders = new DefaultHttpHeaders(false);
	private Set<Cookie> responseCookies = new HashSet<>(4);
	private HttpResponseStatus status = HttpResponseStatus.OK;
	private CharSequence contentType = null;
	private boolean closed;

	/**
	 * 获得请求
	 */
	public HttpServerRequest getRequest() {
		return this;
	}

	/**
	 * 状态码
	 * 
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
	public HttpServerResponse status(HttpResponseStatus status) {
		this.status = status;
		return this;
	}

	/**
	 * 500
	 * 
	 * @return
	 */
	public HttpServerResponse error() {
		return this.status(HttpResponseStatus.INTERNAL_SERVER_ERROR);
	}

	/**
	 * 404
	 * 
	 * @return
	 */
	public HttpServerResponse notFound() {
		return this.status(HttpResponseStatus.NOT_FOUND);
	}

	/**
	 * 401
	 * 
	 * @return
	 */
	public HttpServerResponse unauthorized() {
		return this.status(HttpResponseStatus.UNAUTHORIZED);
	}

	/**
	 * 301
	 * 
	 * @return
	 */
	public HttpServerResponse redirect() {
		return this.status(HttpResponseStatus.MOVED_PERMANENTLY);
	}

	/**
	 * 301
	 * 
	 * @return
	 */
	public HttpServerResponse ok() {
		return this.status(HttpResponseStatus.OK);
	}

	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponse json() {
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_JSON);
		return this;
	}

	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponse text() {
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		return this;
	}

	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponse html() {
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_HTML);
		return this;
	}

	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponse xml() {
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_XML);
		return this;
	}

	/**
	 * 自动判断类型 只判断这两种类型
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
	 * 
	 * @return
	 */
	public HttpServerResponse cache(int maxAge) {
		responseHeaders.set(HttpHeaderNames.CACHE_CONTROL,
				StringUtils.format("%s:%s", HttpHeaderValues.MAX_AGE, maxAge));
		return this;
	}

	/**
	 * 设置内容类型
	 * 
	 * @param contentType
	 * @return
	 */
	public HttpServerResponse contentType(CharSequence contentType) {
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
	 * 返回所有headers
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		Map<String, String> map = new HashMap<>(this.responseHeaders.size());
		this.responseHeaders.forEach(header -> map.put(header.getKey(), header.getValue()));
		return map;
	}

	/**
	 * 设置header
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	public HttpServerResponse header(CharSequence name, CharSequence value) {
		this.responseHeaders.set(name, value);
		return this;
	}

	/**
	 * 设置cookie
	 * 
	 * @param cookie
	 * @return
	 */
	public HttpServerResponse cookie(Cookie cookie) {
		this.responseCookies.add(cookie);
		return this;
	}

	/**
	 * 删除cookie
	 * 
	 * @param name
	 * @return
	 */
	public HttpServerResponse removeCookie(String name) {
		Optional<Cookie> cookieOpt = this.responseCookies.stream().filter(cookie -> cookie.name().equals(name))
				.findFirst();
		cookieOpt.ifPresent(cookie -> {
			cookie.setValue("");
			cookie.setMaxAge(-1);
		});
		Cookie nettyCookie = new io.netty.handler.codec.http.cookie.DefaultCookie(name, "");
		nettyCookie.setMaxAge(-1);
		this.responseCookies.add(nettyCookie);
		return this;
	}

	/**
	 * 输出流
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		if (os != null) {
			this.content.clear();
			IOUtils.closeQuietly(os);
		}
		os = new ByteBufOutputStream(this.content);
		return os;
	}

	/**
	 * 输出数据 数据会累计在一起
	 * 
	 * @param content
	 */
	public <T> HttpServerResponse buffer(T content) {
		ObjectUtil.checkNotNull(content, "content cannot null");
		if (content instanceof File) {
			file = (File) content;
		} else {
			this.content.clear();
			byte[] buffer = String.valueOf(content).getBytes(HttpConst.DEFAULT_CHARSET);
			if (this.content.capacity() < buffer.length) {
				this.content.capacity(buffer.length - this.content.capacity());
			}
			this.content.writeBytes(buffer);
		}
		return this;
	}

	/**
	 * 输出数据
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	public <T> HttpServerResponse orJsonBuffer(T content) {
		if (responseHeaders.contains(HttpHeaderNames.CONTENT_TYPE)
				&& responseHeaders.get(HttpHeaderNames.CONTENT_TYPE).equals(HttpConst.APPLICATION_JSON.toString())) {
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
	private HttpResponse render() {

		// content
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, this.content);

		// headers
		if (!responseHeaders.contains(HttpHeaderNames.CONTENT_TYPE)) {
			responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		}
		if (!responseHeaders.contains(HttpHeaderNames.CACHE_CONTROL)) {
			responseHeaders.set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
		}
		int contentSize = response.content().readableBytes();
		responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, contentSize);
		responseHeaders.set(HttpHeaderNames.DATE, ServerContextHandler.date);
		responseHeaders.set(HttpConst.X_POWER_BY, HttpConst.VERSION);
		if (!responseHeaders.contains(HttpHeaderNames.SERVER)) {
			responseHeaders.set(HttpHeaderNames.SERVER, HttpConst.VERSION);
		}
		if (this.responseCookies.size() > 0) {
			this.responseCookies.forEach(
					cookie -> responseHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
		}
		response.headers().set(responseHeaders);
		return response;
	}

	/**
	 * 输出 只能执行一次
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

		// 直接输出文件
		if (this.file != null) {
			this.outFile();
			return;
		}
		HttpResponse _response = this.render();
		boolean keepAlive = isKeepAlive();
		if (!keepAlive) {
			channel().writeAndFlush(_response).addListener(ChannelFutureListener.CLOSE);
		} else {
			_response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
			channel().writeAndFlush(_response);
		}
	}

	/**
	 * 输出 file -- 输出文件
	 */
	protected void outFile() {
		RandomAccessFile raf = null;
		long length = -1;
		try {
			raf = new RandomAccessFile(file, "r");
			length = raf.length();
			channel().write(new DefaultFileRegion(raf.getChannel(), 0, length));
		} catch (Exception e) {
			channel().writeAndFlush("ERR: " + e.getClass().getSimpleName() + ": " + e.getMessage() + '\n');
			return;
		} finally {
			if (length < 0 && raf != null) {
				IOUtils.closeQuietly(raf);
			}
		}
	}

	/**
	 * 输出错误信息
	 * 
	 * @param code
	 * @throws UnsupportedEncodingException
	 */
	protected void out(String msg) {
		this.buffer(msg);
		this.out();
	}

	// ------------- 处理请求 ------------------
	/**
	 * 处理请求 请求可以分为两部分，请求解析部分，响应部分 通过 onSubscribe 来分开，所以可以在 onSubscribe
	 * 做一些释放请求资源的事情（应该可以）
	 * 
	 * 目前使用 zore-copy 的方式使用共享资源，write 的时候会自动释放，不需要手动释放
	 */
	@Override
	protected void onHandlerStart() {
		try {
			this.initRequest(channel(), request);
			this.handler.apply(this, this).subscribe(this);
		} catch (Exception e) {
			this.onError(e);
		}
	}

	/**
	 * 完成请求
	 */
	@Override
	public void onComplete() {
		try {
			this.out();
		} finally {
			IOUtils.closeQuietly(this);
		}
	}

	/**
	 * 错误处理
	 */
	@Override
	public void onError(Throwable e) {
		this.getResponse().error().buffer(e);
		this.onComplete();
	}

	/**
	 * 清除资源
	 */
	@Override
	public void close() throws IOException {

		// 关闭请求数据
		if (this.files != null) {
			this.files.clear();
			this.files = null;
		}
		this.content = null;
		this.remoteAddress = null;
		this.uri = null;
		this.url = null;
		this.method = null;
		if (this.requestHeaders != null) {
			this.requestHeaders.clear();
			this.requestHeaders = null;
		}
		if (this.attributes != null) {
			this.attributes.clear();
			this.attributes = null;
		}
		if (this.parameters != null) {
			this.parameters.clear();
			this.parameters = null;
		}
		if (this.requestCookies != null) {
			this.requestCookies.clear();
			this.requestCookies = null;
		}
		if (this.pathVariables != null) {
			this.pathVariables.clear();
			this.pathVariables = null;
		}
		IOUtils.closeQuietly(is);

		// 关闭响应数据
		this.closed = true;
		IOUtils.closeQuietly(os);
		file = null;
		if (responseHeaders != null) {
			responseHeaders.clear();
			responseHeaders = null;
		}
		if (responseCookies != null) {
			responseCookies.clear();
			responseCookies = null;
		}
		contentType = null;
	}

	// --------------- 创建 ---------------------
	public static HttpServerOperations bind(Channel channel,
			BiFunction<? super HttpServerRequest, ? super HttpServerResponse, Mono<Void>> handler,
			ContextHandler context, String serverName, FullHttpRequest request) {
		return new HttpServerOperations(channel, handler, context, serverName, (FullHttpRequest) request);
	}
}