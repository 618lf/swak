package com.swak.actuator.session;

import java.util.Set;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.security.session.Session;
import com.swak.security.session.SessionRepository;

/**
 * {@link Endpoint} to expose a user's {@link Session}s.
 * @author lifeng
 */
@Endpoint(id = "sessions")
public class SessionsEndpoint {
	
	private final SessionRepository<? extends Session> sessionRepository;
	
	public SessionsEndpoint(SessionRepository<? extends Session> sessionRepository) {
		this.sessionRepository = sessionRepository;
	}
	
	@Operation
	public SessionDescriptor getSession(@Selector String sessionId) {
		Session session = this.sessionRepository.getSession(sessionId);
		if (session == null) {
			return null;
		}
		return new SessionDescriptor(session);
	}
	
	@Operation
	public void deleteSession(@Selector String sessionId) {
		this.sessionRepository.removeSession(sessionId);
	}
	
	/**
	 * A description of user's {@link Session session}. Primarily intended for
	 * serialization to JSON.
	 */
	public static final class SessionDescriptor {

		private final String id;

		private final Set<String> attributeNames;

		private final long creationTime;

		private final long lastAccessedTime;

		private final long maxInactiveInterval;

		public SessionDescriptor(Session session) {
			this.id = session.getId();
			this.attributeNames = session.getAttributeNames();
			this.creationTime = session.getCreationTime();
			this.lastAccessedTime = session.getLastAccessedTime();
			this.maxInactiveInterval = session.getMaxInactiveInterval();
		}

		public String getId() {
			return this.id;
		}

		public Set<String> getAttributeNames() {
			return this.attributeNames;
		}

		public long getCreationTime() {
			return creationTime;
		}

		public long getLastAccessedTime() {
			return lastAccessedTime;
		}

		public long getMaxInactiveInterval() {
			return maxInactiveInterval;
		}
		
	}
}