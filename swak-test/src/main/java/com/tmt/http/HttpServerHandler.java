package com.tmt.http;

import java.util.List;

import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

import com.swak.common.utils.Lists;
import com.swak.reactivex.handler.ExceptionHandlingWebHandler;
import com.swak.reactivex.handler.FilteringWebHandler;
import com.swak.reactivex.handler.HttpHandler;
import com.swak.reactivex.handler.HttpWebHandlerAdapter;
import com.swak.reactivex.handler.WebHandler;
import com.swak.reactivex.server._HttpServerOptions;
import com.swak.reactivex.web.DispatcherHandler;
import com.swak.reactivex.web.method.HandlerAdapter;
import com.swak.reactivex.web.method.HandlerMapping;
import com.swak.reactivex.web.method.HandlerResultHandler;
import com.swak.reactivex.web.method.RequestBodyHandlerResult;
import com.swak.reactivex.web.method.RequestMappingHandlerAdapter;
import com.swak.reactivex.web.method.RequestMappingHandlerMapping;

/**
 * 测试入口
 * @author lifeng
 */
public class HttpServerHandler {
	
	public static ConversionService buildConversionService() {
		return new DefaultFormattingConversionService();
	}
	
	public static List<HandlerMapping> buildHandlerMappings() {
		RequestMappingHandlerMapping mapping = new RequestMappingHandlerMapping();
		List<HandlerMapping> mappings = Lists.newArrayList();
		mappings.add(mapping);
		
		// controller
		// Object handler = new HelloController();
		// mapping.registryMapping(handler);
		return mappings;
	}
	
	public static List<HandlerAdapter> buildHandlerAdapters() {
		RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
		List<HandlerAdapter> adapters  = Lists.newArrayList();
		adapters.add(adapter);
		
		adapter.initArgumentResolvers(buildConversionService());
		return adapters;
	}
	
	public static List<HandlerResultHandler> buildResultHandlers() {
		RequestBodyHandlerResult result = new RequestBodyHandlerResult();
		List<HandlerResultHandler> results  = Lists.newArrayList();
		results.add(result);
		
		result.initValueResolvers();
		return results;
	}
	
	public static WebHandler buildWebHandler() {
		List<HandlerMapping> mappings = buildHandlerMappings();
		List<HandlerAdapter> adapters  = buildHandlerAdapters();
		List<HandlerResultHandler> results  = buildResultHandlers();
		DispatcherHandler handler = new DispatcherHandler();
		handler.setMappings(mappings);
		handler.setAdapters(adapters);
		handler.setResultHandlers(results);
		return handler;
	}
	
	public static HttpHandler buildHttpHandler(WebHandler handler) {
		WebHandler delegate = new FilteringWebHandler(handler, Lists.newArrayList());
		delegate = new ExceptionHandlingWebHandler(delegate, Lists.newArrayList());
		HttpHandler httpHandler = new HttpWebHandlerAdapter(delegate);
		return httpHandler;
	}
	
	public static void main(String[] args) {
		HttpHandler httpHandler = buildHttpHandler(buildWebHandler());
		_HttpServerOptions options = _HttpServerOptions.apply(httpHandler);
		httpHandler.apply(options).subscribe(options);
	}
}