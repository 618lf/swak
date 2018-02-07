package com.swak.mvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.HandlerInterceptor;
import com.swak.mvc.method.HandlerMethod;

public class HandlerExecutionChain {

	private static Logger logger = LoggerFactory.getLogger(HandlerExecutionChain.class);
	
	private final HandlerMethod handler;
	private HandlerInterceptor[] interceptors;
	private List<HandlerInterceptor> interceptorList;
	private int interceptorIndex = -1;
	
	/**
	 * Create a new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 */
	public HandlerExecutionChain(Object handler) {
		this(handler, (HandlerInterceptor[]) null);
	}
	
	/**
	 * Create a new HandlerExecutionChain.
	 * @param handler the handler object to execute
	 * @param interceptors the array of interceptors to apply
	 * (in the given order) before the handler itself executes
	 */
	public HandlerExecutionChain(Object handler, HandlerInterceptor... interceptors) {
		if (handler instanceof HandlerExecutionChain) {
			HandlerExecutionChain originalChain = (HandlerExecutionChain) handler;
			this.handler = originalChain.getHandler();
			this.interceptorList = new ArrayList<HandlerInterceptor>();
			CollectionUtils.mergeArrayIntoCollection(originalChain.getInterceptors(), this.interceptorList);
			CollectionUtils.mergeArrayIntoCollection(interceptors, this.interceptorList);
		}
		else {
			this.handler = (HandlerMethod)handler;
			this.interceptors = interceptors;
		}
	}

	public HandlerInterceptor[] getInterceptors() {
		return interceptors;
	}

	public void setInterceptors(HandlerInterceptor[] interceptors) {
		this.interceptors = interceptors;
	}

	public List<HandlerInterceptor> getInterceptorList() {
		return interceptorList;
	}

	public void setInterceptorList(List<HandlerInterceptor> interceptorList) {
		this.interceptorList = interceptorList;
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
			if (this.interceptors != null) {
				// An interceptor array specified through the constructor
				this.interceptorList.addAll(Arrays.asList(this.interceptors));
			}
		}
		this.interceptors = null;
		return this.interceptorList;
	}
	
	/**
	 * handler 前置处理器
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	boolean applyPreHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = 0; i < interceptors.length; i++) {
				HandlerInterceptor interceptor = interceptors[i];
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
	void applyPostHandle(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = interceptors.length - 1; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
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
	void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response, Exception ex)
			throws Exception {
		HandlerInterceptor[] interceptors = getInterceptors();
		if (!ObjectUtils.isEmpty(interceptors)) {
			for (int i = this.interceptorIndex; i >= 0; i--) {
				HandlerInterceptor interceptor = interceptors[i];
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
