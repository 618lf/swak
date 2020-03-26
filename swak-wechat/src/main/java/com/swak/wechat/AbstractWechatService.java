package com.swak.wechat;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import com.swak.codec.Encodes;
import com.swak.http.HttpService;
import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.JaxbMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.codec.MsgParse;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.message.AbstractEventMsg;
import com.swak.wechat.message.AbstractReqMsg;
import com.swak.wechat.message.EventMsgUserAttention;
import com.swak.wechat.message.MenuEventMsgClick;
import com.swak.wechat.message.MsgHead;
import com.swak.wechat.message.ReqMsg;
import com.swak.wechat.message.RespMsg;
import com.swak.wechat.message.RespMsgNone;
import com.swak.wechat.pay.MchOrderquery;
import com.swak.wechat.pay.MchPayment;
import com.swak.wechat.pay.Refundorder;
import com.swak.wechat.pay.Refundquery;
import com.swak.wechat.pay.SandboxSignKey;
import com.swak.wechat.pay.Unifiedorder;
import com.swak.wechat.tmpmsg.TemplateMessageResult;
import com.swak.wechat.token.AccessToken;
import com.swak.wechat.token.Ticket;
import com.swak.wechat.user.SessionToken;
import com.swak.wechat.user.SnsToken;
import com.swak.wechat.user.UserInfo;

/**
 * Wechat 服务
 * 
 * @author lifeng
 */
public abstract class AbstractWechatService {

	/**
	 * Ssl Http 服务
	 */
	private HttpService ssl_HttpService;

	/**
	 * Http 服务
	 * 
	 * @return
	 */
	public abstract HttpService getHttpService();

	/**
	 * 获取 SSl 服务， 如果有多个wechat 的ssl 需要处理可以根据 WechatConfig 管理多个
	 * 
	 * @param app
	 * @return
	 */
	public HttpService getSslHttpService(WechatConfig app) {
		if (ssl_HttpService == null) {
			ssl_HttpService = this.getHttpService().sslConfig(app.getSslContext());
		}
		return ssl_HttpService;
	}

	// ==========================================================
	// 调用 API: userinfo
	// ==========================================================

	/**
	 * 获取用户信息
	 * 
	 * @param access_token
	 * @param openId
	 * @return
	 */
	public CompletableFuture<UserInfo> userinfo(String access_token, String openId) {
		CompletableFuture<UserInfo> future = this.getHttpService().post()
				.setUrl("https://api.weixin.qq.com/cgi-bin/user/info").addFormParam("access_token", access_token)
				.addFormParam("openid", openId).addFormParam("lang", "zh_CN").json(UserInfo.class).future();
		return future.thenApply(res -> {
			return res;
		});
	}

	/**
	 * 发送模板消息
	 * 
	 * @param access_token
	 * @param messageJson
	 * @return
	 */
	public CompletableFuture<TemplateMessageResult> sendTemplateMessage(String access_token, String messageJson) {
		CompletableFuture<TemplateMessageResult> future = this.getHttpService().post()
				.setUrl("https://api.weixin.qq.com/cgi-bin/message/template/send")
				.addQueryParam("access_token", access_token).setBody(StringUtils.getBytesUtf8(messageJson))
				.json(TemplateMessageResult.class).future();
		return future.thenApply(res -> {
			return res;
		});
	}

	// ==========================================================
	// 小程序登陆
	// ==========================================================

	/**
	 * 换取token
	 * 
	 * @param code
	 * @param state
	 * @return
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
	 * @param url
	 * @return
	 */
	public CompletableFuture<String> toAuthorize(WechatConfig app, String url, String state) {
		StringBuilder _content = new StringBuilder(com.swak.Constants.REDIRECT_URL_PREFIX)
				.append("https://open.weixin.qq.com/connect/oauth2/authorize").append("?appid=").append(app.getAppId())
				.append("&redirect_uri=").append(Encodes.urlEncode(url)).append("&response_type=code").append("&scope=")
				.append("snsapi_userinfo").append("&state=").append(state).append("#wechat_redirect");
		return CompletableFuture.completedFuture(_content.toString());
	}

