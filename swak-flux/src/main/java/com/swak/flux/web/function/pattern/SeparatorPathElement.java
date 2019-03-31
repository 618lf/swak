package com.swak.flux.web.function.pattern;

import com.swak.flux.web.function.pattern.PathPattern.MatchingContext;

public class SeparatorPathElement extends PathElement {

	SeparatorPathElement(int pos, char separator) {
		super(pos, separator);
	}

	/**
	 * Matching a separator is easy, basically the character at candidateIndex must
	 * be the separator.
	 */
	@Override
	public boolean matches(int pathIndex, MatchingContext matchingContext) {
		if (pathIndex < matchingContext.pathLength && matchingContext.isSeparator(pathIndex)) {
			if (isNoMorePattern()) {
				if (matchingContext.determineRemainingPath) {
					matchingContext.remainingPathIndex = pathIndex + 1;
					return true;
				} else {
					return (pathIndex + 1 == matchingContext.pathLength);
				}
			} else {
				pathIndex++;
				return (this.next != null && this.next.matches(pathIndex, matchingContext));
			}
		}
		return false;
	}

	@Override
	public int getNormalizedLength() {
		return 1;
	}

	public String toString() {
		return "Separator(" + this.separator + ")";
	}

	public char[] getChars() {
		return new char[] { this.separator };
	}
}
