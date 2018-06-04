package com.swak.reactivex.web.method.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.MethodParameter;

import com.swak.reactivex.transport.http.HttpServerRequest;
import com.swak.reactivex.web.method.HandlerMethodArgumentResolver;

/**
 * 处理器的集合 -- 支持缓存
 * @author lifeng
 */
public class HandlerMethodArgumentResolverComposite implements HandlerMethodArgumentResolver {

	private List<HandlerMethodArgumentResolver> resolvers = new ArrayList<HandlerMethodArgumentResolver>();
	
	private final Map<MethodParameter, HandlerMethodArgumentResolver> argumentResolverCache = new ConcurrentHashMap<MethodParameter, HandlerMethodArgumentResolver>(
			256);

	/**
	 * 添加處理器
	 * @param resolvers
	 * @return
	 */
	public HandlerMethodArgumentResolverComposite addResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		for (HandlerMethodArgumentResolver resolver : resolvers) {
			this.resolvers.add(resolver);
		}
		return this;
	}

	/**
	 * 找到一个处理器并缓存
	 */
	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return (getArgumentResolver(parameter) != null);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, HttpServerRequest webRequest){
		HandlerMethodArgumentResolver resolver = getArgumentResolver(parameter);
		if (resolver == null) {
			throw new IllegalArgumentException(
					"Unknown parameter type [" + parameter.getParameterType().getName() + "]");
		}
		return resolver.resolveArgument(parameter, webRequest);
	}

	/**
	 * 具有缓存机制
	 * @param parameter
	 * @return
	 */
	private HandlerMethodArgumentResolver getArgumentResolver(MethodParameter parameter) {
		HandlerMethodArgumentResolver result = this.argumentResolverCache.get(parameter);
		if (result == null) {
			for (HandlerMethodArgumentResolver methodArgumentResolver : this.resolvers) {
				if (methodArgumentResolver.supportsParameter(parameter)) {
					result = methodArgumentResolver;
					this.argumentResolverCache.put(parameter, result);
					break;
				}
			}
		}
		return result;
	}
}