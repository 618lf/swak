package com.swak.flux.web.function.pattern;

import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.swak.flux.web.function.pattern.PathPattern.MatchingContext;

public abstract class PathElement {

	// Score related
	protected static final int WILDCARD_WEIGHT = 100;

	protected static final int CAPTURE_VARIABLE_WEIGHT = 1;

	protected static final MultiValueMap<String, String> NO_PARAMETERS = new LinkedMultiValueMap<>();

	// Position in the pattern where this path element starts
	protected final int pos;

	// The separator used in this path pattern
	protected final char separator;

	// The next path element in the chain
	@Nullable
	protected PathElement next;

	// The previous path element in the chain
	@Nullable
	protected PathElement prev;

	/**
	 * Create a new path element.
	 * 
	 * @param pos
	 *            the position where this path element starts in the pattern data
	 * @param separator
	 *            the separator in use in the path pattern
	 */
	PathElement(int pos, char separator) {
		this.pos = pos;
		this.separator = separator;
	}

	/**
	 * Attempt to match this path element.
	 * 
	 * @param candidatePos
	 *            the current position within the candidate path
	 * @param matchingContext
	 *            encapsulates context for the match including the candidate
	 * @return {@code true} if it matches, otherwise {@code false}
	 */
	public abstract boolean matches(int candidatePos, MatchingContext matchingContext);

	/**
	 * @return the length of the path element where captures are considered to be
	 *         one character long.
	 */
	public abstract int getNormalizedLength();

	public abstract char[] getChars();

	/**
	 * Return the number of variables captured by the path element.
	 */
	public int getCaptureCount() {
		return 0;
	}

	/**
	 * Return the number of wildcard elements (*, ?) in the path element.
	 */
	public int getWildcardCount() {
		return 0;
	}

	/**
	 * Return the score for this PathElement, combined score is used to compare
	 * parsed patterns.
	 */
	public int getScore() {
		return 0;
	}

	/**
	 * @return true if the there are no more PathElements in the pattern
	 */
	protected final boolean isNoMorePattern() {
		return this.next == null;
	}
}
