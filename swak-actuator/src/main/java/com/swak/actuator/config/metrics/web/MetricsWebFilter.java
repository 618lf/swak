package com.swak.actuator.config.metrics.web;

import java.util.concurrent.TimeUnit;

import org.reactivestreams.Publisher;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import com.swak.reactivex.handler.WebFilter;
import com.swak.reactivex.handler.WebFilterChain;
import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.server.HttpServerResponse;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tag;
import reactor.core.publisher.Mono;

/**
 * Intercepts incoming HTTP requests handled by Spring WebFlux handlers.
 *
 * @author Jon Schneider
 * @author Brian Clozel
 * @since 2.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class MetricsWebFilter implements WebFilter {

	private final MeterRegistry registry;
	private final WebTagsProvider tagsProvider;
	private final String metricName;
	
	public MetricsWebFilter(MeterRegistry registry, WebTagsProvider tagsProvider,
			String metricName) {
		this.registry = registry;
		this.tagsProvider = tagsProvider;
		this.metricName = metricName;
	}
	
	@Override
	public Mono<Void> filter(HttpServerRequest request, HttpServerResponse response, WebFilterChain chain) {
		return chain.filter(request, response).compose((call) -> filter(request, call));
	}
	
	private Publisher<Void> filter(HttpServerRequest request, Mono<Void> call) {
		long start = System.nanoTime();
		return call.doOnSuccess((done) -> success(request, start)).doOnError((cause) -> {
			error(request, start, cause);
		});
	}
	
	private void success(HttpServerRequest request, long start) {
		Iterable<Tag> tags = this.tagsProvider.httpRequestTags(request, null);
		this.registry.timer(this.metricName, tags).record(System.nanoTime() - start,
				TimeUnit.NANOSECONDS);
	}

	private void error(HttpServerRequest request, long start, Throwable cause) {
		Iterable<Tag> tags = this.tagsProvider.httpRequestTags(request, cause);
		this.registry.timer(this.metricName, tags).record(System.nanoTime() - start,
				TimeUnit.NANOSECONDS);
	}
}
