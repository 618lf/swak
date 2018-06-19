package com.swak.reactivex.transport.http.server;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.swak.reactivex.transport.NettyInbound;
import com.swak.reactivex.transport.http.Subject;
import com.swak.reactivex.transport.http.multipart.MultipartFile;

import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.cookie.Cookie;

public interface HttpServerRequest extends NettyInbound, Closeable {

	/**
	 * 获得身份
	 * @return
	 */
	Subject getSubject();
	
	/**
	 * 设置身份
	 * @param subject
	 */
    void setSubject(Subject subject);
	
	/**
	 * 获得响应
	 * @return
	 */
	HttpServerResponse getResponse();
	
	/**
	 * 获得服务名称
	 * @return
	 */
	String getServerName();
	
	/**
	 * 获取请求的地址
	 * 包含 请求的参数
	 * @return
	 */
	String getRequestURI();

	/**
	 * 获取请求的地址
	 * 不包含请求的参数
	 * @return
	 */
	String getRequestURL();

	/**
	 * 获取请求的方法
	 * 
	 * @return
	 */
	HttpMethod getRequestMethod();

	/**
	 * 获取客户端的地址
	 * 
	 * @return
	 */
	String getRemoteAddress();

	/**
	 * 是否长链接
	 * 
	 * @return
	 */
	boolean isKeepAlive();

	/**
	 * 请求的参数
	 * 
	 * @param name
	 * @return
	 */
	List<String> getParameterValues(String name);

	/**
	 * 获取的请求的参数
	 * 
	 * @return
	 */
	Map<String, List<String>> getParameterMap();

	/**
	 * 默认的都是UTF_8
	 * 
	 * @return
	 */
	String getCharacterEncoding();
	
	/**
	 * 设置属性
	 * 
	 * @param name
	 * @param value
	 */
	void removeAttribute(String name);

	/**
	 * 设置属性
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, Object value);

	/**
	 * 获取参数
	 * 
	 * @param name
	 * @return
	 */
	Object getAttribute(String name);

	/**
	 * heanders
	 * 
	 * @return
	 */
	Iterator<String> getRequestHeaderNames();

	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	String getRequestHeader(String name);
	
	/**
	 * 指定名称的header
	 * 
	 * @param name
	 * @return
	 */
	Map<String, String> getRequestHeaders();
	
	/**
	 * 说的所有的cookie
	 * 
	 * @return
	 */
	Iterator<Cookie> getCookies();

	/**
	 * 获得指定的cookie
	 * 
	 * @param name
	 * @return
	 */
	Cookie getCookie(String name);

	/**
	 * 请求的输入流
	 * 
	 * @return
	 */
	InputStream getInputStream();
	
	/**
	 * 上传的文件
	 * 
	 * @return
	 */
	Map<String, MultipartFile> getMultipartFiles();
	
	/**
	 * 路径的变量
	 * @return
	 */
	Map<String, String> getPathVariables();
	
	/**
	 * 路径的变量
	 * @return
	 */
    void addPathVariables(Map<String, String> pathVariables);

    /**
     * 关闭
     */
	void close() throws IOException;
}
