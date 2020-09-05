package com.swak.utils.xml;

import java.io.OutputStream;
import java.io.Writer;

import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * cdata 转码问题
 * 
 * @author lifeng
 * @date 2020年9月5日 下午3:49:16
 */
public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter {

	public CDataXMLStreamWriter(XMLStreamWriter writer) {
		super(writer);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		if (text != null && text.startsWith("<![CDATA[") && text.endsWith("]]>")) {
			super.writeCData(text.substring(9, text.length() - 3));
			return;
		}
		super.writeCharacters(text);
	}

	@Override
	public void writeCharacters(char[] text, int start, int len) throws XMLStreamException {
		if (text != null && text.length > 12 && text[0] == '<' && text[1] == '!' && text[2] == '[' && text[3] == 'C'
				&& text[4] == 'D' && text[5] == 'A' && text[6] == 'T' && text[7] == 'A' && text[8] == '['
				&& text[text.length - 1] == '>' && text[text.length - 2] == ']' && text[text.length - 3] == ']') {
			super.writeCData(new String(text, 9, text.length - 12));
			return;
		}
		super.writeCharacters(text, start, len);
	}

	/**
	 * 安全输出
	 * 
	 * @param writer
	 * @return
	 * @throws JAXBException
	 */
	public static XMLStreamWriter createWriter(Writer writer) throws JAXBException {
		try {
			XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			return new CDataXMLStreamWriter(streamWriter);
		} catch (Exception e) {
			throw new JAXBException(e.getMessage());
		}
	}

	/**
	 * 安全输出
	 * 
	 * @param writer
	 * @return
	 * @throws JAXBException
	 */
	public static XMLStreamWriter createWriter(OutputStream writer) throws JAXBException {
		try {
			XMLStreamWriter streamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(writer);
			return new CDataXMLStreamWriter(streamWriter);
		} catch (Exception e) {
			throw new JAXBException(e.getMessage());
		}
	}
}
