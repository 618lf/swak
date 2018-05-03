package com.swak.reactivex.web.method;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;
import com.swak.reactivex.web.Handler;
import com.swak.reactivex.web.HandlerAdapter;
import com.swak.reactivex.web.method.resolver.HandlerMethodArgumentResolverComposite;
import com.swak.reactivex.web.method.resolver.HttpCookieValueMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.PathVariableMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.RequestHeaderMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.RequestParamMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.ServerRequestMethodArgumentResolver;
import com.swak.reactivex.web.method.resolver.ServerResponseMethodArgumentResolver;

/**
 * 请求处理器
 * 
 * @author lifeng
 */
public class RequestMappingHandlerAdapter implements HandlerAdapter, ApplicationContextAware {

	private HandlerMethodArgumentResolver argumentResolver;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConversionService conversionService = applicationContext.getBean(ConversionService.class);
		initArgumentResolvers(conversionService);
	}
	
	/**
	 * 初始化参数解析
	 * @param conversionService
	 */
	public void initArgumentResolvers(ConversionService conversionService) {
		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
		resolvers.add(new PathVariableMethodArgumentResolver(conversionService));
		resolvers.add(new RequestHeaderMethodArgumentResolver(conversionService));
		resolvers.add(new HttpCookieValueMethodArgumentResolver(conversionService));
		resolvers.add(new RequestParamMethodArgumentResolver(conversionService));
		resolvers.add(new ServerRequestMethodArgumentResolver());
		resolvers.add(new ServerResponseMethodArgumentResolver());
		this.argumentResolver = new HandlerMethodArgumentResolverComposite().addResolvers(resolvers);
	}

	@Override
	public HandlerResult handle(HttpServerRequest request, HttpServerResponse response, Handler handler){
		HandlerMethod _handler = (HandlerMethod)handler;
		Object[] args = getMethodArgumentValues(request, _handler);
		Object returnValue = _handler.doInvoke(args);
		return new HandlerResult(_handler, returnValue);
	}
	
	private Object[] getMethodArgumentValues(HttpServerRequest request, HandlerMethod handler){
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

	@Override
	public boolean supports(Handler handler) {
		return handler instanceof HandlerMethod;
	}
}