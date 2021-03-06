package com.swak.captcha.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import com.swak.captcha.Captcha;

/**
 * 验证码创建者实体类C。该类是一个未完成的实现。请后续开发者完善。
 *
 * @author bing <503718696@qq.com>
 * @date 2016-5-15 21:08:41
 * @version v0.1
 */
public class CBuilder extends AbstractBuilder {

	private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";// 用于生产的母字符串

	@Override
	public Captcha generateCaptcha() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			sb.append(randString.charAt(random.nextInt(randString.length())));
		}
		return new Captcha(sb.toString());
	}

	@Override
	public BufferedImage generateImage(Captcha code) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = bi.getGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		g.setColor(this.getRandColor());
		// 绘制随机字符
		drawCodeString(g, code.getCode());
		g.dispose();
		return bi;
	}

	/**
	 * 绘制字符串。
	 */
	private void drawCodeString(Graphics g, String code) {
		g.setFont(font);
		for (int i = 0; i < codeLength; i++) {
			g.translate(random.nextInt(10), random.nextInt(4));
			g.setColor(this.getRandColor());
			g.drawString(String.valueOf(code.charAt(i)), 13 * i, 16);
		}
	}
}