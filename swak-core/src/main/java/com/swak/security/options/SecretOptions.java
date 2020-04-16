package com.swak.security.options;

/**
 * Options describing a secret.
 *
 * @author <a href="mailto:marco@viafoura.com">Marco Monaco</a>
 */
public class SecretOptions {
	// Defaults
	private static final String TYPE = "HS256";

	private String type;
	private String secret;

	public SecretOptions() {
		init();
	}

	public SecretOptions(SecretOptions other) {
		type = other.getType();
		secret = other.getSecret();
	}

	public String getType() {
		return type;
	}

	public SecretOptions setType(String type) {
		this.type = type;
		return this;
	}

	public String getSecret() {
		return secret;
	}

	public SecretOptions setSecret(String secret) {
		this.secret = secret;
		return this;
	}

	private void init() {
		type = TYPE;
	}
}
