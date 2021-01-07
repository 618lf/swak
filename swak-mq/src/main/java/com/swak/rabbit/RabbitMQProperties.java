package com.swak.rabbit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.rabbitmq.client.Address;
import com.rabbitmq.client.ConnectionFactory;
import com.swak.Constants;

/**
 * RabbitMQ client options, most
 *
 * @author <a href="mailto:julien@julienviet.com">Julien Viet</a>
 */
@ConfigurationProperties(prefix = Constants.RABBITMQ_PREFIX)
public class RabbitMQProperties {

	/**
	 * The default port = {@code - 1} - {@code 5671} for SSL otherwise {@code 5672}
	 */
	public static final int DEFAULT_PORT = -1;

	/**
	 * The default host = {@code localhost}
	 */
	public static final String DEFAULT_HOST = ConnectionFactory.DEFAULT_HOST;

	/**
	 * The default user = {@code guest}
	 */
	public static final String DEFAULT_USER = ConnectionFactory.DEFAULT_USER;

	/**
	 * The default password = {@code guest}
	 */
	public static final String DEFAULT_PASSWORD = ConnectionFactory.DEFAULT_PASS;

	/**
	 * The default virtual host = {@code /}
	 */
	public static final String DEFAULT_VIRTUAL_HOST = ConnectionFactory.DEFAULT_VHOST;

	/**
	 * The default connection timeout = {@code 60000}
	 */
	public static final int DEFAULT_CONNECTION_TIMEOUT = ConnectionFactory.DEFAULT_CONNECTION_TIMEOUT;

	/**
	 * The default connection timeout = {@code 60}
	 */
	public static final int DEFAULT_REQUESTED_HEARTBEAT = ConnectionFactory.DEFAULT_HEARTBEAT;

	/**
	 * The default handshake timeout = {@code 10000}
	 */
	public static final int DEFAULT_HANDSHAKE_TIMEOUT = ConnectionFactory.DEFAULT_HANDSHAKE_TIMEOUT;

	/**
	 * The default requested channel max = {@code 0}
	 */
	public static final int DEFAULT_REQUESTED_CHANNEL_MAX = ConnectionFactory.DEFAULT_CHANNEL_MAX;

	/**
	 * The default network recovery internal = {@code 5000}
	 */
	public static final long DEFAULT_NETWORK_RECOVERY_INTERNAL = 5000L;

	/**
	 * The default automatic recovery enabled = {@code false}
	 */
	public static final boolean DEFAULT_AUTOMATIC_RECOVERY_ENABLED = false;

	/**
	 * The default connection retry delay = {@code 10000}
	 */
	public static final long DEFAULT_CONNECTION_RETRY_DELAY = 10000L;

	/**
	 * The default connection retries = {@code null} (no retry)
	 */
	public static final Integer DEFAULT_CONNECTION_RETRIES = null;

	private Integer connectionRetries = DEFAULT_CONNECTION_RETRIES;
	private long connectionRetryDelay = DEFAULT_CONNECTION_RETRY_DELAY;
	private String uri = null;
	private List<Address> addresses = Collections.emptyList();
	private String user = DEFAULT_USER;
	private String password = DEFAULT_PASSWORD;
	private String host = DEFAULT_HOST;
	private String virtualHost = DEFAULT_VIRTUAL_HOST;
	private int port = DEFAULT_PORT;
	private int connectionTimeout = DEFAULT_CONNECTION_TIMEOUT;
	private int requestedHeartbeat = DEFAULT_REQUESTED_HEARTBEAT;
	private int handshakeTimeout = DEFAULT_HANDSHAKE_TIMEOUT;
	private int requestedChannelMax = DEFAULT_REQUESTED_CHANNEL_MAX;
	private long networkRecoveryInterval = DEFAULT_NETWORK_RECOVERY_INTERNAL;
	private boolean automaticRecoveryEnabled = DEFAULT_AUTOMATIC_RECOVERY_ENABLED;
	private boolean includeProperties = false;
	private boolean publisherConfirms = true;
	private boolean publisherReturns = true;

	public RabbitMQProperties() {
	}

