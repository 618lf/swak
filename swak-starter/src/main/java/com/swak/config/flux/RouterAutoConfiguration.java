package com.swak.config.flux;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.io.ResourceLoader;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.lang.Nullable;

import com.swak.config.freemarker.FreeMarkerAutoConfiguration;
import com.swak.config.jdbc.DataSourceAutoConfiguration;
import com.swak.config.jdbc.DataSourceTransactionManagerConfiguration;
import com.swak.exception.BaseRuntimeException;
import com.swak.flux.handler.DefaultWebExceptionHandler;
import com.swak.flux.handler.ExceptionHandlingWebHandler;
import com.swak.flux.handler.FilteringWebHandler;
import com.swak.flux.handler.HttpWebHandlerAdapter;
import com.swak.flux.handler.WebExceptionHandler;
import com.swak.flux.handler.WebFilter;
import com.swak.flux.handler.WebHandler;
import com.swak.flux.transport.server.HttpServerProperties;
import com.swak.flux.transport.server.ReactiveServer;
import com.swak.flux.web.DispatcherHandler;
import com.swak.flux.web.HandlerAdapter;
import com.swak.flux.web.HandlerMapping;
import com.swak.flux.web.HandlerResultHandler;
import com.swak.flux.web.converter.JaxbHttpMessageConverter;
import com.swak.flux.web.converter.JsonHttpMessageConverter;
import com.swak.flux.web.converter.StringHttpMessageConverter;
import com.swak.flux.web.converter.TemplateHttpMessageConverter;
import com.swak.flux.web.formatter.DateFormatterConverter;
import com.swak.flux.web.formatter.StringEscapeFormatterConverter;
import com.swak.flux.web.function.HandlerFunctionAdapter;
import com.swak.flux.web.function.RouterFunctionMapping;
import com.swak.flux.web.interceptor.HandlerInterceptor;
import com.swak.flux.web.method.RequestMappingHandlerAdapter;
import com.swak.flux.web.method.RequestMappingHandlerMapping;
import com.swak.flux.web.result.RequestBodyHandlerResult;
import com.swak.flux.web.statics.StaticHandler;
import com.swak.flux.web.statics.StaticHandlerMapping;
import com.swak.flux.web.statics.StaticHanlderAdapter;
import com.swak.freemarker.FreeMarkerConfigurer;
import com.swak.utils.Lists;

/**
 * Web 服务配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass(ReactiveServer.class)
@AutoConfigureAfter({ FreeMarkerAutoConfiguration.class, SecurityAutoConfiguration.class,
		DataSourceAutoConfiguration.class, DataSourceTransactionManagerConfiguration.class })
public class RouterAutoConfiguration implements ApplicationContextAware {
	
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

	// ---------- staticMapping ---------
	@Bean
	public StaticHandlerMapping staticHandlerMapping(HttpServerProperties properties) {
		ResourceLoader resourceLoader = this.applicationContext;
		StaticHandler staticHandler = new StaticHandler(resourceLoader);
		try {

			// 注册静态资源
			if (properties.getStatics() != null && properties.getStatics().length > 0) {
				staticHandler.setLocationValues(Lists.newArrayList(properties.getStatics()));
			}

			staticHandler.afterPropertiesSet();
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
		StaticHandlerMapping mapping = new StaticHandlerMapping();
		mapping.setStaticHandler(staticHandler);
		return mapping;
	}

	@Bean
	public StaticHanlderAdapter staticHanlderAdapter() {
		StaticHanlderAdapter adapter = new StaticHanlderAdapter();
		return adapter;
	}

	// ---------- handlerResult ---------
	@Bean
	@ConditionalOnBean(FreeMarkerConfigurer.class)
	public TemplateHttpMessageConverter templateHttpMessageConverter(FreeMarkerConfigurer configurer) {
		return new TemplateHttpMessageConverter(configurer.getConfiguration());
	}

	@Bean
	public HandlerResultHandler requestMappingHandlerResult(
			ObjectProvider<TemplateHttpMessageConverter> httpMessageConverter) {
		HandlerResultHandler result = new RequestBodyHandlerResult();
		TemplateHttpMessageConverter converter = httpMessageConverter.getIfAvailable();
		if (converter != null) {
			result.addConverter(converter);
		}
		addMessageConverters(result);
		return result;
	}

	protected void addMessageConverters(HandlerResultHandler result) {
		result.addConverter(new StringHttpMessageConverter());
		result.addConverter(new JaxbHttpMessageConverter());
		result.addConverter(new JsonHttpMessageConverter());
	}

	// ---------- DispatcherHandler ---------
	@Bean
	public DispatcherHandler webHandler() {
		return new DispatcherHandler();
	}

	// ---------- WebExceptionHandler ---------
	@Bean
	public WebExceptionHandler webExceptionHandler() {
		return new DefaultWebExceptionHandler();
	}

	// ---------- httpHandler ---------
	@Bean
	public HttpWebHandlerAdapter httpHandler(DispatcherHandler webHandler) {
		SortedBeanContainer container = new SortedBeanContainer();
		applicationContext.getAutowireCapableBeanFactory().autowireBean(container);

		// Dispatcher Handler
		webHandler.setMappings(Collections.unmodifiableList(container.getMappings()));
		webHandler.setAdapters(Collections.unmodifiableList(container.getAdapters()));
		webHandler.setResultHandlers(Collections.unmodifiableList(container.getResultHandlers()));
		webHandler.setInterceptors(Collections.unmodifiableList(container.getInterceptors()));

		// Http Handler
		WebHandler delegate = new FilteringWebHandler(webHandler, container.getFilters());
		delegate = new ExceptionHandlingWebHandler(delegate, container.getExceptionHandlers());
		return new HttpWebHandlerAdapter(delegate);
	}

	// Autowire lists for @Bean + @Order
	private static class SortedBeanContainer {

		private List<HandlerMapping> mappings = Collections.emptyList();
		private List<HandlerAdapter> adapters = Collections.emptyList();
		private List<HandlerResultHandler> resultHandlers = Collections.emptyList();
		private List<WebFilter> filters = Collections.emptyList();
		private List<WebExceptionHandler> exceptionHandlers = Collections.emptyList();
		private List<HandlerInterceptor> interceptors = Collections.emptyList();

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

		public List<HandlerMapping> getMappings() {
			return mappings;
		}

		@Autowired(required = false)
		public void setMappings(List<HandlerMapping> mappings) {
			this.mappings = mappings;
		}

		public List<HandlerAdapter> getAdapters() {
			return adapters;
		}

		@Autowired(required = false)
		public void setAdapters(List<HandlerAdapter> adapters) {
			this.adapters = adapters;
		}

		public List<HandlerResultHandler> getResultHandlers() {
			return resultHandlers;
		}

		@Autowired(required = false)
		public void setResultHandlers(List<HandlerResultHandler> resultHandlers) {
			this.resultHandlers = resultHandlers;
		}

		public List<HandlerInterceptor> getInterceptors() {
			return interceptors;
		}

		@Autowired(required = false)
		public void setInterceptors(List<HandlerInterceptor> interceptors) {
			this.interceptors = interceptors;
		}
	}
}