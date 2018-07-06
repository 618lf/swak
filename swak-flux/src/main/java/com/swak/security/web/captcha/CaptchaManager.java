package com.swak.security.web.captcha;

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
import com.swak.utils.Ints;

/**
 * 验证码管理器
 * 
 * @author lifeng
 */
public class CaptchaManager {

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
	 * 校验验证码 验证码格式： A:XXX A： 类型 XXX： 实际的验证码
	 * 
	 * @param captcha
	 * @param cCode
	 * @return
	 */
	public static boolean check(String captcha, String cCode) {
		if (captcha == null || cCode == null || captcha.isEmpty() || cCode.isEmpty() || captcha.length() <= 1) {
			return false;
		}
		captcha = captcha.substring(1);
		return captcha.equals(cCode);
	}
}