package com.swak.mvc.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.google.common.io.Closeables;
import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

@SuppressWarnings("restriction")
public class JaxbMapper {

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
		Class<?> clazz = ClassUtils.getUserClass(root);
		return toXml(root, clazz);
	}

	/**
	 * Java Object->Xml with encoding.
	 */
	public static String toXml(Object root, Class<?> clazz) {
		Marshaller marshaller = null;
		try {
			StringWriter writer = new StringWriter();
			marshaller = createMarshaller(clazz);
			marshaller.marshal(root, writer);
			String xml = writer.toString();
			Closeables.close(writer, true);
			return xml;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Java Object->Xml with encoding.
	 */
	public static void toXml(Object root, OutputStream out) {
		Marshaller marshaller = null;
		try {
			marshaller = createMarshaller(ClassUtils.getUserClass(root));
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
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = createUnmarshaller(clazz);
			StringReader reader = new StringReader(xml);
			return (T) unmarshaller.unmarshal(reader);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Xml->Java Object.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T fromXml(InputStream xml, Class<T> clazz) {
		Unmarshaller unmarshaller = null;
		try {
			unmarshaller = createUnmarshaller(clazz);
			XMLReader xmlReader = XMLReaderFactory.createXMLReader();
			Source Source = new SAXSource(xmlReader, new InputSource(xml));
			return (T) unmarshaller.unmarshal(Source);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建Marshaller并设定encoding(可为null).
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Marshaller createMarshaller(Class<?> clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			Marshaller marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
			marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
                public void escape(char[] ac, int i, int j, boolean flag,Writer writer) throws IOException {
                writer.write( ac, i, j ); }
            });
			return marshaller;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 创建UnMarshaller.
	 * 线程不安全，需要每次创建或pooling。
	 */
	public static Unmarshaller createUnmarshaller(Class<?> clazz) {
		try {
			JAXBContext jaxbContext = getJaxbContext(clazz);
			Unmarshaller marshaller = jaxbContext.createUnmarshaller();
			return marshaller;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected static JAXBContext getJaxbContext(Class<?> clazz) {
		Assert.notNull(clazz, "'clazz' must not be null");
		JAXBContext jaxbContext = CONTEXT.get(clazz);
		if (jaxbContext == null) {
			try {
				jaxbContext = JAXBContext.newInstance(clazz);
				CONTEXT.putIfAbsent(clazz, jaxbContext);
			} catch (JAXBException ex) {
				throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz
						+ "]: " + ex.getMessage(), ex);
			}
		}
		return jaxbContext;
	}
}
