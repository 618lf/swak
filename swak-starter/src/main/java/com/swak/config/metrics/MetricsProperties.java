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
package com.swak.config.metrics;

import java.util.concurrent.TimeUnit;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * 定义指标收集属性
 */
@ConfigurationProperties(prefix = Constants.ACTUATOR_METRICS)
public class MetricsProperties {

	private Reporter reporter;
	private Pool pool;
	private Jvm jvm;
	private Sql sql;
	private Method method;

	public Pool getPool() {
		return pool;
	}

	public void setPool(Pool pool) {
		this.pool = pool;
	}

	public Sql getSql() {
		return sql;
	}

	public void setSql(Sql sql) {
		this.sql = sql;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public void setReporter(Reporter reporter) {
		this.reporter = reporter;
	}

	public Jvm getJvm() {
		return jvm;
	}

	public void setJvm(Jvm jvm) {
		this.jvm = jvm;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	/**
	 * 配置上报的频率
	 * 
	 * @author lifeng
	 * @date 2020年11月6日 上午10:50:30
	 */
	public class Reporter {
		private long initialDelay = 1;
		private long period = 10;
		private TimeUnit unit = TimeUnit.SECONDS;

		public long getInitialDelay() {
			return initialDelay;
		}

		public void setInitialDelay(long initialDelay) {
			this.initialDelay = initialDelay;
		}

		public long getPeriod() {
			return period;
		}

		public void setPeriod(long period) {
			this.period = period;
		}

		public TimeUnit getUnit() {
			return unit;
		}

		public void setUnit(TimeUnit unit) {
			this.unit = unit;
		}
	}

	/**
	 * 配置JVM 的收集
	 * 
	 * @author lifeng
	 * @date 2020年11月6日 上午10:50:30
	 */
	public class Pool {
		private boolean open = Boolean.TRUE;

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}
	}

	/**
	 * 配置JVM 的收集
	 * 
	 * @author lifeng
	 * @date 2020年11月6日 上午10:50:30
	 */
	public class Jvm {
		private boolean open = Boolean.TRUE;

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}
	}

	/**
	 * 配置Sql 的收集
	 * 
	 * @author lifeng
	 * @date 2020年11月6日 上午10:50:30
	 */
	public class Sql {
		private boolean open = Boolean.TRUE;

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}
	}

	/**
	 * 配置方法的收集
	 * 
	 * @author lifeng
	 * @date 2020年11月6日 上午10:50:30
	 */
	public class Method {
		private boolean open = Boolean.TRUE;
		private boolean all;

		public boolean isOpen() {
			return open;
		}

		public void setOpen(boolean open) {
			this.open = open;
		}

		public boolean isAll() {
			return all;
		}

		public void setAll(boolean all) {
			this.all = all;
		}
	}
}