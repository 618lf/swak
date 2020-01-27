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
package com.swak.config.rxtx;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * Configuration properties for Rxtx.
 *
 */
@ConfigurationProperties(prefix = Constants.RXTX_PREFIX)
public class RxtxProperties {

	private int works = 2;
	private int heartbeatSeconds = 10;

	public int getWorks() {
		return works;
	}

	public void setWorks(int works) {
		this.works = works;
	}

	public int getHeartbeatSeconds() {
		return heartbeatSeconds;
	}

	public void setHeartbeatSeconds(int heartbeatSeconds) {
		this.heartbeatSeconds = heartbeatSeconds;
	}
}