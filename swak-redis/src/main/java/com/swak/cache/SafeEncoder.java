package com.swak.cache;

import org.apache.commons.codec.Charsets;

import com.swak.exception.CacheException;

public class SafeEncoder {
	
	public static byte[][] encodeMany(final String... strs) {
		byte[][] many = new byte[strs.length][];
		for (int i = 0; i < strs.length; i++) {
			many[i] = encode(strs[i]);
		}
		return many;
	}

	public static byte[] encode(final String str) {
		if (str == null) {
			throw new CacheException("value sent to redis cannot be null");
		}
		return str.getBytes(Charsets.UTF_8);
	}

	public static String encode(final byte[] data) {
		return data != null ? new String(data, Charsets.UTF_8) : null;
	}
}