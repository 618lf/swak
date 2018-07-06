package com.swak.security.web.captcha.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.GeneralPath;
import java.awt.image.BufferedImage;

import com.swak.security.web.captcha.Captcha;

/**
 * 波浪形干扰线验证码
 *
 * @author 冷川 <li_shuijun@163.com>
 * @date 2016-6-22
 */
public class HBuilder extends AbstractBuilder {

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
	public Image generateImage(Captcha code) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = bi.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);
		g.setColor(ColorUtil.randomColor());
		// 绘制波浪线
		drawWave(g);
		// 绘制随机字符
		drawCodeString(g, code.getCode());
		g.dispose();
		return bi;
	}

	/**
	 * 绘制验证码字符串
	 *
	 * @param g
	 * @param code
	 */
	private void drawCodeString(Graphics g, String code) {
		g.setFont(font);
		for (int i = 0; i < codeLength; i++) {
			g.translate(random.nextInt(10), random.nextInt(4));
			g.setColor(ColorUtil.randomColor());
			g.drawString(String.valueOf(code.charAt(i)), 13 * i, 16);
		}
	}

	/**
	 * 绘制波浪线
	 *
	 * @param g
	 */
	private void drawWave(Graphics g) {
		GeneralPath gp = new GeneralPath();
		gp.moveTo(0, height / 2);
		Graphics2D g2d = (Graphics2D) g;
		for (double i = 0; i <= 8 * Math.PI; i += 0.0001 * Math.PI) {
			gp.lineTo(10 * i, height / 2 + 5 * Math.sin(2 * i));
		}
		g2d.draw(gp);
		g2d.draw(gp);
	}

}
