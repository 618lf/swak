package com.swak.flux.web.method;

import java.io.Closeable;
import java.io.IOException;
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

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodIntrospector;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.swak.asm.FieldCache;
import com.swak.flux.transport.http.HttpConst;
import com.swak.flux.transport.http.Principal;
import com.swak.flux.transport.http.Subject;
import com.swak.flux.transport.http.server.HttpServerRequest;
import com.swak.flux.transport.http.server.HttpServerResponse;
import com.swak.flux.web.AbstractHandlerMapping;
import com.swak.flux.web.annotation.RequestMethod;
import com.swak.flux.web.method.pattern.UrlPathHelper;
import com.swak.flux.web.template.Model;
import com.swak.utils.Maps;

public abstract class AbstractRequestMappingHandlerMapping extends AbstractHandlerMapping {

	private Map<String, Match> matchLookup = new HashMap<String, Match>();
	private MappingRegistry mappingRegistry = new MappingRegistry();

	public MappingRegistry getMappingRegistry() {
		return mappingRegistry;
	}

	/**
	 * 将一个对象注册为mapping
	 * 
	 * @param handler
	 */
	public void registryMapping(Object handler) {
		final Class<?> userType = ClassUtils.getUserClass(handler.getClass());
		Map<Method, RequestMappingInfo> methods = MethodIntrospector.selectMethods(userType,
				new MethodIntrospector.MetadataLookup<RequestMappingInfo>() {
					@Override
					public RequestMappingInfo inspect(Method method) {
						try {
							return getMappingForMethod(handler, method, userType);
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

	protected abstract RequestMappingInfo getMappingForMethod(Object handler, Method method, Class<?> handlerType);

	/**
	 * 获得请求的执行链
	 */
	@Override
	public HandlerMethod getHandler(HttpServerRequest request) {
		String lookupPath = UrlPathHelper.getLookupPathForRequest(request);
		if (logger.isDebugEnabled()) {
			logger.debug("Looking up handler method for path " + lookupPath);
		}

		RequestMethod lookupMethod = null;
		try {
			lookupMethod = RequestMethod.valueOf(request.getRequestMethod().name().toUpperCase());
		} catch (Exception e) {
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

		return handlerMethod;
	}

	/**
	 * 根据url查找method
	 * 
	 * @param lookupPath
	 * @param request
	 * @return
	 * @throws Exception
	 */
	protected HandlerMethod lookupHandlerMethod(String lookupPath, RequestMethod lookupMethod,
			HttpServerRequest request) {
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
			this.recordPath(bestMatch, request);
			return bestMatch.getHandlerMethod();
		}
		return null;
	}

	private void recordPath(Match bestMatch, HttpServerRequest request) {
		Set<String> mapping = bestMatch.getMapping();
		if (mapping != null && !mapping.isEmpty()) {
			request.setAttribute(HttpConst.ATTRIBUTE_FOR_PATH, mapping.stream().findFirst());
		}
	}

	protected void addMatchingMappings(Collection<RequestMappingInfo> mappings, List<Match> matches, String lookupPath,
			RequestMethod lookupMethod) {
		for (RequestMappingInfo mapping : mappings) {
			Match match = mapping.getMatchingCondition(lookupPath, lookupMethod);
			if (match != null) {
				match.setHandlerMethod(this.mappingRegistry.getMappings().get(mapping));
				matches.add(match);
			}
		}
	}

	protected void handleMatch(Set<String> patterns, String lookupPath, HttpServerRequest request) {
		if (patterns.size() == 1) {
			Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(patterns.iterator().next(),
					lookupPath);
			Map<String, String> decodedUriVariables = UrlPathHelper.decodePathVariables(request, uriVariables);
			request.addPathVariables(decodedUriVariables);
		} else {
			Iterator<String> macths = patterns.iterator();
			Map<String, String> all = Maps.newHashMap();
			while (macths.hasNext()) {
				Map<String, String> uriVariables = getPathMatcher().extractUriTemplateVariables(macths.next(),
						lookupPath);
				Map<String, String> decodedUriVariables = UrlPathHelper.decodePathVariables(request, uriVariables);
				all.putAll(decodedUriVariables);
			}
			request.addPathVariables(all);
		}
	}

	/**
	 * 释放资源
	 */
	@Override
	public void close() throws IOException {
		this.mappingRegistry.close();
		this.matchLookup.clear();
	}

	/**
	 * 注册器
	 * 
	 * @author lifeng
	 */
	public class MappingRegistry implements Closeable {

		private ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		private Map<RequestMappingInfo, HandlerMethod> mappingLookup = new LinkedHashMap<RequestMappingInfo, HandlerMethod>();
		private MultiValueMap<String, RequestMappingInfo> urlLookup = new LinkedMultiValueMap<String, RequestMappingInfo>();

		public void register(RequestMappingInfo mapping, Object handler, Method method) {
			try {
				this.readWriteLock.writeLock().lock();
				HandlerMethod handlerMethod = new HandlerMethod(handler, method);
				this.mappingLookup.put(mapping, handlerMethod);
				List<String> directUrls = getDirectUrls(mapping);
				for (String url : directUrls) {
					this.urlLookup.add(url, mapping);
				}
				initHandler(handlerMethod);
			} finally {
				this.readWriteLock.writeLock().unlock();
			}
		}

		// 初始化处理器
		private void initHandler(HandlerMethod handlerMethod) {
			MethodParameter[] parameters = handlerMethod.getParameters();
			for (int i = 0; i < parameters.length; i++) {
				MethodParameter parameter = parameters[i];

				// 实际的类型
				Class<?> parameterType = parameter.getNestedParameterType();

				// 对于集合类型支持第一层
				this.initField(parameterType);
			}
		}

		// 只覆盖支持的类型：自定义的实体类
		private void initField(Class<?> parameterType) {
			if (parameterType == HttpServerRequest.class || parameterType == HttpServerResponse.class
					|| parameterType == Subject.class || parameterType == Model.class
					|| parameterType == Principal.class || BeanUtils.isSimpleProperty(parameterType)
					|| parameterType.isAssignableFrom(Collection.class) || parameterType.isAssignableFrom(Map.class)) {
				return;
			}
			FieldCache.set(parameterType);
		}

		private List<String> getDirectUrls(RequestMappingInfo mapping) {
			List<String> urls = new ArrayList<String>(1);
			for (String path : mapping.getPatterns()) {
				if (!getPathMatcher().isPattern(path)) {
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

		/**
		 * 释放资源
		 */
		@Override
		public void close() throws IOException {
			mappingLookup.clear();
			urlLookup.clear();
			mappingLookup = null;
			urlLookup = null;
		}
	}
}
