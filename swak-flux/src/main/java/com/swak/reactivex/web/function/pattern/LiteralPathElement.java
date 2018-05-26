package com.swak.reactivex.web.function.pattern;

import com.swak.reactivex.web.function.pattern.PathContainer.Element;
import com.swak.reactivex.web.function.pattern.PathContainer.PathSegment;
import com.swak.reactivex.web.function.pattern.PathPattern.MatchingContext;

public class LiteralPathElement extends PathElement {

	private char[] text;

	private int len;

	private boolean caseSensitive;

	public LiteralPathElement(int pos, char[] literalText, boolean caseSensitive, char separator) {
		super(pos, separator);
		this.len = literalText.length;
		this.caseSensitive = caseSensitive;
		if (caseSensitive) {
			this.text = literalText;
		} else {
			// Force all the text lower case to make matching faster
			this.text = new char[literalText.length];
			for (int i = 0; i < len; i++) {
				this.text[i] = Character.toLowerCase(literalText[i]);
			}
		}
	}

	@Override
	public boolean matches(int pathIndex, MatchingContext matchingContext) {
		if (pathIndex >= matchingContext.pathLength) {
			// no more path left to match this element
			return false;
		}
		Element element = matchingContext.pathElements.get(pathIndex);
		if (!(element instanceof PathContainer.PathSegment)) {
			return false;
		}
		String value = ((PathSegment) element).valueToMatch();
		if (value.length() != len) {
			// Not enough data to match this path element
			return false;
		}

		char[] data = ((PathContainer.PathSegment) element).valueToMatchAsChars();
		if (this.caseSensitive) {
			for (int i = 0; i < len; i++) {
				if (data[i] != this.text[i]) {
					return false;
				}
			}
		} else {
			for (int i = 0; i < len; i++) {
				// TODO revisit performance if doing a lot of case insensitive matching
				if (Character.toLowerCase(data[i]) != this.text[i]) {
					return false;
				}
			}
		}

		pathIndex++;
		if (isNoMorePattern()) {
			if (matchingContext.determineRemainingPath) {
				matchingContext.remainingPathIndex = pathIndex;
				return true;
			} else {
				if (pathIndex == matchingContext.pathLength) {
					return true;
				} else {
					return (matchingContext.isMatchOptionalTrailingSeparator()
							&& (pathIndex + 1) == matchingContext.pathLength && matchingContext.isSeparator(pathIndex));
				}
			}
		} else {
			return (this.next != null && this.next.matches(pathIndex, matchingContext));
		}
	}

	@Override
	public int getNormalizedLength() {
		return this.len;
	}

	public char[] getChars() {
		return this.text;
	}

	public String toString() {
		return "Literal(" + String.valueOf(this.text) + ")";
	}
}