package com.swak.api.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import com.swak.annotation.Body;
import com.swak.annotation.Email;
import com.swak.annotation.GetMapping;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.Length;
import com.swak.annotation.Max;
import com.swak.annotation.Min;
import com.swak.annotation.NotNull;
import com.swak.annotation.RestPage;
import com.swak.annotation.Phone;
import com.swak.annotation.PostMapping;
import com.swak.annotation.Regex;
import com.swak.annotation.RequestMapping;
import com.swak.annotation.RestApi;
import com.swak.doc.ApiReturn;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * 抽象的方法
 * 
 * @author lifeng
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
	 * Controller
	 *
	 * @param cls
	 * @return
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
	 * Controller
	 *
	 * @param cls
	 * @return
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
	 * @param method
	 * @return
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
	 * 是否是忽略的参数
	 * 
	 * @param parameter
	 * @return
	 */
	default boolean isIgnoreParams(JavaParameter parameter) {
		String parameterType = parameter.getName();
		if (IGNORE_PARAMS.contains(parameterType)) {
			return true;
		}
		return false;
	}

	/**
	 * 是否基本类型
	 * 
	 * @param type0
	 * @return
	 */
	default boolean isSimpleProperty(String type0) {
		String type = type0.contains("java.lang") ? type0.substring(type0.lastIndexOf(".") + 1, type0.length()) : type0;
		type = type.toLowerCase();
		switch (type) {
		case "integer":
			return true;
		case "int":
			return true;
		case "long":
			return true;
		case "double":
			return true;
		case "float":
			return true;
		case "short":
			return true;
		case "bigdecimal":
			return true;
		case "char":
			return true;
		case "string":
			return true;
		case "boolean":
			return true;
		case "byte":
			return true;
		case "java.sql.timestamp":
			return true;
		case "java.util.date":
			return true;
		case "java.time.localdatetime":
			return true;
		case "localdatetime":
			return true;
		case "localdate":
			return true;
		case "java.time.localdate":
			return true;
		case "java.math.bigdecimal":
			return true;
		case "java.math.biginteger":
			return true;
		default:
			return false;
		}
	}

	/**
	 * validate java collection
	 *
	 * @param type
	 *            java typeName
	 * @return boolean
	 */
	default boolean isCollection(String type) {
		switch (type) {
		case "java.util.List":
			return true;
		case "java.util.LinkedList":
			return true;
		case "java.util.ArrayList":
			return true;
		case "java.util.Set":
			return true;
		case "java.util.TreeSet":
			return true;
		case "java.util.HashSet":
			return true;
		case "java.util.SortedSet":
			return true;
		case "java.util.Collection":
			return true;
		case "java.util.ArrayDeque":
			return true;
		case "java.util.PriorityQueue":
			return true;
		default:
			return false;
		}
	}

	/**
	 * Check if it is an map
	 *
	 * @param type
	 *            java type
	 * @return boolean
	 */
	default boolean isMap(String type) {
		switch (type) {
		case "java.util.Map":
			return true;
		case "java.util.SortedMap":
			return true;
		case "java.util.TreeMap":
			return true;
		case "java.util.LinkedHashMap":
			return true;
		case "java.util.HashMap":
			return true;
		case "java.util.concurrent.ConcurrentHashMap":
			return true;
		case "java.util.Properties":
			return true;
		case "java.util.Hashtable":
			return true;
		default:
			return false;
		}
	}

	/**
	 * check array
	 *
	 * @param type
	 *            type name
	 * @return boolean
	 */
	default boolean isArray(String type) {
		return type.contains("[]");
	}

	/**
	 * obtain params comments
	 *
	 * @param javaMethod
	 *            JavaMethod
	 * @param tagName
	 *            java comments tag
	 * @param className
	 *            class name
	 * @return Map
	 */
	default Map<String, String> getParamsComments(final JavaMethod javaMethod, final String tagName) {
		List<DocletTag> paramTags = javaMethod.getTagsByName(tagName);
		Map<String, String> paramTagMap = new HashMap<>();
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
				pName = (value.indexOf(" ") > -1) ? value.substring(0, value.indexOf(" ")) : value;
				pValue = value.indexOf(" ") > -1 ? value.substring(value.indexOf(' ') + 1) : NO_COMMENTS_FOUND;
			}
			paramTagMap.put(pName, pValue);
		}
		return paramTagMap;
	}

	/**
	 * 处理 类型
	 * 
	 * @param javaTypeName
	 * @return
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
			return "string";
		case "string":
			return "string";
		case "char":
			return "string";
		case "java.util.List":
			return "array";
		case "list":
			return "array";
		case "java.lang.Integer":
			return "int32";
		case "integer":
			return "int32";
		case "int":
			return "int32";
		case "short":
			return "int16";
		case "java.lang.Short":
			return "int16";
		case "double":
			return "double";
		case "java.lang.Long":
			return "int64";
		case "long":
			return "int64";
		case "java.lang.Float":
			return "float";
		case "float":
			return "float";
		case "bigdecimal":
			return "number";
		case "biginteger":
			return "number";
		case "java.lang.Boolean":
			return "boolean";
		case "boolean":
			return "boolean";
		case "java.util.Byte":
			return "string";
		case "byte":
			return "string";
		case "map":
			return "map";
		case "date":
			return "string";
		case "localdatetime":
			return "string";
		case "localdate":
			return "string";
		case "multipartfile":
			return "file";
		default:
			return "object";
		}
	}

	/**
	 * 
	 * 
	 * @param returnType
	 * @return
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
	 * @param fullyName
	 *            fully name
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