package com.swak.oss.aliyun;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;

import org.asynchttpclient.Response;

import com.swak.codec.Base64;
import com.swak.http.HttpService;
import com.swak.http.builder.RequestBuilder;
import com.swak.oss.OssConfig;
import com.swak.oss.OssConfig.Bucket;
import com.swak.oss.OssResponse;
import com.swak.oss.OssStore;
import com.swak.oss.aliyun.utils.DateUtil;
import com.swak.oss.aliyun.utils.SignUtils;
import com.swak.utils.IOUtils;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;

import io.netty.handler.codec.http.HttpMethod;

/**
 * 对象存储的服务: 请继承此对象
 * 
 * @author lifeng
 */
public abstract class AbstractOssService {

	/**
	 * 返回 http 的服务
	 * 
	 * @return
	 */
	public abstract HttpService getHttpService();

	/**
	 * 生成上传的签名
	 * 
	 * @param config
	 * @return
	 */
	public Map<String, Object> generatePolicy(OssConfig config, String key, Integer fileSize, long expire) {
		if (fileSize != null && fileSize > 1024 * 1024 * 100) {
			return generatePolicyChunk(config, key);
		}
		return generatePolicySimple(config, key, expire);
	}

	/**
	 * 分片上传
	 * 
	 * @param config
	 * @param key
	 * @return
	 */
	protected Map<String, Object> generatePolicyChunk(OssConfig config, String key) {
		String mimeType = MimeType.getMimeType(key);
		Bucket bucket = config.getBuckets().get(mimeType);
		OssRequest request = request().setAccessKeyId(config.getAccessKeyId())
				.setAccessKeySecret(config.getAccessKeySecret()).setEndpoint(config.getEndpoint()).setBucket(bucket)
				.setKey(key);
		Map<String, Object> respMap = new LinkedHashMap<String, Object>();
		respMap.put("accessid", config.getAccessKeyId());
		respMap.put("accessSecret", config.getAccessKeySecret());
		respMap.put("region", StringUtils.substringBefore(config.getEndpoint().getHost(), "."));
		respMap.put("bucket", bucket.getName());
		respMap.put("key", key);
		respMap.put("store", request.toStore());
		return respMap;
	}

