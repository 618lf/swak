package com.swak.api.builder;

import com.swak.annotation.*;
import com.swak.doc.ApiReturn;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.StringUtils;
import com.thoughtworks.qdox.model.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;
import java.util.Map;

/**
 * 定义抽象的方法
 *
 * @author: lifeng
 * @date: 2020/3/28 15:17
 */
public interface IBuilder {

	List<String> CONTOLLERS = Lists.newArrayList(RestApi.class.getName(), RestPage.class.getName());
	List<String> REQUEST_MAPPINGS = Lists.newArrayList(GetMapping.class.getName(), PostMapping.class.getName(),
			RequestMapping.class.getName());
	List<String> IGNORE_PARAMS = Lists.newArrayList("HttpServerRequest", "HttpServerResponse", "RoutingContext",
			"Subject", "BindErrors");
	List<String> ANNO_PARAMS = Lists.newArrayList(Body.class.getName(), Json.class.getName(), Header.class.getName());
	List<String> VALID_PARAMS = Lists.newArrayList(Email.class.getName(), Length.class.getName(), Max.class.getName(),
			Min.class.getName(), NotNull.class.getName(), Phone.class.getName(), Regex.class.getName());
	int DEEP = 3;
	String CLASS_Object = "Object";
	String CLASS_Timestamp = "Timestamp";
	String CLASS_Date = "Date";
	String CLASS_Locale = "Locale";
	String FIELD_this$0 = "this$0";
	String FIELD_serialVersionUID = "serialVersionUID";
	String NAME_PROP = "name";
	String VALUE_PROP = "value";
	String MSG_PROP = "msg";
	String PATH_PROP = "path";
	String METHOD_PROP = "method";
	String PRIVATE = "private";
	String NO_COMMENTS_FOUND = "No comments found.";
	String PARAM = "param";
	String RETURN = "return";
	String VOID = "void";
	String MODEL = "Model";
	List<String> FILES = Lists.newArrayList("PlainFile", "MultipartFile");

	/**
	 * 是否响应XML
	 *
	 * @param cls java类
	 * @return 类注解
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default JavaAnnotation isResponseXml(JavaClass cls) {
		List<JavaAnnotation> classAnnotations = cls.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getCanonicalName();
			if (XmlRootElement.class.getName().equals(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * 是否 api
	 *
	 * @param cls java类
	 * @return 类注解
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default JavaAnnotation isController(JavaClass cls) {
		List<JavaAnnotation> classAnnotations = cls.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getCanonicalName();
			if (CONTOLLERS.contains(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * 是否请求方法
	 *
	 * @param method java方法
	 * @return 类注解
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default JavaAnnotation isRequestMethod(JavaMethod method) {

		// 不能是 private 方法
		if (method.getModifiers().contains(PRIVATE)) {
			return null;
		}

		// 需要配置成为 request mapping
		List<JavaAnnotation> classAnnotations = method.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getCanonicalName();
			if (REQUEST_MAPPINGS.contains(annotationName)) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * 是否忽略的参数
	 *
	 * @param parameter java参数
	 * @return 是否忽略
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isIgnoreParams(JavaParameter parameter) {
		String parameterType = parameter.getName();
		return IGNORE_PARAMS.contains(parameterType);
	}

	/**
	 * 是否基本类型
	 *
	 * @param type0 类型
	 * @return 是否基本类型
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isSimpleProperty(String type0) {
		String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1) : type0;
		type = type.toLowerCase();
		switch (type) {
		case "integer":
		case "long":
		case "int":
		case "double":
		case "float":
		case "short":
		case "bigdecimal":
		case "char":
		case "string":
		case "boolean":
		case "byte":
		case "java.sql.timestamp":
		case "java.util.date":
		case "java.time.localdatetime":
		case "localdatetime":
		case "localdate":
		case "java.time.localdate":
		case "java.math.bigdecimal":
		case "java.math.biginteger":
			return true;
		default:
			return false;
		}
	}

	/**
	 * 是否 Object
	 *
	 * @param type 类型
	 * @return 是否 Object
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isObject(String type) {
		switch (type) {
		case "java.lang.Object":
			return true;
		default:
			return false;
		}
	}

	/**
	 * 是否集合类型
	 *
	 * @param type 类型
	 * @return 是否集合类型
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isCollection(String type) {
		switch (type) {
		case "java.util.List":
		case "java.util.LinkedList":
		case "java.util.ArrayList":
		case "java.util.Set":
		case "java.util.TreeSet":
		case "java.util.HashSet":
		case "java.util.SortedSet":
		case "java.util.Collection":
		case "java.util.ArrayDeque":
		case "java.util.PriorityQueue":
			return true;
		default:
			return false;
		}
	}

	/**
	 * 是否 Map
	 *
	 * @param type 类型
	 * @return 是否 Map
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isMap(String type) {
		switch (type) {
		case "java.util.Map":
		case "java.util.SortedMap":
		case "java.util.TreeMap":
		case "java.util.LinkedHashMap":
		case "java.util.HashMap":
		case "java.util.concurrent.ConcurrentHashMap":
		case "java.util.Properties":
		case "java.util.Hashtable":
			return true;
		default:
			return false;
		}
	}

	/**
	 * 是否 数组
	 *
	 * @param type 类型
	 * @return 是否 数组
	 * @author lifeng
	 * @date 2020/3/28 15:18
	 */
	default boolean isArray(String type) {
		return type.contains("[]");
	}

