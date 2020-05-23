package com.swak.utils;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * 基于java 的 xml解析的简单封装
 *
 * @author: lifeng
 * @date: 2020/3/29 15:21
 */
public class XmlParse {

    /**
     * 可以是单例
     */
    private static DocumentBuilderFactory INSTANCE = DocumentBuilderFactory.newInstance();

    private XmlParse() {
    }

    /**
     * 将xmlStr转为 w3c Document
     * 必须每次 newDocumentBuilder(); 才能保证线程安全
     *
     * @param xmlStr xml字符串
     * @return Document
     */
    public static Document parse(String xmlStr) {
        DocumentBuilderFactory dbf = getDocumentBuilderFactory();

        StringReader sr = null;
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            sr = new StringReader(xmlStr);
            InputSource is = new InputSource(sr);
            return db.parse(is);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(sr);
        }
    }

    /**
     * 获取xml节点中的文本
     *
     * @param element 节点
     * @param name    名称
     * @return 文本
     */
    public static String elementText(Element element, String name) {
        NodeList node = element.getElementsByTagName(name);
        if (node.getLength() == 0) {
            return null;
        }
        return node.item(0).getTextContent();
    }

    private static DocumentBuilderFactory getDocumentBuilderFactory() {
        return INSTANCE;
    }
}