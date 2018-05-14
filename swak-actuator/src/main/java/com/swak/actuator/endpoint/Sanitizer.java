package com.swak.actuator.endpoint;

import java.util.regex.Pattern;

import org.springframework.util.Assert;

public class Sanitizer {
	
	private static final String[] REGEX_PARTS = { "*", "$", "^", "+" };

	private Pattern[] keysToSanitize;

	public Sanitizer() {
		this("password", "secret", "key", "token", ".*credentials.*", "vcap_services",
				"sun.java.command");
	}

	public Sanitizer(String... keysToSanitize) {
		setKeysToSanitize(keysToSanitize);
	}

	/**
	 * Keys that should be sanitized. Keys can be simple strings that the property ends
	 * with or regular expressions.
	 * @param keysToSanitize the keys to sanitize
	 */
	public void setKeysToSanitize(String... keysToSanitize) {
		Assert.notNull(keysToSanitize, "KeysToSanitize must not be null");
		this.keysToSanitize = new Pattern[keysToSanitize.length];
		for (int i = 0; i < keysToSanitize.length; i++) {
			this.keysToSanitize[i] = getPattern(keysToSanitize[i]);
		}
	}

	private Pattern getPattern(String value) {
		if (isRegex(value)) {
			return Pattern.compile(value, Pattern.CASE_INSENSITIVE);
		}
		return Pattern.compile(".*" + value + "$", Pattern.CASE_INSENSITIVE);
	}

	private boolean isRegex(String value) {
		for (String part : REGEX_PARTS) {
			if (value.contains(part)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Sanitize the given value if necessary.
	 * @param key the key to sanitize
	 * @param value the value
	 * @return the potentially sanitized value
	 */
	public Object sanitize(String key, Object value) {
		if (value == null) {
			return null;
		}
		for (Pattern pattern : this.keysToSanitize) {
			if (pattern.matcher(key).matches()) {
				return "******";
			}
		}
		return value;
	}
}