	/**
	 * 参数说明
	 *
	 * @param javaMethod 方法
	 * @param tagName    参数
	 * @return 参数说明
	 * @author lifeng
	 * @date 2020/3/28 15:24
	 */
	default Map<String, String> getParamsComments(final JavaMethod javaMethod, final String tagName) {
		List<DocletTag> paramTags = javaMethod.getTagsByName(tagName);
		Map<String, String> paramTagMap = Maps.newHashMap();
		for (DocletTag docletTag : paramTags) {
			String value = docletTag.getValue();
			if (StringUtils.isEmpty(value)) {
				throw new RuntimeException("ERROR: #" + javaMethod.getName() + "() - bad @" + tagName + " javadoc from "
						+ javaMethod.getDeclaringClass().getCanonicalName() + ", must be add comment if you use it.");
			}
			String pName;
			String pValue;
			int idx = value.indexOf("\n");
			// existed \n
			if (idx > -1) {
				pName = value.substring(0, idx);
				pValue = value.substring(idx + 1);
			} else {
				pName = (!value.contains(" ")) ? value : value.substring(0, value.indexOf(" "));
				pValue = value.contains(" ") ? value.substring(value.indexOf(' ') + 1) : NO_COMMENTS_FOUND;
			}
			paramTagMap.put(pName, pValue);
		}
		return paramTagMap;
	}

	/**
	 * 处理 类型
	 *
	 * @param javaTypeName 需要处理的类型
	 * @return 处理后的类型
	 * @author lifeng
	 * @date 2020/3/28 15:26
	 */
	default String processTypeNameForParam(String javaTypeName) {
		if (javaTypeName.length() == 1) {
			return "object";
		}
		if (javaTypeName.contains("[]")) {
			return "array";
		}
		switch (javaTypeName) {
		case "java.lang.String":
		case "string":
		case "char":
			return "string";
		case "java.util.List":
		case "list":
			return "array";
		case "java.lang.Integer":
		case "integer":
		case "int":
			return "int32";
		case "short":
		case "java.lang.Short":
			return "int16";
		case "double":
			return "double";
		case "java.lang.Long":
		case "long":
			return "int64";
		case "java.lang.Float":
		case "float":
			return "float";
		case "bigdecimal":
		case "biginteger":
			return "number";
		case "java.lang.Boolean":
		case "boolean":
			return "boolean";
		case "map":
			return "map";
		case "java.util.Byte":
		case "byte":
			return "byte";
		case "date":
		case "localdatetime":
		case "localdate":
		case "multipartfile":
			return "file";
		default:
			return "object";
		}
	}

	/**
	 * 获得泛型
	 *
	 * @param typeName 类型名称
	 * @return 泛型
	 * @author lifeng
	 * @date 2020/3/28 15:30
	 */
	default String[] getSimpleGicName(String typeName) {
		if (typeName.contains("<")) {
			String pre = typeName.substring(0, typeName.indexOf("<"));
			if (isMap(pre)) {
				return getMapKeyValueType(typeName);
			}
			String type = typeName.substring(typeName.indexOf("<") + 1, typeName.lastIndexOf(">"));
			if (isCollection(pre)) {
				return type.split(" ");
			}
			return type.split(",");
		} else {
			return typeName.split(" ");
		}
	}

	default String[] getMapKeyValueType(String gName) {
		if (gName.contains("<")) {
			String[] arr = new String[2];
			String key = gName.substring(gName.indexOf("<") + 1, gName.indexOf(","));
			String value = gName.substring(gName.indexOf(",") + 1, gName.lastIndexOf(">"));
			arr[0] = key;
			arr[1] = value;
			return arr;
		} else {
			return new String[0];
		}
	}

	/**
	 * process return type
	 *
	 * @param fullyName fully name
	 * @return ApiReturn
	 */
	default ApiReturn processReturnType(String fullyName) {
		ApiReturn apiReturn = new ApiReturn();
		if (fullyName.startsWith("java.util.concurrent.CompletionStage")
				|| fullyName.startsWith("java.util.concurrent.CompletableFuture")) {
			if (fullyName.contains("<")) {
				String[] strings = getSimpleGicName(fullyName);
				String newFullName = strings[0];
				if (newFullName.contains("<")) {
					apiReturn.setGenericCanonicalName(newFullName);
					apiReturn.setSimpleName(newFullName.substring(0, newFullName.indexOf("<")));
				} else {
					apiReturn.setGenericCanonicalName(newFullName);
					apiReturn.setSimpleName(newFullName);
				}
			}
		} else {
			apiReturn.setGenericCanonicalName(fullyName);
			if (fullyName.contains("<")) {
				apiReturn.setSimpleName(fullyName.substring(0, fullyName.indexOf("<")));
			} else {
				apiReturn.setSimpleName(fullyName);
			}
		}
		return apiReturn;
	}
}