	/**
	 * 换取token
	 * 
	 * @param code
	 * @param state
	 * @return
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
	 * @param code
	 * @param state
	 * @return
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
	 * @param app
	 * @return
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
	 * @param token
	 * @return
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
	 * @param app
	 * @param ticket
	 * @param url
	 * @return
	 */
	public Map<String, String> signUrl(WechatConfig app, Ticket ticket, String url) {
		String noncestr = UUIdGenerator.uuid();
		String timestamp = Long.toString(System.currentTimeMillis() / 1000);
		StringBuilder signStr = new StringBuilder();
		signStr.append("jsapi_ticket=").append(ticket.getTicket()).append("&").append("noncestr=").append(noncestr)
				.append("&").append("timestamp=").append(timestamp).append("&").append("url=").append(url);

		// 生成签名
		String signature = SignUtils.urlSign(signStr.toString());
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
	 * @param app
	 * @param unifiedorder
	 * @return
	 */
	public CompletableFuture<Map<String, Object>> unifiedorder(WechatConfig app, Unifiedorder unifiedorder) {
		CompletableFuture<String> future = null;
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
		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			unifiedorder.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(unifiedorder);
			return app.request(this.getHttpService(), url, reqBody);
		}).thenApply(res -> {
			return app.process(res, unifiedorder.getSign_type());
		}).thenApply(res -> {
			if (unifiedorder.getTrade_type().equals("NATIVE")) {
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
	 * @param app
	 * @param unifiedorder
	 * @return
	 */
	public CompletableFuture<Map<String, Object>> queryOrder(WechatConfig app, MchOrderquery mchOrderquery) {
		CompletableFuture<String> future = null;
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
		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			mchOrderquery.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(mchOrderquery);
			return app.request(this.getHttpService(), url, reqBody);
		}).thenApply(res -> {
			return app.process(res, mchOrderquery.getSign_type());
		});
	}

	/**
	 * 退款
	 * 
	 * @param app
	 * @param refund
	 * @return
	 */
	public CompletableFuture<Map<String, Object>> refundOrder(WechatConfig app, Refundorder refund) {
		CompletableFuture<String> future = null;
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
		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			refund.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(refund);
			return app.request(this.getHttpService(), url, reqBody);
		}).thenApply(res -> {
			return app.process(res, refund.getSign_type());
		});
	}

	/**
	 * 退款申请查询
	 * 
	 * @param query
	 * @return
	 */
	public CompletableFuture<Map<String, Object>> refundQuery(WechatConfig app, Refundquery query) {
		CompletableFuture<String> future = null;
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
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			query.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(query);
			return app.request(getSslHttpService(app), url, reqBody);
		}).thenApply(res -> {
			return app.process(res, query.getSign_type());
		});
	}

	/**
	 * 获得沙箱测试密钥
	 * 
	 * @return
	 * @throws Exception
	 */
	private CompletableFuture<String> getSandboxSignKey(WechatConfig app, String sign_type) {
		SandboxSignKey data = new SandboxSignKey();
		data.setMch_id(app.getMchId());
		data.setNonce_str(UUIdGenerator.uuid());
		data.setSign_type(sign_type);
		data.setSign(SignUtils.generateSign(data, data.getSign_type(), app.getMchKey()));
		String reqBody = JaxbMapper.toXml(data);
		String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API)
				.append(Constants.SANDBOX_GET_SIGNKEY_SUFFIX).toString();
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
	 * @param app
	 * @param refund
	 * @return
	 */
	public CompletableFuture<Map<String, Object>> sendamount(WechatConfig app, MchPayment mchPayment) {
		CompletableFuture<String> future = null;
		future = CompletableFuture.completedFuture(Constants.MMPAYMKTTRANSFERS_URL_SUFFIX);
		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			mchPayment.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(mchPayment);
			return app.request(this.getHttpService(), url, reqBody);
		}).thenApply(res -> {
			return Maps.fromXml(res);
		});
	}

	// ==========================================================
	// 接入微信: 接入验证 -》 事件处理
	// ==========================================================

	/**
	 * 校验签名
	 * 
	 * @param timestamp
	 * @param nonce
	 * @param signature
	 * @return
	 */
	public boolean accessSign(WechatConfig app, String timestamp, String nonce, String signature) {
		return SignUtils.accessSign(app.getToken(), timestamp, nonce).equals(signature);
	}

	/**
	 * 处理消息
	 * 
	 * @param app
	 * @param req
	 * @return
	 */
	public CompletionStage<RespMsg> onMessage(WechatConfig app, String req) {

		// 消息
		ReqMsg request = MsgParse.parseXML(req);

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
	 * @param msg
	 * @return
	 */
	private CompletionStage<RespMsg> onUserAttention(WechatConfig app, MsgHead msg) {
		final EventMsgUserAttention _msg = (EventMsgUserAttention) msg;

		// 关注
		if (Constants.EventType.SCAN.name().equals(msg.getEvent())
				|| Constants.EventType.subscribe.name().equals(msg.getEvent())) {

			// 二维码相关的事件(暂不处理) -- 找到二维码（和下面一样的处理方式）
			String qrscene = _msg.getQrscene();
			if (StringUtils.isNotBlank(qrscene)) {
				return app.handleUserScan(_msg);
			}

			// 公众号事件配置
			return app.handleUserAttention(_msg);
		}

		// 取消关注(暂时不用做什么)
		else if (Constants.EventType.unsubscribe.name().equals(msg.getEvent())) {
			return app.handleUserUnsubscribe(_msg);
		}
		return null;
	}

}