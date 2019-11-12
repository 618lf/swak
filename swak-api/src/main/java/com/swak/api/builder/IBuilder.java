package com.swak.api.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.swak.api.DocAnnotationConstants;
import com.swak.api.DocGlobalConstants;
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

	String PRIVATE = "private";
	String IGNORE_TAG = "ignore";
	String GET_MAPPING = "GetMapping";
	String POST_MAPPING = "PostMapping";
	String PUT_MAPPING = "PutMapping";
	String DELETE_MAPPING = "DeleteMapping";
	String REQUEST_MAPPING = "RequestMapping";
	String REQUEST_BODY = "RequestBody";
	String REQUEST_HERDER = "RequestHeader";
	String REQUEST_PARAM = "RequestParam";
	String JSON_CONTENT_TYPE = "application/json; charset=utf-8";
	String MULTIPART_TYPE = "multipart/form-data";
	String MAP_CLASS = "java.util.Map";
	String NO_COMMENTS_FOUND = "No comments found.";
	String VALID = "Valid";
	String PARAM = "param";
	String REQUIRED_PROP = "required";
	String SERIALIZE_PROP = "serialize";
	String NAME_PROP = "name";
	String VALUE_PROP = "value";
	String DEFAULT_VALUE_PROP = "defaultValue";

	/**
	 * 只处理 Controller
	 *
	 * @param cls
	 * @return
	 */
	default boolean isController(JavaClass cls) {
		List<JavaAnnotation> classAnnotations = cls.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getName();
			if (DocAnnotationConstants.SHORT_CONTROLLER.equals(annotationName)
					|| DocAnnotationConstants.SHORT_REST_CONTROLLER.equals(annotationName)
					|| DocGlobalConstants.REST_CONTROLLER_FULLY.equals(annotationName)
					|| DocGlobalConstants.CONTROLLER_FULLY.equals(annotationName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否请求方法
	 * 
	 * @param method
	 * @return
	 */
	default boolean isRequestMethod(JavaMethod method) {

		// 不能是 private 方法
		if (method.getModifiers().contains(PRIVATE)) {
			return false;
		}

		// 需要配置成为 request mapping
		List<JavaAnnotation> classAnnotations = method.getAnnotations();
		for (JavaAnnotation annotation : classAnnotations) {
			String annotationName = annotation.getType().getName();
			if (DocAnnotationConstants.SHORT_CONTROLLER.equals(annotationName)
					|| DocAnnotationConstants.SHORT_REST_CONTROLLER.equals(annotationName)
					|| DocGlobalConstants.REST_CONTROLLER_FULLY.equals(annotationName)
					|| DocGlobalConstants.CONTROLLER_FULLY.equals(annotationName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 是否是忽略的参数
	 * 
	 * @param parameter
	 * @return
	 */
	default boolean isIgnoreParams(JavaParameter parameter) {
		return false;
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
				pValue = value.indexOf(" ") > -1 ? value.substring(value.indexOf(' ') + 1)
						: DocGlobalConstants.NO_COMMENTS_FOUND;
			}
			paramTagMap.put(pName, pValue);
		}
		return paramTagMap;
	}

	/**
	 * 返回请求的地址
	 * 
	 * @return
	 */
	default String getUrl(final JavaMethod javaMethod) {
		return "";
	}

	/**
	 * 返回请求的地址
	 * 
	 * @return
	 */
	default String getMethod(final JavaMethod javaMethod) {
		return "";
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
}