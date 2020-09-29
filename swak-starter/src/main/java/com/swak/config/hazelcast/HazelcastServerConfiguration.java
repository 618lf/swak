/*
 * Copyright 2012-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swak.config.hazelcast;

import java.io.IOException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastConfigResourceCondition;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastInstanceFactory;
import org.springframework.boot.autoconfigure.hazelcast.HazelcastProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * Configuration for Hazelcast server.
 *
 * @author Stephane Nicoll
 * @author Vedran Pavic
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(HazelcastInstance.class)
class HazelcastServerConfiguration {

	static final String CONFIG_SYSTEM_PROPERTY = "hazelcast.config";

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnMissingBean(Config.class)
	@Conditional(ConfigAvailableCondition.class)
	static class HazelcastServerConfigFileConfiguration {

		@Bean
		HazelcastInstance hazelcastInstance(HazelcastProperties properties) throws IOException {
			Resource config = properties.resolveConfigLocation();
			if (config != null) {
				return new HazelcastInstanceFactory(config).getHazelcastInstance();
			}
			return Hazelcast.newHazelcastInstance();
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnSingleCandidate(Config.class)
	static class HazelcastServerConfigConfiguration {

		@Bean
		HazelcastInstance hazelcastInstance(Config config) {
			return new HazelcastInstanceFactory(config).getHazelcastInstance();
		}

	}

	/**
	 * {@link HazelcastConfigResourceCondition} that checks if the
	 * {@code spring.hazelcast.config} configuration key is defined.
	 */
	static class ConfigAvailableCondition extends HazelcastConfigResourceCondition {

		ConfigAvailableCondition() {
			super(CONFIG_SYSTEM_PROPERTY, "file:./hazelcast.xml", "classpath:/hazelcast.xml", "file:./hazelcast.yaml",
					"classpath:/hazelcast.yaml");
		}

	}

}