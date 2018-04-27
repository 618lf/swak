package com.swak.reactivex.web;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.HandlerMethod;
import com.swak.mvc.method.HandlerResult;
import com.swak.reactivex.handler.WebHandler;

import io.reactivex.Observable;

/**
 * mvc 式的处理方式
 * @author lifeng
 */
public class DispatcherHandler implements WebHandler, ApplicationContextAware {
	
	
	public DispatcherHandler() { }
	
    public DispatcherHandler(ApplicationContext applicationContext) {
		
	}
	
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		
	}
	
	@Override
	public Observable<Void> handle(HttpServletRequest request, HttpServletResponse response) {
		return null;
	}
	
	public Observable<HandlerMethod> handleMappering(HttpServletRequest request, HttpServletResponse response) {
		
	}
	
	public Observable<HandlerResult> invokeHandler(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
		
	}
	
	public Observable<Void> handleResult(HttpServletRequest request, HttpServletResponse response, String result) {
		response.buffer(result);
	}
}
