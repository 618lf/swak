package com.swak.vertx.protocol.im;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.swak.annotation.ImOps;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.Sets;
import com.swak.utils.StringUtils;

import io.vertx.core.Handler;
import io.vertx.core.http.ServerWebSocket;
import lombok.ToString;

/**
 * Im 路由器
 * 
 * @author lifeng
 * @date 2020年8月25日 下午10:28:55
 */
public class ImRouter {

	// intersection of regex chars and
	// https://tools.ietf.org/html/rfc3986#section-3.3
	private static final Pattern RE_OPERATORS_NO_STAR = Pattern.compile("([\\(\\)\\$\\+\\.])");

	// Pattern for :<token name> in path
	private static final Pattern RE_TOKEN_SEARCH = Pattern.compile(":([A-Za-z][A-Za-z0-9_]*)");

	/**
	 * 所有的处理器
	 */
	List<ImRoute> routes = Lists.newArrayList(5);

	/**
	 * 分组数据
	 */
	Map<ImPredicate, ImRouteChain> groups = Maps.newHashMap();

	/**
	 * 创建一个 路由
	 * 
	 * @return
	 */
	public ImRoute route() {
		ImRoute route = new ImRoute();
		routes.add(route);
		return route;
	}

	/**
	 * 返回所有的路由
	 * 
	 * @return
	 */
	public List<ImRoute> getRoutes() {
		return routes;
	}

	/**
	 * 处理socket事件
	 */
	public Handler<ServerWebSocket> newHandler() {
		return new ImRouteState(this);
	}

	/**
	 * Route 运行时， 让每个线程拥有一个，单线程无竞争的运行
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午11:47:28
	 */
	public class ImRouteState implements Handler<ServerWebSocket> {

		/**
		 * 路由信息
		 */
		Map<ImPredicate, ImMatch> lru = Maps.newLRUCache(1000);
		ImRouter router;

		public ImRouteState(ImRouter router) {
			this.router = router;
		}

		/**
		 * 查找最佳匹配处理链
		 * 
		 * @param predicate
		 * @return
		 */
		public ImMatch lookup(ImPredicate predicate) {
			ImMatch match = lru.get(predicate);
			match = match != null ? match : this.lookupChain(predicate);
			return this.handleMatch(match, predicate);
		}

		private ImMatch handleMatch(ImMatch match, ImPredicate predicate) {
			Map<String, String> variables = null;
			if (match.getMatch() != null && (variables = this.handleVariables(match, predicate)) != null) {
				return new ImMatch(match.match, match.chain).setVariables(variables);
			}
			return match;
		}

		private Map<String, String> handleVariables(ImMatch match, ImPredicate predicate) {
			Map<String, String> variables = Maps.newHashMap();
			Pattern pattern = match.getMatch().pattern;
			List<String> groups = match.getMatch().groups;
			String path = predicate.path;
			if (pattern != null) {
				Matcher m = pattern.matcher(path);
				if (m.matches()) {
					if (m.groupCount() > 0) {
						if (groups != null && !groups.isEmpty()) {
							// Pattern - named params
							// decode the path as it could contain escaped chars.
							for (int i = 0; i < Math.min(groups.size(), m.groupCount()); i++) {
								final String k = groups.get(i);
								String undecodedValue;
								// We try to take value in three ways:
								// 1. group name of type p0, p1, pN (most frequent and used by vertx params)
								// 2. group name inside the regex
								// 3. No group name
								try {
									undecodedValue = m.group("p" + i);
								} catch (IllegalArgumentException e) {
									try {
										undecodedValue = m.group(k);
									} catch (IllegalArgumentException e1) {
										// Groups starts from 1 (0 group is total match)
										undecodedValue = m.group(i + 1);
									}
								}
								variables.put(k, undecodedValue);
							}
						} else {
							for (int i = 0; i < m.groupCount(); i++) {
								String group = m.group(i + 1);
								if (group != null) {
									final String k = "param" + i;
									variables.put(k, group);
								}
							}
						}
					}
				}
			}
			return variables;
		}

		private ImMatch lookupChain(ImPredicate predicate) {
			if (router.groups.isEmpty()) {
				this.groupRouteChains();
			}
			ImMatch match = this.doLookupChain(predicate);
			lru.put(predicate, match);
			return match;
		}

		private ImMatch doLookupChain(ImPredicate path) {
			Iterator<ImPredicate> predicates = router.groups.keySet().iterator();
			while (predicates.hasNext()) {
				ImPredicate pattern = predicates.next();
				if (pattern.patternMatch(path)) {
					return new ImMatch(pattern, router.groups.get(pattern));
				}
			}
			return ImMatch.NONE;
		}

		private synchronized void groupRouteChains() {
			if (router.groups.isEmpty()) {
				Set<String> paths = Sets.newHashSet();
				for (ImRoute route : router.routes) {
					paths.add(route.getPath());
				}
				for (String path : paths) {
					this.groupRouteChains(path, ImOps.Connect);
					this.groupRouteChains(path, ImOps.Message);
					this.groupRouteChains(path, ImOps.Error);
					this.groupRouteChains(path, ImOps.Close);
				}
			}
		}

