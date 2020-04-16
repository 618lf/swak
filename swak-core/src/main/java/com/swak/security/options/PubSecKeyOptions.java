package com.swak.security.options;

/**
 * Options describing how a Cryptographic Key.
 *
 * @author <a href="mailto:plopes@redhat.com">Paulo Lopes</a>
 */
public class PubSecKeyOptions {

	private String algorithm;
	private boolean certificate;
	private boolean symmetric;
	private String publicKey;
	private String secretKey;

	/**
	 * Default constructor
	 */
	public PubSecKeyOptions() {
	}

	/**
	 * Copy constructor
	 *
	 * @param other the options to copy
	 */
	public PubSecKeyOptions(PubSecKeyOptions other) {
		algorithm = other.getAlgorithm();
		publicKey = other.getPublicKey();
		secretKey = other.getSecretKey();
		symmetric = other.isSymmetric();
		certificate = other.isCertificate();
	}

	public String getAlgorithm() {
		return algorithm;
	}

	public PubSecKeyOptions setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
		return this;
	}

	public String getPublicKey() {
		return publicKey;
	}

	public PubSecKeyOptions setPublicKey(String publicKey) {
		this.publicKey = publicKey;
		return this;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public PubSecKeyOptions setSecretKey(String secretKey) {
		this.secretKey = secretKey;
		return this;
	}

	public boolean isSymmetric() {
		return symmetric;
	}

	public PubSecKeyOptions setSymmetric(boolean symmetric) {
		this.symmetric = symmetric;
		return this;
	}

	public boolean isCertificate() {
		return certificate;
	}

	public PubSecKeyOptions setCertificate(boolean certificate) {
		this.certificate = certificate;
		return this;
	}
}
