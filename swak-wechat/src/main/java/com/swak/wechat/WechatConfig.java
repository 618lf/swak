package com.swak.wechat;

import com.swak.http.HttpService;
import com.swak.utils.Maps;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.message.EventMsgUserAttention;
import com.swak.wechat.message.MenuEventMsgClick;
import com.swak.wechat.message.ReqMsg;
import com.swak.wechat.message.RespMsg;
import io.netty.handler.ssl.SslContext;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * 微信的基本配置
 *
 * @author: lifeng
 * @date: 2020/4/1 12:16
 */
public interface WechatConfig {

    /**
     * app_id
     *
     * @return appid
     */
    String getAppId();

    /**
     * app_Secret
     *
     * @return app_Secret
     */
    String getSecret();

    /**
     * 接入token
     *
     * @return token
     */
    String getToken();

    /**
     * 消息加密
     *
     * @return 消息加密
     */
    String getAesKey();

    /**
     * 原始ID
     *
     * @return 原始ID
     */
    String getSrcId();

    /**
     * 配置信息
     *
     * @return 配置信息
     */
    Object getSetting();

    /**
     * 商户App - 默认和 app_id 相同
     *
     * @return 商户App
     */
    default String getMchApp() {
        return this.getAppId();
    }

    /**
     * 商户Id
     *
     * @return 商户Id
     */
    String getMchId();

    /**
     * 商户名称
     *
     * @return 商户名称
     */
    String getMchName();

    /**
     * 商户Key
     *
     * @return 商户Key
     */
    String getMchKey();

    /**
     * 回调地址 - 支付
     *
     * @return 回调地址
     */
    String getPayNotifyUrl();

    /**
     * 回调地址 - 退款
     *
     * @return 回调地址
     */
    String getRefundNotifyUrl();

    /**
     * 获得证书的配置
     *
     * @return 获得证书的配置
     */
    SslContext getSslContext();

    /**
     * 是否使用沙箱测试
     *
     * @return 是否使用沙箱测试
     */
    default boolean isUseSandbox() {
        return false;
    }

    /**
     * 修改商户Key - 沙箱测试时使用
     *
     * @param mchKey 修改商户Key
     */
    void setMchKey(String mchKey);

    /**
     * 修改回调的地址
     *
     * @param notifyUrl 回调的地址
     */
    void setPayNotifyUrl(String notifyUrl);

    /**
     * 修改回调的地址
     *
     * @param notifyUrl 回调的地址
     */
    void setRefundNotifyUrl(String notifyUrl);

    /**
     * Http 连接超时时间
     *
     * @return 连接超时时间
     */
    default int getHttpConnectTimeoutMs() {
        return 6 * 1000;
    }

    /**
     * Http 读取超时时间
     *
     * @return 读取超时时间
     */
    default int getHttpReadTimeoutMs() {
        return 8 * 1000;
    }

    /**
     * 请求
     *
     * @param client 客户端
     * @param url    地址
     * @param data   数据
     * @return 异步结果
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
     * @param xml xml格式数据
     * @return 解析结果
     */
    default Map<String, Object> process(String xml) {
        return this.process(xml, Constants.HMACSHA256);
    }

    /**
     * 处理请求
     *
     * @param xml      xml格式数据
     * @param signType 签名类型
     * @return 解析结果
     */
    default Map<String, Object> process(String xml, String signType) {
        Map<String, Object> respData = Maps.fromXml(xml);
        String sign = String.valueOf(respData.get(Constants.FIELD_SIGN));
        if (Constants.SUCCESS.equals(respData.get(Constants.FIELD_RETURN_CODE))
                && SignUtils.generateSign(respData, signType, this.getMchKey()).equals(sign)) {
            return respData;
        }
        throw new WechatErrorException(String.format("Invalid sign value in XML: %s", xml));
    }

    /**
     * 处理消息 - 用户关注
     *
     * @param request 关注请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleUserAttention(EventMsgUserAttention request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理消息 - 用户扫码
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleUserScan(EventMsgUserAttention request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理消息 - 用户取消关注
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleUserUnsubscribe(EventMsgUserAttention request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理消息 - 用户点击菜单
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleClickMenu(MenuEventMsgClick request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 处理消息
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleMessage(ReqMsg request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 事件处理
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleEvent(ReqMsg request) {
        return CompletableFuture.completedFuture(null);
    }

    /**
     * 默认处理
     *
     * @param request 请求
     * @return 异步结果
     */
    default CompletionStage<RespMsg> handleDefault(ReqMsg request) {
        return CompletableFuture.completedFuture(null);
    }
}
