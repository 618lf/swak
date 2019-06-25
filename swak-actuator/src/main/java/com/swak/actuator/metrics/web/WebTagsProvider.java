package com.swak.actuator.metrics.web;

import com.swak.flux.transport.server.HttpServerRequest;

import io.micrometer.core.instrument.Tag;

@FunctionalInterface
public interface WebTagsProvider {

	/**
	 * 统计的tag
	 * @param request
	 * @param ex
	 * @return
	 */
	Iterable<Tag> httpRequestTags(HttpServerRequest request, Throwable ex);
}
