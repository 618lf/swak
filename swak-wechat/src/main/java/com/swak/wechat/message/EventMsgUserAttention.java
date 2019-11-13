package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.w3c.dom.Element;

import com.swak.utils.CDataAdapter;
import com.swak.utils.StringUtils;
import com.swak.utils.XmlParse;

/**
 * 用户关注事件
 * 
 * 为方便处理，将原消息中的subscribe，scan，unsubscribe合为一并处理了。
 * 
 * 1. 用户在关注与取消关注公众号事，微信会把这个事件推送到开发者填写的URL。 方便开发者给用户下发欢迎消息或者做帐号的解绑。
 * 此种情况下，事件值为subscribe或unsubscribe，其余各项为空。
 * 
 * 2. 扫描二维码事件，其事件值为subscribe或scan (eventKey 不为空)
 * 
 * 用户扫描带场景值二维码时，可能推送以下两种事件： 1.如果用户还未关注公众号，则用户可以关注公众号，关注后微信会将带场景值关注事件推送给开发者。
 * 2.如果用户已经关注公众号，则微信会将带场景值扫描事件推送给开发者。
 * 
 * 如果未关注，则EVENT 为 subscribe 如果已关注，则EVENT 为 scan
 *
 * @author rikky.cai
 * @qq:6687523
 * @Email:6687523@qq.com
 */
public class EventMsgUserAttention extends AbstractEventMsg {

	private static final long serialVersionUID = 1L;

	@XmlElement(name = "EventKey")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String eventKey;
	@XmlElement(name = "Ticket")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	private String ticket;

	public String getEventKey() {
		return eventKey;
	}

	public void setEventKey(String eventKey) {
		this.eventKey = eventKey;
	}

	public String getTicket() {
		return ticket;
	}

	public void setTicket(String ticket) {
		this.ticket = ticket;
	}

	@Override
	public void read(Element element) {
		this.eventKey = XmlParse.elementText(element, "EventKey");
		this.ticket = XmlParse.elementText(element, "Ticket");
	}

	/**
	 * 得到场景二维码 weixin bug 关注时居然推送 last_trade_no_XXX 这样额eventkey
	 * 
	 * @return
	 */
	public String getQrscene() {

		// 正常的扫锚关注事件
		if (StringUtils.isNotBlank(eventKey) && StringUtils.startsWith(eventKey, "qrscene_")) {
			return StringUtils.remove(eventKey, "qrscene_");
		}

		// weixin bug 关注时居然推送 last_trade_no_ 这样的eventkey， 系统不处理这样的事件
		else if (StringUtils.isNotBlank(eventKey) && !StringUtils.startsWith(eventKey, "last_trade_no_")) {
			return eventKey;
		}
		return null;
	}

	public String toString() {
		StringBuilder msg = new StringBuilder();
		msg.append("eventKey:").append(this.getEventKey()).append("\n");
		msg.append("ticket:").append(this.getTicket());
		return msg.toString();
	}
}
