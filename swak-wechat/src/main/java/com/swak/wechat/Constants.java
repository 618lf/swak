package com.swak.wechat;

/**
 * 微信 相关的常量
 * 
 * @author lifeng
 */
public interface Constants {

	// 基本
	String BASE_URI = "https://api.weixin.qq.com";
	String MEDIA_URI = "http://file.api.weixin.qq.com";
	String QRCODE_DOWNLOAD_URI = "https://mp.weixin.qq.com";

	// 支付
	String MCH_URI_DOMAIN_API = "api.mch.weixin.qq.com";
	String MCH_URI_DOMAIN_API2 = "api2.mch.weixin.qq.com";
	String FAIL = "FAIL";
	String SUCCESS = "SUCCESS";
	String HMACSHA256 = "HMAC-SHA256";
	String MD5 = "MD5";

	String FIELD_SIGN = "sign";
	String FIELD_SIGN_TYPE = "sign_type";

	String MICROPAY_URL_SUFFIX = "/pay/micropay";
	String UNIFIEDORDER_URL_SUFFIX = "/pay/unifiedorder";
	String ORDERQUERY_URL_SUFFIX = "/pay/orderquery";
	String REVERSE_URL_SUFFIX = "/secapi/pay/reverse";
	String CLOSEORDER_URL_SUFFIX = "/pay/closeorder";
	String REFUND_URL_SUFFIX = "/secapi/pay/refund";
	String REFUNDQUERY_URL_SUFFIX = "/pay/refundquery";
	String DOWNLOADBILL_URL_SUFFIX = "/pay/downloadbill";
	String REPORT_URL_SUFFIX = "/payitil/report";
	String SHORTURL_URL_SUFFIX = "/tools/shorturl";
	String AUTHCODETOOPENID_URL_SUFFIX = "/tools/authcodetoopenid";
	String MMPAYMKTTRANSFERS_URL_SUFFIX = "/mmpaymkttransfers/promotion/transfers";

	// sandbox
	String SANDBOX_GET_SIGNKEY_SUFFIX = "/sandboxnew/pay/getsignkey";
	String SANDBOX_MICROPAY_URL_SUFFIX = "/sandboxnew/pay/micropay";
	String SANDBOX_UNIFIEDORDER_URL_SUFFIX = "/sandboxnew/pay/unifiedorder";
	String SANDBOX_ORDERQUERY_URL_SUFFIX = "/sandboxnew/pay/orderquery";
	String SANDBOX_REVERSE_URL_SUFFIX = "/sandboxnew/secapi/pay/reverse";
	String SANDBOX_CLOSEORDER_URL_SUFFIX = "/sandboxnew/pay/closeorder";
	String SANDBOX_REFUND_URL_SUFFIX = "/sandboxnew/secapi/pay/refund";
	String SANDBOX_REFUNDQUERY_URL_SUFFIX = "/sandboxnew/pay/refundquery";
	String SANDBOX_DOWNLOADBILL_URL_SUFFIX = "/sandboxnew/pay/downloadbill";
	String SANDBOX_REPORT_URL_SUFFIX = "/sandboxnew/payitil/report";
	String SANDBOX_SHORTURL_URL_SUFFIX = "/sandboxnew/tools/shorturl";
	String SANDBOX_AUTHCODETOOPENID_URL_SUFFIX = "/sandboxnew/tools/authcodetoopenid";
}