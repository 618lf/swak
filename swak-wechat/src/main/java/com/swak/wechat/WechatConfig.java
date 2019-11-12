package com.swak.wechat;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import com.swak.http.builder.RequestBuilder;
import com.swak.utils.IOUtils;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.swak.wechat.codec.SignUtils;
import com.swak.wechat.message.EventMsgUserAttention;
import com.swak.wechat.message.MenuEventMsgClick;
import com.swak.wechat.message.ReqMsg;
import com.swak.wechat.message.ReqMsgImage;
import com.swak.wechat.message.ReqMsgText;
import com.swak.wechat.message.RespMsg;

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
	SSLContext getSSLContext();

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
	default CompletableFuture<String> request(String url, String data, boolean useCert) {
		SSLContext sslcontext = null;
		if (useCert && (sslcontext = this.getSSLContext()) != null) {
			return this.request(url, data, sslcontext);
		}
		return RequestBuilder.post().text().setUrl(url).setHeader("Content-Type", "text/xml")
				.setHeader("User-Agent", "wxpay sdk java v1.0 " + this.getMchId())
				.setRequestTimeout(this.getHttpConnectTimeoutMs()).setReadTimeout(this.getHttpReadTimeoutMs())
				.setBody(data).future();
	}

	/**
	 * 用 SSL 请求，需要設置证书，不同于Https的请求。
	 * 
	 * @category 暂时这么做，如果有可能将Http请求模块全部改为： okHttp
	 * @param url
	 * @param data
	 * @param sslcontext
	 * @return
	 */
	default CompletableFuture<String> request(String url, String data, SSLContext sslContext) {
		return CompletableFuture.supplyAsync(() -> {
			// CloseableHttpClient httpClient = null;
			// try {
			// SSLConnectionSocketFactory sslConnectionSocketFactory = new
			// SSLConnectionSocketFactory(sslContext,
			// new String[] { "TLSv1" }, null, new DefaultHostnameVerifier());
			//
			// BasicHttpClientConnectionManager connManager = new
			// BasicHttpClientConnectionManager(
			// RegistryBuilder.<ConnectionSocketFactory>create()
			// .register("http", PlainConnectionSocketFactory.getSocketFactory())
			// .register("https", sslConnectionSocketFactory).build(),
			// null, null, null);
			// httpClient =
			// HttpClientBuilder.create().setConnectionManager(connManager).build();
			// HttpPost httpPost = new HttpPost(url);
			// RequestConfig requestConfig =
			// RequestConfig.custom().setSocketTimeout(this.getHttpReadTimeoutMs())
			// .setConnectTimeout(this.getHttpConnectTimeoutMs()).build();
			// httpPost.setConfig(requestConfig);
			//
			// StringEntity postEntity = new StringEntity(data, "UTF-8");
			// httpPost.addHeader("Content-Type", "text/xml");
			// httpPost.addHeader("User-Agent", "wxpay sdk java v1.0 " + this.getMchId());
			// httpPost.setEntity(postEntity);
			//
			// HttpResponse httpResponse = httpClient.execute(httpPost);
			// HttpEntity httpEntity = httpResponse.getEntity();
			// return EntityUtils.toString(httpEntity, "UTF-8");
			// } catch (Exception e) {
			// return null;
			// } finally {
			// IOUtils.closeQuietly(httpClient);
			// }

			try {
				URL URL = new URL(url);
				HttpsURLConnection conn = (HttpsURLConnection) URL.openConnection();
				conn.setSSLSocketFactory(sslContext.getSocketFactory());
				conn.setConnectTimeout(this.getHttpConnectTimeoutMs());
				conn.setReadTimeout(this.getHttpReadTimeoutMs());
				conn.setDoOutput(true);
				conn.setDoInput(true);
				conn.setUseCaches(false);
				conn.setRequestMethod("POST");
				conn.setRequestProperty("Content-Type", "text/xml");
				conn.setRequestProperty("User-Agent", "wxpay sdk java v1.0 " + this.getMchId());
				OutputStream os = conn.getOutputStream();
				os.write(StringUtils.getBytesUtf8(data));
				os.flush();
				IOUtils.closeQuietly(os);
				int code = conn.getResponseCode();
				if (code == 200) {
					BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					StringBuilder responseData = new StringBuilder();
					String line = null;
					while ((line = in.readLine()) != null) {
						responseData.append(line);
					}
					IOUtils.closeQuietly(in);
					return responseData.toString();
				}
				conn.disconnect();
				return null;
			} catch (Exception e) {
				return null;
			}
		});
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
	 * 处理消息 - 处理接收到的文本消息
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleTextMessage(ReqMsgText msg) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息 - 处理接收到的图片消息
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleImageMessage(ReqMsgImage msg) {
		return CompletableFuture.completedFuture(null);
	}

	/**
	 * 处理消息 - 处理接收到的其他消息
	 * 
	 * @param request
	 * @param type
	 * @param config
	 * @return
	 */
	default CompletionStage<RespMsg> handleOtherMessage(ReqMsg msg) {
		return CompletableFuture.completedFuture(null);
	}
}
