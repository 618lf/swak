package com.swak.wechat.pay;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;

/**
 * 红包付款结果
 * @author root
 */
@XmlRootElement(name="xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class MchRedpackResult {

	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String return_code;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String return_msg;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String sign;
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
	private String mch_billno;
	@XmlElement
	private String mch_id;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String wxappid;
	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String re_openid;
	@XmlElement
	private int total_amount;
	@XmlElement
	private int send_time;
	@XmlElement
	private String send_listid;
	
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
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
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
	public String getMch_billno() {
		return mch_billno;
	}
	public void setMch_billno(String mch_billno) {
		this.mch_billno = mch_billno;
	}
	public String getMch_id() {
		return mch_id;
	}
	public void setMch_id(String mch_id) {
		this.mch_id = mch_id;
	}
	public String getWxappid() {
		return wxappid;
	}
	public void setWxappid(String wxappid) {
		this.wxappid = wxappid;
	}
	public String getRe_openid() {
		return re_openid;
	}
	public void setRe_openid(String re_openid) {
		this.re_openid = re_openid;
	}
	public int getTotal_amount() {
		return total_amount;
	}
	public void setTotal_amount(int total_amount) {
		this.total_amount = total_amount;
	}
	public int getSend_time() {
		return send_time;
	}
	public void setSend_time(int send_time) {
		this.send_time = send_time;
	}
	public String getSend_listid() {
		return send_listid;
	}
	public void setSend_listid(String send_listid) {
		this.send_listid = send_listid;
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