package com.swak.wechat.message;

import com.swak.utils.CDataAdapter;
import com.swak.utils.XmlParse;
import org.w3c.dom.Element;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * 点击菜单跳转链接时的事件推送
 *
 * @author: lifeng
 * @date: 2020/4/1 11:23
 */
public class MenuEventMsgLocationSelect extends MenuEventMsg {

    private static final long serialVersionUID = 1L;

    /**
     * 地理位置维度
     */
    @XmlElement(name = "Location_X")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String location_X;

    /**
     * 地理位置经度
     */
    @XmlElement(name = "Location_Y")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String location_Y;

    /**
     * 地图缩放大小
     */
    @XmlElement(name = "Scale")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String scale;

    /**
     * 地理位置信息
     */
    @XmlElement(name = "Label")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String label;

    @XmlElement(name = "Poiname")
    @XmlJavaTypeAdapter(CDataAdapter.class)
    private String poiname;

    public String getLocation_X() {
        return location_X;
    }

    public void setLocation_X(String location_X) {
        this.location_X = location_X;
    }

    public String getLocation_Y() {
        return location_Y;
    }

    public void setLocation_Y(String location_Y) {
        this.location_Y = location_Y;
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getPoiname() {
        return poiname;
    }

    public void setPoiname(String poiname) {
        this.poiname = poiname;
    }

    @Override
    public void read(Element element) {
        super.read(element);
        this.location_X = XmlParse.elementText(element, "Location_X");
        this.location_Y = XmlParse.elementText(element, "Location_Y");
        this.scale = XmlParse.elementText(element, "Scale");
        this.label = XmlParse.elementText(element, "Label");
        this.poiname = XmlParse.elementText(element, "Poiname");
    }
}
