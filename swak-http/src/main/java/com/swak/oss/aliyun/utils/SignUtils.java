package com.swak.oss.aliyun.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.swak.Constants;
import com.swak.codec.Base64;
import com.swak.oss.aliyun.OssContants;
import com.swak.utils.StringUtils;

/**
 * 对请求做签名
 * 
 * @author lifeng
 */
public class SignUtils {

	public static String composeRequestAuthorization(String accessKeyId, String signature) {
		return OssContants.AUTHORIZATION_PREFIX + accessKeyId + ":" + signature;
	}

	public static String buildCanonicalString(String method, String bucketName, String resource,
			Map<String, String> headers, Map<String, String> parameters, String expires) {

		String resourcePath = "/" + ((bucketName != null) ? bucketName + "/" : "")
				+ ((resource != null ? resource : ""));

		StringBuilder canonicalString = new StringBuilder();
		canonicalString.append(method).append(OssContants.NEW_LINE);

		TreeMap<String, String> headersToSign = new TreeMap<String, String>();

		if (headers != null) {
			for (Entry<String, String> header : headers.entrySet()) {
				if (header.getKey() == null) {
					continue;
				}

				String lowerKey = header.getKey().toLowerCase();
				if (lowerKey.equals(OssContants.CONTENT_TYPE.toLowerCase())
						|| lowerKey.equals(OssContants.CONTENT_MD5.toLowerCase())
						|| lowerKey.equals(OssContants.DATE.toLowerCase())
						|| lowerKey.startsWith(OssContants.OSS_PREFIX)) {
					headersToSign.put(lowerKey, header.getValue().trim());
				}
			}
		}

		if (!headersToSign.containsKey(OssContants.CONTENT_TYPE.toLowerCase())) {
			headersToSign.put(OssContants.CONTENT_TYPE.toLowerCase(), "");
		}
		if (!headersToSign.containsKey(OssContants.CONTENT_MD5.toLowerCase())) {
			headersToSign.put(OssContants.CONTENT_MD5.toLowerCase(), "");
		}

		// Append all headers to sign to canonical string
		for (Map.Entry<String, String> entry : headersToSign.entrySet()) {
			String key = entry.getKey();
			Object value = entry.getValue();

			if (key.startsWith(OssContants.OSS_PREFIX)) {
				canonicalString.append(key).append(':').append(value);
			} else {
				canonicalString.append(value);
			}

			canonicalString.append(OssContants.NEW_LINE);
		}
		// Append canonical resource to canonical string
		canonicalString.append(buildCanonicalizedResource(resourcePath, parameters));
		return canonicalString.toString();
	}

	public static String buildCanonicalizedResource(String resourcePath, Map<String, String> parameters) {
		StringBuilder builder = new StringBuilder();
		builder.append(resourcePath);
		if (parameters != null) {
			String[] parameterNames = parameters.keySet().toArray(new String[parameters.size()]);
			Arrays.sort(parameterNames);
			char separator = '?';
			for (String paramName : parameterNames) {
				if (!OssContants.SIGNED_PARAMTERS.contains(paramName)) {
					continue;
				}
				builder.append(separator);
				builder.append(paramName);
				String paramValue = parameters.get(paramName);
				if (paramValue != null) {
					builder.append("=").append(paramValue);
				}
				separator = '&';
			}
		}
		return builder.toString();
	}

	/**
	 * 
	 * 创建签名
	 * 
	 * @param secretAccessKey
	 * @param method
	 * @param bucketName
	 * @param resource
	 * @param headers
	 * @param parameters
	 * @return
	 */
	public static String buildSignature(String secretAccessKey, String method, String bucketName, String resource,
			Map<String, String> headers, Map<String, String> parameters) {
		String canonicalString = buildCanonicalString(method, bucketName, resource, headers, parameters, null);
		return HmacSHA1Signature.me().computeSignature(secretAccessKey, canonicalString);
	}

	/**
	 * 生成 Policy 签名
	 * 
	 * @param secretAccessKey
	 * @param postPolicy
	 * @return
	 */
	public static String buildPolicySignature(String secretAccessKey, String postPolicy) {
		byte[] binaryData = StringUtils.getBytesUtf8(postPolicy);
		String encPolicy = new String(Base64.encodeBase64(binaryData));
		return HmacSHA1Signature.me().computeSignature(secretAccessKey, encPolicy);
	}

