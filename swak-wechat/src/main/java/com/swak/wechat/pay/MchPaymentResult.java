package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

/**
 * 企业付款
 * @author root
 *
 */
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchPaymentResult {
	
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String return_code;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String return_msg;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String mch_appid;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String mchid;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String device_info;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String nonce_str;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String result_code;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String err_code;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String err_code_des;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String partner_trade_no;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String payment_no;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String payment_time;
	
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
	public String getReturn_msg() {
		return return_msg;
	}
	public void setReturn_msg(String return_msg) {
		this.return_msg = return_msg;
	}
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
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getErr_code() {
		return err_code;
	}
	public void setErr_code(String err_code) {
		this.err_code = err_code;
	}
	public String getErr_code_des() {
		return err_code_des;
	}
	public void setErr_code_des(String err_code_des) {
		this.err_code_des = err_code_des;
	}
	public String getPartner_trade_no() {
		return partner_trade_no;
	}
	public void setPartner_trade_no(String partner_trade_no) {
		this.partner_trade_no = partner_trade_no;
	}
	public String getPayment_no() {
		return payment_no;
	}
	public void setPayment_no(String payment_no) {
		this.payment_no = payment_no;
	}
	public String getPayment_time() {
		return payment_time;
	}
	public void setPayment_time(String payment_time) {
		this.payment_time = payment_time;
	}
	
	/**
	 * 是否支付成功
	 * @return
	 */
	public boolean isSuccess() {
		if("SUCCESS".equals(this.getReturn_code())
				&& "SUCCESS".equals(this.getResult_code())) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}
}
