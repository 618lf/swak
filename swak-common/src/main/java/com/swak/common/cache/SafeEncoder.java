package com.swak.common.cache;

import com.swak.common.exception.CacheException;

import io.lettuce.core.protocol.LettuceCharsets;

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
		return str.getBytes(LettuceCharsets.UTF8);
	}

	public static String encode(final byte[] data) {
		return data != null ? new String(data, LettuceCharsets.UTF8) : null;
	}
}