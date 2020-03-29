package com.swak.utils;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * XMl 包裹
 *
 * @author: lifeng
 * @date: 2020/3/29 13:55
 */
public class CDataAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String v) {
        return v;
    }

    @Override
    public String marshal(String v) {
        return "<![CDATA[" + v + "]]>";
    }
}