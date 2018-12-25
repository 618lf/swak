package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.StringUtils;
import com.swak.wechat.WechatConfig;
import com.swak.wechat.codec.SignUtils;

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchOrderquery {

	@XmlElement
	private String appid;

	@XmlElement
	private String mch_id;

	@XmlElement
	private String transaction_id;

	@XmlElement
	private String out_trade_no;

	@XmlElement
	private String nonce_str;

	@XmlElement
	private String sign;

	@XmlElement
	private String sign_type;

	public String getAppid() {
		return appid;
	}

	public void setAppid(String appid) {
		this.appid = appid;
	}

	public String getMch_id() {
		return mch_id;
	}

	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getNonce_str() {
		return nonce_str;
	}

	public void setNonce_str(String nonce_str) {
		this.nonce_str = nonce_str;
	}

	public String getSign() {
		return sign;
	}

	public void setSign(String sign) {
		this.sign = sign;
	}

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	/**
	 * 校验并设置签名
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void checkAndSign(WechatConfig config) {
		if (StringUtils.isBlank(getAppid())) {
			this.setAppid(config.getAppId());
		}
		if (StringUtils.isBlank(this.getMch_id())) {
			this.setMch_id(config.getMchId());
		}
		if (StringUtils.isBlank(this.getNonce_str())) {
			this.setNonce_str(UUIdGenerator.uuid());
		}
		this.setSign(SignUtils.generateSign(this, this.getSign_type(), config.getMchKey()));
	}
}
