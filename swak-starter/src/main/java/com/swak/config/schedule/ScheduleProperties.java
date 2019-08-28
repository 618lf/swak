/*
 * Copyright 2012-2018 the original author or authors.
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
package com.swak.config.schedule;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * Configuration properties for Schedule.
 */
@ConfigurationProperties(prefix = Constants.QUARTZ_PREFIX)
public class ScheduleProperties {

	private Integer coreThreads = 1;

	public Integer getCoreThreads() {
		return coreThreads;
	}

	public void setCoreThreads(Integer coreThreads) {
		this.coreThreads = coreThreads;
	}
}