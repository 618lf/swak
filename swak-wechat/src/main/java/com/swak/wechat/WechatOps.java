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
import com.swak.wechat.pay.SandboxSignKey;
import com.swak.wechat.pay.Unifiedorder;
import com.swak.wechat.user.SnsToken;
import com.swak.wechat.user.UserInfo;

/**
 * 微信的 Api 服务
 * 
 * @author lifeng
 */
public class WechatOps {

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
	// 微信支付： 下单 -> 查询订单
	// ==========================================================

	/**
	 * 统一下单
	 * 
	 * @param app
	 * @param unifiedorder
	 * @return
	 */
	public static CompletableFuture<Map<String, String>> unifiedorder(WechatConfig app, Unifiedorder unifiedorder) {
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
		});
	}
	
	/**
	 * 查询订单
	 * 
	 * @param app
	 * @param unifiedorder
	 * @return
	 */
	public static CompletableFuture<Map<String, String>> queryOrder(WechatConfig app, MchOrderquery mchOrderquery) {
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
			Map<String, String> maps = Maps.fromXml(res);
			return maps.get("sandbox_signkey");
		});
	}
}