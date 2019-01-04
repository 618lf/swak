package com.swak.utils;

import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

/**
 * Json 操作工具类
 * @author lifeng
 */
public class JsonMapper {

	//序列化
	private static SerializerFeature[] FEATURES = {SerializerFeature.DisableCircularReferenceDetect};
	
	/**
	 * json to List<java bean>
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> List<T> fromJsonToList(String json, Class<T> clazz){
		return JSON.parseArray(json, clazz);
	}
	
	/**
	 * json to java bean
	 * @param json
	 * @param clazz
	 * @return
	 */
	public static <T> T fromJson(String json, Class<T> clazz) {
		return JSON.parseObject(json, clazz);
	}
	
	/**
	 * java bean to json
	 * @param object
	 * @return
	 */
	public static String toJson(Object object) {
		return toJson(object, FEATURES);
	}
	
	/**
	 * java bean to json
	 * @param object
	 * @return
	 */
	public static String toJson(Object object, SerializerFeature[] features) {
		if (features == null || features.length == 0){
			features = new SerializerFeature[]{SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullListAsEmpty};
		}
		return toJSONString(object, JSON.DEFAULT_GENERATE_FEATURE, features);
	}
	
	/**
	 * copy from JSON 支持 long to string
	 * 
	 * @param object
	 * @param defaultFeatures
	 * @param features
	 * @return
	 */
	public static String toJSONString(Object object, int defaultFeatures, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter((Writer) null, defaultFeatures, features);

        try {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.getMapping().put(Long.class, longSerializer);
            serializer.write(object);
            return out.toString();
        } finally {
            out.close();
        }
    }
	
//	/**
//	 * 写入 outputStream
//	 * @param os
//	 * @param charset
//	 * @param object
//	 * @param config
//	 * @param filters
//	 * @param dateFormat
//	 * @param defaultFeatures
//	 * @param features
//	 * @return
//	 * @throws IOException
//	 */
//	public static final int writeJSONString(OutputStream os, //
//			Charset charset, //
//			Object object, //
//			SerializeConfig config, //
//			SerializeFilter[] filters, //
//			String dateFormat, //
//			int defaultFeatures, //
//			SerializerFeature... features) throws IOException {
//		SerializeWriter writer = new SerializeWriter(null, defaultFeatures,
//				features);
//
//		try {
//			JSONSerializer serializer = new JSONSerializer(writer, config);
//
//			if (dateFormat != null && dateFormat.length() != 0) {
//				serializer.setDateFormat(dateFormat);
//				serializer.config(SerializerFeature.WriteDateUseDateFormat,
//						true);
//			}
//
//			if (filters != null) {
//				for (SerializeFilter filter : filters) {
//					serializer.addFilter(filter);
//				}
//			}
//			
//			serializer.getMapping().put(Long.class, longSerializer);
//
//			serializer.write(object);
//
//			int len = writer.writeToEx(os, charset);
//			return len;
//		} finally {
//			writer.close();
//		}
//	}
	
	/**
	 * 支持 long 转string
	 */
	static ObjectSerializer longSerializer = new ObjectSerializer() {

		@Override
		public void write(JSONSerializer serializer, Object object,
				Object fieldName, Type fieldType, int features)
				throws IOException {
			SerializeWriter out = serializer.getWriter();
			if (object != null) {
				out.writeString(object.toString());
			} else {
				out.writeNull();
			}
		}
	};
}