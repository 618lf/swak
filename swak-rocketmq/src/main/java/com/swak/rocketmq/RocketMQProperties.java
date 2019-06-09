package com.swak.rocketmq;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.swak.Constants;

/**
 * RocketMQ client options, most
 */
@ConfigurationProperties(prefix = Constants.ROCKETMQ_PREFIX)
public class RocketMQProperties {

	/**
	 * The name server for rocketMQ, formats: `host:port;host:port`.
	 */
	private String nameServer;

	private Producer producer;

	public String getNameServer() {
		return nameServer;
	}

	public void setNameServer(String nameServer) {
		this.nameServer = nameServer;
	}

	public RocketMQProperties.Producer getProducer() {
		return producer;
	}

	public void setProducer(RocketMQProperties.Producer producer) {
		this.producer = producer;
	}

	public static class Producer {

		/**
		 * Name of producer.
		 */
		private String group;

		/**
		 * Millis of send message timeout.
		 */
		private int sendMessageTimeout = 3000;

		/**
		 * Compress message body threshold, namely, message body larger than 4k will be
		 * compressed on default.
		 */
		private int compressMessageBodyThreshold = 1024 * 4;

		/**
		 * Maximum number of retry to perform internally before claiming sending failure
		 * in synchronous mode. This may potentially cause message duplication which is
		 * up to application developers to resolve.
		 */
		private int retryTimesWhenSendFailed = 2;

		/**
		 * <p>
		 * Maximum number of retry to perform internally before claiming sending failure
		 * in asynchronous mode.
		 * </p>
		 * This may potentially cause message duplication which is up to application
		 * developers to resolve.
		 */
		private int retryTimesWhenSendAsyncFailed = 2;

		/**
		 * Indicate whether to retry another broker on sending failure internally.
		 */
		private boolean retryNextServer = false;

		/**
		 * Maximum allowed message size in bytes.
		 */
		private int maxMessageSize = 1024 * 1024 * 4;

		public String getGroup() {
			return group;
		}

		public void setGroup(String group) {
			this.group = group;
		}

		public int getSendMessageTimeout() {
			return sendMessageTimeout;
		}

		public void setSendMessageTimeout(int sendMessageTimeout) {
			this.sendMessageTimeout = sendMessageTimeout;
		}

		public int getCompressMessageBodyThreshold() {
			return compressMessageBodyThreshold;
		}

		public void setCompressMessageBodyThreshold(int compressMessageBodyThreshold) {
			this.compressMessageBodyThreshold = compressMessageBodyThreshold;
		}

		public int getRetryTimesWhenSendFailed() {
			return retryTimesWhenSendFailed;
		}

		public void setRetryTimesWhenSendFailed(int retryTimesWhenSendFailed) {
			this.retryTimesWhenSendFailed = retryTimesWhenSendFailed;
		}

		public int getRetryTimesWhenSendAsyncFailed() {
			return retryTimesWhenSendAsyncFailed;
		}

		public void setRetryTimesWhenSendAsyncFailed(int retryTimesWhenSendAsyncFailed) {
			this.retryTimesWhenSendAsyncFailed = retryTimesWhenSendAsyncFailed;
		}

		public boolean isRetryNextServer() {
			return retryNextServer;
		}

		public void setRetryNextServer(boolean retryNextServer) {
			this.retryNextServer = retryNextServer;
		}

		public int getMaxMessageSize() {
			return maxMessageSize;
		}

		public void setMaxMessageSize(int maxMessageSize) {
			this.maxMessageSize = maxMessageSize;
		}
	}
}
