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
 * 统一支付请求参数
 * 
 * @author Yi
 *
 */
@XmlRootElement(name = "xml")
@XmlAccessorType(XmlAccessType.FIELD)
public class Unifiedorder {

	@XmlElement
	private String appid;

	@XmlElement
	private String mch_id;

	@XmlElement
	private String device_info;

	@XmlElement
	private String nonce_str;

	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String sign;

	@XmlElement
	private String sign_type;

	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String body;

	@XmlElement
	@XmlJavaTypeAdapter(value = CDataAdapter.class)
	private String attach;

	@XmlElement
	private String out_trade_no;

	@XmlElement
	private String fee_type;

	@XmlElement
	private String total_fee;

	@XmlElement
	private String spbill_create_ip;

	@XmlElement
	private String time_start;

	@XmlElement
	private String time_expire;

	@XmlElement
	private String goods_tag;

	@XmlElement
	private String notify_url;

	@XmlElement
	private String trade_type;

	@XmlElement
	private String openid;

	@XmlElement
	private String product_id;

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

	public String getSign_type() {
		return sign_type;
	}

	public void setSign_type(String sign_type) {
		this.sign_type = sign_type;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getAttach() {
		return attach;
	}

	public void setAttach(String attach) {
		this.attach = attach;
	}

	public String getOut_trade_no() {
		return out_trade_no;
	}

	public void setOut_trade_no(String out_trade_no) {
		this.out_trade_no = out_trade_no;
	}

	public String getFee_type() {
		return fee_type;
	}

	public void setFee_type(String fee_type) {
		this.fee_type = fee_type;
	}

	public String getTotal_fee() {
		return total_fee;
	}

	public void setTotal_fee(String total_fee) {
		this.total_fee = total_fee;
	}

	public String getSpbill_create_ip() {
		return spbill_create_ip;
	}

	public void setSpbill_create_ip(String spbill_create_ip) {
		this.spbill_create_ip = spbill_create_ip;
	}

	public String getTime_start() {
		return time_start;
	}

	public void setTime_start(String time_start) {
		this.time_start = time_start;
	}

	public String getTime_expire() {
		return time_expire;
	}

	public void setTime_expire(String time_expire) {
		this.time_expire = time_expire;
	}

	public String getGoods_tag() {
		return goods_tag;
	}

	public void setGoods_tag(String goods_tag) {
		this.goods_tag = goods_tag;
	}

	public String getNotify_url() {
		return notify_url;
	}

	public void setNotify_url(String notify_url) {
		this.notify_url = notify_url;
	}

	public String getTrade_type() {
		return trade_type;
	}

	public void setTrade_type(String trade_type) {
		this.trade_type = trade_type;
	}

	public String getOpenid() {
		return openid;
	}

	public void setOpenid(String openid) {
		this.openid = openid;
	}

	public String getProduct_id() {
		return product_id;
	}

	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}

	/**
	 * 校验并设置签名
	 * 
	 * @param config
	 * @throws Exception
	 */
	public void checkAndSign(WechatConfig config) {
		if (StringUtils.isBlank(this.getNotify_url())) {
			this.setNotify_url(config.getNotifyUrl());
		}
		if (StringUtils.isBlank(getAppid())) {
			this.setAppid(config.getAppId());
		}
		if (StringUtils.isBlank(this.getMch_id())) {
			this.setMch_id(config.getMchId());
		}
		if (StringUtils.isBlank(this.getNonce_str())) {
			this.setNonce_str(UUIdGenerator.uuid());
		}
		if (StringUtils.isBlank(this.getBody()) || StringUtils.isBlank(this.getOut_trade_no())
				|| StringUtils.isBlank(this.getTotal_fee()) || StringUtils.isBlank(this.getTrade_type())
				|| StringUtils.isBlank(this.getSpbill_create_ip())) {
			throw new WechatErrorException("支付是必填参数未填写");
		}
		this.setSign(SignUtils.generateSign(this, this.getSign_type(), config.getMchKey()));
	}
}
