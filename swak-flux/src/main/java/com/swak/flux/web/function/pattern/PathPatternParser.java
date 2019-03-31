package com.swak.flux.web.function.pattern;

public class PathPatternParser {

	private boolean matchOptionalTrailingSeparator = true;

	private boolean caseSensitive = true;

	/**
	 * Whether a {@link PathPattern} produced by this parser should should
	 * automatically match request paths with a trailing slash.
	 *
	 * <p>
	 * If set to {@code true} a {@code PathPattern} without a trailing slash will
	 * also match request paths with a trailing slash. If set to {@code false} a
	 * {@code PathPattern} will only match request paths with a trailing slash.
	 *
	 * <p>
	 * The default is {@code true}.
	 */
	public void setMatchOptionalTrailingSeparator(boolean matchOptionalTrailingSeparator) {
		this.matchOptionalTrailingSeparator = matchOptionalTrailingSeparator;
	}

	/**
	 * Whether optional trailing slashing match is enabled.
	 */
	public boolean isMatchOptionalTrailingSeparator() {
		return this.matchOptionalTrailingSeparator;
	}

	/**
	 * Whether path pattern matching should be case-sensitive.
	 * <p>
	 * The default is {@code true}.
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * Whether case-sensitive pattern matching is enabled.
	 */
	public boolean isCaseSensitive() {
		return this.caseSensitive;
	}

	/**
	 * Accessor used for the separator to use.
	 * <p>
	 * Currently not exposed for configuration with URI path patterns and mainly for
	 * use in InternalPathPatternParser and PathPattern. If required in the future,
	 * a similar option would also need to be exposed in
	 * {@link org.springframework.http.server.PathContainer PathContainer}.
	 */
	char getSeparator() {
		return '/';
	}

	/**
	 * Process the path pattern content, a character at a time, breaking it into
	 * path elements around separator boundaries and verifying the structure at each
	 * stage. Produces a PathPattern object that can be used for fast matching
	 * against paths. Each invocation of this method delegates to a new instance of
	 * the {@link InternalPathPatternParser} because that class is not thread-safe.
	 * 
	 * @param pathPattern
	 *            the input path pattern, e.g. /foo/{bar}
	 * @return a PathPattern for quickly matching paths against request paths
	 * @throws PatternParseException
	 *             in case of parse errors
	 */
	public PathPattern parse(String pathPattern) throws PatternParseException {
		return new InternalPathPatternParser(this).parse(pathPattern);
	}
}
