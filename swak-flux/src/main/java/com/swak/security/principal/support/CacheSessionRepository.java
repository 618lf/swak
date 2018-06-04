package com.swak.security.principal.support;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.cache.collection.AsyncMultiMap;
import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Session;
import com.swak.security.principal.NoneSession;
import com.swak.security.principal.SessionRepository;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

/**
 * 基于缓存的 session 管理
 * @author lifeng
 */
public class CacheSessionRepository implements SessionRepository {

	private String SESSION_ATTR_PREFIX = "attr:";
	private String CREATION_TIME_ATTR = "ct";
	private String LASTACCESSED_TIME_ATTR = "lat";
	private String PRINCIPAL_ATTR = "p";
	private String AUTHENTICATED_ATTR = "authed";
	private String RUNASPRINCIPALS_ATTR = "rps";
	private int sessionTimeout = 1800;
	private AsyncMultiMap<String, Object> _cache;
	
	public CacheSessionRepository(AsyncMultiMap<String, Object> _cache) {
		this._cache = _cache.expire(sessionTimeout).complex();
	}
	
	public void setSessionTimeout(int sessionTimeout) {
		this.sessionTimeout = sessionTimeout;
	}
	
	/**
	 * 创建session
	 */
	@Override
	public Mono<Session> createSession(Principal principal, boolean authenticated) {
		CacheSession session = new CacheSession(UUID.randomUUID().toString());
		session.principal = principal;
		session.authenticated = authenticated;
		return session.saveDelta();
	}

	/**
	 * 获取session
	 */
	@Override
	public Mono<Session> getSession(String id) {
		return Workers.sink(_cache.get(id), (entries) -> {
			if (entries == null || entries.isEmpty()) {
				return NoneSession.NONE;
			}
			return loadSession(id, entries);
		}); 
	}
	
	@SuppressWarnings("unchecked")
	private CacheSession loadSession(String id, Map<String, Object> entries) {
		CacheSession session = new CacheSession(id);
		for (Map.Entry<String, Object> entry : entries.entrySet()) {
			 String key = entry.getKey();
			 if (CREATION_TIME_ATTR.equals(key)) {
				session.creationTime = ((Long) entry.getValue());
			 }
			 else if (PRINCIPAL_ATTR.equals(key)) {
				session.principal = (Principal) (entry.getValue());
			 }
			 else if (AUTHENTICATED_ATTR.equals(key)) {
				session.authenticated = ((Boolean) entry.getValue());
			 }
			 else if (RUNASPRINCIPALS_ATTR.equals(key)) {
				session.runAsPrincipals = (Stack<Principal>)entry.getValue();
			 }
			 else if (key.startsWith(SESSION_ATTR_PREFIX)) {
				session._setAttribute(key.substring(SESSION_ATTR_PREFIX.length()), entry.getValue());
			 }
		}
		return session.access();
	}
	
	/**
	 * 删除session
	 */
	@Override
	public Mono<Boolean> removeSession(Session session) {
		if (session != null && StringUtils.hasText(session.getId())) {
			return Mono.fromCompletionStage( _cache.delete(session.getId())).map(s -> false);
		}
		return Mono.just(false);
	}

	/**
	 * 删除session
	 */
	@Override
	public Mono<Boolean> removeSession(String sessionId) {
		if (StringUtils.hasText(sessionId)) {
			return Mono.fromCompletionStage(_cache.delete(sessionId)).map(s -> true);
		}
		return Mono.empty();
	}
	
	/**
	 * session 的存储名称
	 * @param attributeName
	 * @return
	 */
	public String getSessionAttrNameKey(String attributeName) {
		return new StringBuilder(SESSION_ATTR_PREFIX).append(attributeName).toString();
	}
	
	/**
	 * 基于缓存的 Session
	 * @author lifeng
	 */
	public class CacheSession implements Session {
		
		protected String id;
		protected long creationTime = System.currentTimeMillis();
		protected long lastAccessedTime;
		protected Principal principal = null;
		protected boolean authenticated = false;
		protected Stack<Principal> runAsPrincipals;
		protected Map<String, Object> sessionAttrs;
		
		public CacheSession(String id) {
			this.id = id;
		}
		
