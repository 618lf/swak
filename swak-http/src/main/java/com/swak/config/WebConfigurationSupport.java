package com.swak.config;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;

import com.swak.common.eventbus.system.SystemEventPublisher;
import com.swak.reactivex.handler.DefaultWebExceptionHandler;
import com.swak.reactivex.handler.ExceptionHandlingWebHandler;
import com.swak.reactivex.handler.FilteringWebHandler;
import com.swak.reactivex.handler.HttpWebHandlerAdapter;
import com.swak.reactivex.handler.WebExceptionHandler;
import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebHandler;
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

/**
 * web 相关的服务配置
 * @author lifeng
 */
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
	
	// ------------- conversionService --------------
	@Bean
	public FormattingConversionService conversionService() {
		FormattingConversionService service = new DefaultFormattingConversionService();
		addFormatters(service);
		return service;
	}
	
	protected void addFormatters(FormatterRegistry registry) {
		registry.addConverter(new DateFormatterConverter());
		registry.addConverter(new StringEscapeFormatterConverter());
	}
	
	// ---------- WebExceptionHandler ---------
	@Bean
	public WebExceptionHandler webExceptionHandler() {
		return new DefaultWebExceptionHandler();
	}
	
	// ---------- DispatcherHandler ---------
	@Bean
	public DispatcherHandler webHandler() {
		return new DispatcherHandler();
	}
	
	// ---------- httpHandler ---------
	@Bean
	public HttpWebHandlerAdapter httpHandler(DispatcherHandler webHandler) {
		SortedBeanContainer container = new SortedBeanContainer();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(container);
		WebHandler delegate = new FilteringWebHandler(webHandler, container.getFilters());
		delegate = new ExceptionHandlingWebHandler(delegate, container.getExceptionHandlers(), container.getEventPublisher());
		return new HttpWebHandlerAdapter(delegate);
	}
	
	// ---------- requestMapping ---------
	@Bean
	public RequestMappingHandlerMapping requestMappingHandlerMapping() {
		RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
		return mapping;
	}
	
	@Bean
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter(ConversionService conversionService) {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter(conversionService);
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
	
	// ---------- handlerResult ---------
	
	@Bean
	public HandlerResultHandler requestMappingHandlerResult() {
		HandlerResultHandler result = new RequestBodyHandlerResult();
		addMessageConverters(result);
		return result;
	}
	
	protected void addMessageConverters(HandlerResultHandler result) {
		result.addConverter(new StringHttpMessageConverter());
		result.addConverter(new Jaxb2RootElementHttpMessageConverter());
		result.addConverter(new JsonHttpMessageConverter());
	}
	
	// Autowire lists for @Bean + @Order
	private static class SortedBeanContainer {
		
		private List<WebFilter> filters = Collections.emptyList();

		private List<WebExceptionHandler> exceptionHandlers = Collections.emptyList();
		
		private SystemEventPublisher eventPublisher;

		@Autowired(required = false)
		public void setFilters(List<WebFilter> filters) {
			this.filters = filters;
		}

		public List<WebFilter> getFilters() {
			return this.filters;
		}

		@Autowired(required = false)
		public void setExceptionHandlers(List<WebExceptionHandler> exceptionHandlers) {
			this.exceptionHandlers = exceptionHandlers;
		}

		public List<WebExceptionHandler> getExceptionHandlers() {
			return this.exceptionHandlers;
		}

		public SystemEventPublisher getEventPublisher() {
			return eventPublisher;
		}

		@Autowired(required = false)
		public void setEventPublisher(SystemEventPublisher eventPublisher) {
			this.eventPublisher = eventPublisher;
		}
	}
}
