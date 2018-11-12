package com.swak.reactivex.transport.http.server;

import com.swak.reactivex.transport.options.ServerOptions;

import io.netty.bootstrap.ServerBootstrap;

/**
 * http 服务器的配置信息
 * 
 * @author lifeng
 */
public class HttpServerOptions extends ServerOptions {

	private final int minCompressionResponseSize;
	private final int maxInitialLineLength;
	private final int maxHeaderSize;
	private final int maxChunkSize;
	private final int initialBufferSize;
	private final boolean validateHeaders;
	private final boolean enableCors;

	private HttpServerOptions(HttpServerOptions.Builder builder) {
		super(builder);
		this.minCompressionResponseSize = builder.minCompressionResponseSize;
		this.maxInitialLineLength = builder.maxInitialLineLength;
		this.maxHeaderSize = builder.maxHeaderSize;
		this.maxChunkSize = builder.maxChunkSize;
		this.validateHeaders = builder.validateHeaders;
		this.initialBufferSize = builder.initialBufferSize;
		this.enableCors = builder.enableCors;
	}

	/**
	 * Returns the minimum response size before the output is compressed. By default
	 * the compression is disabled.
	 *
	 * @return Returns the minimum response size before the output is compressed.
	 */
	public int minCompressionResponseSize() {
		return minCompressionResponseSize;
	}

	/**
	 * 
	 * @return
	 */
	public boolean enabledCompression() {
		return minCompressionResponseSize >= 0;
	}
	
	/**
	 * 
	 * @return
	 */
	public boolean enableCors() {
		return enableCors;
	}

	/**
	 * Returns the maximum length configured for the initial HTTP line.
	 *
	 * @return the initial HTTP line maximum length
	 * @see io.netty.handler.codec.http.HttpServerCodec
	 */
	public int httpCodecMaxInitialLineLength() {
		return maxInitialLineLength;
	}

	/**
	 * Returns the configured HTTP header maximum size.
	 *
	 * @return the configured HTTP header maximum size
	 * @see io.netty.handler.codec.http.HttpServerCodec
	 */
	public int httpCodecMaxHeaderSize() {
		return maxHeaderSize;
	}

	/**
	 * Returns the configured HTTP chunk maximum size.
	 *
	 * @return the configured HTTP chunk maximum size
	 * @see io.netty.handler.codec.http.HttpServerCodec
	 */
	public int httpCodecMaxChunkSize() {
		return maxChunkSize;
	}

	/**
	 * Returns the HTTP validate headers flag.
	 *
	 * @return true if the HTTP codec validates headers, false otherwise
	 * @see io.netty.handler.codec.http.HttpServerCodec
	 */
	public boolean httpCodecValidateHeaders() {
		return validateHeaders;
	}

	/**
	 * Returns the configured HTTP codec initial buffer size.
	 *
	 * @return the configured HTTP codec initial buffer size
	 * @see io.netty.handler.codec.http.HttpServerCodec
	 */
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
		public static final boolean DEFAULT_VALIDATE_HEADERS = true;
		public static final int DEFAULT_INITIAL_BUFFER_SIZE = 128;
		public static final boolean DEFAULT_ENABLE_CORS = false;

		private int minCompressionResponseSize = -1;
		private int maxInitialLineLength = DEFAULT_MAX_INITIAL_LINE_LENGTH;
		private int maxHeaderSize = DEFAULT_MAX_HEADER_SIZE;
		private int maxChunkSize = DEFAULT_MAX_CHUNK_SIZE;
		private boolean validateHeaders = DEFAULT_VALIDATE_HEADERS;
		private int initialBufferSize = DEFAULT_INITIAL_BUFFER_SIZE;
		private boolean enableCors = DEFAULT_ENABLE_CORS;

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
		public final Builder compression(boolean enabled) {
			this.minCompressionResponseSize = enabled ? 0 : -1;
			return this;
		}

		/**
		 * Enable GZip response compression if the client request presents accept
		 * encoding headers AND the response reaches a minimum threshold
		 *
		 * @param minResponseSize
		 *            compression is performed once response size exceeds given value in
		 *            byte
		 * @return {@code this}
		 */
		public final Builder compression(int minResponseSize) {
			if (minResponseSize < 0) {
				throw new IllegalArgumentException("minResponseSize must be positive");
			}
			this.minCompressionResponseSize = minResponseSize;
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