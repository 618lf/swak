package com.swak.flux.transport.http.server;

import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;

/**
 * http 服务器的配置信息
 * 
 * @author lifeng
 */
public class HttpServerOptions extends ServerOptions {

	private final int maxInitialLineLength;
	private final int maxHeaderSize;
	private final int maxChunkSize;
	private final int initialBufferSize;
	private final boolean validateHeaders;
	private final boolean enableCors;
	private final boolean enableCompression;

	private HttpServerOptions(HttpServerOptions.Builder builder) {
		super(builder);
		this.maxInitialLineLength = builder.maxInitialLineLength;
		this.maxHeaderSize = builder.maxHeaderSize;
		this.maxChunkSize = builder.maxChunkSize;
		this.validateHeaders = builder.validateHeaders;
		this.initialBufferSize = builder.initialBufferSize;
		this.enableCors = builder.enableCors;
		this.enableCompression = builder.enableCompression;
	}

	public boolean enabledCompression() {
		return enableCompression;
	}

	public boolean enableCors() {
		return enableCors;
	}

	public boolean enableReadIdle() {
		return this.getReadTimeoutMillis() != -1;
	}

	public boolean enableWriteIdle() {
		return this.getWriteTimeoutMillis() != -1;
	}

	public int httpCodecMaxInitialLineLength() {
		return maxInitialLineLength;
	}

	public int httpCodecMaxHeaderSize() {
		return maxHeaderSize;
	}

	public int httpCodecMaxChunkSize() {
		return maxChunkSize;
	}

	public boolean httpCodecValidateHeaders() {
		return validateHeaders;
	}

	public int httpCodecInitialBufferSize() {
		return initialBufferSize;
	}

	/**
	 * 创建一个构建器
	 * 
	 * @return
	 */
	public static Builder builder() {
		return new Builder();
	}

	public static class Builder extends ServerOptions.Builder {

		public static final int DEFAULT_MAX_INITIAL_LINE_LENGTH = 4096;
		public static final int DEFAULT_MAX_HEADER_SIZE = 8192;
		public static final int DEFAULT_MAX_CHUNK_SIZE = 8192;
		public static final boolean DEFAULT_VALIDATE_HEADERS = false;
		public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
		public static final boolean DEFAULT_ENABLE_CORS = false;
		public static final boolean DEFAULT_ENABLE_COMPRESSION = false;

		private int maxInitialLineLength = DEFAULT_MAX_INITIAL_LINE_LENGTH;
		private int maxHeaderSize = DEFAULT_MAX_HEADER_SIZE;
		private int maxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
		private boolean validateHeaders = DEFAULT_VALIDATE_HEADERS;
		private int initialBufferSize = DEFAULT_INITIAL_BUFFER_SIZE;
		private boolean enableCors = DEFAULT_ENABLE_CORS;
		private boolean enableCompression = DEFAULT_ENABLE_COMPRESSION;

		protected Builder() {
			super(new ServerBootstrap());
		}

		/**
		 * Enable GZip response compression if the client request presents accept
		 * encoding headers
		 *
		 * @param enabled
		 *            true whether compression is enabled
		 * @return {@code this}
		 */
		public final Builder enableCompression(boolean enabled) {
			this.enableCompression = enabled;
			return this;
		}

		/**
		 * Configure the maximum length that can be decoded for the HTTP request's
		 * initial line. Defaults to {@code #DEFAULT_MAX_INITIAL_LINE_LENGTH}.
		 *
		 * @param value
		 *            the value for the maximum initial line length (strictly positive)
		 * @return this option builder for further configuration
		 */
		public final Builder maxInitialLineLength(int value) {
			if (value <= 0) {
				throw new IllegalArgumentException("maxInitialLineLength must be strictly positive");
			}
			this.maxInitialLineLength = value;
			return this;
		}

		/**
		 * Configure the maximum header size that can be decoded for the HTTP request.
		 * Defaults to {@link #DEFAULT_MAX_HEADER_SIZE}.
		 *
		 * @param value
		 *            the value for the maximum header size (strictly positive)
		 * @return this option builder for further configuration
		 */
		public final Builder maxHeaderSize(int value) {
			if (value <= 0) {
				throw new IllegalArgumentException("maxHeaderSize must be strictly positive");
			}
			this.maxHeaderSize = value;
			return this;
		}

		/**
		 * Configure the maximum chunk size that can be decoded for the HTTP request.
		 * Defaults to {@link #DEFAULT_MAX_CHUNK_SIZE}.
		 *
		 * @param value
		 *            the value for the maximum chunk size (strictly positive)
		 * @return this option builder for further configuration
		 */
		public final Builder maxChunkSize(int value) {
			if (value <= 0) {
				throw new IllegalArgumentException("maxChunkSize must be strictly positive");
			}
			this.maxChunkSize = value;
			return this;
		}

		/**
		 * Configure whether or not to validate headers when decoding requests. Defaults
		 * to #DEFAULT_VALIDATE_HEADERS.
		 *
		 * @param validate
		 *            true to validate headers, false otherwise
		 * @return this option builder for further configuration
		 */
		public final Builder validateHeaders(boolean validate) {
			this.validateHeaders = validate;
			return this;
		}

		/**
		 * Configure the initial buffer size for HTTP request decoding. Defaults to
		 * {@link #DEFAULT_INITIAL_BUFFER_SIZE}.
		 *
		 * @param value
		 *            the initial buffer size to use (strictly positive)
		 * @return {@code this}
		 */
		public final Builder initialBufferSize(int value) {
			if (value <= 0) {
				throw new IllegalArgumentException("initialBufferSize must be strictly positive");
			}
			this.initialBufferSize = value;
			return this;
		}

		/**
		 * enableCors
		 *
		 * @param validate
		 *            true to validate headers, false otherwise
		 * @return this option builder for further configuration
		 */
		public final Builder enableCors(boolean enableCors) {
			this.enableCors = enableCors;
			return this;
		}

		/**
		 * 构建一个实际的配置
		 * 
		 * @return
		 */
		public HttpServerOptions build() {
			return new HttpServerOptions(this);
		}
	}
}