package com.swak.wechat;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.swak.codec.Encodes;
import com.swak.http.builder.RequestBuilder;
import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.JaxbMapper;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.pay.MchOrderquery;
import com.swak.wechat.pay.Refundorder;
import com.swak.wechat.pay.Refundquery;
import com.swak.wechat.pay.SandboxSignKey;
import com.swak.wechat.pay.Unifiedorder;
import com.swak.wechat.tmpmsg.TemplateMessageResult;
import com.swak.wechat.token.AccessToken;
import com.swak.wechat.token.Ticket;
import com.swak.wechat.user.SnsToken;
import com.swak.wechat.user.UserInfo;

/**
 * 微信的 Api 服务
 * 
 * @author lifeng
 */
public class WechatOps {

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
	public static CompletableFuture<UserInfo> userinfo(String access_token, String openId) {
		CompletableFuture<UserInfo> future = RequestBuilder.post().setUrl("https://api.weixin.qq.com/cgi-bin/user/info")
				.addFormParam("access_token", access_token).addFormParam("openid", openId).addFormParam("lang", "zh_CN")
				.json(UserInfo.class).future();
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
	public static CompletableFuture<TemplateMessageResult> sendTemplateMessage(String access_token,
			String messageJson) {
		CompletableFuture<TemplateMessageResult> future = RequestBuilder.post()
				.setUrl("https://api.weixin.qq.com/cgi-bin/message/template/send")
				.addFormParam("access_token", access_token).setBody(StringUtils.getBytesUtf8(messageJson))
				.json(UserInfo.class).future();
		return future.thenApply(res -> {
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
	public static CompletableFuture<String> toAuthorize(WechatConfig app, String url, String state) {
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
	public static CompletableFuture<SnsToken> oauth2AccessToken(WechatConfig app, String code, String state) {
		if (!(StringUtils.isNotBlank(code) && StringUtils.isNotBlank(state))) {
			return CompletableFuture.completedFuture(null);
		}
		CompletableFuture<SnsToken> future = RequestBuilder.get()
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
	public static CompletableFuture<UserInfo> accessToken2Userinfo(SnsToken token) {
		CompletableFuture<UserInfo> future = RequestBuilder.get().setUrl("https://api.weixin.qq.com/sns/userinfo")
				.addQueryParam("access_token", token.getAccess_token()).addQueryParam("openid", token.getOpenid())
				.addQueryParam("lang", "zh_CN").json(UserInfo.class).future();
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
	public static CompletableFuture<AccessToken> accessToken(WechatConfig app) {
		CompletableFuture<AccessToken> future = RequestBuilder.get().setUrl("https://api.weixin.qq.com/cgi-bin/token")
				.addQueryParam("grant_type", "client_credential").addQueryParam("appid", app.getAppId())
				.addQueryParam("secret", app.getSecret()).json(AccessToken.class).future();
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
	public static CompletableFuture<Ticket> jsSdkTicket(AccessToken token) {
		CompletableFuture<Ticket> future = RequestBuilder.get()
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
	public static Map<String, String> signUrl(WechatConfig app, Ticket ticket, String url) {
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
	public static CompletableFuture<Map<String, Object>> unifiedorder(WechatConfig app, Unifiedorder unifiedorder) {
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
			return app.request(url, reqBody);
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
	public static CompletableFuture<Map<String, Object>> queryOrder(WechatConfig app, MchOrderquery mchOrderquery) {
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
			return app.request(url, reqBody);
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
	public static CompletableFuture<Map<String, Object>> refundOrder(WechatConfig app, Refundorder refund) {
		CompletableFuture<String> future = null;
		if (app.isUseSandbox()) {
			refund.setSign_type(Constants.MD5);
			future = getSandboxSignKey(app, refund.getSign_type()).thenApply(res -> {
				app.setMchKey(res);
				return Constants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
			});
		} else {
			refund.setSign_type(Constants.HMACSHA256);
			future = CompletableFuture.completedFuture(Constants.UNIFIEDORDER_URL_SUFFIX);
		}
		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			refund.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(refund);
			return app.request(url, reqBody);
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
	public static CompletableFuture<Map<String, Object>> refundQuery(WechatConfig app,  Refundquery query) {
		CompletableFuture<String> future = null;
 		if (app.isUseSandbox()) {
 			query.setSign_type(Constants.MD5);
 			future = getSandboxSignKey(app, query.getSign_type()).thenApply(res -> {
 				app.setMchKey(res);
 				return Constants.SANDBOX_UNIFIEDORDER_URL_SUFFIX;
 			});
 		} else {
 			query.setSign_type(Constants.HMACSHA256);
 			future = CompletableFuture.completedFuture(Constants.UNIFIEDORDER_URL_SUFFIX);
 		}
		
 		return future.thenCompose(res -> {
			String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API).append(res).toString();
			query.checkAndSign(app);
			String reqBody = JaxbMapper.toXml(query);
			return app.request(url, reqBody);
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
	private static CompletableFuture<String> getSandboxSignKey(WechatConfig app, String sign_type) {
		SandboxSignKey data = new SandboxSignKey();
		data.setMch_id(app.getMchId());
		data.setNonce_str(UUIdGenerator.uuid());
		data.setSign_type(sign_type);
		data.setSign(SignUtils.generateSign(data, data.getSign_type(), app.getMchKey()));
		String reqBody = JaxbMapper.toXml(data);
		String url = new StringBuilder("https://").append(Constants.MCH_URI_DOMAIN_API)
				.append(Constants.SANDBOX_GET_SIGNKEY_SUFFIX).toString();
		return app.request(url, reqBody).thenApply(res -> {
			Map<String, Object> maps = Maps.fromXml(res);
			return String.valueOf(maps.get("sandbox_signkey"));
		});
	}
}