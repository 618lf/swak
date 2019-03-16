package com.swak.captcha;

import java.awt.image.BufferedImage;

/**
 * 验证码接口
 * 
 * @author lifeng
 */
public class Captcha {

	private BufferedImage image;
	private String code;
	private String result;

	public Captcha(String code) {
		this.code = code;
	}

	public Captcha(String code, String result) {
		this.result = result;
		this.code = code;
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getResult() {
		return result == null ? code : result;
	}

	public void setResult(String result) {
		this.result = result;
	}
}