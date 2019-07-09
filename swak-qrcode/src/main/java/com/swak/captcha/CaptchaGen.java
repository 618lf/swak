package com.swak.captcha;

import com.swak.captcha.builder.ABuilder;
import com.swak.captcha.builder.BBuilder;
import com.swak.captcha.builder.Builder;
import com.swak.captcha.builder.CBuilder;
import com.swak.captcha.builder.DBuilder;
import com.swak.captcha.builder.EBuilder;
import com.swak.captcha.builder.FBuilder;
import com.swak.captcha.builder.GBuilder;
import com.swak.captcha.builder.HBuilder;
import com.swak.utils.Ints;

/**
 * 验证码管理器
 * 
 * java 验证码的效率如下： 生成：20w每秒 存储：很慢5000/s
 * 
 * 所以對於圖片的處理最好是使用lua 來做。 如果堅持要用java来处理则，被列为耗时的api：
 * 
 * 总结下lua下的图片处理： 1. 验证码： 目前只有 lua-gd 这样一个lua 包，而且好久没更新. 2. 图片处理到可以使用 imageMAGICK
 * lua 或 im4java 这样的工具
 * 
 * : ImageIo 是一个耗时的操作
 * 
 * @author lifeng
 */
public class CaptchaGen {

	private static String TYPES = "ABCDEFGHJK";

	/**
	 * 创建验证码
	 * 
	 * @return
	 */
	public static Captcha of() {
		return random().build();
	}

	/**
	 * 随机创建
	 * 
	 * @return
	 */
	public static Builder random() {
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
			return new HBuilder();
		case 'K':
			return new HBuilder();
		default:
			return new ABuilder();
		}
	}
}