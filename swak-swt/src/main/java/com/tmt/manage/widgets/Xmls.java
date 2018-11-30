package com.tmt.manage.widgets;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;

/**
 * 使用Jaxb2.0实现XML<->Java Object的Mapper.
 * 
 * 在创建时需要设定所有需要序列化的Root对象的Class. 特别支持Root对象是Collection的情形.
 * 
 * @author calvin
 * @version 2013-01-15
 */
@SuppressWarnings("restriction")
public class Xmls {

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
		Marshaller marshaller = null;
		try {
			StringWriter writer = new StringWriter();
			marshaller = createMarshaller(clazz);
			marshaller.marshal(root, writer);
			String xml = writer.toString();
			writer.close();
			return xml;
		} catch (Exception e) {
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
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
			throw new RuntimeException("Could not instantiate JAXBContext for class [" + clazz + "]: " + e.getMessage(),
					e);
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
			marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
			marshaller.setProperty(CharacterEscapeHandler.class.getName(), new CharacterEscapeHandler() {
				public void escape(char[] ac, int i, int j, boolean flag, Writer writer) throws IOException {
					writer.write(ac, i, j);
				}
			});
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