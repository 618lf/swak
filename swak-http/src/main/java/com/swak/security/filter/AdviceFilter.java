package com.swak.security.filter;

import java.io.IOException;

import com.swak.http.Filter;
import com.swak.http.FilterChain;
import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;

/**
 * 基本的filter
 * @author lifeng
 */
public abstract class AdviceFilter implements Filter {

	/**
	 * 执行前
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	protected boolean preHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return true;
	}

	/**
	 * 执行后(没啥用)
	 * 
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	protected void postHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
	}

	/**
	 * 最后执行
	 * @param request
	 * @param response
	 * @param exception
	 * @throws Exception
	 */
	protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, Exception exception) throws Exception {
	}
	
	/**
	 * 执行后面的过滤器
	 * 
	 * @param request
	 * @param response
	 * @param chain
	 * @throws Exception
	 */
	protected void executeChain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws Exception {
		chain.doFilter(request, response);
	}

	/**
	 * 执行逻辑
	 * @param request
	 * @param response
	 * @param chain
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) {

		Exception exception = null;

		try {
			
			// 执行前返回是否需要执行后面的逻辑
			boolean continueChain = preHandle(request, response);
			if (continueChain) {
				executeChain(request, response, chain);
			}
			
			// 这个没用了
			postHandle(request, response);
		} catch (Exception e) {
			exception = e;
		} finally {
			cleanup(request, response, exception);
		}
	}
    
	/**
	 * 这个还不知到是干嘛的
	 * @param request
	 * @param response
	 * @param existing
	 * @throws ServletException
	 * @throws IOException
	 */
	protected void cleanup(HttpServletRequest request, HttpServletResponse response,
			Exception existing) {
		Exception exception = existing;
		try {
			afterCompletion(request, response, exception);
		} catch (Exception e) {
			if (exception == null) {
				exception = e;
			}
		}
		if (exception != null) {
			throw new RuntimeException(exception);
		}
	}
}
