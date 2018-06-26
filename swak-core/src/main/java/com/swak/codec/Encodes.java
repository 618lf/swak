/**
 * Copyright (c) 2005-2012 springside.org.cn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 */
package com.swak.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.swak.codec.Base64;
import com.swak.codec.DecoderException;
import com.swak.codec.Hex;

/**
 * 封装各种格式的编码解码工具类.
 * 1.Commons-Codec的 hex/base64 编码
 * 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape
 * 4.JDK提供的URLEncoder
 * @author calvin
 * @version 2013-01-15
 */
public class Encodes {

	// Hex
	//------------------------------------------
	public static String encodeHex(byte[] input) {
		return Hex.encodeHexString(input);
	}
	public static byte[] decodeHex(String input) {
		try {
			return Hex.decodeHex(input.toCharArray());
		} catch (DecoderException e) {
			throw new RuntimeException(e);
		}
	}

	// Base64
	//----------------------------------------------
	public static String encodeBase64(byte[] input) {
		return Base64.encodeBase64String(input);
	}
	public static String encodeBase64URLSafeString(byte[] input) {
		return Base64.encodeBase64URLSafeString(input);
	}
	public static byte[] decodeBase64(String input) {
		return Base64.decodeBase64(input);
	}
	public static String decodeBase64(String input, String charsetName) {
		try {
			return new String(Base64.decodeBase64(input), charsetName);
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	// URL
	//---------------------------------------
	public static String urlEncode(String part) {
		try {
			return URLEncoder.encode(part, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public static String urlDecode(String part) {

		try {
			return URLDecoder.decode(part, StandardCharsets.UTF_8.toString());
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	public static String toUnicode(String input) {
		StringBuilder builder = new StringBuilder();
		char[] chars = input.toCharArray();
		for (char ch : chars) {
			if (ch < 256) {
				builder.append(ch);
			} else {
				builder.append("\\u" + Integer.toHexString(ch & 0xffff));
			}
		}
		return builder.toString();
	}
}
