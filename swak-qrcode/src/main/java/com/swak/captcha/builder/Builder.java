package com.swak.captcha.builder;

import com.swak.captcha.Captcha;

/**
 * 验证码创建者接口。 它拥有获取验证码
 *
 * @author bing <503718696@qq.com>
 * @date 2016-5-15 20:52:45
 * @version v0.1
 */
public interface Builder {

	/**
	 * 验证码图像和验证码字符串。
	 *
	 * @return 返回验证码图像和验证码字符串。
	 */
	Captcha build();
}