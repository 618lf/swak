package com.swak.reactivex.transport.http.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import com.swak.reactivex.transport.NettyOutbound;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;

/**
 * 响应
 * @author lifeng
 */
public interface HttpServerResponse extends NettyOutbound, Closeable {
	
	/**
	 * 请求
	 * @return
	 */
	HttpServerRequest getRequest();
	
    /**
     * 状态码
     * @return
     */
	HttpResponseStatus getStatus();

	/**
	 * 设置状态码
	 * 
	 * @param status
	 * @return
	 */
	HttpServerResponse status(HttpResponseStatus status);
	
	/**
	 * 500
	 * @return
	 */
	HttpServerResponse error();
	
	/**
	 * 404
	 * @return
	 */
	HttpServerResponse notFound();
	
	/**
	 * 401
	 * @return
	 */
	HttpServerResponse unauthorized();
	
	/**
	 * 301
	 * @return
	 */
	HttpServerResponse redirect();
	
	/**
	 * 200
	 * @return
	 */
	HttpServerResponse ok();
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	HttpServerResponse json();
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	HttpServerResponse text();
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	HttpServerResponse html();
	
	/**
	 * 设置输出格式
	 * 
	 * @param status
	 * @return
	 */
	HttpServerResponse xml();
	
	/**
	 * 输出base64数据
	 * @return
	 */
	HttpServerResponse base64();
	
	/**
	 * 输出文件类型
	 * @param mime
	 * @return
	 */
	HttpServerResponse mime(CharSequence mime);
	
	/**
	 * 根据 accept 判断接收的类型
	 * 只有返回string 或抛出异常时才会使用
	 * @return
	 */
	HttpServerResponse accept();
	
	/**
	 * 设置浏览器缓存，默认是无缓存
	 * @return
	 */
	HttpServerResponse cache(long maxAgeSeconds, long lastModifiedTime);

	/**
	 * 设置内容类型
	 * 
	 * @param contentType
	 * @return
	 */
	HttpServerResponse contentType(CharSequence contentType);
	
	/**
	 * 设置跨域
	 * 
	 * @param contentType
	 * @return
	 */
	HttpServerResponse allow(CharSequence contentType);

	/**
	 * 获得内容类型
	 * 
	 * @return
	 */
	String getContentType();
	
	/**
	 * 返回所有headers
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders();

	/**
	 * 设置header
	 * 
	 * @param name
	 * @param value
	 * @return
	 */
	HttpServerResponse header(CharSequence name, CharSequence value);

	/**
	 * 设置cookie
	 * 
	 * @param cookie
	 * @return
	 */
	HttpServerResponse cookie(Cookie cookie);

	/**
	 * 删除cookie
	 * 
	 * @param name
	 * @return
	 */
	HttpServerResponse removeCookie(String name);

	/**
	 * 输出流
	 * 暂时不支持这种写数据的方式，或者这种直接写道文件，然后通过文件做响应
	 * @return
	 */
	OutputStream getOutputStream();

	/**
	 * 输出数据
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	<T> HttpServerResponse buffer(T content);
	
	/**
	 * 如果是json则会使用这个数据
	 * 
	 * @param content
	 * @throws UnsupportedEncodingException
	 */
	<T> HttpServerResponse orJsonBuffer(T content);

	/**
	 * 关闭
	 * @throws IOException
	 */
	void close() throws IOException;
}
