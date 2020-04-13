package com.swak.sms;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Map;

import com.swak.oss.aliyun.utils.HmacSHA1Signature;

import io.netty.handler.codec.http.HttpMethod;

public class SignUtils {
	private final static String URL_ENCODING = "UTF-8";
	private final static String SEPARATOR = "&";

	/**
	 * 对 Map 进行签名
	 * 
	 * @param params
	 * @return
	 */
	public static String sign(HttpMethod method, String accessKeySecret, Map<String, String> queries) {
		String[] sortedKeys = queries.keySet().toArray(new String[] {});
		Arrays.sort(sortedKeys);
		StringBuilder canonicalizedQueryString = new StringBuilder();
		for (String key : sortedKeys) {
			canonicalizedQueryString.append("&").append(SignUtils.percentEncode(key)).append("=")
					.append(SignUtils.percentEncode(queries.get(key)));
		}

		StringBuilder stringToSign = new StringBuilder();
		stringToSign.append(method.name());
		stringToSign.append(SEPARATOR);
		stringToSign.append(SignUtils.percentEncode("/"));
		stringToSign.append(SEPARATOR);
		stringToSign.append(SignUtils.percentEncode(canonicalizedQueryString.toString().substring(1)));
		return HmacSHA1Signature.me().computeSignature(accessKeySecret, stringToSign.toString());
	}

	public static String encode(String value) {
		try {
			return URLEncoder.encode(value, URL_ENCODING);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("");
		}
	}

	public static String percentEncode(String value) {
		try {
			return value != null
					? URLEncoder.encode(value, URL_ENCODING).replace("+", "%20").replace("*", "%2A").replace("%7E", "~")
					: null;
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("");
		}
	}
}