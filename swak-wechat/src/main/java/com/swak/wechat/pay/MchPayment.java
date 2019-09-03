package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.incrementer.UUIdGenerator;
import com.swak.utils.CDataAdapter;
import com.swak.utils.StringUtils;
import com.swak.wechat.WechatConfig;
import com.swak.wechat.WechatErrorException;
import com.swak.wechat.codec.SignUtils;

/**
 * 企业付款
 * @author root
 *
 */
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchPayment {
	
	@XmlElement
	private String mch_appid;
	@XmlElement
	private String mchid;
	@XmlElement
	private String device_info;
	@XmlElement
	private String nonce_str;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String sign;
	@XmlElement
	private String partner_trade_no;
	@XmlElement
	private String openid;
	@XmlElement
	private String check_name;
	@XmlElement
	private String re_user_name;
	@XmlElement
	private int    amount;
	@XmlElement
	private String desc;
	@XmlElement
	private String spbill_create_ip;
	
	public String getMch_appid() {
		return mch_appid;
	}
	public void setMch_appid(String mch_appid) {
		this.mch_appid = mch_appid;
	}
	public String getMchid() {
		return mchid;
	}
	public void setMchid(String mchid) {
		this.mchid = mchid;
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
	public String getPartner_trade_no() {
		return partner_trade_no;
	}
	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getCheck_name() {
		return check_name;
	}
	public void setCheck_name(String check_name) {
		this.check_name = check_name;
	}
	public String getRe_user_name() {
		return re_user_name;
	}
	public void setRe_user_name(String re_user_name) {
		this.re_user_name = re_user_name;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}
	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}
	
	/**
	 * 校验并设置签名
	 * 
	 * @param config
	 * @throws Exception 
	 */
	public void checkAndSign(WechatConfig config) {
		if (StringUtils.isBlank(getMch_appid())) {
			this.setMch_appid(config.getMchApp());
		}
		if (StringUtils.isBlank(this.getMchid())) {
			this.setMchid(config.getMchId());
		}
		if (StringUtils.isBlank(this.getNonce_str())) {
			this.setNonce_str(UUIdGenerator.uuid());
		}
		if (StringUtils.isBlank(this.getPartner_trade_no())) {
			throw new WechatErrorException("付款的必填参数未填写：商户订单号不能为空");
		}
		this.setSign(SignUtils.generateSign(this, null, config.getMchKey()));
	}
}
