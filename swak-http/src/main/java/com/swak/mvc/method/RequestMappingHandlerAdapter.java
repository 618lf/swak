package com.swak.mvc.method;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.http.HttpServletRequest;
import com.swak.http.HttpServletResponse;
import com.swak.mvc.method.converter.HttpMessageConverter;
import com.swak.mvc.method.converter.Jaxb2RootElementHttpMessageConverter;
import com.swak.mvc.method.converter.JsonHttpMessageConverter;
import com.swak.mvc.method.converter.StringHttpMessageConverter;
import com.swak.mvc.method.resolver.HandlerMethodArgumentResolverComposite;
import com.swak.mvc.method.resolver.HttpCookieValueMethodArgumentResolver;
import com.swak.mvc.method.resolver.PathVariableMethodArgumentResolver;
import com.swak.mvc.method.resolver.RequestHeaderMethodArgumentResolver;
import com.swak.mvc.method.resolver.RequestParamMethodArgumentResolver;
import com.swak.mvc.method.resolver.RequestResponseBodyMethodReturnValueResolver;
import com.swak.mvc.method.resolver.ServletRequestMethodArgumentResolver;
import com.swak.mvc.method.resolver.ServletResponseMethodArgumentResolver;

/**
 * 请求处理器
 * 
 * @author lifeng
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter, InitializingBean {

	private HandlerMethodArgumentResolver argumentResolver;
	private HandlerMethodReturnValueResolver returnValueResolver;
	private ConversionService conversionService;

	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	public ConversionService getConversionService() {
		return conversionService;
	}

	@Override
	public void afterPropertiesSet() {
		List<HandlerMethodArgumentResolver> resolvers = getDefaultArgumentResolvers();
		List<HttpMessageConverter<?>> converters = this.getDefaultMessageConverters();
		this.argumentResolver = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
	    this.returnValueResolver = new RequestResponseBodyMethodReturnValueResolver(converters);
	}

	private List<HandlerMethodArgumentResolver> getDefaultArgumentResolvers() {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		resolvers.add(new PathVariableMethodArgumentResolver(this.getConversionService()));
		resolvers.add(new RequestHeaderMethodArgumentResolver(this.getConversionService()));
		resolvers.add(new HttpCookieValueMethodArgumentResolver(this.getConversionService()));
		resolvers.add(new RequestParamMethodArgumentResolver(this.getConversionService()));
		resolvers.add(new ServletRequestMethodArgumentResolver());
		resolvers.add(new ServletResponseMethodArgumentResolver());
		return resolvers;
	}
	
	private List<HttpMessageConverter<?>> getDefaultMessageConverters() {
		List<HttpMessageConverter<?>> converters = new ArrayList<HttpMessageConverter<?>>();
		converters.add(new StringHttpMessageConverter());
		converters.add(new Jaxb2RootElementHttpMessageConverter());
		converters.add(new JsonHttpMessageConverter());
		return converters;
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler)
			throws Exception {
		Object[] args = getMethodArgumentValues(request, handler);
		Object returnValue = handler.doInvoke(args);
		this.handleReturnValue(returnValue, response, handler);
	}
	
	private void handleReturnValue(Object returnValue, HttpServletResponse response, HandlerMethod handler) throws Exception {
		returnValueResolver.handleReturnValue(returnValue, handler.getReturnValue(), response);
	}

	private Object[] getMethodArgumentValues(HttpServletRequest request, HandlerMethod handler) throws Exception {
		MethodParameter[] parameters = handler.getParameters();
		Object[] args = new Object[parameters.length];
		for (int i = 0; i < parameters.length; i++) {
			MethodParameter parameter = parameters[i];
			args[i] = this.argumentResolver.resolveArgument(parameter, request);
			if (args[i] == null) {
				throw new IllegalStateException("Could not resolve method parameter at index "
						+ parameter.getParameterIndex() + " in " + parameter.getMethod().toGenericString());
			}
		}
		return args;
	}
}