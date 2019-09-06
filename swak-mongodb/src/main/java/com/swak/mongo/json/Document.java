package com.swak.mongo.json;

import java.util.List;
import java.util.Map;

import org.bson.BsonDocument;
import org.bson.BsonDocumentWrapper;
import org.bson.codecs.configuration.CodecRegistry;
import org.springframework.beans.BeanUtils;

import com.swak.utils.Lists;
import com.swak.utils.Maps;

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
		super(parseBean(bean));
	}

	/**
	 * id 的转换
	 * 
	 * @param bean
	 * @return
	 */
	private static <T> Map<String, Object> parseBean(T bean) {
		Map<String, Object> values = _parseBean(bean);
		if (values.containsKey(_ID_FIELD)) {
			values.put(ID_FIELD, values.remove(_ID_FIELD));
		}
		return values;
	}

	/**
	 * id 的转换
	 * 
	 * @param bean
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private static <T> Map<String, Object> _parseBean(T bean) {
		Map<String, Object> values = Maps.toMap(bean);
		values.entrySet().forEach((e) -> {
			Object value = e.getValue();
			if (value != null && value instanceof List) {
				List<Object> os = (List<Object>) value;
				List<Object> _os = null;
				if (os != null && os.size() != 0) {
					Class<?> _type = os.get(0).getClass();
					if (BeanUtils.isSimpleProperty(_type) || _type.isAssignableFrom(List.class)
							|| _type.isAssignableFrom(Map.class)) {
						_os = os;
					} else {
						_os = Lists.newArrayList();
						for (Object o : os) {
							Map<String, Object> _o = _parseBean(o);
							_os.add(_o);
						}
					}
				}
				e.setValue(_os);
			}
		});
		return values;
	}
	
    @Override
    public <C> BsonDocument toBsonDocument(final Class<C> documentClass, final CodecRegistry codecRegistry) {
        return new BsonDocumentWrapper<Document>(this, codecRegistry.get(Document.class));
    }
}