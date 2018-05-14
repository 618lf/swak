package com.swak.actuator.config.metrics.web;

import com.swak.reactivex.server.HttpServerRequest;

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
