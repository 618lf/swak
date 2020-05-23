package com.swak.security.jwt;

import java.io.ByteArrayInputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.AlgorithmParameters;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.ECParameterSpec;
import java.security.spec.ECPoint;
import java.security.spec.ECPrivateKeySpec;
import java.security.spec.ECPublicKeySpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.InvalidParameterSpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.swak.security.jwt.impl.SignatureHelper;

/**
 * JWK https://tools.ietf.org/html/rfc7517
 *
 * In a nutshell a JWK is a Key(Pair) encoded as JSON. This implementation
 * follows the spec with some limitations:
 *
 * * Supported algorithms are: "RS256", "RS384", "RS512", "ES256", "ES384",
 * "ES512", "HS256", "HS384", "HS512"
 *
 * The rationale for this choice is to support the required algorithms for JWT.
 *
 * The constructor takes a single JWK (the the KeySet) or a PEM encoded pair
 * (used by Google and useful for importing standard PEM files from OpenSSL).
 *
 * * Certificate chains (x5c) only allow a single element chain, certificate
 * urls and fingerprints are not considered.
 *
 * @author Paulo Lopes
 */
public final class JWK implements Crypto {

	private static final Charset UTF8 = StandardCharsets.UTF_8;

	// JSON JWK properties
	private final String kid;
	private String alg;

	// decoded
	private PrivateKey privateKey;
	private PublicKey publicKey;
	private Signature signature;
	private Cipher cipher;
	private X509Certificate certificate;
	private Mac mac;

	// verify/sign mode
	private boolean symmetric;
	// special handling for ECDSA
	private boolean ecdsa;
	private int ecdsaLength;

	/**
	 * Creates a Key(Pair) from pem formatted strings.
	 *
	 * @param algorithm the algorithm e.g.: RS256
	 * @param pemPub    the public key in PEM format
	 * @param pemSec    the private key in PEM format
	 */
	public JWK(String algorithm, String pemPub, String pemSec) {
		this(algorithm, false, pemPub, pemSec);
	}

	/**
	 * Creates a Key(Pair) from pem formatted strings.
	 *
	 * @param algorithm     the algorithm e.g.: RS256
	 * @param isCertificate when true the public PEM is assumed to be a X509
	 *                      Certificate
	 * @param pemPub        the public key in PEM format
	 * @param pemSec        the private key in PEM format
	 */
	public JWK(String algorithm, boolean isCertificate, String pemPub, String pemSec) {

		try {
			@SuppressWarnings("serial")
			final Map<String, String> alias = new HashMap<String, String>() {
				{
					put("RS256", "SHA256withRSA");
					put("RS384", "SHA384withRSA");
					put("RS512", "SHA512withRSA");
					put("ES256", "SHA256withECDSA");
					put("ES384", "SHA384withECDSA");
					put("ES512", "SHA512withECDSA");
				}
			};

			final KeyFactory kf;

			switch (algorithm) {
			case "RS256":
			case "RS384":
			case "RS512":
				kf = KeyFactory.getInstance("RSA");
				break;
			case "ES256":
			case "ES384":
			case "ES512":
				kf = KeyFactory.getInstance("EC");
				ecdsa = true;
				ecdsaLength = ECDSALength(alias.get(algorithm));
				break;
			default:
				throw new RuntimeException("Unknown algorithm factory for: " + algorithm);
			}

			alg = algorithm;
			kid = algorithm + (pemPub != null ? pemPub.hashCode() : "") + "-"
					+ (pemSec != null ? pemSec.hashCode() : "");

			if (pemPub != null) {
				if (isCertificate) {
					final CertificateFactory cf = CertificateFactory.getInstance("X.509");
					certificate = (X509Certificate) cf
							.generateCertificate(new ByteArrayInputStream(pemPub.getBytes(UTF8)));
				} else {
					final X509EncodedKeySpec keyspec = new X509EncodedKeySpec(Base64.getMimeDecoder().decode(pemPub));
					publicKey = kf.generatePublic(keyspec);
				}
			}

			if (pemSec != null) {
				final PKCS8EncodedKeySpec keyspec = new PKCS8EncodedKeySpec(Base64.getMimeDecoder().decode(pemSec));
				privateKey = kf.generatePrivate(keyspec);
			}

			// use default
			signature = Signature.getInstance(alias.get(alg));

		} catch (InvalidKeySpecException | CertificateException | NoSuchAlgorithmException e) {
			// error
			throw new RuntimeException(e);
		}
	}

