package com.swak.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.xml.sax.InputSource;

/**
 * 使用Jaxb2.0实现XML<->Java Object的Mapper.
 * 
 * 在创建时需要设定所有需要序列化的Root对象的Class. 特别支持Root对象是Collection的情形.
 * 
 * @author calvin
 * @version 2013-01-15
 */
public class JaxbMapper {

	private static SAXParserFactory FACTORY = SAXParserFactory.newInstance();
	private static ConcurrentMap<Class<?>, JAXBContext> CONTEXT = null;

	/**
	 * 池化
	 */
	static {
		CONTEXT = new ConcurrentHashMap<Class<?>, JAXBContext>();
	}

	/**
	 * Java Object->Xml without encoding.
	 */
	public static String toXml(Object root) {
		return toXml(root, root.getClass());
	}

	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, Class<?> clazz) {
		try {
			StringWriter writer = new StringWriter();
			Marshaller marshaller = createMarshaller(clazz);
			marshaller.marshal(root, writer);
			String xml = writer.toString();
			IOUtils.closeQuietly(writer);
			return xml;
		} catch (JAXBException e) {
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
		}
	}

	/**
	 * Java Object->Xml with encoding.
	 */
	public static void toXml(Object root, OutputStream out) {
		try {
			Marshaller marshaller = createMarshaller(root.getClass());
			marshaller.marshal(root, new StreamResult(out));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Xml->Java Object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(String xml, Class<T> clazz) {
		try {
			Unmarshaller unmarshaller = createUnmarshaller(clazz);
			StringReader reader = new StringReader(xml);
			return (T) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
		}
	}

	/**
	 * Xml->Java Object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(InputStream xml, Class<T> clazz) {
		try {
			Unmarshaller unmarshaller = createUnmarshaller(clazz);
			SAXParser xmlReader = FACTORY.newSAXParser();
			Source Source = new SAXSource(xmlReader.getXMLReader(), new InputSource(xml));
			return (T) unmarshaller.unmarshal(Source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建Marshaller并设定encoding(可为null). 线程不安全，需要每次创建或pooling。
	 */
	public static Marshaller createMarshaller(Class<?> clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			return marshaller;
		} catch (Exception e) {
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
		}
	}

	/**
	 * 创建UnMarshaller. 线程不安全，需要每次创建或pooling。
	 */
	public static Unmarshaller createUnmarshaller(Class<?> clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			Unmarshaller marshaller = jaxbContext.createUnmarshaller();
			return marshaller;
		} catch (Exception e) {
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
		}
	}

	protected static JAXBContext getJaxbContext(Class<?> clazz) {
		JAXBContext jaxbContext = CONTEXT.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz);
				CONTEXT.putIfAbsent(clazz, jaxbContext);
			} catch (JAXBException ex) {
				throw new RuntimeException(
						"Could not instantiate JAXBContext for class [" + clazz + "]: " + ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}
}