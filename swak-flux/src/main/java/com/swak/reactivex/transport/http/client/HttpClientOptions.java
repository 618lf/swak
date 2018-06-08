package com.swak.reactivex.transport.http.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.Objects;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLParameters;

import com.swak.reactivex.transport.options.ClientOptions;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.ssl.SslHandler;

/**
 * http client
 * @author lifeng
 */
public class HttpClientOptions extends ClientOptions {

	private final boolean acceptGzip;

	protected HttpClientOptions(Builder builder) {
		super(builder);
		this.acceptGzip = builder.acceptGzip;
	}

	/**
	 * Return a new {@link InetSocketAddress} from the URI.
	 * <p>
	 * If the port is undefined (-1), a default port is used (80 or 443 depending on
	 * whether the URI is secure or not). If {@link #useProxy(String) a proxy} is
	 * used, the returned address is provided unresolved.
	 *
	 * @param uri
	 *            {@link URI} to extract host and port information from
	 * @return a new eventual {@link InetSocketAddress}
	 */
	public final InetSocketAddress getRemoteAddress(URI uri) {
		Objects.requireNonNull(uri, "uri");
		boolean secure = isSecure(uri);
		int port = uri.getPort() != -1 ? uri.getPort() : (secure ? 443 : 80);
		return createInetSocketAddress(uri.getHost(), port, true);
	}

	boolean isSecure(URI uri) {
		return uri.getScheme() != null && (uri.getScheme().toLowerCase().equals(HttpClient.HTTPS_SCHEME)
				|| uri.getScheme().toLowerCase().equals(HttpClient.WSS_SCHEME));
	}
	
	@Override
	public SslHandler getSslHandler(ByteBufAllocator allocator) {
		SslHandler handler =  super.getSslHandler(allocator);
		SSLEngine sslEngine = handler.engine();
		SSLParameters sslParameters = sslEngine.getSSLParameters();
		sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
		sslEngine.setSSLParameters(sslParameters);
		return handler;
	}
	
	/**
	 * Returns true when gzip support is enabled otherwise - false
	 *
	 * @return returns true when gzip support is enabled otherwise - false
	 */
	public boolean acceptGzip() {
		return this.acceptGzip;
	}

	public static final class Builder extends ClientOptions.Builder {

		private boolean acceptGzip;

		private Builder() {
			super(new Bootstrap());
		}

		/**
		 * Enable GZip accept-encoding header and support for compressed response
		 *
		 * @param enabled
		 *            true whether gzip support is enabled
		 * @return {@code this}
		 */
		public final Builder compression(boolean enabled) {
			this.acceptGzip = enabled;
			return this;
		}

		@Override
		public HttpClientOptions build() {
			super.build();
			return new HttpClientOptions(this);
		}
	}

	/**
	 * Create a new HttpClientOptions.Builder
	 *
	 * @return a new HttpClientOptions.Builder
	 */
	public static HttpClientOptions.Builder builder() {
		return new HttpClientOptions.Builder();
	}
}
