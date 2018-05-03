package com.swak.config;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;

import com.swak.reactivex.handler.HttpWebHandlerAdapter;
import com.swak.reactivex.web.DispatcherHandler;
import com.swak.reactivex.web.HandlerResultHandler;
import com.swak.reactivex.web.converter.DateFormatterConverter;
import com.swak.reactivex.web.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.reactivex.web.converter.JsonHttpMessageConverter;
import com.swak.reactivex.web.converter.StringEscapeFormatterConverter;
import com.swak.reactivex.web.converter.StringHttpMessageConverter;
import com.swak.reactivex.web.function.HandlerFunctionAdapter;
import com.swak.reactivex.web.function.RouterFunctionMapping;
import com.swak.reactivex.web.method.RequestMappingHandlerAdapter;
import com.swak.reactivex.web.method.RequestMappingHandlerMapping;
import com.swak.reactivex.web.result.RequestBodyHandlerResult;

public class WebConfigurationSupport implements ApplicationContextAware {

	@Nullable
	private ApplicationContext applicationContext;
	
	@Override
	public void setApplicationContext(@Nullable ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
	
	@Nullable
	public final ApplicationContext getApplicationContext() {
		return this.applicationContext;
	}
	
	@Bean
	public DispatcherHandler webHandler() {
		return new DispatcherHandler();
	}
	
	@Bean
	public HttpWebHandlerAdapter httpHandler(DispatcherHandler webHandler) {
		return new HttpWebHandlerAdapter(webHandler);
	}
	
	// ---------- requestMapping ---------
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
		return mapping;
	}
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		return adapter;
	}
	
	// ---------- functionMapping ---------
	@Bean
	public RouterFunctionMapping routerFunctionMapping() {
		RouterFunctionMapping mapping = new RouterFunctionMapping();
		return mapping;
	}
	
	@Bean
	public HandlerFunctionAdapter handlerFunctionAdapter() {
		HandlerFunctionAdapter adapter = new HandlerFunctionAdapter();
		return adapter;
	}
	
	@Bean
	public FormattingConversionService conversionService() {
		FormattingConversionService service = new DefaultFormattingConversionService();
		addFormatters(service);
		return service;
	}
	
	@Bean
	public HandlerResultHandler requestMappingHandlerResult() {
		HandlerResultHandler result = new RequestBodyHandlerResult();
		addMessageConverters(result);
		return result;
	}
	
	/**
	 * 参数
	 * @param registry
	 */
	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new DateFormatterConverter());
		registry.addConverter(new StringEscapeFormatterConverter());
	}
	
	/**
	 * 返回值
	 * @param result
	 */
	protected void addMessageConverters(HandlerResultHandler result) {
		result.addConverter(new StringHttpMessageConverter());
		result.addConverter(new Jaxb2RootElementHttpMessageConverter());
		result.addConverter(new JsonHttpMessageConverter());
	}
}
