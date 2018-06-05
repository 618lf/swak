package com.swak.actuator.metrics.web;

import org.springframework.util.StringUtils;

import com.swak.reactivex.transport.http.HttpConst;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.web.HandlerMapping;

import io.micrometer.core.instrument.Tag;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * Factory methods for {@link Tag Tags} associated with a request-response exchange that
 * is handled by WebFlux.
 *
 * @author Jon Schneider
 * @author Andy Wilkinson
 * @since 2.0.0
 */
public class WebFluxTags {

	private static final Tag URI_NOT_FOUND = Tag.of("uri", "NOT_FOUND");

	private static final Tag URI_REDIRECTION = Tag.of("uri", "REDIRECTION");

	private WebFluxTags() {
	}

	/**
	 * Creates a {@code method} tag based on the
	 * {@link org.springframework.http.server.reactive.ServerHttpRequest#getMethod()
	 * method} of the {@link ServerWebExchange#getRequest()} request of the given
	 * {@code exchange}.
	 * @param exchange the exchange
	 * @return the method tag whose value is a capitalized method (e.g. GET).
	 */
	public static Tag method(HttpServerRequest request) {
		return Tag.of("method", request.getRequestMethod().toString());
	}

	/**
	 * Creates a {@code method} tag based on the response status of the given
	 * {@code exchange}.
	 * @param exchange the exchange
	 * @return the "status" tag derived from the response status
	 */
	public static Tag status(HttpServerRequest request) {
		HttpResponseStatus status = request.getResponse().getStatus();
		return Tag.of("status", status.toString());
	}

	/**
	 * Creates a {@code uri} tag based on the URI of the given {@code exchange}. Uses the
	 * {@link HandlerMapping#BEST_MATCHING_PATTERN_ATTRIBUTE} best matching pattern.
	 * @param exchange the exchange
	 * @return the uri tag derived from the exchange
	 */
	public static Tag uri(HttpServerRequest request) {
		if (request != null) {
			Object pattern = request.getAttribute(HttpConst.ATTRIBUTE_FOR_PATH);
			if (pattern != null && !StringUtils.isEmpty(pattern)) {
				return Tag.of("uri", pattern.toString()); 
			}
			
			HttpResponseStatus status = request.getResponse().getStatus();
			if (status != null && status == HttpResponseStatus.MOVED_PERMANENTLY) {
				return URI_REDIRECTION;
			}
			if (status != null && status == HttpResponseStatus.NOT_FOUND) {
				return URI_NOT_FOUND;
			}
			String path = request.getRequestURL();
			return Tag.of("uri", path.isEmpty() ? "root" : path);
		}
		return Tag.of("uri", "UNKNOWN");
	}

	/**
	 * Creates an {@code exception} tag based on the {@link Class#getSimpleName() simple
	 * name} of the class of the given {@code exception}.
	 * @param exception the exception, may be {@code null}
	 * @return the exception tag derived from the exception
	 */
	public static Tag exception(Throwable exception) {
		if (exception != null) {
			return Tag.of("exception", exception.getClass().getSimpleName());
		}
		return Tag.of("exception", "none");
	}
}
