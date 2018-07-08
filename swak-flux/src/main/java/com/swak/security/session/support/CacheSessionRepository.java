package com.swak.security.session.support;

import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.cache.collection.AsyncMultiMap;
import com.swak.executor.Workers;
import com.swak.reactivex.transport.http.Principal;
import com.swak.reactivex.transport.http.Session;
import com.swak.security.session.NoneSession;
import com.swak.security.session.SessionRepository;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import reactor.core.publisher.Mono;

/**
 * 基于缓存的 session 管理
 * 
 * @author lifeng
 */
public class CacheSessionRepository implements SessionRepository {

	private final AsyncMultiMap<String, Object> _cache;
	private String SESSION_ATTR_PREFIX = "attr:";
	private String CREATION_TIME_ATTR = "ct";
	private String LASTACCESSED_TIME_ATTR = "lat";
	private String PRINCIPAL_ATTR = "p";
	private String AUTHENTICATED_ATTR = "authed";
	private String RUNASPRINCIPALS_ATTR = "rps";
	private int sessionTimeout = 1800;

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
	public Session createSession() {
		return new CacheSession(UUID.randomUUID().toString());
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
			} else if (PRINCIPAL_ATTR.equals(key)) {
				session.principal = (Principal) (entry.getValue());
			} else if (AUTHENTICATED_ATTR.equals(key)) {
				session.authenticated = ((Boolean) entry.getValue());
			} else if (RUNASPRINCIPALS_ATTR.equals(key)) {
				session.runAsPrincipals = (Stack<Principal>) entry.getValue();
			} else if (key.startsWith(SESSION_ATTR_PREFIX)) {
				session.innerSetAttribute(key.substring(SESSION_ATTR_PREFIX.length()), entry.getValue());
			}
		}
		return session;
	}

	/**
	 * 删除session
	 */
	@Override
	public Mono<Boolean> removeSession(Session session) {
		if (session != null && StringUtils.hasText(session.getId())) {
			return Mono.fromCompletionStage(_cache.delete(session.getId())).map(s -> false);
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
	 * 
	 * @param attributeName
	 * @return
	 */
	public String getSessionAttrNameKey(String attributeName) {
		return new StringBuilder(SESSION_ATTR_PREFIX).append(attributeName).toString();
	}

	/**
	 * 基于缓存的 Session
	 * 
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
		protected boolean changed = false;

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
		@SuppressWarnings("unchecked")
		public <T> T getAttribute(String attributeName) {
			return (T) (sessionAttrs != null ? sessionAttrs.get(attributeName) : null);
		}

		@Override
		public Set<String> getAttributeNames() {
			return sessionAttrs != null ? sessionAttrs.keySet() : null;
		}

		// ---------------- 改变属性的方式 --------------------------
		@Override
		public void setPrincipal(Principal principal) {
			this.principal = principal;
			this.changed = true;
		}

		/**
		 * 会立即刷新数据
		 */
		@Override
		public void setRunAsPrincipals(Stack<Principal> runAsPrincipals) {
			this.runAsPrincipals = runAsPrincipals;
			this.changed = true;
		}

		/**
		 * 会立即刷新数据
		 */
		@Override
		public void setAuthenticated(boolean authenticated) {
			this.authenticated = authenticated;
			this.changed = true;
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
			this.changed = true;
		}
		
		/**
		 * 内部设置属性 -- 读取时候设置
		 * @param key
		 * @param v
		 */
		private void innerSetAttribute(String key, Object v) {
			if (sessionAttrs == null) {
				sessionAttrs = Maps.newHashMap();
			}
			sessionAttrs.put(key, v);
		}

		/**
		 * 删除属性
		 */
		@Override
		public void removeAttribute(String attributeName) {
			if (sessionAttrs != null && attributeName != null) {
				sessionAttrs.remove(attributeName);
				this.changed = true;
			}
		}

		/**
		 * 请求结束后会销毁内容中的数据
		 */
		@Override
		public void destory() {
			this.id = null;
			this.principal = null;
			if (this.runAsPrincipals != null) {
				this.runAsPrincipals.clear();
			}
			this.runAsPrincipals = null;
			if (this.sessionAttrs != null) {
				sessionAttrs.clear();
			}
			this.sessionAttrs = null;
		}

		/**
		 * session 提交, 提交之后才会存储到服务器
		 * 不管怎样都会提交一次
		 */
		@Override
		public Mono<Void> onCommit() {
			if (this.changed) {
				return this.saveDelta();
			}
			return Mono.fromCompletionStage(_cache.put(this.id, LASTACCESSED_TIME_ATTR, System.currentTimeMillis()))
					.map(s -> null);
		}

		/**
		 * 这个地方怎样做到高效， 需要删除值为 null 的数据
		 * @return
		 */
		private Mono<Void> saveDelta() {
			Map<String, Object> delta = Maps.newHashMap();
			delta.put(CREATION_TIME_ATTR, this.getCreationTime());
			delta.put(LASTACCESSED_TIME_ATTR, this.getLastAccessedTime());
			if (this.getPrincipal() != null) {
				delta.put(PRINCIPAL_ATTR, this.getPrincipal());
			}
			delta.put(AUTHENTICATED_ATTR, this.isAuthenticated());
			if (sessionAttrs != null) {
				for(String attr: sessionAttrs.keySet()) {
					if (sessionAttrs.get(attr) != null) {
						delta.put(getSessionAttrNameKey(attr), sessionAttrs.get(attr));
					}
				}
			}
			return Mono.fromCompletionStage(_cache.put(this.id, delta)).map(s -> null);
		}

		// ----------- obj Override------------------
		public boolean equals(Object obj) {
			return obj instanceof Session && this.id.equals(((Session) obj).getId());
		}

		public int hashCode() {
			return this.id.hashCode();
		}
	}
}
