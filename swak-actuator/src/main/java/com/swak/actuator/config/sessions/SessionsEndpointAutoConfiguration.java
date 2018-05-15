package com.swak.actuator.config.sessions;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.session.SessionsEndpoint;
import com.swak.config.AppAutoConfiguration.SessionAutoConfiguration;
import com.swak.reactivex.Session;
import com.swak.security.session.SessionRepository;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for {@link SessionsEndpoint}.
 *
 * @author Vedran Pavic
 * @since 2.0.0
 */
@Configuration
@ConditionalOnClass(SessionRepository.class)
@AutoConfigureAfter(SessionAutoConfiguration.class)
public class SessionsEndpointAutoConfiguration {

	@Bean
	@ConditionalOnBean(SessionRepository.class)
	@ConditionalOnMissingBean
	public SessionsEndpoint sessionEndpoint(
			SessionRepository<? extends Session> sessionRepository) {
		return new SessionsEndpoint(sessionRepository);
	}
}