	/**
	 * 直传
	 * 
	 * @param config
	 * @param key
	 * @param expire
	 * @return
	 */
	protected Map<String, Object> generatePolicySimple(OssConfig config, String key, long expire) {
		String dir = key.substring(0, key.indexOf("/") + 1);
		String file = key.substring(key.indexOf("/") + 1);
		String formatedExpiration = DateUtil.formatIso8601Date(new Date(expire));
		String jsonizedExpiration = String.format("\"expiration\":\"%s\"", formatedExpiration);
		StringBuilder jsonizedConds = new StringBuilder();
		jsonizedConds.append("\"conditions\":[");
		jsonizedConds.append(String.format("[\"content-length-range\",%d,%d],", 0, 1048576000));
		jsonizedConds.append(String.format("[\"starts-with\",\"$%s\",\"%s\"]", "key", dir));
		jsonizedConds.append("]");
		String postPolicy = new StringBuilder().append(String.format("{%s,%s}", jsonizedExpiration, jsonizedConds))
				.toString();
		byte[] binaryData = StringUtils.getBytesUtf8(postPolicy);
		String encodedPolicy = Base64.encodeBase64String(binaryData);
		String postSignature = SignUtils.buildPolicySignature(config.getAccessKeySecret(), postPolicy);

		String mimeType = MimeType.getMimeType(file);
		Bucket bucket = config.getBuckets().get(mimeType);
		OssRequest request = request().setAccessKeyId(config.getAccessKeyId())
				.setAccessKeySecret(config.getAccessKeySecret()).setEndpoint(config.getEndpoint()).setBucket(bucket)
				.setKey(key);

		Map<String, Object> respMap = new LinkedHashMap<String, Object>();
		respMap.put("accessid", config.getAccessKeyId());
		respMap.put("policy", encodedPolicy);
		respMap.put("signature", postSignature);
		respMap.put("name", file);
		respMap.put("key", key);
		respMap.put("store", request.toStore());
		respMap.put("host", request.policyURL());
		respMap.put("expire", String.valueOf(expire / 1000));
		return respMap;
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key    通过 文件夹/文件名来使用文件夹功能
	 * @param data
	 * @return
	 */
	public CompletableFuture<OssResponse> putObject(OssConfig config, String key, byte[] data) {
		return putObject(config, null, key, new ByteArrayInputStream(data));
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key    通过 文件夹/文件名来使用文件夹功能
	 * @param data
	 * @return
	 */
	public CompletableFuture<OssResponse> putObject(OssConfig config, Bucket $bucket, String key, byte[] data) {
		return putObject(config, $bucket, key, new ByteArrayInputStream(data));
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key    通过 文件夹/文件名来使用文件夹功能
	 * @param data
	 * @return
	 */
	public CompletableFuture<OssResponse> putObject(OssConfig config, String key, File data) {
		try {
			return putObject(config, null, key, new FileInputStream(data));
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File Not Found", e);
		}
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key    通过 文件夹/文件名来使用文件夹功能
	 * @param data
	 * @return
	 */
	public CompletableFuture<OssResponse> putObject(OssConfig config, Bucket $bucket, String key, InputStream data) {
		if (StringUtils.startsWith(key, "/")) {
			throw new RuntimeException("Key can not start with '/' or '\'");
		}
		String mimeType = MimeType.getMimeType(key);
		Bucket bucket = $bucket == null ? config.getBuckets().get(mimeType) : $bucket;
		Map<String, String> headers = Maps.newHashMap();
		headers.put(OssContants.CONTENT_TYPE, mimeType);
		headers.put(OssContants.DATE, DateUtil.formatRfc822Date(new Date()));
		OssRequest request = request().setAccessKeyId(config.getAccessKeyId())
				.setAccessKeySecret(config.getAccessKeySecret()).setMethod(HttpMethod.PUT)
				.setEndpoint(config.getEndpoint()).setBucket(bucket).setKey(key).setContent(data).setHeaders(headers);
		return request.future().thenApply(r -> {
			OssResponse response = new OssResponse();
			response.setStatusCode(r.getStatusCode());
			response.setBucket(request.getBucket());
			response.setResourcePath(request.getResourcePath());
			response.setSize(request.getSize());
			if (r.getStatusCode() == 200) {
				response.setSignedUrl(
						request.reset().setExpiration(new Date(System.currentTimeMillis() + 3600 * 1000)).signedURL());
			} else {
				response.setErrorMessgae(r.toString());
			}
			return response;
		});
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key    通过 文件夹/文件名来使用文件夹功能
	 * @param data
	 * @return
	 */
	public CompletableFuture<OssResponse> delObject(OssConfig config, String key) {
		if (StringUtils.startsWith(key, "/")) {
			throw new RuntimeException("Key can not start with '/' or '\'");
		}
		String mimeType = MimeType.getMimeType(key);
		Bucket bucket = config.getBuckets().get(mimeType);
		Map<String, String> headers = Maps.newHashMap();
		headers.put(OssContants.DATE, DateUtil.formatRfc822Date(new Date()));
		OssRequest request = request().setAccessKeyId(config.getAccessKeyId())
				.setAccessKeySecret(config.getAccessKeySecret()).setMethod(HttpMethod.DELETE)
				.setEndpoint(config.getEndpoint()).setBucket(bucket).setKey(key).setHeaders(headers);
		return request.future().thenApply(r -> {
			OssResponse response = new OssResponse();
			response.setStatusCode(r.getStatusCode());
			response.setBucket(request.getBucket());
			response.setResourcePath(request.getResourcePath());
			if (r.getStatusCode() != 200) {
				response.setErrorMessgae(r.toString());
			}
			return response;
		});
	}

	/**
	 * 上传数据, key 需要是唯一的
	 * 
	 * @param config
	 * @param key
	 * @param data
	 * @return
	 */
	public String signedURL(OssConfig config, String key, int seconds) {
		String mimeType = MimeType.getMimeType(key);
		Bucket bucket = config.getBuckets().get(mimeType);
		return request().setAccessKeyId(config.getAccessKeyId()).setAccessKeySecret(config.getAccessKeySecret())
				.setEndpoint(config.getEndpoint()).setBucket(bucket).setKey(key)
				.setExpiration(new Date(System.currentTimeMillis() + seconds * 1000)).signedURL();
	}

	/**
	 * 创建一个请求
	 * 
	 * @return
	 */
	private OssRequest request() {
		return new OssRequest();
	}

	/**
	 * Oss 请求
	 * 
	 * @author lifeng
	 */
	public class OssRequest {

		/* bucket */
		private Bucket bucket;

		/* object name */
		private String key;

		/* The resource path being requested */
		private String resourcePath;

		/* The service endpoint to which this request should be sent */
		private URI endpoint;

		/* The HTTP method to use when sending this request */
		private HttpMethod method = HttpMethod.GET;

		/* Use a LinkedHashMap to preserve the insertion order. */
		private Map<String, String> parameters = new LinkedHashMap<String, String>();

		/* The HTTP headers to use when sending this request */
		private Map<String, String> headers = new HashMap<String, String>();

		/* The HTTP Content to use when sending this request */
		private InputStream content;
		private Integer size = -1;

		private String accessKeyId;
		private String accessKeySecret;
		private Date expiration;

		public Date getExpiration() {
			return expiration;
		}

		public OssRequest setExpiration(Date expiration) {
			this.expiration = expiration;
			return this;
		}

		public String getAccessKeyId() {
			return accessKeyId;
		}

		public OssRequest setAccessKeyId(String accessKeyId) {
			this.accessKeyId = accessKeyId;
			return this;
		}

		public String getAccessKeySecret() {
			return accessKeySecret;
		}

		public OssRequest setAccessKeySecret(String accessKeySecret) {
			this.accessKeySecret = accessKeySecret;
			return this;
		}

		public Bucket getBucket() {
			return bucket;
		}

		public OssRequest setBucket(Bucket bucket) {
			this.bucket = bucket;
			return this;
		}

		public String getKey() {
			return key;
		}

		public OssRequest setKey(String key) {
			this.key = key;
			this.resourcePath = SignUtils.urlEncodeKey(key);
			return this;
		}

		public String getResourcePath() {
			return resourcePath;
		}

		public OssRequest setResourcePath(String resourcePath) {
			this.resourcePath = resourcePath;
			return this;
		}

		public URI getEndpoint() {
			return endpoint;
		}

		public OssRequest setEndpoint(URI endpoint) {
			this.endpoint = endpoint;
			return this;
		}

		public OssRequest setEndpoint(String endpoint) {
			try {
				this.endpoint = new URI(endpoint);
			} catch (URISyntaxException e) {
			}
			return this;
		}

		public HttpMethod getMethod() {
			return method;
		}

		public OssRequest setMethod(HttpMethod method) {
			this.method = method;
			return this;
		}

		public Map<String, String> getParameters() {
			return parameters;
		}

		public OssRequest setParameters(Map<String, String> parameters) {
			this.parameters = parameters;
			return this;
		}

		public Map<String, String> getHeaders() {
			return headers;
		}

		public OssRequest setHeaders(Map<String, String> headers) {
			this.headers = headers;
			return this;
		}

		public InputStream getContent() {
			return content;
		}

		public OssRequest setContent(InputStream content) {
			this.content = content;
			try {
				this.size = this.content.available();
			} catch (IOException e) {
			}
			return this;
		}

		public Integer getSize() {
			return size;
		}

		/**
		 * 清空数据
		 * 
		 * @return
		 */
		public OssRequest reset() {
			parameters = new LinkedHashMap<String, String>();
			headers = new HashMap<String, String>();
			this.method = HttpMethod.GET;
			IOUtils.closeQuietly(this.content);
			this.size = -1;
			return this;
		}

		/**
		 * 执行请求
		 * 
		 * @return
		 */
		public CompletableFuture<Response> future() {

			// 请求地址
			String url = this.requestUrl(true);

			// 请求签名
			this.requestSign();

			// 发送请求
			RequestBuilder builder = getHttpService().method(method).setUrl(url).setBody(this.content).plain();
			for (Entry<String, String> entry : headers.entrySet()) {
				if (entry.getKey().equalsIgnoreCase(OssContants.CONTENT_LENGTH)
						|| entry.getKey().equalsIgnoreCase(OssContants.HOST)) {
					continue;
				}
				builder.addHeader(entry.getKey(), entry.getValue());
			}
			return builder.future();
		}

		private String requestUrl(boolean hasResource) {
			StringBuilder url = new StringBuilder();
			url.append(String.format("%s://", this.getEndpoint().getScheme()));
			url.append(this.getBucket().getName()).append(".").append(this.getEndpoint().getHost());
			url.append(this.getEndpoint().getPort() != -1 ? String.format(":%s", this.getEndpoint().getPort()) : "");
			url.append(this.getEndpoint().getPath());
			if (hasResource && StringUtils.isNotBlank(this.resourcePath)) {
				url.append("/").append(this.resourcePath);
			}
			if (this.method != null && this.method != HttpMethod.POST) {
				String paramString = SignUtils.paramToQueryString(this.parameters);
				if (paramString != null) {
					url.append("?").append(paramString);
				}
			}
			return url.toString();
		}

		private void requestSign() {
			String signature = SignUtils.buildSignature(this.accessKeySecret, this.method.name(), this.bucket.getName(),
					this.resourcePath, this.headers, this.parameters);
			headers.put(OssContants.AUTHORIZATION, SignUtils.composeRequestAuthorization(this.accessKeyId, signature));
			SignUtils.convertHeaderCharsetToIso88591(headers);
		}

		/**
		 * 设置签名的url
		 * 
		 * @return
		 */
		public String signedURL() {
			String url = this.requestUrl(true);
			if (this.bucket.isAuth()) {
				String signed = SignUtils.buildSignedParam(accessKeyId, accessKeySecret, this.method.name(),
						bucket.getName(), resourcePath, headers, parameters, expiration);
				return new StringBuilder(url).append("?").append(signed).toString();
			}
			return url;
		}

		/**
		 * 设置签名的url
		 * 
		 * @return
		 */
		public String policyURL() {
			return this.requestUrl(false);
		}

		/**
		 * 转为数据存储
		 * 
		 * @return
		 */
		public OssStore toStore() {
			OssStore ossStore = new OssStore();
			ossStore.setBucket(bucket.getName());
			ossStore.setResourcePath(resourcePath);
			if (!bucket.isAuth()) {
				ossStore.setSignedUrl(this.signedURL());
			}
			return ossStore;
		}
	}
}