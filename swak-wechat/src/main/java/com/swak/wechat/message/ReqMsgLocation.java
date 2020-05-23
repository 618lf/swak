package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

/**
 * 地理位置消息请求
 *
 * @author lifeng
 */
public class ReqMsgLocation extends AbstractReqMsg {

    private static final long serialVersionUID = 1L;

    private String location_X;
    private String location_Y;
    private String scale;
    private String label;

    public String getLocation_X() {
        return location_X;
    }

    public void setLocation_X(String locationX) {
        this.location_X = locationX;
    }

    public String getLocation_Y() {
        return location_Y;
    }

    public void setLocation_Y(String locationY) {
        this.location_Y = locationY;
    }

    public String getScale() {
        return scale;
    }

    public String getLabel() {
        return label;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void read(Element element) {
        super.read(element);
        this.location_X = XmlParse.elementText(element, "Location_X");
        this.location_Y = XmlParse.elementText(element, "Location_Y");
        this.scale = XmlParse.elementText(element, "Scale");
        this.label = XmlParse.elementText(element, "Label");
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("msgId:").append(this.getMsgId()).append("\n");
        msg.append("location_X:").append(this.getLocation_X()).append("\n");
        msg.append("location_Y:").append(this.getLocation_Y()).append("\n");
        msg.append("scale:").append(this.getScale()).append("\n");
        msg.append("label:").append(this.getLabel()).append("\n");
        return msg.toString();
    }
}
