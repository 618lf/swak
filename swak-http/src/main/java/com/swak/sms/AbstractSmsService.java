package com.swak.sms;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.Response;

import com.swak.http.HttpService;
import com.swak.http.builder.RequestBuilder;
import com.swak.oss.aliyun.utils.DateUtil;
import com.swak.sms.SmsConfig.Scene;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

import io.netty.handler.codec.http.HttpMethod;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 短信服务
 * 
 * @author lifeng
 */
public abstract class AbstractSmsService {

	/**
	 * 返回 http 的服务
	 * 
	 * @return
	 */
	public abstract HttpService getHttpService();

	/**
	 * 发送消息
	 * 
	 * @param config 配置
	 * @param type   场景类型
	 * @param phone  手机号
	 * @param params 参数
	 * @return 异步结果
	 */
	public CompletableFuture<Response> send(SmsConfig config, Object type, String phone, Map<String, String> params) {
		Scene scene = config.getScenes().get(type);
		Map<String, String> queryParameter = Maps.newHashMap();
		queryParameter.put("Version", "2017-05-25");
		queryParameter.put("Action", "SendSms");
		queryParameter.put("Format", "JSON");
		queryParameter.put("RegionId", "default");
		queryParameter.put("PhoneNumbers", phone);
		queryParameter.put("SignName", scene.getSign());
		queryParameter.put("TemplateCode", scene.getTemplate());
		queryParameter.put("TemplateParam", JsonMapper.toJson(params));
		return request().setEndpoint(config.getEndpoint()).setAccessKeyId(config.getAccessKeyId())
				.setAccessKeySecret(config.getAccessKeySecret()).setParameters(queryParameter).future();
	}

	/**
	 * 创建一个请求
	 * 
	 * @return
	 */
	private SmsRequest request() {
		return new SmsRequest();
	}

	/**
	 * SMS 请求
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public class SmsRequest {
		private URI endpoint;
		private HttpMethod method = HttpMethod.POST;
		private Map<String, String> parameters;
		private String accessKeyId;
		private String accessKeySecret;

		/**
		 * 执行请求
		 * 
		 * @return
		 */
		public CompletableFuture<Response> future() {

			// 请求签名
			Map<String, String> parameters = this.requestSign();

			// 请求地址
			String url = this.requestUrl(parameters);

			// 发送请求
			RequestBuilder builder = getHttpService().method(method).setUrl(url).plain();
			return builder.future();
		}

		private String requestUrl(Map<String, String> parameters) {
			// 制作URL
			StringBuilder queryBuilder = new StringBuilder("");
			for (Entry<String, String> entry : parameters.entrySet()) {
				String key = entry.getKey();
				String val = entry.getValue();
				queryBuilder.append(SignUtils.encode(key));
				if (val != null) {
					queryBuilder.append("=").append(SignUtils.encode(val));
				}
				queryBuilder.append("&");
			}
			int strIndex = queryBuilder.length();
			if (parameters.size() > 0) {
				queryBuilder.deleteCharAt(strIndex - 1);
			}
			StringBuilder urlBuilder = new StringBuilder("");
			urlBuilder.append(endpoint.toString()).append("?").append(queryBuilder.toString());
			return urlBuilder.toString();
		}

		private Map<String, String> requestSign() {
			Map<String, String> immutableMap = new HashMap<String, String>(parameters);
			immutableMap.put("Timestamp", DateUtil.formatIso8601Date_sms(new Date()));
			immutableMap.put("SignatureMethod", "HMAC-SHA1");
			immutableMap.put("SignatureVersion", "1.0");
			immutableMap.put("SignatureNonce", UUID.randomUUID().toString());
			immutableMap.put("AccessKeyId", accessKeyId);

			// 制作签名
			Map<String, String> paramsToSign = new HashMap<String, String>(immutableMap);
			String Signature = SignUtils.sign(method, accessKeySecret + '&', paramsToSign);
			immutableMap.put("Signature", Signature);
			return immutableMap;
		}
	}
}