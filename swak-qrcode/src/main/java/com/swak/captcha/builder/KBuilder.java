package com.swak.captcha.builder;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.swak.captcha.Captcha;

/**
 * 效率慢
 * <pre>
 * 作者：张小八
 * 项目：SpringWind-CaptchaSystem
 * 日期：2016-06-20
 * 备注：波浪线,背景色块,随机字符色块旋转
 * </pre>
 */
@Deprecated
public class KBuilder extends AbstractBuilder {

	// 用于生产的母字符串
	private String randString = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

	@Override
	public Captcha generateCaptcha() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < codeLength; i++) {
			sb.append(randString.charAt(random.nextInt(randString.length())));
		}
		return new Captcha(sb.toString());
	}

	/**
	 * 返回验证码图片
	 *
	 * @return
	 */

	@Override
	public BufferedImage generateImage(Captcha code) {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics g = image.getGraphics();
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, width, height);

		g.setFont(font);
		g.setColor(Color.green);
		g.drawRect(0, 0, width - 1, height - 1);
		// 绘制波浪线
		drawLines(g);
		// 绘制验证码
		drawCodeString(g, code.getCode());
		return image;
	}

	/**
	 * 绘制波浪线
	 *
	 * @param g
	 */
	private void drawLines(Graphics g) {
		// 加入干扰线条
		for (int i = 0; i < 10; i++) {
			g.setColor(getRandColor(40, 150));
			int x = random.nextInt(width);
			int y = random.nextInt(height);
			int x1 = random.nextInt(width);
			int y1 = random.nextInt(height);
			g.drawLine(x, y, x1, y1);
		}
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
		Graphics2D g2d = (Graphics2D) g.create();
		for (int i = 0; i < codeLength; i++) {
			// 旋转角度
			double rot = -0.25 + Math.abs(Math.toRadians(random.nextInt(50)));
			g2d.rotate(rot, (i + 1) * 15, 10);
			// 绘制色块
			g2d.setColor(Color.green);
			g2d.fillRect((i + 1) * 15, 5, 10, 12);
			// 随机字体颜色
			g2d.setColor(this.getRandColor());
			g2d.drawString(String.valueOf(code.charAt(i)), (i + 1) * 15, 20);
			// 恢复角度
			g2d.rotate(-rot, (i + 1) * 15, 10);
		}
		g2d.dispose();
	}
}