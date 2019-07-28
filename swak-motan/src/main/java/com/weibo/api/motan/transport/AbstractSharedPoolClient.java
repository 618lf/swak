/*
 *  Copyright 2009-2016 Weibo, Inc.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.weibo.api.motan.transport;

import java.util.ArrayList;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.swak.reactivex.threads.Contexts;
import com.weibo.api.motan.common.URLParamType;
import com.weibo.api.motan.exception.MotanServiceException;
import com.weibo.api.motan.rpc.URL;
import com.weibo.api.motan.util.CollectionUtil;
import com.weibo.api.motan.util.LoggerUtil;
import com.weibo.api.motan.util.MathUtil;

/**
 * @author sunnights
 */
@SuppressWarnings("rawtypes")
public abstract class AbstractSharedPoolClient extends AbstractClient {
	// private static final ThreadPoolExecutor EXECUTOR = new
	// StandardThreadExecutor(1, 300, 20000,
	// new DefaultThreadFactory("AbstractPoolClient-initPool-", true));
	private static final ThreadPoolExecutor EXECUTOR = Contexts.createServerContext("Motan.Client-init", 1, 300, 20000,
			2, TimeUnit.SECONDS, new AbortPolicy());
	private final AtomicInteger idx = new AtomicInteger();
	protected SharedObjectFactory factory;
	protected ArrayList<Channel> channels;
	protected int connections;

	public AbstractSharedPoolClient(URL url) {
		super(url);
		connections = url.getIntParameter(URLParamType.minClientConnection.getName(),
				URLParamType.minClientConnection.getIntValue());
		if (connections <= 0) {
			connections = URLParamType.minClientConnection.getIntValue();
		}
	}

	protected void initPool() {
		factory = createChannelFactory();

		channels = new ArrayList<>(connections);
		for (int i = 0; i < connections; i++) {
			channels.add((Channel) factory.makeObject());
		}

		initConnections(url.getBooleanParameter(URLParamType.asyncInitConnection.getName(),
				URLParamType.asyncInitConnection.getBooleanValue()));
	}

	protected abstract SharedObjectFactory createChannelFactory();

	protected void initConnections(boolean async) {
		if (async) {
			EXECUTOR.execute(new Runnable() {
				@Override
				public void run() {
					createConnections();
				}
			});
		} else {
			createConnections();
		}
	}

	private void createConnections() {
		for (Channel channel : channels) {
			try {
				channel.open();
			} catch (Exception e) {
				LoggerUtil.error("NettyClient init pool create connect Error: url=" + url.getUri(), e);
			}
		}
	}

	@SuppressWarnings("unchecked")
	protected Channel getChannel() {
		int index = MathUtil.getNonNegativeRange24bit(idx.getAndIncrement());
		Channel channel;

		for (int i = index; i < connections + 1 + index; i++) {
			channel = channels.get(i % connections);
			if (!channel.isAvailable()) {
				factory.rebuildObject(channel, i != connections + 1);
			}
			if (channel.isAvailable()) {
				return channel;
			}
		}

		String errorMsg = this.getClass().getSimpleName() + " getChannel Error: url=" + url.getUri();
		LoggerUtil.error(errorMsg);
		throw new MotanServiceException(errorMsg);
	}

	protected void closeAllChannels() {
		if (!CollectionUtil.isEmpty(channels)) {
			for (Channel channel : channels) {
				channel.close();
			}
		}
	}
}
