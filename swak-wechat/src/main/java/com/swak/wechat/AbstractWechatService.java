package com.swak.wechat;

import com.swak.codec.Encodes;
import com.swak.http.HttpService;
import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.JaxbMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.codec.MsgParse;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.message.*;
import com.swak.wechat.pay.*;
import com.swak.wechat.tmpmsg.TemplateMessageResult;
import com.swak.wechat.token.AccessToken;
import com.swak.wechat.token.Ticket;
import com.swak.wechat.user.SessionToken;
import com.swak.wechat.user.SnsToken;
import com.swak.wechat.user.UserInfo;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * Wechat 服务
 *
 * @author: lifeng
 * @date: 2020/4/1 11:38
 */
public abstract class AbstractWechatService {

    /**
     * Ssl Http 服务
     */
    private HttpService sslHttpService;

    /**
     * Http 服务
     *
     * @return http 服务
     */
    public abstract HttpService getHttpService();

    /**
     * 获取 SSl 服务， 如果有多个wechat 的ssl 需要处理可以根据 WechatConfig 管理多个
     *
     * @param app app
     * @return http ssl 服务
     */
    public HttpService getSslHttpService(WechatConfig app) {
        if (sslHttpService == null) {
            sslHttpService = this.getHttpService().sslConfig(app.getSslContext());
        }
        return sslHttpService;
    }

    // ==========================================================
    // 调用 API: userinfo
    // ==========================================================

    /**
     * 获取用户信息
     *
     * @param accessToken 接入token
     * @param openId      用户 openId
     * @return 异步结果
     */
    public CompletableFuture<UserInfo> userinfo(String accessToken, String openId) {
        CompletableFuture<UserInfo> future = this.getHttpService().post()
                .setUrl("https://api.weixin.qq.com/cgi-bin/user/info").addFormParam("access_token", accessToken)
                .addFormParam("openid", openId).addFormParam("lang", "zh_CN").json(UserInfo.class).future();
        return future.thenApply(res -> res);
    }

    /**
     * 发送模板消息
     *
     * @param accessToken 接入token
     * @param messageJson 模板消息
     * @return 异步结果
     */
    public CompletableFuture<TemplateMessageResult> sendTemplateMessage(String accessToken, String messageJson) {
        CompletableFuture<TemplateMessageResult> future = this.getHttpService().post()
                .setUrl("https://api.weixin.qq.com/cgi-bin/message/template/send")
                .addQueryParam("access_token", accessToken).setBody(StringUtils.getBytesUtf8(messageJson))
                .json(TemplateMessageResult.class).future();
        return future.thenApply(res -> res);
    }

    // ==========================================================
    // 小程序登陆
    // ==========================================================

    /**
     * 换取token
     *
     * @param app  app
     * @param code code
     * @return 异步结果
     */
    public CompletableFuture<SessionToken> oauth2Session(WechatConfig app, String code) {
        if (!(StringUtils.isNotBlank(code))) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<SessionToken> future = this.getHttpService().get()
                .setUrl("https://api.weixin.qq.com/sns/jscode2session").addQueryParam("appid", app.getAppId())
                .addQueryParam("secret", app.getSecret()).addQueryParam("js_code", code)
                .addQueryParam("grant_type", "authorization_code").json(SessionToken.class).future();
        return future.thenApply(res -> {
            if (res != null) {
                res.setCode(code);
            }
            return res;
        });
    }

    // ==========================================================
    // 网页授权登录 toAuthorize -> oauth2AccessToken -> accessToken2Userinfo
    // ==========================================================

    /**
     * 授权登录的地址
     *
     * @param app   app
     * @param url   地址
     * @param state state
     * @return 异步结果
     */
    public CompletableFuture<String> toAuthorize(WechatConfig app, String url, String state) {
        String content = com.swak.Constants.REDIRECT_URL_PREFIX +
                "https://open.weixin.qq.com/connect/oauth2/authorize" + "?appid=" + app.getAppId() +
                "&redirect_uri=" + Encodes.urlEncode(url) + "&response_type=code" + "&scope=" +
                "snsapi_userinfo" + "&state=" + state + "#wechat_redirect";
        return CompletableFuture.completedFuture(content);
    }

