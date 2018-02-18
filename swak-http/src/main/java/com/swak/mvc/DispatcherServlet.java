package com.swak.mvc;

import org.springframework.context.ApplicationContext;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Servlet;
import com.swak.mvc.method.HandlerAdapter;
import com.swak.mvc.method.HandlerException;
import com.swak.mvc.method.HandlerMapping;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 分发Servlet
 * @author lifeng
 */
public class DispatcherServlet implements Servlet {

	private ApplicationContext applicationContext;
	private HandlerMapping handlerMapping;
	private HandlerAdapter handlerAdapter;
	private HandlerException handlerException;
	
	/**
	 * 初始化
	 */
	@Override
	public void init(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
		this.initStrategies();
	}
	
	/**
	 * 默认的策略
	 */
	private void initStrategies() {
		handlerMapping = applicationContext.getBean(HandlerMapping.class);
		handlerAdapter = applicationContext.getBean(HandlerAdapter.class);
		handlerException = applicationContext.getBean(HandlerException.class);
	}
	
	/**
	 * 提供服务
	 */
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) {
		HandlerExecutionChain mappedHandler = null;
		Exception dispatchException = null;
		
		// 获取请求执行链
		try {
			try {
				mappedHandler = getHandler(request);
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					response.send(HttpResponseStatus.NOT_FOUND, "request not found");
					return;
				}
				
				// pre handler
				if (!mappedHandler.applyPreHandle(request, response)) {
					return;
				}
				
				// handler and response the request
				handlerAdapter.handle(request, response, mappedHandler.getHandler());
				
				// post handler
				mappedHandler.applyPostHandle(request, response);
			} catch (Exception e) {
				dispatchException = e;
			}
			
			// response exception info
			this.processDispatchResult(request, response, mappedHandler, dispatchException);
		} finally {
			try {
				mappedHandler.triggerAfterCompletion(request, response, dispatchException);
			} catch (Exception e) {}
		}
	}
	
	private HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		HandlerExecutionChain handler = handlerMapping.getHandler(request);
		if (handler != null) {
			return handler;
		}
		return null;
	}
	
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			HandlerExecutionChain mappedHandler, Exception exception) {
		if (exception == null) {
			return;
		}
		
		// deal the exception
		Object value = handlerException.resolveException(request, response, mappedHandler.getHandler(), exception);
		
		// response the exception
		if (value != null) {
			response.send(HttpResponseStatus.INTERNAL_SERVER_ERROR, value.toString());
		}
	}

	@Override
	public void destroy() {
		
	}
}