	public RabbitMQProperties(RabbitMQProperties that) {
		connectionRetries = that.connectionRetries;
		connectionRetryDelay = that.connectionRetryDelay;
		uri = that.uri;
		addresses = that.addresses;
		user = that.user;
		password = that.password;
		host = that.host;
		virtualHost = that.virtualHost;
		port = that.port;
		connectionTimeout = that.connectionTimeout;
		requestedHeartbeat = that.requestedHeartbeat;
		handshakeTimeout = that.handshakeTimeout;
		networkRecoveryInterval = that.networkRecoveryInterval;
		automaticRecoveryEnabled = that.automaticRecoveryEnabled;
		includeProperties = that.includeProperties;
		requestedChannelMax = that.requestedChannelMax;
		publisherConfirms = that.publisherConfirms;
		publisherReturns = that.publisherReturns;
	}

	public Integer getConnectionRetries() {
		return connectionRetries;
	}

	public RabbitMQProperties setConnectionRetries(Integer connectionRetries) {
		this.connectionRetries = connectionRetries;
		return this;
	}

	public long getConnectionRetryDelay() {
		return connectionRetryDelay;
	}

	public RabbitMQProperties setConnectionRetryDelay(long connectionRetryDelay) {
		this.connectionRetryDelay = connectionRetryDelay;
		return this;
	}

	public List<Address> getAddresses() {
		return Collections.unmodifiableList(addresses);
	}

	public RabbitMQProperties setAddresses(List<Address> addresses) {
		this.addresses = new ArrayList<>(addresses);
		return this;
	}

	public String getUri() {
		return uri;
	}

	public RabbitMQProperties setUri(String uri) {
		this.uri = uri;
		return this;
	}

	public String getUser() {
		return user;
	}

	public RabbitMQProperties setUser(String user) {
		this.user = user;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public RabbitMQProperties setPassword(String password) {
		this.password = password;
		return this;
	}

	public String getHost() {
		return host;
	}

	public RabbitMQProperties setHost(String host) {
		this.host = host;
		return this;
	}

	public String getVirtualHost() {
		return virtualHost;
	}

	public RabbitMQProperties setVirtualHost(String virtualHost) {
		this.virtualHost = virtualHost;
		return this;
	}

	public int getPort() {
		return port;
	}

	public RabbitMQProperties setPort(int port) {
		this.port = port;
		return this;
	}

	public int getConnectionTimeout() {
		return connectionTimeout;
	}

	public RabbitMQProperties setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
		return this;
	}

	public int getRequestedHeartbeat() {
		return requestedHeartbeat;
	}

	public RabbitMQProperties setRequestedHeartbeat(int requestedHeartbeat) {
		this.requestedHeartbeat = requestedHeartbeat;
		return this;
	}

	public int getHandshakeTimeout() {
		return handshakeTimeout;
	}

	public RabbitMQProperties setHandshakeTimeout(int handshakeTimeout) {
		this.handshakeTimeout = handshakeTimeout;
		return this;
	}

	public int getRequestedChannelMax() {
		return requestedChannelMax;
	}

	public RabbitMQProperties setRequestedChannelMax(int requestedChannelMax) {
		this.requestedChannelMax = requestedChannelMax;
		return this;
	}

	public long getNetworkRecoveryInterval() {
		return networkRecoveryInterval;
	}

	public RabbitMQProperties setNetworkRecoveryInterval(long networkRecoveryInterval) {
		this.networkRecoveryInterval = networkRecoveryInterval;
		return this;
	}

	public boolean isAutomaticRecoveryEnabled() {
		return automaticRecoveryEnabled;
	}

	public RabbitMQProperties setAutomaticRecoveryEnabled(boolean automaticRecoveryEnabled) {
		this.automaticRecoveryEnabled = automaticRecoveryEnabled;
		return this;
	}

	public boolean getIncludeProperties() {
		return includeProperties;
	}

	public RabbitMQProperties setIncludeProperties(boolean includeProperties) {
		this.includeProperties = includeProperties;
		return this;
	}

	public boolean isPublisherConfirms() {
		return publisherConfirms;
	}

	public RabbitMQProperties setPublisherConfirms(boolean publisherConfirms) {
		this.publisherConfirms = publisherConfirms;
		return this;
	}

	public boolean isPublisherReturns() {
		return publisherReturns;
	}

	public RabbitMQProperties setPublisherReturns(boolean publisherReturns) {
		this.publisherReturns = publisherReturns;
		return this;
	}

}
