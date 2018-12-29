package com.swak.wechat.codec;

import java.security.MessageDigest;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.swak.codec.Digests;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.Constants;
import com.swak.wechat.WechatErrorException;

/**
 * 签名工具类
 * 
 * @author lifeng
 */
public class SignUtils {

	/**
	 * 生成签名
	 * 
	 * @param map
	 * @param paternerKey
	 * @return
	 * @throws Exception
	 */
	public static String generateSign(Object object, String signType, String paternerKey) {
		Map<String, Object> maps = Maps.toMap(object);
		return generateSign(maps, signType, paternerKey);
	}

	/**
	 * 生成签名
	 * 
	 * @param map
	 * @param paternerKey
	 * @return
	 * @throws Exception
	 */
	public static String generateSign(Map<String, Object> object, String signType, String paternerKey) {
		Map<String, Object> tmap = Maps.sort(object);
		if (tmap.containsKey(Constants.FIELD_SIGN)) {
			tmap.remove(Constants.FIELD_SIGN);
		}
		String str = Maps.join(tmap);
		str = new StringBuilder(str).append("&key=").append(paternerKey).toString();
		if (StringUtils.isNotBlank(signType) && Constants.MD5.equals(signType)) {
			return MD5(str).toUpperCase();
		} else if (StringUtils.isNotBlank(signType) && Constants.HMACSHA256.equals(signType)) {
			return HMACSHA256(str, paternerKey);
		}
		return Digests.md5(str).toUpperCase();
	}

	/**
	 * 生成 MD5
	 *
	 * @param data
	 *            待处理数据
	 * @return MD5结果
	 */
	private static String MD5(String data) {
		try {
			java.security.MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] array = md.digest(data.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte item : array) {
				sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new WechatErrorException("生成 MD5异常", e);
		}
	}

	/**
	 * 生成 HMACSHA256
	 * 
	 * @param data
	 *            待处理数据
	 * @param key
	 *            密钥
	 * @return 加密结果
	 * @throws Exception
	 */
	private static String HMACSHA256(String data, String key) {
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(key.getBytes("UTF-8"), "HmacSHA256");
			sha256_HMAC.init(secret_key);
			byte[] array = sha256_HMAC.doFinal(data.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte item : array) {
				sb.append(Integer.toHexString((item & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString().toUpperCase();
		} catch (Exception e) {
			throw new WechatErrorException("生成 HMACSHA256 异常", e);
		}
	}

	/**
	 * 校验签名
	 * 
	 * @param xmlBean
	 * @param signKey
	 * @return
	 * @throws Exception
	 */
	public static boolean checkSign(Object xmlBean, String signType, String paternerKey) throws Exception {
		Map<String, Object> params = Maps.toMap(xmlBean);
		String sign = String.valueOf(params.get(Constants.FIELD_SIGN));
		return generateSign(params, signType, paternerKey).equals(sign);
	}
}