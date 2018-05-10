package com.swak.reactivex.web.function;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.util.Assert;

import com.swak.reactivex.server.HttpServerRequest;
import com.swak.reactivex.web.function.pattern.PathContainer;
import com.swak.reactivex.web.function.pattern.PathPattern;
import com.swak.reactivex.web.function.pattern.PathPatternParser;

import io.netty.handler.codec.http.HttpMethod;

public abstract class RequestPredicates {

	private static final PathPatternParser DEFAULT_PATTERN_PARSER = new PathPatternParser();
	
	/**
	 * Return a {@code RequestPredicate} that always matches.
	 * @return a predicate that always matches
	 */
	public static RequestPredicate all() {
		return request -> true;
	}
	
	/**
	 * Return a {@code RequestPredicate} that tests against the given HTTP method.
	 * @param httpMethod the HTTP method to match to
	 * @return a predicate that tests against the given HTTP method
	 */
	public static RequestPredicate method(HttpMethod httpMethod) {
		return new HttpMethodPredicate(httpMethod);
	}
	
	/**
	 * Return a {@code RequestPredicate} that tests the request path against the given path pattern.
	 * @param pattern the pattern to match to
	 * @return a predicate that tests against the given path pattern
	 */
	public static RequestPredicate path(String pattern) {
		Assert.notNull(pattern, "'pattern' must not be null");
		return pathPredicates(DEFAULT_PATTERN_PARSER).apply(pattern);
	}
	
	/**
	 * Return a function that creates new path-matching {@code RequestPredicates} from pattern
	 * Strings using the given {@link PathPatternParser}. This method can be used to specify a
	 * non-default, customized {@code PathPatternParser} when resolving path patterns.
	 * @param patternParser the parser used to parse patterns given to the returned function
	 * @return a function that resolves patterns Strings into path-matching
	 * {@code RequestPredicate}s
	 */
	public static Function<String, RequestPredicate> pathPredicates(PathPatternParser patternParser) {
		Assert.notNull(patternParser, "'patternParser' must not be null");
		return pattern -> new PathPatternPredicate(patternParser.parse(pattern));
	}
	
	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code GET}
	 * and the given {@code pattern} matches against the request path.
	 * @param pattern the path pattern to match against
	 * @return a predicate that matches if the request method is GET and if the given pattern
	 * matches against the request path
	 */
	public static RequestPredicate GET(String pattern) {
		return method(HttpMethod.GET).and(path(pattern));
	}
	
	/**
	 * Return a {@code RequestPredicate} that matches if request's HTTP method is {@code POST}
	 * and the given {@code pattern} matches against the request path.
	 * @param pattern the path pattern to match against
	 * @return a predicate that matches if the request method is POST and if the given pattern
	 * matches against the request path
	 */
	public static RequestPredicate POST(String pattern) {
		return method(HttpMethod.POST).and(path(pattern));
	}
	
	/**
	 * path 处理
	 * @author lifeng
	 */
	public static class PathPatternPredicate implements RequestPredicate {
		private final PathPattern pattern;
		public PathPatternPredicate(PathPattern pattern) {
			this.pattern = pattern;
		}
		
		@Override
		public boolean test(HttpServerRequest request) {
			PathContainer pathContainer = PathContainer.parsePath(request.getRequestURL());
			PathPattern.PathMatchInfo info = this.pattern.matchAndExtract(pathContainer);
			if (info != null) {
				request.addPathVariables(info.getUriVariables());
				return true;
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * HttpMethod 处理
	 * @author lifeng
	 */
	public static class HttpMethodPredicate implements RequestPredicate {

		private final HttpMethod httpMethod;
		
		public HttpMethodPredicate(HttpMethod httpMethod) {
			this.httpMethod = httpMethod;
		}
		
		@Override
		public boolean test(HttpServerRequest request) {
			boolean match = this.httpMethod == request.getRequestMethod();
			return match;
		}
	}
	
	static class AndRequestPredicate implements RequestPredicate {

		private final RequestPredicate left;

		private final RequestPredicate right;

		public AndRequestPredicate(RequestPredicate left, RequestPredicate right) {
			this.left = left;
			this.right = right;
		}

		@Override
		public boolean test(HttpServerRequest t) {
			return this.left.test(t) && this.right.test(t);
		}

		@Override
		public Optional<HttpServerRequest> nest(HttpServerRequest request) {
			return this.left.nest(request).flatMap(this.right::nest);
		}

		@Override
		public String toString() {
			return String.format("(%s && %s)", this.left, this.right);
		}
	}
}