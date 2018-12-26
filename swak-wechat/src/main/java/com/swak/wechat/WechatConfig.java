package com.swak.wechat;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.swak.http.builder.RequestBuilder;
import com.swak.utils.Maps;
import com.swak.wechat.codec.SignUtils;

/**
 * 微信的基本配置
 * 
 * @author lifeng
 */
public interface WechatConfig {

	/**
	 * app_id
	 * 
	 * @return
	 */
	String getAppId();

	/**
	 * app_Secret
	 * 
	 * @return
	 */
	String getSecret();

	/**
	 * 接入token
	 * 
	 * @return
	 */
	String getToken();

	/**
	 * 消息加密
	 * 
	 * @return
	 */
	String getAesKey();

	/**
	 * 原始ID
	 * 
	 * @return
	 */
	String getSrcId();

	/**
	 * 配置信息
	 * 
	 * @return
	 */
	Object getSetting();

	/**
	 * 商户Id
	 * 
	 * @return
	 */
	String getMchId();
	
	/**
	 * 商户名称
	 * 
	 * @return
	 */
	String getMchName();

	/**
	 * 商户Key
	 * 
	 * @return
	 */
	String getMchKey();

	/**
	 * 回调地址
	 * 
	 * @return
	 */
	String getNotifyUrl();

	/**
	 * 是否使用沙箱测试
	 * 
	 * @return
	 */
	default boolean isUseSandbox() {
		return false;
	}

	/**
	 * 修改商户Key - 沙箱测试时使用
	 * 
	 * @return
	 */
	void setMchKey(String mchKey);
	
	/**
	 * 修改回调的地址
	 * 
	 * @param notifyUrl
	 */
	void setNotifyUrl(String notifyUrl);

	/**
	 * Http 连接超时时间
	 * 
	 * @return
	 */
	default int getHttpConnectTimeoutMs() {
		return 6 * 1000;
	}

	/**
	 * Http 读取超时时间
	 * 
	 * @return
	 */
	default int getHttpReadTimeoutMs() {
		return 8 * 1000;
	}

	/**
	 * 请求
	 * 
	 * @param url
	 * @param data
	 * @return
	 */
	default CompletableFuture<String> request(String url, String data) {
		return RequestBuilder.post().text().setUrl(url).setHeader("Content-Type", "text/xml")
				.setHeader("User-Agent", "wxpay sdk java v1.0 " + this.getMchId())
				.setRequestTimeout(this.getHttpConnectTimeoutMs()).setReadTimeout(this.getHttpReadTimeoutMs())
				.setBody(data).future();
	}

	/**
	 * 处理请求
	 * 
	 * @param xml
	 * @param signType
	 * @param key
	 * @return
	 */
	default Map<String, Object> process(String xml) {
		return this.process(xml, Constants.HMACSHA256);
	}

	/**
	 * 处理请求
	 * 
	 * @param xml
	 * @param signType
	 * @param key
	 * @return
	 */
	default Map<String, Object> process(String xml, String signType) {
		Map<String, Object> respData = Maps.fromXml(xml);
		String sign = String.valueOf(respData.get(Constants.FIELD_SIGN));
		if (Constants.SUCCESS.equals(respData.get("return_code"))
				&& SignUtils.generateSign(respData, signType, this.getMchKey()).equals(sign)) {
			return respData;
		}
		throw new WechatErrorException(String.format("Invalid sign value in XML: %s", xml));
	}
}
