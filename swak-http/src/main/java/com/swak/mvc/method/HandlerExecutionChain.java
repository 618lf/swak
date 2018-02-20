package com.swak.mvc.method;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.ExecutionChain;
import com.swak.mvc.method.HandlerInterceptor;
import com.swak.mvc.method.HandlerMethod;

/**
 * 带有 handler 和 interceptor 的执行链
 * @author lifeng
 */
public class HandlerExecutionChain implements ExecutionChain {

	private static Logger logger = LoggerFactory.getLogger(HandlerExecutionChain.class);
	
	private final HandlerMethod handler;
	private List<HandlerInterceptor> interceptorList;
	private int interceptorIndex = -1;
	
	public HandlerExecutionChain(HandlerMethod handler) {
		this.handler = handler;
	}

	public HandlerMethod getHandler() {
		return handler;
	}
	
	public void addInterceptor(HandlerInterceptor interceptor) {
		initInterceptorList().add(interceptor);
	}

	public void addInterceptors(HandlerInterceptor... interceptors) {
		if (!ObjectUtils.isEmpty(interceptors)) {
			initInterceptorList().addAll(Arrays.asList(interceptors));
		}
	}
	
	private List<HandlerInterceptor> initInterceptorList() {
		if (this.interceptorList == null) {
			this.interceptorList = new ArrayList<HandlerInterceptor>();
		}
		return this.interceptorList;
	}
	
	/**
	 * handler 前置处理器
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	public boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!ObjectUtils.isEmpty(interceptorList)) {
			int size = interceptorList.size();
			for (int i = 0; i < size; i++) {
				HandlerInterceptor interceptor = interceptorList.get(i);
				if (!interceptor.preHandle(request, response, this.handler)) {
					triggerAfterCompletion(request, response, null);
					return false;
				}
				this.interceptorIndex = i;
			}
		}
		return true;
	}
	
	/**
	 * 后置处理器
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	public void applyPostHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!ObjectUtils.isEmpty(interceptorList)) {
			int size = interceptorList.size();
			for (int i = size - 1; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptorList.get(i);
				interceptor.postHandle(request, response, this.handler);
			}
		}
	}
	
	/**
	 * 结束处理器
	 * @param request
	 * @param response
	 * @param ex
	 * @throws Exception
	 */
	public void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex)
			throws Exception {
		if (!ObjectUtils.isEmpty(interceptorList)) {
			for (int i = this.interceptorIndex; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptorList.get(i);
				try {
					interceptor.afterCompletion(request, response, this.handler, ex);
				}
				catch (Throwable ex2) {
					logger.error("HandlerInterceptor.afterCompletion threw exception", ex2);
				}
			}
		}
	}
}
