package com.swak.mvc.method;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
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
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;

import com.google.common.collect.Maps;
import com.swak.http.HttpServletRequest;
import com.swak.mvc.HandlerExecutionChain;
import com.swak.mvc.annotation.RequestMapping;
import com.swak.mvc.utils.UrlPathHelper;

/**
 * 请求处理以及
 * 
 * @author lifeng
 */
public class RequestMappingHandlerMapping implements HandlerMapping, ApplicationContextAware, InitializingBean {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private ApplicationContext applicationContext;
	private PathMatcher pathMatcher = new AntPathMatcher();
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
					});
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
		return requestMapping != null ? (RequestMappingInfo.paths(pathMatcher, requestMapping.value())) : null;
	}

	/**
	 * 获得请求的执行链
	 */
	@Override
	public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		String lookupPath = getUrlPathHelper().getLookupPathForRequest(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up handler method for path " + lookupPath);
		}

		// HandlerMethod
		HandlerMethod handlerMethod = lookupHandlerMethod(lookupPath, request);
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
	protected HandlerMethod lookupHandlerMethod(String lookupPath, HttpServletRequest request) throws Exception {
		Match bestMatch = this.lookupBestMatch(lookupPath);
		if (bestMatch == null) {
			List<Match> matches = new ArrayList<Match>();
			List<RequestMappingInfo> directPathMatches = this.mappingRegistry.getMappingsByUrl(lookupPath);
			if (directPathMatches != null) {
				addMatchingMappings(directPathMatches, matches, lookupPath);
			}
			if (matches.isEmpty()) {
				addMatchingMappings(this.mappingRegistry.getMappings().keySet(), matches, lookupPath);
			}
			if (!matches.isEmpty()) {
				Collections.sort(matches);
				bestMatch = matches.get(0);
				matchLookup.put(lookupPath, bestMatch);
			}
		}
		if (bestMatch != null) {
			this.handleMatch(bestMatch.mapping, lookupPath, request);
			return bestMatch.handlerMethod;
		}
		return null;
	}
	
	/**
	 * 用户缓存最佳的匹配器
	 * @param lookupPath
	 * @return
	 */
	protected Match lookupBestMatch(String lookupPath) {
		return matchLookup.get(lookupPath);
	}

	protected void addMatchingMappings(Collection<RequestMappingInfo> mappings, List<Match> matches,
			String lookupPath) {
		for (RequestMappingInfo mapping : mappings) {
			Set<String> matchs = mapping.getMatchingCondition(lookupPath);
			if (matchs != null) {
				matches.add(new Match(lookupPath, matchs, this.mappingRegistry.getMappings().get(mapping)));
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

	protected HandlerExecutionChain getHandlerExecutionChain(HandlerMethod handler, String lookupPath) {
		HandlerExecutionChain chain = new HandlerExecutionChain(handler);
		for (HandlerInterceptor interceptor : this.adaptedInterceptors) {
			if (interceptor instanceof MappedInterceptor) {
				MappedInterceptor mappedInterceptor = (MappedInterceptor) interceptor;
				if (mappedInterceptor.matches(lookupPath, this.pathMatcher)) {
					chain.addInterceptor(mappedInterceptor.getInterceptor());
				}
			} else {
				chain.addInterceptor(interceptor);
			}
		}
		return chain;
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

	/**
	 * 匹配器
	 * @author lifeng
	 *
	 */
	public class Match implements Comparable<Match>{

		private final String lookupPath;
		private final Set<String> mapping;
		private final HandlerMethod handlerMethod;

		public Match(String lookupPath, Set<String> mapping, HandlerMethod handlerMethod) {
			this.lookupPath = lookupPath;
			this.mapping = mapping;
			this.handlerMethod = handlerMethod;
		}

		@Override
		public String toString() {
			return this.mapping.toString();
		}

		@Override
		public int compareTo(Match other) {
			Comparator<String> patternComparator = getPathMatcher().getPatternComparator(lookupPath);
			Iterator<String> iterator = this.mapping.iterator();
			Iterator<String> iteratorOther = other.mapping.iterator();
			while (iterator.hasNext() && iteratorOther.hasNext()) {
				int result = patternComparator.compare(iterator.next(), iteratorOther.next());
				if (result != 0) {
					return result;
				}
			}
			if (iterator.hasNext()) {
				return -1;
			} else if (iteratorOther.hasNext()) {
				return 1;
			} else {
				return 0;
			}
		}
		
		@Override
		public boolean equals(Object other) {
			if (this == other) {
				return true;
			}
			if (other != null && getClass() == other.getClass()) {
				RequestMappingInfo obj = (RequestMappingInfo) other;
				return mapping.equals(obj.getPatterns());
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.mapping.hashCode();
		}
	}
}
