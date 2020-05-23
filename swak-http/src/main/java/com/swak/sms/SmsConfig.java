package com.swak.sms;

import java.net.URI;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * 短信的配置
 * 
 * @author lifeng
 * @date 2020年4月13日 下午3:52:37
 */
@Getter
@Setter
@Accessors(chain = true)
public class SmsConfig {

	protected URI endpoint;
	protected String accessKeyId;
	protected String accessKeySecret;
	protected Map<Object, Scene> scenes;

	/**
	 * 场景
	 * 
	 * @author lifeng
	 */
	@Getter
	@Setter
	@Accessors(chain = true)
	public static class Scene {
		private String sign;
		private String template;

		public static Scene of() {
			return new Scene();
		}
	}
}