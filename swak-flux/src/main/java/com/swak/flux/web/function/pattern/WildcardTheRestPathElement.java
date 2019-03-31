package com.swak.flux.web.function.pattern;

public class WildcardTheRestPathElement extends PathElement{

	WildcardTheRestPathElement(int pos, char separator) {
		super(pos, separator);
	}


	@Override
	public boolean matches(int pathIndex, PathPattern.MatchingContext matchingContext) {
		// If there is more data, it must start with the separator
		if (pathIndex < matchingContext.pathLength && !matchingContext.isSeparator(pathIndex)) {
			return false;
		}
		if (matchingContext.determineRemainingPath) {
			matchingContext.remainingPathIndex = matchingContext.pathLength;
		}
		return true;
	}

	@Override
	public int getNormalizedLength() {
		return 1;
	}

	@Override
	public int getWildcardCount() {
		return 1;
	}


	public String toString() {
		return "WildcardTheRest(" + this.separator + "**)";
	}

	@Override
	public char[] getChars() {
		return (this.separator+"**").toCharArray();
	}
}
