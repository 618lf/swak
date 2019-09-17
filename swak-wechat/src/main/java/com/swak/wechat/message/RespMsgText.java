package com.swak.wechat.message;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.swak.utils.CDataAdapter;
import com.swak.wechat.Constants.RespType;

/**
 * 文本应答消息
 * 
 * @author lifeng
 * 
 */
@XmlRootElement(name="xml")
public class RespMsgText extends RespMsg {
	private static final long serialVersionUID = 1L;
	private String content;
	public RespMsgText() {}
	public RespMsgText(MsgHead req, String content) {
		super(req, RespType.text.name());
		this.content = content;
	}
	@XmlElement(name="Content")
	@XmlJavaTypeAdapter(CDataAdapter.class)
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}
