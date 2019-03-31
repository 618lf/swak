package com.swak.flux.web.function.pattern;

import java.util.List;

import org.springframework.util.MultiValueMap;

public interface PathContainer {

	/**
	 * The original path that was parsed.
	 */
	String value();

	/**
	 * The list of path elements, either {@link Separator} or {@link PathSegment}.
	 */
	List<Element> elements();

	/**
	 * Extract a sub-path from the given offset into the elements list.
	 * 
	 * @param index
	 *            the start element index (inclusive)
	 * @return the sub-path
	 */
	default PathContainer subPath(int index) {
		return subPath(index, elements().size());
	}

	/**
	 * Extract a sub-path from the given start offset (inclusive) into the element
	 * list and to the end offset (exclusive).
	 * 
	 * @param startIndex
	 *            the start element index (inclusive)
	 * @param endIndex
	 *            the end element index (exclusive)
	 * @return the sub-path
	 */
	default PathContainer subPath(int startIndex, int endIndex) {
		return DefaultPathContainer.subPath(this, startIndex, endIndex);
	}

	/**
	 * Parse the path value into a sequence of {@link Separator Separator} and
	 * {@link PathSegment PathSegment} elements.
	 * 
	 * @param path
	 *            the encoded, raw URL path value to parse
	 * @return the parsed path
	 */
	static PathContainer parsePath(String path) {
		return DefaultPathContainer.createFromUrlPath(path);
	}

	/**
	 * Common representation of a path element, e.g. separator or segment.
	 */
	interface Element {

		/**
		 * Return the original, raw (encoded) value for the path component.
		 */
		String value();
	}

	/**
	 * Path separator element.
	 */
	interface Separator extends Element {
	}

	/**
	 * Path segment element.
	 */
	interface PathSegment extends Element {

		/**
		 * Return the path segment value to use for pattern matching purposes. By
		 * default this is the same as {@link #value()} but may also differ in
		 * sub-interfaces (e.g. decoded, sanitized, etc.).
		 */
		String valueToMatch();

		/**
		 * The same as {@link #valueToMatch()} but as a {@code char[]}.
		 */
		char[] valueToMatchAsChars();

		/**
		 * Path parameters parsed from the path segment.
		 */
		MultiValueMap<String, String> parameters();
	}
}
