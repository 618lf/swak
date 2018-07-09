package com.swak.security.web.captcha;

import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import com.swak.reactivex.transport.http.multipart.MimeType;
import com.swak.reactivex.transport.http.server.HttpServerRequest;
import com.swak.reactivex.transport.http.server.HttpServerResponse;
import com.swak.security.web.captcha.builder.ABuilder;
import com.swak.security.web.captcha.builder.BBuilder;
import com.swak.security.web.captcha.builder.Builder;
import com.swak.security.web.captcha.builder.CBuilder;
import com.swak.security.web.captcha.builder.DBuilder;
import com.swak.security.web.captcha.builder.EBuilder;
import com.swak.security.web.captcha.builder.FBuilder;
import com.swak.security.web.captcha.builder.GBuilder;
import com.swak.security.web.captcha.builder.HBuilder;
import com.swak.security.web.captcha.builder.JBuilder;
import com.swak.security.web.captcha.builder.KBuilder;
import com.swak.security.web.cookie.CookieProvider;
import com.swak.utils.Ints;
import com.swak.utils.StringUtils;

/**
 * 验证码管理器
 * 
 * @author lifeng
 */
public class CaptchaManager {

	private static String VALIDATE_CODE = "captcha-etag";
	private static String IMAGE_TYPE = "JPEG";
	private static String TYPES = "ABCDEFGHJK";

	/**
	 * 创建验证码
	 * 
	 * @return
	 */
	public static Captcha build() {
		return getBuilder().build();
	}

	private static Builder getBuilder() {
		int num = Ints.random(TYPES.length());
		char type = TYPES.charAt(num);
		switch (type) {
		case 'A':
			return new ABuilder();
		case 'B':
			return new BBuilder();
		case 'C':
			return new CBuilder();
		case 'D':
			return new DBuilder();
		case 'E':
			return new EBuilder();
		case 'F':
			return new FBuilder();
		case 'G':
			return new GBuilder();
		case 'H':
			return new HBuilder();
		case 'J':
			return new JBuilder();
		case 'K':
			return new KBuilder();
		default:
			return new ABuilder();
		}
	}

	/**
	 * 将验证码输出
	 * 
	 * @param captcha
	 * @param response
	 */
	public static void image(HttpServerRequest request, HttpServerResponse response) {
		Captcha captcha = build();
		CookieProvider.setAttribute(request, response, VALIDATE_CODE, captcha.getResult());
		OutputStream out = response.getOutputStream();
		try {
			ImageIO.write(captcha.getImage(), IMAGE_TYPE, out);
			response.mime(MimeType.getMimeType(IMAGE_TYPE));
		} catch (IOException e) {
		}
	}

	/**
	 * 将验证码输出
	 * 
	 * @param captcha
	 * @param response
	 */
	public static void base64(HttpServerRequest request, HttpServerResponse response) {
		image(request, response);
		response.base64();
	}

	/**
	 * 校验验证码
	 * 
	 * @param captcha
	 * @param cCode
	 * @return
	 */
	public static boolean check(HttpServerRequest request, HttpServerResponse response, String captcha) {
		if (captcha == null || captcha.isEmpty() || captcha.length() <= 1) {
			return false;
		}
		String _captcha = CookieProvider.getAttribute(request, response, VALIDATE_CODE);
		if (_captcha == null || _captcha.isEmpty() || _captcha.length() <= 1) {
			return false;
		}
		return StringUtils.equalsIgnoreCase(captcha, _captcha);
	}
}