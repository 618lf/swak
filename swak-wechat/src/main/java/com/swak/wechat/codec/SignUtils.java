package com.swak.wechat.codec;

import com.swak.codec.Digests;
import com.swak.codec.Hex;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.Constants;
import com.swak.wechat.WechatErrorException;
import com.swak.wechat.pay.PayJsRequest;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Map;

/**
 * 签名工具类
 *
 * @author lifeng
 */
public class SignUtils {

    /**
     * 生成签名
     *
     * @param object      请求对象
     * @param signType    签名类型
     * @param paternerKey key
     * @return 签名的字符串
     */
    public static String generateSign(Object object, String signType, String paternerKey) {
        Map<String, Object> maps = Maps.toMap(object);
        return generateSign(maps, signType, paternerKey);
    }

    /**
     * 生成签名
     *
     * @param object      请求对象
     * @param signType    签名类型
     * @param paternerKey key
     * @return 签名的字符串
     */
    public static String generateSign(Map<String, Object> object, String signType, String paternerKey) {
        Map<String, Object> tmap = Maps.sort(object);
        tmap.remove(Constants.FIELD_SIGN);
        String str = Maps.join(tmap);
        str = str + "&key=" + paternerKey;
        if (StringUtils.isNotBlank(signType) && Constants.MD5.equals(signType)) {
            return md5(str).toUpperCase();
        } else if (StringUtils.isNotBlank(signType) && Constants.HMACSHA256.equals(signType)) {
            return hmacsha256(str, paternerKey);
        }
        return Digests.md5(str).toUpperCase();
    }

    /**
     * 生成 MD5
     *
     * @param data 待处理数据
     * @return MD5结果
     */
    private static String md5(String data) {
        try {
            java.security.MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] array = md.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            throw new WechatErrorException("生成 MD5异常", e);
        }
    }

    /**
     * 生成 HMACSHA256
     *
     * @param data 待处理数据
     * @param key  密钥
     * @return 加密结果
     */
    private static String hmacsha256(String data, String key) {
        try {
            Mac sha256Hmac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256Hmac.init(secretKey);
            byte[] array = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte item : array) {
                sb.append(Integer.toHexString((item & 0xFF) | 0x100), 1, 3);
            }
            return sb.toString().toUpperCase();
        } catch (Exception e) {
            throw new WechatErrorException("生成 HMACSHA256 异常", e);
        }
    }

    /**
     * 校验签名
     *
     * @param xmlBean     xml 对象
     * @param signType    签名类型
     * @param paternerKey 加密的key
     * @return 是否签名正确
	 */
    public static boolean checkSign(Object xmlBean, String signType, String paternerKey) {
        Map<String, Object> params = Maps.toMap(xmlBean);
        String sign = String.valueOf(params.get(Constants.FIELD_SIGN));
        return generateSign(params, signType, paternerKey).equals(sign);
    }

    /**
     * 生成支付JS请求对象
     *
     * @param nonceStr    随机字符串
     * @param prepayId    预支付订单号
     * @param appId       微信app
     * @param signType    签名类型
     * @param paternerKey 商户支付密钥
     * @return 支付字符串
     */
    public static String generateJsPayJson(String nonceStr, String prepayId, String appId, String signType, String paternerKey) {
        String payPackage = "prepay_id=" + prepayId;
        PayJsRequest payJsRequest = new PayJsRequest();
        payJsRequest.setAppId(appId);
        payJsRequest.setNonceStr(nonceStr);
        payJsRequest.setPackage_(payPackage);
        payJsRequest.setSignType(signType);
        payJsRequest.setTimeStamp(System.currentTimeMillis() / 1000 + "");
        Map<String, Object> mapS = Maps.toMap(payJsRequest);
        try {
            payJsRequest.setPaySign(SignUtils.generateSign(mapS, signType, paternerKey));
        } catch (Exception ignored) {
        }
        return JsonMapper.toJson(payJsRequest);
    }


    /**
     * url 签名
     *
     * @param arr 参数
     * @return 签名
     */
    public static String urlSign(String... arr) {
        Arrays.sort(arr);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            String a = arr[i];
            sb.append(a);
            if (i != arr.length - 1) {
                sb.append('&');
            }
        }
        return Hex.encodeHexString(Digests.sha1(StringUtils.getBytesUtf8(sb.toString())));
    }

    /**
     * 微信事件接入签名
     *
     * @param arr 参数
     * @return 签名
     */
    public static String accessSign(String... arr) {
        Arrays.sort(arr);
        StringBuilder sb = new StringBuilder();
        for (String a : arr) {
            sb.append(a);
        }
        return Hex.encodeHexString(Digests.sha1(StringUtils.getBytesUtf8(sb.toString())));
    }
}