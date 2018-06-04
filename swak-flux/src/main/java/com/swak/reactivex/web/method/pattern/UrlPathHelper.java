package com.swak.reactivex.web.method.pattern;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.swak.reactivex.transport.http.HttpServerRequest;

public class UrlPathHelper {

	/**
	 * Return the mapping lookup path for the given request, within the current
	 * servlet mapping if applicable, else within the web application.
	 * <p>Detects include request URL if called within a RequestDispatcher include.
	 * @param request current HTTP request
	 * @return the lookup path
	 * @see #getPathWithinApplication
	 * @see #getPathWithinServletMapping
	 */
	public String getLookupPathForRequest(HttpServerRequest request) {
		return getPathWithinApplication(request);
	}


	/**
	 * Return the path within the web application for the given request.
	 * <p>Detects include request URL if called within a RequestDispatcher include.
	 * @param request current HTTP request
	 * @return the path within the web application
	 */
	public String getPathWithinApplication(HttpServerRequest request) {
		String requestUri = getRequestUri(request);
		String path = getRemainingPath(requestUri, "", true);
		if (path != null) {
			// Normal case: URI contains context path.
			return (StringUtils.hasText(path) ? path : "/");
		}
		else {
			return requestUri;
		}
	}

	/**
	 * Match the given "mapping" to the start of the "requestUri" and if there
	 * is a match return the extra part. This method is needed because the
	 * context path and the servlet path returned by the HttpServerRequest are
	 * stripped of semicolon content unlike the requesUri.
	 */
	private String getRemainingPath(String requestUri, String mapping, boolean ignoreCase) {
		int index1 = 0;
		int index2 = 0;
		for (; (index1 < requestUri.length()) && (index2 < mapping.length()); index1++, index2++) {
			char c1 = requestUri.charAt(index1);
			char c2 = mapping.charAt(index2);
			if (c1 == ';') {
				index1 = requestUri.indexOf('/', index1);
				if (index1 == -1) {
					return null;
				}
				c1 = requestUri.charAt(index1);
			}
			if (c1 == c2) {
				continue;
			}
			else if (ignoreCase && (Character.toLowerCase(c1) == Character.toLowerCase(c2))) {
				continue;
			}
			return null;
		}
		if (index2 != mapping.length()) {
			return null;
		}
		else if (index1 == requestUri.length()) {
			return "";
		}
		else if (requestUri.charAt(index1) == ';') {
			index1 = requestUri.indexOf('/', index1);
		}
		return (index1 != -1 ? requestUri.substring(index1) : "");
	}

	/**
	 * Return the request URI for the given request, detecting an include request
	 * URL if called within a RequestDispatcher include.
	 * <p>As the value returned by {@code request.getRequestURI()} is <i>not</i>
	 * decoded by the servlet container, this method will decode it.
	 * <p>The URI that the web container resolves <i>should</i> be correct, but some
	 * containers like JBoss/Jetty incorrectly include ";" strings like ";jsessionid"
	 * in the URI. This method cuts off such incorrect appendices.
	 * @param request current HTTP request
	 * @return the request URI
	 */
	public String getRequestUri(HttpServerRequest request) {
		return request.getRequestURL();
	}
	
	public Map<String, String> decodePathVariables(HttpServerRequest request, Map<String, String> vars) {
		Map<String, String> decodedVars = new LinkedHashMap<String, String>(vars.size());
		for (Entry<String, String> entry : vars.entrySet()) {
			decodedVars.put(entry.getKey(), decodeInternal(request, entry.getValue()));
		}
		return decodedVars;
	}
	
	private String decodeInternal(HttpServerRequest request, String source) {
		String enc = determineEncoding(request);
		try {
			return decode(source, enc);
		}
		catch (UnsupportedEncodingException ex) {
			try {
				return URLDecoder.decode(source, enc);
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	protected String determineEncoding(HttpServerRequest request) {
		String enc = request.getCharacterEncoding();
		if (enc == null) {
			enc = "utf-8";
		}
		return enc;
	}
	
	public static String decode(String source, String encoding) throws UnsupportedEncodingException {
		if (source == null) {
			return null;
		}
		Assert.hasLength(encoding, "Encoding must not be empty");
		int length = source.length();
		ByteArrayOutputStream bos = new ByteArrayOutputStream(length);
		boolean changed = false;
		for (int i = 0; i < length; i++) {
			int ch = source.charAt(i);
			if (ch == '%') {
				if ((i + 2) < length) {
					char hex1 = source.charAt(i + 1);
					char hex2 = source.charAt(i + 2);
					int u = Character.digit(hex1, 16);
					int l = Character.digit(hex2, 16);
					if (u == -1 || l == -1) {
						throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
					}
					bos.write((char) ((u << 4) + l));
					i += 2;
					changed = true;
				}
				else {
					throw new IllegalArgumentException("Invalid encoded sequence \"" + source.substring(i) + "\"");
				}
			}
			else {
				bos.write(ch);
			}
		}
		return (changed ? new String(bos.toByteArray(), encoding) : source);
	}
}