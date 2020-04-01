package com.swak.wechat.message;

import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;

/**
 * 上报地理位置事件 EVENT为 LOCATION
 * <p>
 * 用户同意上报地理位置后，每次进入公众号会话时，都会在进入时上报地理位置，或在进入会话后每5秒上报一次地理位置， 公众号可以在公众平台网站中修改以上设置。
 * 上报地理位置时，微信会将上报地理位置事件推送到开发者填写的URL。
 *
 * @author rikky.cai
 */
public class EventMsgLocation extends AbstractEventMsg {

    private static final long serialVersionUID = 1L;

    /**
     * 地理位置纬度
     */
    @XmlElement(name = "Latitude")
    private String latitude;

    /**
     * 地理位置经度
     */
    @XmlElement(name = "Longitude")
    private String longitude;

    /**
     * 地理位置精度
     */
    @XmlElement(name = "Precision")
    private String precision;

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPrecision() {
        return precision;
    }

    public void setPrecision(String precision) {
        this.precision = precision;
    }

    @Override
    public String toString() {
		return "latitude:" + this.getLatitude() + "\n" +
				"longitude:" + this.getLongitude() + "\n" +
				"precision:" + this.getPrecision();
    }

    @Override
    public void read(Element element) {
        this.latitude = XmlParse.elementText(element, "Latitude");
        this.longitude = XmlParse.elementText(element, "Longitude");
        this.precision = XmlParse.elementText(element, "Precision");
    }
}
