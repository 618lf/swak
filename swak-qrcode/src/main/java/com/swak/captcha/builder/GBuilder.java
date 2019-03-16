package com.swak.captcha.builder;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.swak.captcha.Captcha;

/**
 *
 * <pre>
 * 作者：cuidianlong
 * 项目：SpringWind-CaptchaSystem
 * 类说明：生成英文区分大小写6位数验证码
 * 日期：2016年5月20日
 * 备注：
 * </pre>
 */
public class GBuilder extends AbstractBuilder {

	private Map<String, String> captchaCodeSource = new HashMap<>();
	private String[] codeSequence = null;// 用于生产的母字符数组

	public GBuilder() {
		captchaCodeSource.put("眉", "眉来眼去");
		captchaCodeSource.put("笑", "笑傲江湖");
		captchaCodeSource.put("哎", "哎呀我去");
		captchaCodeSource.put("风", "风生水起");
		captchaCodeSource.put("亡", "亡羊补牢");
		captchaCodeSource.put("不", "不劳而获");
		// 遍历map中的key，并保存到codeSequence中。注意：成语第一个汉字容易重复，可以用int类型自增
		Set<String> keys = captchaCodeSource.keySet();
		Iterator<String> it = keys.iterator();
		codeSequence = new String[keys.size()];
		int j = 0;
		while (it.hasNext()) {
			codeSequence[j++] = it.next();
		}
	}

	/**
	 * 返回用于构成验证码的字符
	 *
	 * @return
	 */
	@Override
	public Captcha generateCaptcha() {
		StringBuilder sb = new StringBuilder();
		String code = codeSequence[random.nextInt(codeSequence.length)];
		sb.append(captchaCodeSource.get(code));
		return new Captcha(sb.toString());
	}

	/**
	 * 返回验证码图片
	 *
	 * @return
	 */
	@Override
	public BufferedImage generateImage(Captcha code) {
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		Graphics g = bi.getGraphics();
		g.fillRect(0, 0, width, height);
		g.setColor(this.getRandColor());
		// 绘制随机字符
		drawCodeString(g, code.getCode());
		g.dispose();
		return bi;
	}

	/**
	 * 绘制字符串。
	 *
	 * @param g
	 * @param code
	 *            随机字符串
	 * @param i
	 * @return
	 */
	private void drawCodeString(Graphics g, String code) {
		g.setFont(font);
		for (int i = 0; i < codeLength; i++) {
			g.translate(random.nextInt(3), random.nextInt(3));
			g.setColor(this.getRandColor());
			g.drawString(String.valueOf(code.charAt(i)), 13 * i, 16);
		}
	}
}
