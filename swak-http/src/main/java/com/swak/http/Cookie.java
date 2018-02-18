package com.swak.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.swak.common.utils.StringUtils;

/**
 * Cookie
 *
 * @author biezhi 2017/6/1
 */
public class Cookie {

	public static final int DEFAULT_MAX_AGE = -1;
	public static final int DEFAULT_VERSION = -1;

	// These constants are protected on purpose so that the test case can use them
	protected static final String NAME_VALUE_DELIMITER = "=";
	protected static final String ATTRIBUTE_DELIMITER = "; ";
	protected static final long DAY_MILLIS = 86400000; // 1 day = 86,400,000 milliseconds
	protected static final String GMT_TIME_ZONE_ID = "GMT";
	protected static final String COOKIE_DATE_FORMAT_STRING = "EEE, dd-MMM-yyyy HH:mm:ss z";

	protected static final String COOKIE_HEADER_NAME = "Set-Cookie";
	protected static final String PATH_ATTRIBUTE_NAME = "Path";
	protected static final String EXPIRES_ATTRIBUTE_NAME = "Expires";
	protected static final String MAXAGE_ATTRIBUTE_NAME = "Max-Age";
	protected static final String DOMAIN_ATTRIBUTE_NAME = "Domain";
	protected static final String VERSION_ATTRIBUTE_NAME = "Version";
	protected static final String COMMENT_ATTRIBUTE_NAME = "Comment";
	protected static final String SECURE_ATTRIBUTE_NAME = "Secure";
	protected static final String HTTP_ONLY_ATTRIBUTE_NAME = "HttpOnly";
	public static final String DELETED_COOKIE_VALUE = "deleteMe";
	public static final int ONE_YEAR = 60 * 60 * 24 * 365;
	public static final String ROOT_PATH = "/";

	private String name = null;
	private String value = null;
	private String comment = null;
	private String domain = null;
	private String path = "/";
	private long maxAge = -1;
	private boolean secure = false;
	private boolean httpOnly = false;
	private int version;

	public Cookie() {
		this.maxAge = DEFAULT_MAX_AGE;
		this.version = DEFAULT_VERSION;
		this.httpOnly = true;
	}

	public String name() {
		return name;
	}

	public void name(String name) {
		this.name = name;
	}

	public String value() {
		return value;
	}

	public void value(String value) {
		this.value = value;
	}

	public String comment() {
		return this.comment;
	}

	public void comment(String comment) {
		this.comment = comment;
	}

	public String domain() {
		return domain;
	}

	public void domain(String domain) {
		this.domain = domain;
	}

	public String path() {
		return path;
	}

	public void path(String path) {
		this.path = path;
	}

	public long maxAge() {
		return maxAge;
	}

	public void maxAge(long maxAge) {
		this.maxAge = maxAge;
	}

	public boolean secure() {
		return secure;
	}

	public void secure(boolean secure) {
		this.secure = secure;
	}

	public boolean httpOnly() {
		return httpOnly;
	}

	public void httpOnly(boolean httpOnly) {
		this.httpOnly = httpOnly;
	}

	public int version() {
		return version;
	}

	public void version(int version) {
		this.version = version;
	}

	private String calculatePath(HttpServletRequest request) {
		String path = StringUtils.clean(path());
		if (!StringUtils.hasText(path)) {
			path = ROOT_PATH;
		}
		return path;
	}

	public void saveTo(HttpServletRequest request, HttpServletResponse response) {
		String name = name();
		String value = value();
		String comment = comment();
		String domain = domain();
		String path = calculatePath(request);
		long maxAge = maxAge();
		int version = version();
		boolean secure = secure();
		boolean httpOnly = httpOnly();

		addCookieHeader(response, name, value, comment, domain, path, maxAge, version, secure, httpOnly);
	}

	private void addCookieHeader(HttpServletResponse response, String name, String value, String comment, String domain,
			String path, long maxAge, int version, boolean secure, boolean httpOnly) {

		String headerValue = buildHeaderValue(name, value, comment, domain, path, maxAge, version, secure, httpOnly);
		response.header(COOKIE_HEADER_NAME, headerValue);
	}

	protected String buildHeaderValue(String name, String value, String comment, String domain, String path,
			long maxAge, int version, boolean secure, boolean httpOnly) {

		if (!StringUtils.hasText(name)) {
			throw new IllegalStateException("Cookie name cannot be null/empty.");
		}

		StringBuilder sb = new StringBuilder(name).append(NAME_VALUE_DELIMITER);

		if (StringUtils.hasText(value)) {
			sb.append(value);
		}

		appendComment(sb, comment);
		appendDomain(sb, domain);
		appendPath(sb, path);
		appendExpires(sb, maxAge);
		appendVersion(sb, version);
		appendSecure(sb, secure);
		appendHttpOnly(sb, httpOnly);

		return sb.toString();

	}

	private void appendComment(StringBuilder sb, String comment) {
		if (StringUtils.hasText(comment)) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(COMMENT_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(comment);
		}
	}

	private void appendDomain(StringBuilder sb, String domain) {
		if (StringUtils.hasText(domain)) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(DOMAIN_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(domain);
		}
	}

	private void appendPath(StringBuilder sb, String path) {
		if (StringUtils.hasText(path)) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(PATH_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(path);
		}
	}

	private void appendExpires(StringBuilder sb, long maxAge) {
		if (maxAge >= 0) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(MAXAGE_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(maxAge);
			sb.append(ATTRIBUTE_DELIMITER);
			Date expires;
			if (maxAge == 0) {
				// delete the cookie by specifying a time in the past (1 day ago):
				expires = new Date(System.currentTimeMillis() - DAY_MILLIS);
			} else {
				// Value is in seconds. So take 'now' and add that many seconds, and that's our
				// expiration date:
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.SECOND, (int) maxAge);
				expires = cal.getTime();
			}
			String formatted = toCookieDate(expires);
			sb.append(EXPIRES_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(formatted);
		}
	}

	private void appendVersion(StringBuilder sb, int version) {
		if (version > DEFAULT_VERSION) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(VERSION_ATTRIBUTE_NAME).append(NAME_VALUE_DELIMITER).append(version);
		}
	}

	private void appendSecure(StringBuilder sb, boolean secure) {
		if (secure) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(SECURE_ATTRIBUTE_NAME); // No value for this attribute
		}
	}

	private void appendHttpOnly(StringBuilder sb, boolean httpOnly) {
		if (httpOnly) {
			sb.append(ATTRIBUTE_DELIMITER);
			sb.append(HTTP_ONLY_ATTRIBUTE_NAME); // No value for this attribute
		}
	}

	private static String toCookieDate(Date date) {
		TimeZone tz = TimeZone.getTimeZone(GMT_TIME_ZONE_ID);
		DateFormat fmt = new SimpleDateFormat(COOKIE_DATE_FORMAT_STRING, Locale.US);
		fmt.setTimeZone(tz);
		return fmt.format(date);
	}

	public void removeFrom(HttpServletRequest request, HttpServletResponse response) {
		String name = name();
		String value = DELETED_COOKIE_VALUE;
		String comment = null;
		String domain = domain();
		String path = calculatePath(request);
		int maxAge = 0;
		int version = version();
		boolean secure = secure();
		boolean httpOnly = false; 
		
		addCookieHeader(response, name, value, comment, domain, path, maxAge, version, secure, httpOnly);
	}
}
