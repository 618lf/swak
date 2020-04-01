package com.swak.wechat.tmpmsg;

/**
 * 模板消息项
 *
 * @author: lifeng
 * @date: 2020/4/1 11:35
 */
public class TemplateMessageItem {

    private String value;
    private String color;

    public TemplateMessageItem(String value, String color) {
        super();
        this.value = value;
        this.color = color;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

}
