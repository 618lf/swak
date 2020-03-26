package com.swak.oss.aliyun.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.swak.codec.Base64;

/**
 * 使用此方式签名
 * 
 * @author lifeng
 */
public class HmacSHA1Signature {

	/* The default encoding. */
	private static final String DEFAULT_ENCODING = "UTF-8";

	/* Signature method. */
	private static final String ALGORITHM = "HmacSHA1";

	private static final Object LOCK = new Object();

	/* Prototype of the Mac instance. */
	private static Mac macInstance;

	protected byte[] sign(byte[] key, byte[] data, Mac macInstance, Object lock, String algorithm) {
		try {
			// Because Mac.getInstance(String) calls a synchronized method, it
			// could block on
			// invoked concurrently, so use prototype pattern to improve perf.
			if (macInstance == null) {
				synchronized (lock) {
					if (macInstance == null) {
						macInstance = Mac.getInstance(algorithm);
					}
				}
			}

			Mac mac;
			try {
				mac = (Mac) macInstance.clone();
			} catch (CloneNotSupportedException e) {
				// If it is not clonable, create a new one.
				mac = Mac.getInstance(algorithm);
			}
			mac.init(new SecretKeySpec(key, algorithm));
			return mac.doFinal(data);
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("Unsupported algorithm: " + algorithm, ex);
		} catch (InvalidKeyException ex) {
			throw new RuntimeException("Invalid key: " + key, ex);
		}
	}

	public String computeSignature(String key, String data) {
		try {
			byte[] signData = sign(key.getBytes(DEFAULT_ENCODING), data.getBytes(DEFAULT_ENCODING), macInstance, LOCK,
					ALGORITHM);
			return new String(Base64.encodeBase64(signData));
		} catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("Unsupported algorithm: " + DEFAULT_ENCODING, ex);
		}
	}

	/**
	 * 构建签名对象
	 * 
	 * @return
	 */
	public static HmacSHA1Signature me() {
		return new HmacSHA1Signature();
	}
}
