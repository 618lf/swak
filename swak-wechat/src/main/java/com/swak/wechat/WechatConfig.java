package com.swak.wechat;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.http.HttpService;
import com.swak.utils.Maps;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.message.EventMsgUserAttention;
import com.swak.wechat.message.MenuEventMsgClick;
import com.swak.wechat.message.ReqMsg;
import com.swak.wechat.message.RespMsg;

import io.netty.handler.ssl.SslContext;

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
	 * 商户App - 默认和 app_id 相同
	 * 
	 * @return
	 */
	default String getMchApp() {
		return this.getAppId();
	}

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
	 * 回调地址 - 支付
	 * 
	 * @return
	 */
	String getPayNotifyUrl();

	/**
	 * 回调地址 - 退款
	 * 
	 * @return
	 */
	String getRefundNotifyUrl();

	/**
	 * 获得证书的配置
	 * 
	 * @return
	 */
	SslContext getSslContext();

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
	void setPayNotifyUrl(String notifyUrl);

	/**
	 * 修改回调的地址
	 * 
	 * @param notifyUrl
	 */
	void setRefundNotifyUrl(String notifyUrl);

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
	default CompletableFuture<String> request(HttpService client, String url, String data) {
		return client.post().text().setUrl(url).setHeader("Content-Type", "text/xml")
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

	/**
	 * 处理消息 - 用户关注
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleUserAttention(EventMsgUserAttention request) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息 - 用户扫码
	 * 
	 * @param request
	 * @return
	 */
	default CompletionStage<RespMsg> handleUserScan(EventMsgUserAttention request) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息 - 用户取消关注
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleUserUnsubscribe(EventMsgUserAttention request) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息 - 用户点击菜单
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleClickMenu(MenuEventMsgClick msg) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息
	 * 
	 * @param request
	 * @return
	 */
	default CompletionStage<RespMsg> handleMessage(ReqMsg request) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 事件处理
	 * 
	 * @param request
	 * @return
	 */
	default CompletionStage<RespMsg> handleEvent(ReqMsg request) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 默认处理
	 * 
	 * @param request
	 * @return
	 */
	default CompletionStage<RespMsg> handleDefault(ReqMsg request) {
		return CompletableFuture.completedFuture(null);
	}
}
