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

package com.swak.config.mongo;

import java.util.Collections;

import org.springframework.util.Assert;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClients;

/**
 * A factory for a async {@link MongoClient} that applies {@link MongoProperties}.
 *
 * @author Mark Paluch
 * @author Stephane Nicoll
 * @since 2.0.0
 */
@SuppressWarnings("deprecation")
public class MongoClientFactory {

	private final MongoProperties properties;

	public MongoClientFactory(MongoProperties properties) {
		this.properties = properties;
	}

	/**
	 * Creates a {@link MongoClient} using the given {@code settings}. If the environment
	 * contains a {@code local.mongo.port} property, it is used to configure a client to
	 * an embedded MongoDB instance.
	 * @param settings the settings
	 * @return the Mongo client
	 */
	public MongoClient createMongoClient(MongoClientSettings settings) {
		return createNetworkMongoClient(settings);
	}

	private MongoClient createNetworkMongoClient(MongoClientSettings settings) {
		if (hasCustomAddress() || hasCustomCredentials()) {
			return createCredentialNetworkMongoClient(settings);
		}
		ConnectionString connectionString = new ConnectionString(this.properties.determineUri());
		return createMongoClient(createBuilder(settings, connectionString));
	}	

	private MongoClient createCredentialNetworkMongoClient(MongoClientSettings settings) {
		Assert.state(this.properties.getUri() == null,
				"Invalid mongo configuration, " + "either uri or host/port/credentials must be specified");
		Builder builder = builder(settings);
		if (hasCustomCredentials()) {
			applyCredentials(builder);
		}
		String host = getOrDefault(this.properties.getHost(), "localhost");
		int port = getOrDefault(this.properties.getPort(), MongoProperties.DEFAULT_PORT);
		ServerAddress serverAddress = new ServerAddress(host, port);
		builder.applyToClusterSettings((cluster) -> cluster.hosts(Collections.singletonList(serverAddress)));
		return createMongoClient(builder);
	}

	private void applyCredentials(Builder builder) {
		String database = (this.properties.getAuthenticationDatabase() != null)
				? this.properties.getAuthenticationDatabase() : this.properties.getMongoClientDatabase();
		builder.credential((MongoCredential.createCredential(this.properties.getUsername(), database,
				this.properties.getPassword())));
	}

	private <T> T getOrDefault(T value, T defaultValue) {
		return (value != null) ? value : defaultValue;
	}

	private MongoClient createMongoClient(Builder builder) {
		return MongoClients.create(builder.build());
	}

	private Builder createBuilder(MongoClientSettings settings, ConnectionString connection) {
		return builder(settings).applyConnectionString(connection);
	}

	private boolean hasCustomAddress() {
		return this.properties.getHost() != null || this.properties.getPort() != null;
	}

	private boolean hasCustomCredentials() {
		return this.properties.getUsername() != null && this.properties.getPassword() != null;
	}

	private Builder builder(MongoClientSettings settings) {
		if (settings == null) {
			return MongoClientSettings.builder();
		}
		return MongoClientSettings.builder(settings);
	}
}