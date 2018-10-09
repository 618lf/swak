/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.swak.config.quartz;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.quartz.JobStoreType;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Quartz Scheduler integration.
 *
 * @author Vedran Pavic
 * @author Stephane Nicoll
 * @since 2.0.0
 */
@ConfigurationProperties("spring.quartz")
public class QuartzProperties {

	/**
	 * Quartz job store type.
	 */
	private JobStoreType jobStoreType = JobStoreType.MEMORY;

	/**
	 * Additional Quartz Scheduler properties.
	 */
	private final Map<String, String> properties = new HashMap<>();
	
	public JobStoreType getJobStoreType() {
		return this.jobStoreType;
	}

	public void setJobStoreType(JobStoreType jobStoreType) {
		this.jobStoreType = jobStoreType;
	}

	public Map<String, String> getProperties() {
		return this.properties;
	}

}
