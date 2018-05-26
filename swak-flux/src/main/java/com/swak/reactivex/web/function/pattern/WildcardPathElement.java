package com.swak.reactivex.web.function.pattern;

import com.swak.reactivex.web.function.pattern.PathContainer.Element;
import com.swak.reactivex.web.function.pattern.PathPattern.MatchingContext;

public class WildcardPathElement extends PathElement {

	public WildcardPathElement(int pos, char separator) {
		super(pos, separator);
	}

	/**
	 * Matching on a WildcardPathElement is quite straight forward. Scan the
	 * candidate from the candidateIndex onwards for the next separator or the end
	 * of the candidate.
	 */
	@Override
	public boolean matches(int pathIndex, MatchingContext matchingContext) {
		String segmentData = null;
		// Assert if it exists it is a segment
		if (pathIndex < matchingContext.pathLength) {
			Element element = matchingContext.pathElements.get(pathIndex);
			if (!(element instanceof PathContainer.PathSegment)) {
				// Should not match a separator
				return false;
			}
			segmentData = ((PathContainer.PathSegment) element).valueToMatch();
			pathIndex++;
		}

		if (isNoMorePattern()) {
			if (matchingContext.determineRemainingPath) {
				matchingContext.remainingPathIndex = pathIndex;
				return true;
			} else {
				if (pathIndex == matchingContext.pathLength) {
					// and the path data has run out too
					return true;
				} else {
					return (matchingContext.isMatchOptionalTrailingSeparator() && // if optional slash is on...
							segmentData != null && segmentData.length() > 0 && // and there is at least one character to
																				// match the *...
							(pathIndex + 1) == matchingContext.pathLength && // and the next path element is the end of
																				// the candidate...
							matchingContext.isSeparator(pathIndex)); // and the final element is a separator
				}
			}
		} else {
			// Within a path (e.g. /aa/*/bb) there must be at least one character to match
			// the wildcard
			if (segmentData == null || segmentData.length() == 0) {
				return false;
			}
			return (this.next != null && this.next.matches(pathIndex, matchingContext));
		}
	}

	@Override
	public int getNormalizedLength() {
		return 1;
	}

	@Override
	public int getWildcardCount() {
		return 1;
	}

	@Override
	public int getScore() {
		return WILDCARD_WEIGHT;
	}

	public String toString() {
		return "Wildcard(*)";
	}

	@Override
	public char[] getChars() {
		return new char[] { '*' };
	}
}
