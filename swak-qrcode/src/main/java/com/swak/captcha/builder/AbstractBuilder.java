package com.swak.captcha.builder;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.Random;

import com.swak.captcha.Captcha;

/**
 * 验证码创建者抽象类。它只实现了@see pub.greenbamboo.captcha.Captcha接口。
 *
 * @author bing <503718696@qq.com>
 * @date 2016-5-15 20:52:45
 * @version v0.1
 */
public abstract class AbstractBuilder implements Builder {

	protected Font font = new Font("Fixedsys", Font.BOLD, 18);// 验证码字体
	protected Random random = new Random();
	protected int width = 80;// 图片宽
	protected int height = 26;// 图片高
	protected int codeLength = 4;// 验证码个数

	/**
	 * 创建图像验证码。
	 *
	 * @return 返回图像验证码。
	 */
	@Override
	public Captcha build() {
		Captcha captcha = generateCaptcha();
		captcha.setImage(generateImage(captcha));
		return captcha;
	}

	/**
	 * 创建验证码字符串。
	 *
	 * @return 返回验证码字符串。
	 */
	public abstract Captcha generateCaptcha();

	/**
	 * 创建验证码图像。
	 *
	 * @param 输入的验证码。
	 * @return 返回验证码图像。
	 */
	public abstract BufferedImage generateImage(Captcha captcha);
	
	/**
	 * 获得颜色
	 * @param fc
	 * @param bc
	 * @return
	 */
	protected Color getRandColor(int fc, int bc) {
		int f = fc;
		int b = bc;
		if (f > 255) {
			f = 255;
		}
		if (b > 255) {
			b = 255;
		}
		return new Color(f + random.nextInt(b - f), f + random.nextInt(b - f), f + random.nextInt(b - f));
	}
	
	/**
	 * 获得随即颜色
	 * @return
	 */
	protected Color getRandColor() {
        return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
    }
}