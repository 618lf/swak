package com.swak.codec;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 封装各种格式的编码解码工具类. 1.Commons-Codec的 hex/base64 编码 2.自制的base62 编码
 * 3.Commons-Lang的xml/html escape 4.JDK提供的URLEncoder
 *
 * @author: lifeng
 * @date: 2020/3/29 10:46
 */
public class Encodes {

    public static String encodeHex(byte[] input) {
        return Hex.encodeHexString(input);
    }

    public static byte[] decodeHex(String input) {
        return Hex.decodeHex(input.toCharArray());
    }

    public static String encodeBase64(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    public static String encodeBase64UrlSafeString(byte[] input) {
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
                builder.append("\\u").append(Integer.toHexString(ch & 0xffff));
            }
        }
        return builder.toString();
    }
}
