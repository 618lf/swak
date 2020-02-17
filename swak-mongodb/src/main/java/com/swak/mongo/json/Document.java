package com.swak.mongo.json;

import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;

import com.swak.mongo.codec.BeanMaps;

/**
 * 挂名
 * 
 * @author lifeng
 */
public class Document extends org.bson.Document {
	public static final String ID_FIELD = "_id";
	public static final String _ID_FIELD = "id";
	private static final long serialVersionUID = 1L;

	public Document() {
		super();
	}

	public Document(final String key, final Object value) {
		super(key, value);
	}

	public <T> Document(T bean) {
		this(bean, true);
	}

	public <T> Document(T bean, boolean nullAble) {
		super(parseBean(bean, nullAble));
	}

	/**
	 * id 的转换
	 * 
	 * @param bean
	 * @return
	 */
	private static <T> Map<String, Object> parseBean(T bean, boolean nullAble) {
		Map<String, Object> values = BeanMaps.toCascadeMap(bean, nullAble);
		if (values.containsKey(_ID_FIELD)) {
			values.put(ID_FIELD, values.remove(_ID_FIELD));
		}
		return values;
	}

	@Override
	public <C> BsonDocument toBsonDocument(final Class<C> documentClass, final CodecRegistry codecRegistry) {
		return new BsonDocumentWrapper<Document>(this, codecRegistry.get(Document.class));
	}
}