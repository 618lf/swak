package com.swak.flux.transport.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiFunction;

import com.swak.codec.Encodes;
import com.swak.exception.ErrorCode;
import com.swak.flux.transport.HttpConst;
import com.swak.flux.transport.Subject;
import com.swak.flux.transport.multipart.FileProps;
import com.swak.flux.transport.multipart.MimeType;
import com.swak.flux.transport.multipart.MultipartFile;
import com.swak.reactivex.transport.NettyPipeline;
import com.swak.reactivex.transport.channel.ChannelOperations;
import com.swak.reactivex.transport.channel.ContextHandler;
import com.swak.reactivex.transport.channel.GmtDateKit;
import com.swak.reactivex.transport.channel.ServerContextHandler;
import com.swak.utils.IOUtils;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.DefaultFileRegion;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpChunkedInput;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
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
import io.netty.util.ReferenceCountUtil;
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

	// 重定义删除资源的方式（）
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
	private HttpPostRequestDecoder postData = null;

	/**
	 * 初始化
	 * 
	 * @param channel
	 * @param fullHttpRequest
	 */
	protected void initRequest(Channel channel) {
		this.keepAlive = HttpUtil.isKeepAlive(request);
		String remoteAddress = channel.remoteAddress().toString();
		this.remoteAddress = remoteAddress;
		this.uri = StringUtils.removeStart(request.uri(), this.getServerName());
		int pathEndPos = this.uri.indexOf('?');
		this.url = pathEndPos < 0 ? this.uri : this.uri.substring(0, pathEndPos);
		this.method = request.method();
		this.parseParameter(request);
		this.parseHeaders(request);
		this.parseCookies();
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
	@Override
	public boolean isKeepAlive() {
		return keepAlive;
	}

	/**
	 * 是否有修改
	 */
	@Override
	public boolean ifModified(FileProps fileProps) {
		String ifModifiedSince = this.getRequestHeader(HttpHeaderNames.IF_MODIFIED_SINCE.toString());
		if (StringUtils.isNotBlank(ifModifiedSince)) {
			Date ifModifiedSinceDate = GmtDateKit.format(ifModifiedSince);
			long ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime() / 1000;
			if (ifModifiedSinceDateSeconds == fileProps.lastModifiedTime() / 1000) {

				// type
				this.mime(MimeType.getMimeType(fileProps.name()));

				// 304
				this.status(HttpResponseStatus.NOT_MODIFIED);

				// 设置头部
				this.header(HttpHeaderNames.CONTENT_LENGTH, String.valueOf(fileProps.size()));

				// 返回
				return true;
			}
		}
		return false;
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
	 * 请求的参数
	 * 
	 * @param name
	 * @return
	 */
	@Override
	public String getParameter(String name) {
		List<String> values = this.getParameterValues(name);
		return values != null && values.size() > 0 ? values.get(0) : null;
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
			postData = new HttpPostRequestDecoder(HTTP_DATA_FACTORY, request);
			int size = postData.getBodyHttpDatas().size();
			for (int i = 0; i < size; i++) {
				this.parseData(postData.next());
			}
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
	 * 请求的输入流
	 * 
	 * @return
	 */
	public InputStream getInputStream() {
		if (is == null) {
			is = new ByteBufInputStream(this.request.content());
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
				if (this.parameters.containsKey(name)) {
					List<String> values = this.parameters.get(name);
					if (!(values instanceof ArrayList)) {
						values = Lists.newArrayList(values.get(0));
					}
					this.parameters.put(name, values);
					this.parameters.get(name).add(value);
					break;
				}
				this.parameters.put(name, Collections.singletonList(value));
				break;
			case FileUpload:
				FileUpload fileUpload = (FileUpload) data;
				parseFileUpload(fileUpload);
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.error("parse request parameter error", e);
		}
	}

	private void parseFileUpload(FileUpload fileUpload) throws IOException {
		if (this.files == null) {
			this.files = new HashMap<>();
		}
		if (fileUpload.isCompleted()) {
			CharSequence contentType = MimeType.getMimeType(fileUpload.getFilename());
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
	private ByteBuf content = null;
	private FileProps fileProps = null;
	private HttpHeaders responseHeaders = new DefaultHttpHeaders(false);
	private Set<Cookie> responseCookies = new HashSet<>(4);
	private HttpResponseStatus status = HttpResponseStatus.OK;

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
	 * 302
	 * 
	 * @return
	 */
	public HttpServerResponse redirect() {
		return this.status(HttpResponseStatus.FOUND);
	}

	/**
	 * 200
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
	 * 将现有的数据已base64输出 必须先有数据
	 * 
	 * @param status
	 * @return
	 */
	public HttpServerResponse base64() {
		String mime = responseHeaders.get(HttpHeaderNames.CONTENT_TYPE);
		if (StringUtils.isNotBlank(mime)) {
			responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
			StringBuilder base64 = new StringBuilder("data:");
			base64.append(mime).append(";base64,").append(Encodes.encodeBase64(ByteBufUtil.getBytes(this.content)));
			return this.buffer(base64);
		}
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		return this.buffer(Encodes.encodeBase64(ByteBufUtil.getBytes(this.content)));
	}

	/**
	 * 输出指定的文件类型
	 */
	public HttpServerResponse mime(CharSequence mime) {
		responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, mime);
		return this;
	}

	/**
	 * 自动判断类型 只判断这两种类型
	 */
	@Override
	public HttpServerResponse accept() {
		String acceptType = this.getRequestHeader(HttpHeaderNames.ACCEPT.toString());
		if (StringUtils.isBlank(acceptType)) {
			return this;
		} else if (StringUtils.contains(acceptType, "application/json")) {
			return this.json();
		} else if (StringUtils.contains(acceptType, "text/html")) {
			return this.html();
		} else if (StringUtils.contains(acceptType, "text/plain")) {
			return this.text();
		}
		return this;
	}

	/**
	 * 跨域
	 */
	@Override
	public HttpServerResponse allow(CharSequence contentType) {
		return null;
	}

	/**
	 * 设置浏览器缓存，默认是无缓存
	 * 
	 * @return
	 */
	public HttpServerResponse cache(long maxAgeSeconds, long lastModifiedTime) {
		if (maxAgeSeconds > 0) {
			responseHeaders.set(HttpHeaderNames.CACHE_CONTROL,
					StringUtils.format("public, %s=%s", HttpHeaderValues.MAX_AGE, maxAgeSeconds));
		}
		if (lastModifiedTime > 0) {
			responseHeaders.set(HttpHeaderNames.LAST_MODIFIED, GmtDateKit.format(new Date(lastModifiedTime)));
		}
		return this;
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

	// ********尝试这种方式是否可行**********
	private void resetContent(int initialCapacity) {
		if (this.content != null) {
			ReferenceCountUtil.release(this.content);
			this.content = null;
		}
		if (initialCapacity > 0) {
			this.content = PooledByteBufAllocator.DEFAULT.buffer(initialCapacity);
		}
	}
	// ************************

	/**
	 * 注意： 每次调用都会清空数据
	 * 
	 * @return
	 */
	public OutputStream getOutputStream() {
		this.resetContent(256);
		if (os != null) {
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
		if (content instanceof FileProps) {
			fileProps = (FileProps) content;
		} else if (content instanceof ByteBuf) {
			ByteBuf buffer = (ByteBuf) content;
			this.resetContent(0); // 释放之前的资源
			this.content = buffer;
		} else if (content instanceof byte[]) {
			byte[] buffer = (byte[]) content;
			this.resetContent(buffer.length);
			this.content.writeBytes(buffer);
		} else {
			byte[] buffer = String.valueOf(content).getBytes(HttpConst.DEFAULT_CHARSET);
			this.resetContent(buffer.length);
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

	// ------------- 发送数据 ------------------

	/**
	 * 输出 只能执行一次 只能在 mono 的最后执行，不能中途调用
	 * 
	 * @throws IOException
	 */
	protected void send() {
		if (!this.channel().isActive()) {
			IOUtils.closeQuietly(this);
			return;
		}
		HttpResponse response = this.render();
		if (this.fileProps != null) {
			this.sendFile(response);
		} else {
			this.sendData(response);
		}
	}

	/**
	 * 只能调用一次
	 * 
	 * @return
	 * @throws IOException
	 */
	protected HttpResponse render() {

		// 返回的响应
		HttpResponse response = null;

		// 根据是否需要设置内容返回不通的响应
		if (this.fileProps != null) {
			response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
		} else {
			ByteBuf content = this.content == null ? Unpooled.EMPTY_BUFFER : this.content;
			response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
			int contentSize = content.readableBytes();
			responseHeaders.set(HttpHeaderNames.CONTENT_LENGTH, contentSize);
		}

		// 长链接
		if (isKeepAlive()) {
			responseHeaders.set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		}

		// 缓存控制
		if (!responseHeaders.contains(HttpHeaderNames.CACHE_CONTROL) && status == HttpResponseStatus.OK) {
			responseHeaders.set(HttpHeaderNames.CACHE_CONTROL, HttpHeaderValues.NO_CACHE);
		}

		// Public Headers
		if (!responseHeaders.contains(HttpHeaderNames.CONTENT_TYPE)) {
			responseHeaders.set(HttpHeaderNames.CONTENT_TYPE, HttpConst.APPLICATION_TEXT);
		}
		responseHeaders.set(HttpHeaderNames.DATE, ServerContextHandler.DATE);
		responseHeaders.set(HttpConst.X_POWER_BY, HttpConst.VERSION);
		responseHeaders.set(HttpHeaderNames.SERVER, HttpConst.VERSION);
		if (this.responseCookies.size() > 0) {
			this.responseCookies.forEach(
					cookie -> responseHeaders.add(HttpHeaderNames.SET_COOKIE, ServerCookieEncoder.LAX.encode(cookie)));
		}
		response.headers().set(responseHeaders);
		return response;
	}

	/**
	 * 输出数据
	 * 
	 * @param response
	 */
	protected void sendData(HttpResponse response) {
		ChannelFuture future = channel().writeAndFlush(response);
		future.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture complete) {
				sendComplete(complete);
			}
		});
	}

	/**
	 * 输出 file -- 输出文件
	 * 
	 * 可以 ChunkedFile https://blog.csdn.net/kenhins/article/details/45486301
	 */
	protected void sendFile(HttpResponse response) {
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileProps.size());
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, MimeType.getMimeType(fileProps.name()));
		channel().write(response);
		ChannelFuture lastContentFuture = null;
		if (channel().pipeline().get(NettyPipeline.HttpCompressor) != null
				|| channel().pipeline().get(NettyPipeline.SslHandler) != null) {
			lastContentFuture = channel().writeAndFlush(new HttpChunkedInput(fileProps.chunked()),
					channel().newProgressivePromise());
		} else {
			channel().write(new DefaultFileRegion(fileProps.channel(), 0, fileProps.size()),
					channel().newProgressivePromise());
			lastContentFuture = channel().writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
		}
		lastContentFuture.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture complete) {
				sendComplete(complete);
			}
		});
	}

	/**
	 * 发送结束
	 */
	protected void sendComplete(ChannelFuture complete) {
		if (!this.isKeepAlive()) {
			complete.channel().close();
		}
		IOUtils.closeQuietly(this);
	}

	// ------------- 处理请求 ------------------
	/**
	 * 处理请求 请求可以分为两部分，请求解析部分，响应部分 通过 onSubscribe 来分开，所以可以在 onSubscribe
	 * 做一些释放请求资源的事情（应该可以）
	 * 
	 * 目前使用 zore-copy 的方式使用共享资源，write 的时候会自动释放，不需要手动释放
	 */
	@Override
	public void onHandlerStart() {
		try {
			this.initRequest(channel());
			this.handler.apply(this, this).subscribe(this);
		} catch (Exception e) {
			logger.error("Handler Start Error:", e);
			this.onError(e);
		}
	}

	/**
	 * 完成请求, 发送消息完成后在关闭
	 */
	@Override
	public void onComplete() {
		this.send();
	}

	/**
	 * 错误处理
	 */
	@Override
	public void onError(Throwable e) {
		this.getResponse().json().buffer(ErrorCode.SERVER_ERROR.toJson());
		this.onComplete();
	}

	/**
	 * 清除资源
	 */
	@Override
	public void close() throws IOException {
		try {
			if (this.files != null) {
				this.files.clear();
				this.files = null;
			}
			IOUtils.close(fileProps);
			IOUtils.close(is);
			IOUtils.close(os);
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
			if (responseHeaders != null) {
				responseHeaders.clear();
				responseHeaders = null;
			}
			if (responseCookies != null) {
				responseCookies.clear();
				responseCookies = null;
			}
			if (this.content != null && this.content.refCnt() > 0) {
				ReferenceCountUtil.release(this.content);
			}
			if (this.postData != null) {
				this.postData.destroy();
			}
			if (this.request.refCnt() > 0) {
				ReferenceCountUtil.release(this.request);
			}
		} catch (Exception e) {
			logger.error("Clear Resource Rrror:", e);
		}
	}

	// --------------- 创建 ---------------------
	public static HttpServerOperations bind(Channel channel,
			BiFunction<? super HttpServerRequest, ? super HttpServerResponse, Mono<Void>> handler,
			ContextHandler context, String serverName, FullHttpRequest request) {
		return new HttpServerOperations(channel, handler, context, serverName, (FullHttpRequest) request);
	}
}