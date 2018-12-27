package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.StringUtils;
import com.swak.wechat.WechatConfig;
import com.swak.wechat.WechatErrorException;
import com.swak.wechat.codec.SignUtils;

/**
 * 退款查询
 * 
 * @author Yi
 * 
 */

@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class Refundquery {

	@XmlElement
	private String appid;
	
	@XmlElement
	private String mch_id;
	
	@XmlElement
	private String device_info;
	
	@XmlElement
	private String nonce_str;
	
	@XmlElement
	private String sign;
	
	@XmlElement
	private String sign_type;
	
	@XmlElement
	private String transaction_id;
	
	@XmlElement
	private String out_trade_no;
	
	@XmlElement
	private String out_refund_no;
	
	@XmlElement
	private String refund_id;

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

	public String getDevice_info() {
		return device_info;
	}

	public void setDevice_info(String device_info) {
		this.device_info = device_info;
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

	public String getTransaction_id() {
		return transaction_id;
	}

	public void setTransaction_id(String transaction_id) {
		this.transaction_id = transaction_id;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getOut_refund_no() {
		return out_refund_no;
	}

	public void setOut_refund_no(String out_refund_no) {
		this.out_refund_no = out_refund_no;
	}

	public String getRefund_id() {
		return refund_id;
	}

	public void setRefund_id(String refund_id) {
		this.refund_id = refund_id;
	}
	
	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	/**
	 * 校验并设置签名
	 * 
	 * @param config
	 * @throws Exception 
	 */
	public void checkAndSign(WechatConfig config) throws Exception {
		if (StringUtils.isBlank(getAppid())) {
			this.setAppid(config.getMchApp());
		}
		if (StringUtils.isBlank(this.getMch_id())) {
			this.setMch_id(config.getMchId());
		}
		if (StringUtils.isBlank(this.getNonce_str())) {
			this.setNonce_str(UUIdGenerator.uuid());
		}
		if (StringUtils.isBlank(this.getOut_refund_no()) && StringUtils.isBlank(this.getTransaction_id())) {
			throw new WechatErrorException("退款的必填参数未填写");
		}
		this.setSign(SignUtils.generateSign(this, this.getSign_type(), config.getMchKey()));
	}
}
