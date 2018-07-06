package com.swak.security.web.captcha;

import java.awt.Image;

/**
 * 验证码接口
 * 
 * @author lifeng
 */
public class Captcha {

	private Image image;
	private String code;
	private String result;

	public Captcha(String code) {
		this.code = code;
	}

	public Captcha(String code, String result) {
		this.result = result;
		this.code = code;
	}

	public Image getImage() {
		return image;
	}

	public void setImage(Image image) {
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