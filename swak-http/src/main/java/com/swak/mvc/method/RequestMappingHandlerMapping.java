package com.swak.mvc.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.PathMatcher;

import com.swak.common.utils.Maps;
import com.swak.http.HttpServletRequest;
import com.swak.http.PathMatcherHelper;
import com.swak.mvc.HandlerExecutionChain;
import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.annotation.RequestMethod;
import com.swak.mvc.utils.UrlPathHelper;

/**
 * 请求处理以及
 * 
 * @author lifeng
 */
public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware, InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApplicationContext applicationContext;
	private PathMatcher pathMatcher = PathMatcherHelper.getMatcher();
	private UrlPathHelper urlPathHelper = new UrlPathHelper();
	private final List<HandlerInterceptor> adaptedInterceptors = new ArrayList<HandlerInterceptor>();
	private final MappingRegistry mappingRegistry = new MappingRegistry();
	private final Map<String, Match> matchLookup = new HashMap<String, Match>();
	
	public PathMatcher getPathMatcher() {
		return this.pathMatcher;
	}

	public UrlPathHelper getUrlPathHelper() {
		return urlPathHelper;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 初始化所有的请求
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		String[] beanNames = this.applicationContext.getBeanNamesForAnnotation(Controller.class);
		
		for (String beanName : beanNames) {
			Object handler = null;
			try {
				handler = this.applicationContext.getBean(beanName);
			} catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
				}
			}

			final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
			Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
					new MethodIntrospector.MetadataLookup<RequestMappingInfo>() {
						@Override
						public RequestMappingInfo inspect(Method method) {
							try {
								return getMappingForMethod(method, userType);
							} catch (Throwable ex) {
								throw new IllegalStateException(
										"Invalid mapping on handler class [" + userType.getName() + "]: " + method, ex);
							}
						}
					}
			);
			if (logger.isDebugEnabled()) {
				logger.debug(methods.size() + " request handler methods found on " + userType + ": " + methods);
			}

			for (Map.Entry<Method, RequestMappingInfo> entry : methods.entrySet()) {
				Method invocableMethod = AopUtils.selectInvocableMethod(entry.getKey(), userType);
				RequestMappingInfo mapping = entry.getValue();
				this.mappingRegistry.register(mapping, handler, invocableMethod);
			}
		}
	}

	protected RequestMappingInfo getMappingForMethod(Method method, Class<?> handlerType) {
		RequestMappingInfo info = createRequestMappingInfo(method);
		if (info != null) {
			RequestMappingInfo typeInfo = createRequestMappingInfo(handlerType);
			if (typeInfo != null) {
				info = typeInfo.combine(info);
			}
		}
		return info;
	}

	protected RequestMappingInfo createRequestMappingInfo(AnnotatedElement element) {
		RequestMapping requestMapping = AnnotatedElementUtils.findMergedAnnotation(element, RequestMapping.class);
		return requestMapping != null ? (RequestMappingInfo.paths(requestMapping.method(), requestMapping.value())) : null;
	}

	/**
	 * 获得请求的执行链
	 */
	@Override
	public ExecutionChain getHandler(HttpServletRequest request) throws Exception {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up handler method for path " + lookupPath);
		}
		
		RequestMethod lookupMethod = null;
		try {
			lookupMethod = RequestMethod.valueOf(request.getRequestMethod().toUpperCase());
		}catch(Exception e) {
			throw new RuntimeException("do not support method");
		}

		// HandlerMethod
		HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, lookupMethod, request);
		if (logger.isDebugEnabled()) {
			if (handlerMethod != null) {
				logger.debug("Returning handler method [" + handlerMethod + "]");
			} else {
				logger.debug("Did not find handler method for [" + lookupPath + "]");
			}
		}

		// HandlerExecutionChain
		return getHandlerExecutionChain(handlerMethod, lookupPath);
	}

	/**
	 * 根据url查找method
	 * 
	 * @param lookupPath
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected HandlerMethod lookupHandlerMethod(String lookupPath, RequestMethod lookupMethod, HttpServletRequest request) throws Exception {
		Match bestMatch = matchLookup.get(lookupPath);
		if (bestMatch == null) {
			List<Match> matches = new ArrayList<Match>();
			List<RequestMappingInfo> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
			if (directPathMatches != null) {
				addMatchingMappings(directPathMatches, matches, lookupPath, lookupMethod);
			}
			if (matches.isEmpty()) {
				addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, lookupPath, lookupMethod);
			}
			if (!matches.isEmpty()) {
				Collections.sort(matches);
				bestMatch = matches.get(0);
				matchLookup.put(lookupPath, bestMatch);
			}
		}
		if (bestMatch != null) {
			this.handleMatch(bestMatch.getMapping(), lookupPath, request);
			return bestMatch.getHandlerMethod();
		}
		return null;
	}
	
	protected void addMatchingMappings(Collection<RequestMappingInfo> mappings, List<Match> matches,
			String lookupPath, RequestMethod lookupMethod) {
		for (RequestMappingInfo mapping : mappings) {
			Match match = mapping.getMatchingCondition(lookupPath, lookupMethod);
			if (match != null) {
				match.setHandlerMethod(this.mappingRegistry.getMappings().get(mapping));
				matches.add(match);
			}
		}
	}

	protected void handleMatch(Set<String> patterns, String lookupPath, HttpServletRequest request) {
		if (patterns.size() == 1) {
			Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(patterns.iterator().next(),
					lookupPath);
			Map<String, String> decodedUriVariables = getUrlPathHelper().decodePathVariables(request, uriVariables);
			request.setPathVariables(decodedUriVariables);
		} else {
			Iterator<String> macths = patterns.iterator();
			Map<String, String> all = Maps.newHashMap();
			while (macths.hasNext()) {
				Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(macths.next(),
						lookupPath);
				Map<String, String> decodedUriVariables = getUrlPathHelper().decodePathVariables(request, uriVariables);
				all.putAll(decodedUriVariables);
			}
			request.setPathVariables(all);
		}
	}

	/**
	 * 保证执行过程中不能创建过多的对象
	 * @param handler
	 * @param lookupPath
	 * @return
	 */
	protected ExecutionChain getHandlerExecutionChain(HandlerMethod handler, String lookupPath) {
		HandlerExecutionChain chain = null;
		if (!ObjectUtils.isEmpty(this.adaptedInterceptors)) {
			for(HandlerInterceptor interceptor : this.adaptedInterceptors) {
				HandlerInterceptor _mapping = null;
				if (interceptor instanceof MappedInterceptor) {
					MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
					if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
						_mapping = mappedInterceptor.getInterceptor();
					}
				} else {
					_mapping = interceptor;
				}
				
				// 没有匹配
				if (_mapping == null) {
					continue;
				}
				
				// 匹配到了
				if (chain == null) {
					chain = new HandlerExecutionChain(handler);
				}
				
				// 添加到执行链
				chain.addInterceptor(_mapping);
			}
		}
		
		// 如果没有 interceptor 则 直接返回 handler（也是一个执行链）
		return chain == null ? handler: chain;
	}

	/**
	 * 注册器
	 * @author lifeng
	 */
	public class MappingRegistry {

		private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private final Map<RequestMappingInfo, HandlerMethod> mappingLookup = new LinkedHashMap<RequestMappingInfo, HandlerMethod>();
		private final MultiValueMap<String, RequestMappingInfo> urlLookup = new LinkedMultiValueMap<String, RequestMappingInfo>();

		public void register(RequestMappingInfo mapping, Object handler, Method method) {
			try {
				this.readWriteLock.writeLock().lock();
				HandlerMethod handlerMethod = new HandlerMethod(handler, method);
				this.mappingLookup.put(mapping, handlerMethod);
				List<String> directUrls = getDirectUrls(mapping);
				for (String url : directUrls) {
					this.urlLookup.add(url, mapping);
				}
			} finally {
				this.readWriteLock.writeLock().unlock();
			}
		}

		private List<String> getDirectUrls(RequestMappingInfo mapping) {
			List<String> urls = new ArrayList<String>(1);
			for (String path : mapping.getPatterns()) {
				if (!pathMatcher.isPattern(path)) {
					urls.add(path);
				}
			}
			return urls;
		}

		public List<RequestMappingInfo> getMappingsByUrl(String urlPath) {
			return this.urlLookup.get(urlPath);
		}

		public Map<RequestMappingInfo, HandlerMethod> getMappings() {
			return this.mappingLookup;
		}
	}
}
