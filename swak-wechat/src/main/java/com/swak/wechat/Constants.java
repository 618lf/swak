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
	String ERROR = "ERROR";
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
	
	
	// message
	/**
	 * 用户请求消息类型
	 */
	public static enum ReqType {
		text, //
		image, //
		link, //
		location, //
		video, //
		shortvideo, //
		voice, //
		event, //
		device_text, //
		device_event, //
		device_status, //
		hardware, //
		transfer_customer_service//
	}
	
	/**
	 * 事件消息类型包括，菜单上的的
	 */
	public static enum EventType {
		subscribe, // 关注
		unsubscribe, // 取消关注
		SCAN, // 扫码
		LOCATION, // 定位
		CLICK, // 菜单点击
		VIEW, // 菜单浏览
		scancode_waitmsg, //
		scancode_push, // 扫码推送
		location_select, //
		pic_weixin, //
		pic_photo_or_album, //
		pic_sysphoto, //
		MASSSENDJOBFINISH, // 高级群发接口
		TEMPLATESENDJOBFINISH, // 模板消息发送接口
		enter_agent, //
		card_pass_check, //
		card_not_pass_check, //
		user_get_card, //
		user_del_card, //
		user_consume_card, //
		user_pay_from_pay_cell, //
		user_view_card, //
		user_enter_session_from_card, //
		card_sku_remind, // 库存报警
		kf_create_session, // 客服接入会话
		kf_close_session, // 客服关闭会话
		kf_switch_session, // 客服转接会话
		poi_check_notify, // 门店审核事件推送
		submit_membercard_user_info // 接收会员信息事件推送
	}
	
	/**
	 * 应答消息类型定义。
	 * 
	 * @author cailx
	 * 
	 */
	public static enum RespType {
		image, // 图片消息
		music, // 音乐消息
		news, // 图文消息（点击跳转到外链）
		text, // 文本消息
		video, // 视频消息
		voice, // 语音消息
		mpnews, // 图文消息（点击跳转到图文消息页面）
		file, // 发送文件（CP专用）
		wxcard, // 卡券消息
		transfer_customer_service // 客服
	}
	
	/**
	 * 群发接口消息类型
	 * 
	 * @author lifeng
	 */
	public static enum MassType {
		mpnews, //
		text, //
		voice, //
		image, //
		mpvideo, //
	}
	
	/**
	 * 应答消息类型定义。
	 * 
	 * @author cailx
	 * 
	 */
	public static enum KefuType {
		image, // 图片消息
		music, // 音乐消息
		news, // 图文消息（点击跳转到外链）
		text, // 文本消息
		video, // 视频消息
		voice, // 语音消息
		wxcard, // 卡券消息
	}
	
	/**
	 * 临时素材类型
	 * 
	 * @author lifeng
	 */
	public static enum MediaType {
		image, // 图片消息
		video, // 视频消息
		voice, // 语音消息
		thumb, // 缩略图
	}
}