		@Override
		public Principal getPrincipal() {
			return principal;
		}
		
		@Override
		public boolean isAuthenticated() {
			return authenticated;
		}
		
		@Autowired
		public Stack<Principal> getRunAsPrincipals() {
			return runAsPrincipals;
		}

		@Override
		public String getId() {
			return this.id;
		}

		@Override
		public long getCreationTime() {
			return this.creationTime;
		}

		@Override
		public long getLastAccessedTime() {
			return this.lastAccessedTime;
		}

		@Override
		public long getMaxInactiveInterval() {
			return sessionTimeout;
		}
		
		@Override
		public void setPrincipal(Principal principal) {
			this.principal = principal;
			this.putAndFlush(PRINCIPAL_ATTR, principal);
		}

		/**
		 * 会立即刷新数据
		 */
		@Override
		public void setRunAsPrincipals(Stack<Principal> runAsPrincipals) {
			this.runAsPrincipals = runAsPrincipals;
			this.putAndFlush(RUNASPRINCIPALS_ATTR, runAsPrincipals);
		}
		
		/**
		 * 会立即刷新数据
		 */
		@Override
		public void setAuthenticated(boolean authenticated) {
			this.authenticated = authenticated;
			this.putAndFlush(AUTHENTICATED_ATTR, runAsPrincipals);
		}
		
		/**
		 * 访问 session
		 */
		public CacheSession access() {
			this.lastAccessedTime = System.currentTimeMillis();
			this.putAndFlush(LASTACCESSED_TIME_ATTR, lastAccessedTime);
			return this;
		}
		
		/**
		 * 得到属性
		 */
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getAttribute(String attributeName) {
			return (T) (sessionAttrs != null ? sessionAttrs.get(attributeName) : null);
		}

		/**
		 * 得到所有的属性名称
		 */
		@Override
		public Set<String> getAttributeNames() {
			return sessionAttrs != null ? sessionAttrs.keySet() : null;
		}

		/**
		 * 设置属性
		 */
		@Override
		public <T> void setAttribute(String attributeName, T attributeValue) {
			if (sessionAttrs == null) {
				sessionAttrs = Maps.newHashMap();
			}
			sessionAttrs.put(attributeName, attributeValue);
			putAndFlush(getSessionAttrNameKey(attributeName), attributeValue);
		}

		/**
		 * 删除属性
		 */
		@Override
		public void removeAttribute(String attributeName) {
			if (sessionAttrs != null && attributeName != null) {
				sessionAttrs.remove(attributeName);
				putAndFlush(getSessionAttrNameKey(attributeName), null);
			}
		}
		
		/**
		 * 请求结束后会销毁内容中的数据
		 */
		@Override
		public void destory() {
			this.id = null; this.principal = null;
			if (this.runAsPrincipals != null) {this.runAsPrincipals.clear();} this.runAsPrincipals = null;
			if (this.sessionAttrs != null) {sessionAttrs.clear();} this.sessionAttrs = null;
		}
		
		// 立即刷新数据(自动删除 v == null 的数据)
		private void putAndFlush(String a, Object v) {
			if (v == null) {
				_cache.delete(this.id, a);
			} else {
				_cache.put(this.id, a, v);
			}
		}
		
		// 初始化才刷新数据
		public Mono<Session> saveDelta() {
			Map<String, Object> delta = Maps.newHashMap();
			delta.put(CREATION_TIME_ATTR, this.getCreationTime());
			delta.put(LASTACCESSED_TIME_ATTR, this.getLastAccessedTime());
			delta.put(PRINCIPAL_ATTR, this.getPrincipal());
			delta.put(AUTHENTICATED_ATTR, this.isAuthenticated());
			return Mono.fromCompletionStage(_cache.put(this.id, delta)).map(s -> this);
		}
		
		public void _setAttribute(String key, Object v) {
			if (sessionAttrs == null) {
				sessionAttrs = Maps.newHashMap();
			}
			sessionAttrs.put(key, v);
		}
		
		public boolean equals(Object obj) {
			return obj instanceof Session && this.id.equals(((Session) obj).getId());
		}

		public int hashCode() {
			return this.id.hashCode();
		}
	}
}
