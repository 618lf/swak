package com.swak.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.List;

/**
 * Json 操作工具类
 *
 * @author: lifeng
 * @date: 2020/3/29 14:16
 */
public class JsonMapper {

    /**
     * 序列化
     */
    public static SerializerFeature[] FEATURES = {SerializerFeature.DisableCircularReferenceDetect};

    /**
     * json to List<java bean>
     *
     * @param json  json 字符
     * @param clazz 类
     * @return 结果
     */
    public static <T> List<T> fromJsonToList(String json, Class<T> clazz) {
        return JSON.parseArray(json, clazz);
    }

    /**
     * json to java bean
     *
     * @param json  json 字符
     * @param clazz 类
     * @return 结果
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return JSON.parseObject(json, clazz);
    }

    /**
     * java bean to json
     *
     * @param object 类型
     * @return json
     */
    public static String toJson(Object object) {
        return toJson(object, FEATURES);
    }

    /**
     * java bean to json
     *
     * @param object   类型
     * @param features 功能选项
     * @return json
     */
    public static String toJson(Object object, SerializerFeature[] features) {
        if (features == null || features.length == 0) {
            features = new SerializerFeature[]{SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullListAsEmpty};
        }
        return toJsonString(object, JSON.DEFAULT_GENERATE_FEATURE, features);
    }

    /**
     * copy from JSON 支持 long to string
     *
     * @param object          对象
     * @param defaultFeatures 默认选型
     * @param features        选型
     * @return JSON
     */
    public static String toJsonString(Object object, int defaultFeatures, SerializerFeature... features) {
        try (SerializeWriter out = new SerializeWriter(null, defaultFeatures, features)) {
            JSONSerializer serializer = new JSONSerializer(out);
            serializer.getMapping().put(Long.class, longSerializer);
            serializer.write(object);
            return out.toString();
        }
    }

    /**
     * 支持 long 转string
     */
    static ObjectSerializer longSerializer = (serializer, object, fieldName, fieldType, features) -> {
		SerializeWriter out = serializer.getWriter();
		if (object != null) {
			out.writeString(object.toString());
		} else {
			out.writeNull();
		}
	};
}