	/**
	 * Creates a Symmetric Key from a base64 encoded string.
	 *
	 * @param algorithm the algorithm e.g.: HS256
	 * @param hmac      the symmetric key
	 */
	public JWK(String algorithm, String hmac) {
		try {
			@SuppressWarnings("serial")
			final Map<String, String> alias = new HashMap<String, String>() {
				{
					put("HS256", "HMacSHA256");
					put("HS384", "HMacSHA384");
					put("HS512", "HMacSHA512");
				}
			};

			alg = algorithm;

			// abort if the specified algorithm is not known
			if (!alias.containsKey(alg)) {
				throw new NoSuchAlgorithmException(alg);
			}

			kid = algorithm + hmac.hashCode();

			mac = Mac.getInstance(alias.get(alg));
			mac.init(new SecretKeySpec(hmac.getBytes(UTF8), alias.get(alg)));
			// this is a symmetric key
			symmetric = true;
		} catch (NoSuchAlgorithmException | InvalidKeyException e) {
			throw new RuntimeException(e);
		}
	}

	public JWK(Map<String, Object> json) {
		kid = getString(json, "kid", UUID.randomUUID().toString());

		try {
			switch (getString(json, "kty")) {
			case "RSA":
				createRSA(json);
				break;
			case "EC":
				createEC(json);
				break;
			case "oct":
				createOCT(json);
				break;

			default:
				throw new RuntimeException("Unsupported key type: " + getString(json, "kty"));
			}
		} catch (NoSuchAlgorithmException | InvalidKeyException | InvalidKeySpecException
				| InvalidParameterSpecException | CertificateException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}

	private void createRSA(Map<String, Object> json)
			throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, NoSuchPaddingException {
		@SuppressWarnings("serial")
		final Map<String, String> alias = new HashMap<String, String>() {
			{
				put("RS256", "SHA256withRSA");
				put("RS384", "SHA384withRSA");
				put("RS512", "SHA512withRSA");
			}
		};

		// get the alias for the algorithm
		// alg = json.getString("alg", "RS256");
		alg = getString(json, "alg", "RS256");

		// abort if the specified algorithm is not known
		if (!alias.containsKey(alg)) {
			throw new NoSuchAlgorithmException(alg);
		}

		// public key
		if (jsonHasProperties(json, "n", "e")) {
			final BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "n")));
			final BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "e")));
			publicKey = KeyFactory.getInstance("RSA").generatePublic(new RSAPublicKeySpec(n, e));
		}

		// private key
		if (jsonHasProperties(json, "n", "e", "d", "p", "q", "dp", "dq", "qi")) {
			final BigInteger n = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "n")));
			final BigInteger e = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "e")));
			final BigInteger d = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "d")));
			final BigInteger p = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "p")));
			final BigInteger q = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "q")));
			final BigInteger dp = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "dp")));
			final BigInteger dq = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "dq")));
			final BigInteger qi = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "qi")));

			privateKey = KeyFactory.getInstance("RSA")
					.generatePrivate(new RSAPrivateCrtKeySpec(n, e, d, p, q, dp, dq, qi));
		}

		// certificate chain
		if (json.containsKey("x5c")) {
			// JsonArray x5c = json.getJsonArray("x5c");
			List<String> x5c = getStrings(json, "x5c");

			if (x5c.size() > 1) {
				// TODO: handle more than 1 value
				throw new RuntimeException("Certificate Chain length > 1 is not supported");
			}

			CertificateFactory cf = CertificateFactory.getInstance("X.509");

			certificate = (X509Certificate) cf
					.generateCertificate(new ByteArrayInputStream(addBoundaries(x5c.get(0)).getBytes(UTF8)));
		}

		switch (getString(json, "use", "sig")) {
		case "sig":
			try {
				// use default
				signature = Signature.getInstance(alias.get(alg));
			} catch (NoSuchAlgorithmException e) {
				// error
				throw new RuntimeException(e);
			}
			break;
		case "enc":
			cipher = Cipher.getInstance("RSA");
		}
	}

	private String addBoundaries(final String certificate) {
		return "-----BEGIN CERTIFICATE-----\n" + certificate + "\n-----END CERTIFICATE-----";
	}

	private void createEC(Map<String, Object> json) throws NoSuchAlgorithmException, InvalidKeySpecException,
			InvalidParameterSpecException, NoSuchPaddingException {
		@SuppressWarnings("serial")
		final Map<String, String> alias = new HashMap<String, String>() {
			{
				put("ES256", "SHA256withECDSA");
				put("ES384", "SHA384withECDSA");
				put("ES512", "SHA512withECDSA");
			}
		};

		// get the alias for the algorithm
		// alg = json.getString("alg", "ES256");
		alg = getString(json, "alg", "ES256");
		ecdsa = true;

		// abort if the specified algorithm is not known
		if (!alias.containsKey(alg)) {
			throw new NoSuchAlgorithmException(alg);
		}

		ecdsaLength = ECDSALength(alias.get(alg));

		AlgorithmParameters parameters = AlgorithmParameters.getInstance("EC");
		parameters.init(new ECGenParameterSpec(translate(getString(json, "crv"))));

		// public key
		if (jsonHasProperties(json, "x", "y")) {
			final BigInteger x = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "x")));
			final BigInteger y = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "y")));
			publicKey = KeyFactory.getInstance("EC").generatePublic(
					new ECPublicKeySpec(new ECPoint(x, y), parameters.getParameterSpec(ECParameterSpec.class)));
		}

		// public key
		if (jsonHasProperties(json, "x", "y", "d")) {
			@SuppressWarnings("unused")
			final BigInteger x = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "x")));
			@SuppressWarnings("unused")
			final BigInteger y = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "y")));
			final BigInteger d = new BigInteger(1, Base64.getUrlDecoder().decode(getString(json, "d")));
			privateKey = KeyFactory.getInstance("EC")
					.generatePrivate(new ECPrivateKeySpec(d, parameters.getParameterSpec(ECParameterSpec.class)));
		}

		switch (getString(json, "use", "sig")) {
		case "sig":
			try {
				// use default
				signature = Signature.getInstance(alias.get(alg));
			} catch (NoSuchAlgorithmException e) {
				// error
				throw new RuntimeException(e);
			}
			break;
		case "enc":
		default:
			throw new RuntimeException("EC Encryption not supported");
		}
	}

	private void createOCT(Map<String, Object> json)
			throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException {
		@SuppressWarnings("serial")
		final Map<String, String> alias = new HashMap<String, String>() {
			{
				put("HS256", "HMacSHA256");
				put("HS384", "HMacSHA384");
				put("HS512", "HMacSHA512");
			}
		};

		// get the alias for the algorithm
		// alg = json.getString("alg", "HS256");
		alg = getString(json, "alg", "HS256");

		// abort if the specified algorithm is not known
		if (!alias.containsKey(alg)) {
			throw new NoSuchAlgorithmException(alg);
		}

		mac = Mac.getInstance(alias.get(alg));
		mac.init(new SecretKeySpec(getString(json, "k").getBytes(UTF8), alias.get(alg)));
		// this is a symmetric key
		symmetric = true;
	}

	public String getAlgorithm() {
		return alg;
	}

	@Override
	public String getId() {
		return kid;
	}

	public synchronized byte[] encrypt(byte[] payload) {
		if (cipher == null) {
			throw new RuntimeException("Key use is not 'enc'");
		}

		try {
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			cipher.update(payload);
			return cipher.doFinal();
		} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		}
	}

	public synchronized byte[] decrypt(byte[] payload) {
		if (cipher == null) {
			throw new RuntimeException("Key use is not 'enc'");
		}

		try {
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			cipher.update(payload);
			return cipher.doFinal();
		} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public synchronized byte[] sign(byte[] payload) {
		if (symmetric) {
			return mac.doFinal(payload);
		} else {
			if (signature == null) {
				throw new RuntimeException("Key use is not 'sig'");
			}

			try {
				signature.initSign(privateKey);
				signature.update(payload);
				if (ecdsa) {
					return SignatureHelper.toJWS(signature.sign(), ecdsaLength);
				} else {
					return signature.sign();
				}
			} catch (SignatureException | InvalidKeyException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@Override
	public synchronized boolean verify(byte[] expected, byte[] payload) {
		if (symmetric) {
			return Arrays.equals(expected, sign(payload));
		} else {
			if (signature == null) {
				throw new RuntimeException("Key use is not 'sig'");
			}

			try {
				if (publicKey != null) {
					signature.initVerify(publicKey);
				}
				if (certificate != null) {
					signature.initVerify(certificate);
				}
				signature.update(payload);
				if (ecdsa) {
					return signature.verify(SignatureHelper.toDER(expected));
				} else {
					return signature.verify(expected);
				}
			} catch (SignatureException | InvalidKeyException e) {
				throw new RuntimeException(e);
			}
		}
	}

	@SuppressWarnings("unused")
	private static String translate(String crv) {
		switch (crv) {
		case "P-256":
			return "secp256r1";
		case "P-384":
			return "secp384r1";
		case "P-521":
			return "secp521r1";
		default:
			return "";
		}
	}

	private static boolean jsonHasProperties(Map<String, Object> json, String... properties) {
		for (String property : properties) {
			if (!json.containsKey(property)) {
				return false;
			}
		}

		return true;
	}

	// ****************** 添加的一些适配的方法 ************************
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<String> getStrings(Map<String, Object> map, String key) {
		Objects.requireNonNull(key);
		Object val = map.get(key);
		return (List) val;
	}

	private static String getString(Map<String, Object> map, String key) {
		Objects.requireNonNull(key);
		CharSequence cs = (CharSequence) map.get(key);
		return cs == null ? null : cs.toString();
	}

	private static String getString(Map<String, Object> map, String key, String def) {
		Objects.requireNonNull(key);
		CharSequence cs = (CharSequence) map.get(key);
		return cs != null || map.containsKey(key) ? cs == null ? null : cs.toString() : def;
	}
}
