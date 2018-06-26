package com.swak.http.handler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import org.asynchttpclient.AsyncCompletionHandler;

/**
 * 基本的返回 - 默认是utf-8 的编码
 * @author lifeng
 * @param <T>	
 */
public abstract class AbstractResponse<T> extends AsyncCompletionHandler<T>{

	protected Charset charset = StandardCharsets.UTF_8;
	protected Class<T> clazz;
	public <U extends AbstractResponse<T>> U use(Charset charset) {
		this.charset = charset;
		return as();
	}
	public <U extends AbstractResponse<T>> U use(Class<T> clazz) {
		this.clazz = clazz;
		return as();
	}
	/**
	 * gbk
	 * @return
	 */
	public <U extends AbstractResponse<T>> U gbk() {
		return this.use(Charset.forName("gbk"));
	}
	
	/**
	 * utf-8
	 * @return
	 */
	public <U extends AbstractResponse<T>> U utf8() {
		return this.use(Charset.forName("utf-8"));
	}
	
	@SuppressWarnings("unchecked")
	protected <U extends AbstractResponse<T>> U as() {
		return (U) this;
	}
}