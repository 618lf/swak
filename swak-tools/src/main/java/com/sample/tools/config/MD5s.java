package com.sample.tools.config;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * 创建MD5
 * 
 * @author lifeng
 */
public class MD5s {

	private static final char[] DIGITS_UPPER = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
			'E', 'F' };
	private static final String MD5 = "MD5";

	/**
	 * Md5 编码
	 * 
	 * @param input
	 * @return
	 */
	public static String encode(String input) {
		return new String(encodeHex(digest(input.getBytes(), MD5, null, 1), DIGITS_UPPER));
	}
	
	/**
	 * Md5 编码
	 * 
	 * @param input
	 * @return
	 */
	public static String encode(byte[] input, byte[] salt) {
		return new String(encodeHex(digest(input, MD5, salt, 1), DIGITS_UPPER));
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	protected static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}

	protected static char[] encodeHex(final byte[] data, final char[] toDigits) {
		final int l = data.length;
		final char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
			out[j++] = toDigits[0x0F & data[i]];
		}
		return out;
	}
}