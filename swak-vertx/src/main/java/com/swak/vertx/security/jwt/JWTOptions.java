package com.swak.vertx.security.jwt;

import java.util.ArrayList;
import java.util.List;

/**
 * JWT 的配置项目
 * 
 * @author lifeng
 * 
 */
public class JWTOptions {

	private static final JWTHeader EMPTY = new JWTHeader();

	private int leeway = 0;
	private boolean ignoreExpiration;
	private String algorithm = "HS512";
	private JWTHeader header = EMPTY;
	private boolean noTimestamp;
	private int expiresInSeconds;
	private List<String> audience;
	private String issuer;
	private String subject;

	public int getLeeway() {
		return leeway;
	}

	public JWTOptions setLeeway(int leeway) {
		this.leeway = leeway;
		return this;
	}

	public boolean isIgnoreExpiration() {
		return ignoreExpiration;
	}

	public JWTOptions setIgnoreExpiration(boolean ignoreExpiration) {
		this.ignoreExpiration = ignoreExpiration;
		return this;
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public JWTOptions setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
		return this;
	}

	public JWTHeader getHeader() {
		return header;
	}

	public JWTOptions setHeader(JWTHeader header) {
		this.header = header;
		return this;
	}

	public boolean isNoTimestamp() {
		return noTimestamp;
	}

	public JWTOptions setNoTimestamp(boolean noTimestamp) {
		this.noTimestamp = noTimestamp;
		return this;
	}

	public int getExpiresInSeconds() {
		return expiresInSeconds;
	}

	public JWTOptions setExpiresInSeconds(int expiresInSeconds) {
		this.expiresInSeconds = expiresInSeconds;
		return this;
	}

	public JWTOptions setExpiresInMinutes(int expiresInMinutes) {
		this.expiresInSeconds = expiresInMinutes * 60;
		return this;
	}

	public List<String> getAudience() {
		return audience;
	}

	public JWTOptions setAudience(List<String> audience) {
		this.audience = audience;
		return this;
	}

	public JWTOptions addAudience(String audience) {
		if (this.audience == null) {
			this.audience = new ArrayList<>();
		}
		this.audience.add(audience);
		return this;
	}

	public String getIssuer() {
		return issuer;
	}

	public JWTOptions setIssuer(String issuer) {
		this.issuer = issuer;
		return this;
	}

	public String getSubject() {
		return subject;
	}

	public JWTOptions setSubject(String subject) {
		this.subject = subject;
		return this;
	}
}