    /**
     * 换取token
     *
     * @param app   app
     * @param code  code
     * @param state state
     * @return 异步结果
     */
    public CompletableFuture<SnsToken> oauth2AccessToken(WechatConfig app, String code, String state) {
        if (!(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state))) {
            return CompletableFuture.completedFuture(null);
        }
        CompletableFuture<SnsToken> future = this.getHttpService().get()
                .setUrl("https://api.weixin.qq.com/sns/oauth2/access_token").addQueryParam("appid", app.getAppId())
                .addQueryParam("secret", app.getSecret()).addQueryParam("code", code)
                .addQueryParam("grant_type", "authorization_code").json(SnsToken.class).future();
        return future.thenApply(res -> {
            if (res != null) {
                res.setCode(code);
                res.setState(state);
            }
            return res;
        });
    }

    /**
     * 换取user
     *
     * @param token token
     * @return 异步结果
     */
    public CompletableFuture<UserInfo> accessToken2Userinfo(SnsToken token) {
        CompletableFuture<UserInfo> future = this.getHttpService().get()
                .setUrl("https://api.weixin.qq.com/sns/userinfo").addQueryParam("access_token", token.getAccess_token())
                .addQueryParam("openid", token.getOpenid()).addQueryParam("lang", "zh_CN").json(UserInfo.class)
                .future();
        return future.thenApply(res -> {
            if (res != null) {
                res.setToken(token);
            }
            return res;
        });
    }

    // ==========================================================
    // 调用 API 的token
    // ==========================================================

    /**
     * 获取接入系统的 accessToken
     *
     * @param app app
     * @return 异步结果
     */
    public CompletableFuture<AccessToken> accessToken(WechatConfig app) {
        CompletableFuture<AccessToken> future = this.getHttpService().get()
                .setUrl("https://api.weixin.qq.com/cgi-bin/token").addQueryParam("grant_type", "client_credential")
                .addQueryParam("appid", app.getAppId()).addQueryParam("secret", app.getSecret()).json(AccessToken.class)
                .future();
        return future.thenApply(token -> {
            if (token != null) {
                token.setAddTime(System.currentTimeMillis());
            }
            return token;
        });
    }

    // ==========================================================
    // URL 签名
    // ==========================================================

    /**
     * URL 签名的 Ticket
     *
     * @param token token
     * @return 异步结果
     */
    public CompletableFuture<Ticket> jsSdkTicket(AccessToken token) {
        CompletableFuture<Ticket> future = this.getHttpService().get()
                .setUrl("https://api.weixin.qq.com/cgi-bin/ticket/getticket")
                .addQueryParam("access_token", token.getAccess_token()).addQueryParam("type", "jsapi")
                .json(Ticket.class).future();
        return future.thenApply(ticket -> {
            if (ticket != null) {
                ticket.setAddTime(System.currentTimeMillis());
            }
            return ticket;
        });
    }

    /**
     * 对 URL 进行签名
     *
     * @param app    app
     * @param ticket 票据
     * @param url    地址
     * @return 签名数据
     */
    public Map<String, String> signUrl(WechatConfig app, Ticket ticket, String url) {
        String noncestr = UUIdGenerator.uuid();
        String timestamp = Long.toString(System.currentTimeMillis() / 1000);

        // 生成签名
        String signStr = "jsapi_ticket=" + ticket.getTicket() + "&" + "noncestr=" + noncestr +
                "&" + "timestamp=" + timestamp + "&" + "url=" + url;
        String signature = SignUtils.urlSign(signStr);
        Map<String, String> reMap = Maps.newHashMap();
        reMap.put("noncestr", noncestr);
        reMap.put("timestamp", timestamp);
        reMap.put("signature", signature.toLowerCase());
        reMap.put("url", url);
        reMap.put("appId", app.getAppId());
        return reMap;
    }

    // ==========================================================
    // 微信支付： 下单 -> 查询订单 -> 退款 -> 退款查询
    // ==========================================================

    /**
     * 统一下单
     *
     * @param app          app
     * @param unifiedorder 数据
     * @return 异步结果
     */
    public CompletableFuture<Map<String, Object>> unifiedorder(WechatConfig app, Unifiedorder unifiedorder) {
        CompletableFuture<String> future;
        if (app.isUseSandbox()) {
            unifiedorder.setSign_type(Constants.MD5);
            future = getSandboxSignKey(app, unifiedorder.getSign_type()).thenApply(res -> {
                app.setMchKey(res);
                return Constants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
            });
        } else {
            unifiedorder.setSign_type(Constants.HMACSHA256);
            future = CompletableFuture.completedFuture(Constants.UNIFIEDORDER_URL_SUFFIX);
        }
        return future.thenCompose(res -> this.orderFuture(app, unifiedorder, res)).thenApply(res -> app.process(res, unifiedorder.getSign_type())).thenApply(res -> {
            String nativeTradeType = "NATIVE";
            if (nativeTradeType.equals(unifiedorder.getTrade_type())) {
                return res;
            }
            String signed = SignUtils.generateJsPayJson(unifiedorder.getNonce_str(),
                    String.valueOf(res.get("prepay_id")), app.getAppId(), unifiedorder.getSign_type(), app.getMchKey());
            res.put("jsPayJson", signed);
            return res;
        });
    }

    /**
     * 查询订单
     *
     * @param app           app
     * @param mchOrderquery 数据
     * @return 异步结果
     */
    public CompletableFuture<Map<String, Object>> queryOrder(WechatConfig app, MchOrderquery mchOrderquery) {
        CompletableFuture<String> future;
        if (app.isUseSandbox()) {
            mchOrderquery.setSign_type(Constants.MD5);
            future = getSandboxSignKey(app, mchOrderquery.getSign_type()).thenApply(res -> {
                app.setMchKey(res);
                return Constants.SANDBOX_ORDERQUERY_URL_SUFFIX;
            });
        } else {
            mchOrderquery.setSign_type(Constants.HMACSHA256);
            future = CompletableFuture.completedFuture(Constants.ORDERQUERY_URL_SUFFIX);
        }
        return future.thenCompose(res -> this.orderFuture(app, mchOrderquery, res)).thenApply(res -> app.process(res, mchOrderquery.getSign_type()));
    }

    /**
     * 退款
     *
     * @param app    app
     * @param refund 数据
     * @return 异步结果
     */
    public CompletableFuture<Map<String, Object>> refundOrder(WechatConfig app, Refundorder refund) {
        CompletableFuture<String> future;
        if (app.isUseSandbox()) {
            refund.setSign_type(Constants.MD5);
            future = getSandboxSignKey(app, refund.getSign_type()).thenApply(res -> {
                app.setMchKey(res);
                return Constants.SANDBOX_REFUND_URL_SUFFIX;
            });
        } else {
            refund.setSign_type(Constants.HMACSHA256);
            future = CompletableFuture.completedFuture(Constants.REFUND_URL_SUFFIX);
        }
        return future.thenCompose(res -> this.orderFuture(app, refund, res)).thenApply(res -> app.process(res, refund.getSign_type()));
    }

    /**
     * 订单操作
     *
     * @param app   app
     * @param order 订单
     * @param res   操作地址
     * @return 异步结果
     */
    private CompletableFuture<String> orderFuture(WechatConfig app, Order order, String res) {
        String url = "https://" + Constants.MCH_URI_DOMAIN_API + res;
        order.checkAndSign(app);
        String reqBody = JaxbMapper.toXml(order);
        return app.request(this.getHttpService(), url, reqBody);
    }

    /**
     * 退款申请查询
     *
     * @param app   app
     * @param query 数据
     * @return 异步结果
     */
    public CompletableFuture<Map<String, Object>> refundQuery(WechatConfig app, Refundquery query) {
        CompletableFuture<String> future;
        if (app.isUseSandbox()) {
            query.setSign_type(Constants.MD5);
            future = getSandboxSignKey(app, query.getSign_type()).thenApply(res -> {
                app.setMchKey(res);
                return Constants.SANDBOX_REFUNDQUERY_URL_SUFFIX;
            });
        } else {
            query.setSign_type(Constants.HMACSHA256);
            future = CompletableFuture.completedFuture(Constants.REFUNDQUERY_URL_SUFFIX);
        }
        return future.thenCompose(res -> {
            String url = "https://" + Constants.MCH_URI_DOMAIN_API + res;
            query.checkAndSign(app);
            String reqBody = JaxbMapper.toXml(query);
            return app.request(getSslHttpService(app), url, reqBody);
        }).thenApply(res -> app.process(res, query.getSign_type()));
    }

    /**
     * 获得沙箱测试密钥
     *
     * @param app      app
     * @param signType 签名类型
     * @return 异步结果
     */
    private CompletableFuture<String> getSandboxSignKey(WechatConfig app, String signType) {
        SandboxSignKey data = new SandboxSignKey();
        data.setMch_id(app.getMchId());
        data.setNonce_str(UUIdGenerator.uuid());
        data.setSign_type(signType);
        data.setSign(SignUtils.generateSign(data, data.getSign_type(), app.getMchKey()));
        String reqBody = JaxbMapper.toXml(data);
        String url = "https://" + Constants.MCH_URI_DOMAIN_API +
                Constants.SANDBOX_GET_SIGNKEY_SUFFIX;
        return app.request(this.getHttpService(), url, reqBody).thenApply(res -> {
            Map<String, Object> maps = Maps.fromXml(res);
            return String.valueOf(maps.get("sandbox_signkey"));
        });
    }

    // ==========================================================
    // 微信结算： 发红包 - 发现金
    // ==========================================================

    /**
     * 发现金
     *
     * @param app        app
     * @param mchPayment mch支付
     * @return 异步结果
     */
    public CompletableFuture<Map<String, Object>> sendamount(WechatConfig app, MchPayment mchPayment) {
        CompletableFuture<String> future = CompletableFuture.completedFuture(Constants.MMPAYMKTTRANSFERS_URL_SUFFIX);
        return future.thenCompose(res -> {
            String url = "https://" + Constants.MCH_URI_DOMAIN_API + res;
            mchPayment.checkAndSign(app);
            String reqBody = JaxbMapper.toXml(mchPayment);
            return app.request(this.getHttpService(), url, reqBody);
        }).thenApply(Maps::fromXml);
    }

    // ==========================================================
    // 接入微信: 接入验证 -》 事件处理
    // ==========================================================

    /**
     * 接入签名
     *
     * @param app       app
     * @param timestamp 签名时间
     * @param nonce     随机字符串
     * @param signature 签名
     * @return 接入签名
     */
    public boolean accessSign(WechatConfig app, String timestamp, String nonce, String signature) {
        return SignUtils.accessSign(app.getToken(), timestamp, nonce).equals(signature);
    }

    /**
     * 处理消息
     *
     * @param app app
     * @param req 消息
     * @return 异步结果
     */
    public CompletionStage<RespMsg> onMessage(WechatConfig app, String req) {

        // 消息
        ReqMsg request = MsgParse.parseXml(req);

        // 暂时不支持的消息
        if (request == null) {
            return CompletableFuture.completedFuture(RespMsgNone.INSTANCE);
        }

        // 关注事件，取消关注事件
        if (request instanceof EventMsgUserAttention) {
            return onUserAttention(app, request);
        }

        // 菜单点击事件
        if (request instanceof MenuEventMsgClick) {
            return app.handleClickMenu((MenuEventMsgClick) request);
        }

        // 接收消息
        if (request instanceof AbstractReqMsg) {
            return app.handleMessage(request);
        }

        // 事件处理
        if (request instanceof AbstractEventMsg) {
            return app.handleEvent(request);
        }

        // 默认处理
        return app.handleDefault(request);
    }

    /**
     * 执行关注事件
     *
     * @param app app
     * @param msg 消息
     * @return 异步结果
     */
    private CompletionStage<RespMsg> onUserAttention(WechatConfig app, MsgHead msg) {
        final EventMsgUserAttention realMsg = (EventMsgUserAttention) msg;

        // 关注
        if (Constants.EventType.SCAN.name().equals(msg.getEvent())
                || Constants.EventType.subscribe.name().equals(msg.getEvent())) {

            // 二维码相关的事件(暂不处理) -- 找到二维码（和下面一样的处理方式）
            String qrscene = realMsg.getQrscene();
            if (StringUtils.isNotBlank(qrscene)) {
                return app.handleUserScan(realMsg);
            }

            // 公众号事件配置
            return app.handleUserAttention(realMsg);
        }

        // 取消关注(暂时不用做什么)
        else if (Constants.EventType.unsubscribe.name().equals(msg.getEvent())) {
            return app.handleUserUnsubscribe(realMsg);
        }
        return null;
    }
}
