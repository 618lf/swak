package com.swak.reactivex.web.function;

import java.util.Optional;

import org.springframework.util.Assert;

import com.swak.reactivex.transport.http.server.HttpServerRequest;

/**
 * 请求的条件匹配
 * @author lifeng
 */
public interface RequestPredicate {
	
	/**
	 * 校验是否满足此条件
	 * @param request
	 * @return
	 */
	boolean test(HttpServerRequest request);
	
	/**
	 * Return a composed request predicate that tests against both this predicate AND
	 * the {@code other} predicate. When evaluating the composed predicate, if this
	 * predicate is {@code false}, then the {@code other} predicate is not evaluated.
	 * @param other a predicate that will be logically-ANDed with this predicate
	 * @return a predicate composed of this predicate AND the {@code other} predicate
	 */
	default RequestPredicate and(RequestPredicate other) {
		Assert.notNull(other, "'other' must not be null");
		return new RequestPredicates.AndRequestPredicate(this, other);
	}
	
    /**
     * 
     * @param request
     * @return
     */
	default Optional<HttpServerRequest> nest(HttpServerRequest request) {
		return (test(request) ? Optional.of(request) : Optional.empty());
	}
}