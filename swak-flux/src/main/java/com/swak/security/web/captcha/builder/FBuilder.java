package com.swak.security.web.captcha.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.swak.security.web.captcha.Captcha;

/**
 *
 * <pre>
 * 作者：haibin
 * 项目：SpringWind-CaptchaSystem
 * 类说明：生成中文算数验证码
 * 日期：2016年5月20日
 * 备注：
 * </pre>
 */
public class FBuilder extends AbstractBuilder {

	// 验证码数据源[?是否考虑将生成不同验证码类型的源数据统一放到一个map,key是验证码类型,value：是数据源]
	private String captchaCodeSource = "零壹贰叁肆伍陆柒捌玖";
	// 验证码运算数据（使用 Java Unicode code，加减乘除）
	private String captchaOperation = "加减乘";
	// 验证码运算符等于
	private String captchaEqualOperation = "等于";
	// 验证码个数
	int codeLength = 5;
	// 验证码图片长度
	int width = 100;

	/**
	 * 返回用于构成验证码的字符
	 *
	 * @return
	 */
	@Override
	public Captcha generateCaptcha() {
		StringBuilder sb = new StringBuilder();
		// 随即数据
		int result = 0;
		int rand0 = random.nextInt(10) + 1;
		int rand1 = random.nextInt(10) + 1;

		// 是加法还是减法
		int math = random.nextInt(3);

		// 加法
		if (math == 0) {
			result = rand0 + rand1;
		} else if (math == 1) {
			if (rand0 < rand1) {
				rand0 = rand0 ^ rand1;
				rand1 = rand0 ^ rand1;
				rand0 = rand0 ^ rand1;
			}
			result = rand0 - rand1;
		} else if (math == 2) {
			result = rand0 * rand1;
		}
		sb.append(captchaCodeSource.charAt(rand0)).append(captchaOperation.charAt(math))
				.append(captchaCodeSource.charAt(rand1)).append(captchaEqualOperation);
		return new Captcha(sb.toString(), String.valueOf(result));
	}

	/**
	 * 返回验证码图片
	 *
	 * @return
	 */
	@Override
	public Image generateImage(Captcha code) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.GRAY);
		g.fillRect(0, 0, width, height);
		g.setFont(font);
		g.setColor(Color.BLACK);
		g.drawRect(0, 0, width - 1, height - 1);
		// 绘制验证码
		drawCodeString(g, code.getCode());
		return image;
	}

	/**
	 * 绘制验证码
	 *
	 * @param Graphics
	 *            实例
	 * @param code
	 *            验证码
	 */
	private void drawCodeString(Graphics g, String code) {
		for (int i = 0; i < codeLength; i++) {
			g.setColor(ColorUtil.randomColor());
			g.drawString(String.valueOf(code.charAt(i)), (i + 1) * 15, 16);
		}
	}
}
