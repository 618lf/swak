package com.swak.mvc;

import org.springframework.context.ApplicationContext;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.http.Servlet;
import com.swak.mvc.method.ExecutionChain;
import com.swak.mvc.method.HandlerAdapter;
import com.swak.mvc.method.HandlerException;
import com.swak.mvc.method.HandlerMapping;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 分发Servlet
 * @author lifeng
 */
public class DispatcherServlet implements Servlet {

	private HandlerMapping handlerMapping;
	private HandlerAdapter handlerAdapter;
	private HandlerException handlerException;
	
	
	// 可以自定义如下名称的类来覆盖系统默认的
	private String handlerMappingBeanName = "handlerMapping";
	private String handlerAdapterBeanName = "handlerAdapter";
	private String handlerExceptionBeanName = "handlerException";
	
	/**
	 * 初始化
	 */
	@Override
	public void init(ApplicationContext applicationContext) {
		this.initStrategies(applicationContext);
	}
	
	/**
	 * 默认的策略
	 */
	private void initStrategies(ApplicationContext applicationContext) {
		
		// mapping
		try {
			handlerMapping = applicationContext.getBean(handlerMappingBeanName, HandlerMapping.class);
		}catch (Exception e) {
			handlerMapping = applicationContext.getBean(HandlerMapping.class);
		}
		
		// adapter
		try {
			handlerAdapter = applicationContext.getBean(handlerAdapterBeanName, HandlerAdapter.class);
		}catch (Exception e) {
			handlerAdapter = applicationContext.getBean(HandlerAdapter.class);
		}
		
		// exception
		try {
			handlerException = applicationContext.getBean(handlerExceptionBeanName, HandlerException.class);
		}catch (Exception e) {
			handlerException = applicationContext.getBean(HandlerException.class);
		}
	}
	
	/**
	 * 提供服务
	 */
	@Override
	public void doService(HttpServletRequest request, HttpServletResponse response) {
		ExecutionChain mappedHandler = null;
		Exception dispatchException = null;
		
		// 获取请求执行链
		try {
			try {
				mappedHandler = getHandler(request);
				if (mappedHandler == null || mappedHandler.getHandler() == null) {
					response.status(HttpResponseStatus.NOT_FOUND).text().buffer("request handler not found");
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
		} finally {
			try {
				mappedHandler.triggerAfterCompletion(request, response, dispatchException);
			} catch (Exception e) {}
			
			// response exception info
			this.processDispatchResult(request, response, mappedHandler, dispatchException);
		}
	}
	
	private ExecutionChain getHandler(HttpServletRequest request) throws Exception {
		ExecutionChain handler = handlerMapping.getHandler(request);
		if (handler != null) {
			return handler;
		}
		return null;
	}
	
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,
			ExecutionChain mappedHandler, Exception exception) {
		if (exception == null) {
			return;
		}
		
		// deal the exception
		Object value = handlerException.resolveException(request, response, mappedHandler.getHandler(), exception);
		
		// response the exception
		if (value != null) {
			response.status(HttpResponseStatus.INTERNAL_SERVER_ERROR).buffer(value.toString());
		}
		
		// 必须执行这个
		response.out();
	}

	@Override
	public void destroy() {
		
	}
}