	/**
	 * 对key进行编码
	 * 
	 * @param key
	 * @return
	 */
	public static String urlEncodeKey(String key) {
		StringBuffer resultUri = new StringBuffer();

		String[] keys = key.split("/");
		resultUri.append(urlEncode(keys[0], Constants.DEFAULT_ENCODING.toString()));
		for (int i = 1; i < keys.length; i++) {
			resultUri.append("/").append(urlEncode(keys[i], Constants.DEFAULT_ENCODING.toString()));
		}

		if (key.endsWith("/")) {
			// String#split ignores trailing empty strings,
			// e.g., "a/b/" will be split as a 2-entries array,
			// so we have to append all the trailing slash to the uri.
			for (int i = key.length() - 1; i >= 0; i--) {
				if (key.charAt(i) == '/') {
					resultUri.append("/");
				} else {
					break;
				}
			}
		}

		return resultUri.toString();
	}

	/**
	 * Encode a URL segment with special chars replaced.
	 */
	public static String urlEncode(String value, String encoding) {
		if (value == null) {
			return "";
		}

		try {
			String encoded = URLEncoder.encode(value, encoding);
			return encoded.replace("+", "%20").replace("*", "%2A").replace("~", "%7E").replace("/", "%2F");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("error Encoder url", e);
		}
	}

	private static final String ISO_8859_1_CHARSET = "iso-8859-1";
	private static final String UTF8_CHARSET = "utf-8";

	/**
	 * header 編碼
	 * 
	 * @param headers
	 */
	public static void convertHeaderCharsetToIso88591(Map<String, String> headers) {
		convertHeaderCharset(headers, UTF8_CHARSET, ISO_8859_1_CHARSET);
	}

	private static void convertHeaderCharset(Map<String, String> headers, String fromCharset, String toCharset) {
		for (Map.Entry<String, String> header : headers.entrySet()) {
			if (header.getValue() == null) {
				continue;
			}

			try {
				header.setValue(new String(header.getValue().getBytes(fromCharset), toCharset));
			} catch (UnsupportedEncodingException e) {
				throw new IllegalArgumentException("Invalid charset name: " + e.getMessage(), e);
			}
		}
	}

	/**
	 * Encode request parameters to URL segment.
	 */
	public static String paramToQueryString(Map<String, String> params) {

		if (params == null || params.isEmpty()) {
			return null;
		}

		StringBuilder paramString = new StringBuilder();
		boolean first = true;
		for (Entry<String, String> p : params.entrySet()) {
			String key = p.getKey();
			String value = p.getValue();

			if (!first) {
				paramString.append("&");
			}

			// Urlencode each request parameter
			paramString.append(urlEncode(key, UTF8_CHARSET));
			if (value != null) {
				paramString.append("=").append(urlEncode(value, UTF8_CHARSET));
			}

			first = false;
		}

		return paramString.toString();
	}

	/**
	 * 创建访问签名
	 * 
	 * @param accessKeyId
	 * @param accessKeySecret
	 * @param method
	 * @param bucketName
	 * @param resource
	 * @param headers
	 * @param parameters
	 * @param expiration
	 * @return
	 */
	public static String buildSignedParam(String accessKeyId, String accessKeySecret, String method, String bucketName,
			String resource, Map<String, String> headers, Map<String, String> parameters, Date expiration) {
		String expires = String.valueOf(expiration.getTime() / 1000L);
		headers.put(OssContants.DATE, expires);
		String canonicalString = buildCanonicalString(method, bucketName, resource, headers, parameters, expires);
		String signature = HmacSHA1Signature.me().computeSignature(accessKeySecret, canonicalString);
		Map<String, String> params = new LinkedHashMap<String, String>();
		params.put(OssContants.EXPIRES, expires);
		params.put(OssContants.OSS_ACCESS_KEY_ID, accessKeyId);
		params.put(OssContants.SIGNATURE, signature);
		params.putAll(parameters);
		return paramToQueryString(params);
	}
}