		private synchronized void groupRouteChains(String path, ImOps ops) {
			ImPredicate pattern = new ImPredicate(path, ops);
			ImRouteChain chain = router.groups.computeIfAbsent(pattern, (key) -> {
				return new ImRouteChain();
			});
			for (ImRoute route : router.routes) {
				ImPredicate other = new ImPredicate(route.getPath(), route.getOps());
				if (pattern.pathMatch(other)) {
					chain.route(route);
				}
			}
			if (chain.routes.isEmpty()) {
				router.groups.remove(pattern);
			} else {
				this.handleImPredicate(pattern);
			}
		}

		/**
		 * 处理路径配置
		 * 
		 * @return
		 */
		private ImPredicate handleImPredicate(ImPredicate pattern) {
			String path = pattern.path;
			path = RE_OPERATORS_NO_STAR.matcher(path).replaceAll("\\\\$1");
			Matcher m = RE_TOKEN_SEARCH.matcher(path);
			StringBuffer sb = new StringBuffer();
			List<String> groups = new ArrayList<>();
			int index = 0;
			while (m.find()) {
				String param = "p" + index;
				String group = m.group().substring(1);
				if (groups.contains(group)) {
					throw new IllegalArgumentException(
							"Cannot use identifier " + group + " more than once in pattern string");
				}
				m.appendReplacement(sb, "(?<" + param + ">[^/]+)");
				groups.add(group);
				index++;
			}
			m.appendTail(sb);
			path = sb.toString();
			pattern.pattern = Pattern.compile(path);
			pattern.groups = groups;
			return pattern;
		}

		@Override
		public void handle(ServerWebSocket event) {
			this.consumer().accept(event);
		}

		/**
		 * 处理socket事件
		 * 
		 * @return
		 */
		private Consumer<ServerWebSocket> consumer() {
			return (decorate) -> {

				// 处理异常
				decorate.exceptionHandler(e -> {
					new ImContextImpl(ImOps.Error, this, decorate, e).next();
				});

				// 处理消息
				decorate.frameHandler(message -> {
					new ImContextImpl(ImOps.Message, this, decorate, message).next();
				});

				// 关闭
				decorate.closeHandler(v -> {
					new ImContextImpl(ImOps.Close, this, decorate).next();
				});

				// 处理连接事件
				new ImContextImpl(ImOps.Connect, this, decorate).next();
			};
		}
	}

	public static class ImMatch {

		static ImMatch NONE = none();

		private ImPredicate match;
		private ImRouteChain chain;
		private Map<String, String> variables;

		public ImMatch(ImPredicate match, ImRouteChain chain) {
			this.match = match;
			this.chain = chain;
		}

		public ImPredicate getMatch() {
			return match;
		}

		public ImRouteChain getChain() {
			return chain;
		}

		public Map<String, String> getVariables() {
			return variables;
		}

		public ImMatch setVariables(Map<String, String> variables) {
			this.variables = variables;
			return this;
		}

		public static ImMatch none() {
			ImMatch NONE = new ImMatch(null, new ImRouteChain());
			NONE.variables = Maps.newHashMap();
			return NONE;
		}
	}

	/**
	 * ImRoute 链式操作
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午11:44:24
	 */
	public static class ImRouteChain {

		List<ImRoute> routes;

		public ImRouteChain() {
			this.routes = Lists.newArrayList(1);
		}

		public ImRouteChain route(ImRoute route) {
			this.routes.add(route);
			return this;
		}

		/**
		 * 总是会取到数据
		 * 
		 * @param index
		 * @return
		 */
		public ImRoute next(int index) {
			return routes.size() != 0 ? routes.get(index % routes.size()) : ImRoute.NONE;
		}
	}

	/**
	 * 路由信息
	 * 
	 * @author lifeng
	 * @date 2020年8月25日 下午10:45:45
	 */
	@ToString
	public static class ImRoute {

		/**
		 * NULL 处理
		 */
		static ImRoute NONE = new ImRoute().handler((context) -> {

		});

		private String path = StringUtils.EMPTY;
		private ImOps ops = ImOps.All;
		@lombok.ToString.Exclude
		Handler<ImContext> handler;

		public ImRoute path(String path) {
			this.path = StringUtils.defaultString(path, StringUtils.EMPTY);
			return this;
		}

		public ImRoute ops(ImOps ops) {
			this.ops = ops == null ? ImOps.All : ops;
			return this;
		}

		public ImRoute handler(Handler<ImContext> handler) {
			this.handler = handler;
			return this;
		}

		public ImOps getOps() {
			return ops;
		}

		public String getPath() {
			return path;
		}

		public Handler<ImContext> getHandler() {
			return handler;
		}

	}
}