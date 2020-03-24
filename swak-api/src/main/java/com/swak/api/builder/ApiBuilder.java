package com.swak.api.builder;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.annotation.ApiDoc;
import com.swak.annotation.Header;
import com.swak.annotation.Json;
import com.swak.annotation.RequestMethod;
import com.swak.annotation.Valid;
import com.swak.api.ApiConfig;
import com.swak.api.mock.MockUtils;
import com.swak.doc.Api;
import com.swak.doc.ApiHeader;
import com.swak.doc.ApiMethod;
import com.swak.doc.ApiParam;
import com.swak.doc.ApiReturn;
import com.swak.entity.Result;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.utils.router.RouterUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;

/**
 * 构建 API
 * 
 * @author lifeng
 */
public class ApiBuilder implements IBuilder {

	private JavaProjectBuilder builder;
	private Collection<JavaClass> javaClasses;
	private Map<String, JavaClass> javaFilesMap = new ConcurrentHashMap<>();
	private ApiConfig config;

	/**
	 * 创建 api
	 * 
	 * @param config
	 */
	public ApiBuilder(ApiConfig config) {
		this.config = config;
		this.loadJavaFiles();
	}

	/**
	 * 加载 java 文件
	 */
	private void loadJavaFiles() {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		for (String path : this.config.getSourcePaths()) {
			if (StringUtils.isBlank(path)) {
				continue;
			}
			if (StringUtils.startsWith(path, "/")) {
				path = StringUtils.removeStart(path, "/");
			}
			builder.addSourceTree(new File(path));
		}
		this.builder = builder;
		this.javaClasses = builder.getClasses();
		for (JavaClass cls : javaClasses) {
			javaFilesMap.put(cls.getFullyQualifiedName(), cls);
		}
	}

	/**
	 * 创建 Api
	 * 
	 * @return
	 */
	public List<Api> build() {
		List<Api> apis = Lists.newArrayList();
		for (JavaClass cls : javaClasses) {
			if (isController(cls) != null && this.config.isPackageMatch(cls.getCanonicalName())) {
				apis.add(this.buildApi(cls));
			}
		}
		return apis;
	}

	/**
	 * 创建API文件
	 * 
	 * @param cls
	 * @param order
	 * @return
	 */
	private Api buildApi(JavaClass cls) {

		// api doc
		Api api = new Api().setName(cls.getName()).setDesc(cls.getComment());

		// api mapping
		JavaAnnotation classMapping = this.isController(cls);

		// method mapping
		List<JavaMethod> methods = cls.getMethods();

		for (JavaMethod method : methods) {

			// must has method mapping
			JavaAnnotation methodMapping = this.isRequestMethod(method);
			if (methodMapping == null) {
				continue;
			}

			// must has comment
			if (StringUtils.isEmpty(method.getComment())) {
				throw new RuntimeException(
						"Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
			}

			// method name and comment
			ApiMethod apiMethod = new ApiMethod().setDesc(method.getComment()).setName(method.getName());

			// url and method
			try {
				apiMethod = this.buildRouter(apiMethod, classMapping, methodMapping);
			} catch (Exception e) {
				throw new RuntimeException(
						"Unable to build Router for method " + method.getName() + " in " + cls.getCanonicalName(), e);
			}

			// 请求头
			try {
				apiMethod = this.buildRequestHeaders(apiMethod, method);
			} catch (Exception e) {
				throw new RuntimeException(
						"Unable to build Headers for method " + method.getName() + " in " + cls.getCanonicalName(), e);
			}

			// 请求参数
			try {
				apiMethod = this.buildRequestParams(apiMethod, method);
			} catch (Exception e) {
				throw new RuntimeException("Unable to build Request Params for method " + method.getName() + " in "
						+ cls.getCanonicalName(), e);
			}

			// 响应参数
			try {
				apiMethod = this.buildReturnParams(apiMethod, method);
			} catch (Exception e) {
				throw new RuntimeException(
						"Unable to build Return for method " + method.getName() + " in " + cls.getCanonicalName(), e);
			}

			// save api method
			api.addApiMethod(apiMethod);

		}

		return api;
	}

	/**
	 * 创建 url 和 method
	 * 
	 * @param apiMethod
	 * @param classMapping
	 * @param methodMapping
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private ApiMethod buildRouter(ApiMethod apiMethod, JavaAnnotation classMapping, JavaAnnotation methodMapping) {

		// urls
		Object patterns1 = classMapping.getNamedParameter(PATH_PROP);
		Object patterns2 = methodMapping.getNamedParameter(VALUE_PROP);

		List<String> ps1 = Lists.newArrayList();
		if (patterns1 instanceof String) {
			ps1.add(StringUtils.removeQuotes(String.valueOf(patterns1)));
		} else {
			List<Object> _patterns1 = (List<Object>) patterns1;
			for (Object p : _patterns1) {
				ps1.add(StringUtils.removeQuotes(String.valueOf(p)));
			}
		}

		List<String> ps2 = Lists.newArrayList();
		if (patterns2 instanceof String) {
			ps2.add(StringUtils.removeQuotes(String.valueOf(patterns2)));
		} else {
			List<Object> _patterns2 = (List<Object>) patterns2;
			for (Object p : _patterns2) {
				ps1.add(StringUtils.removeQuotes(String.valueOf(p)));
			}
		}

		List<String> result = Lists.newArrayList();
		if (ps1.size() != 0 && ps2.size() != 0) {
			for (String pattern1 : ps1) {
				for (String pattern2 : ps2) {
					result.add(RouterUtils.combine(pattern1, pattern2));
				}
			}
		} else if (ps1.size() != 0) {
			result = Lists.newArrayList(ps1);
		} else if (ps2.size() != 0) {
			result = Lists.newArrayList(ps2);
		} else {
			result.add(StringUtils.EMPTY);
		}

		// method
		String classMethod = StringUtils.defaultString((String) classMapping.getNamedParameter(METHOD_PROP),
				RequestMethod.ALL.name());
		String methodMethod = methodMapping.getType().getCanonicalName();
		methodMethod = REQUEST_MAPPINGS.get(0).equals(methodMethod) ? RequestMethod.GET.name()
				: (REQUEST_MAPPINGS.get(1).equals(methodMethod) ? RequestMethod.POST.name() : RequestMethod.ALL.name());
		String method = RequestMethod.ALL.name().equals(classMethod) ? methodMethod : classMethod;
		method = RequestMethod.ALL.name().equals(method) ? null : method;
		return apiMethod.setUrls(result).setMethod(method);
	}

	/**
	 * 创建 请求头
	 * 
	 * @param cls
	 * @return
	 */
	private ApiMethod buildRequestHeaders(ApiMethod apiMethod, final JavaMethod method) {
		Map<String, String> paramMap = this.getParamsComments(method, PARAM);
		List<ApiHeader> apiReqHeaders = Lists.newArrayList();
		for (JavaParameter javaParameter : method.getParameters()) {
			String paramName = javaParameter.getName();
			List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
			for (JavaAnnotation annotation : javaAnnotations) {
				String annotationName = annotation.getType().getCanonicalName();
				if (Header.class.getName().equals(annotationName)) {
					ApiHeader apiReqHeader = new ApiHeader().setName(paramName).setDesc(paramMap.get(paramName));
					String typeName = javaParameter.getType().getValue().toLowerCase();
					apiReqHeader.setType(this.processTypeNameForParam(typeName));
					apiReqHeaders.add(apiReqHeader);
					break;
				}
			}
		}
		return apiMethod.setRequestHeaders(apiReqHeaders);
	}

	/**
	 * 创建请求参数
	 * 
	 * @param javaMethod
	 * @param className
	 * @return
	 */
	private ApiMethod buildRequestParams(ApiMethod apiMethod, final JavaMethod javaMethod) {
		Map<String, String> paramTagMap = this.getParamsComments(javaMethod, PARAM);
		List<JavaParameter> parameterList = javaMethod.getParameters();
		if (parameterList.size() < 1) {
			return apiMethod;
		}
		List<ApiParam> paramList = new ArrayList<>();
		for (JavaParameter parameter : parameterList) {
			String paramName = parameter.getName();
			String simpleName = parameter.getType().getValue().toLowerCase();
			String fullTypeName = parameter.getType().getFullyQualifiedName();

			/**
			 * 忽略一些系统内置的参数
			 */
			if (this.isIgnoreParams(parameter)) {
				continue;
			}

			/**
			 * 必须有备注说明
			 */
			if (!paramTagMap.containsKey(paramName)) {
				throw new RuntimeException(
						"ERROR: Unable to find javadoc @param for actual param \"" + paramName + "\" in method "
								+ javaMethod.getName() + " from " + javaMethod.getDeclaringClass().getCanonicalName());
			}
			String comment = paramTagMap.get(paramName);
			if (StringUtils.isEmpty(comment)) {
				comment = NO_COMMENTS_FOUND;
			}

			/**
			 * 注解： Body, Json, Header
			 */
			List<JavaAnnotation> annotations = parameter.getAnnotations();
			if (annotations != null && annotations.size() > 0
					&& !Valid.class.getName().equals(annotations.get(0).getType().getCanonicalName())) {
				for (JavaAnnotation annotation : annotations) {
					String annotationName = annotation.getType().getCanonicalName();
					if (ANNO_PARAMS.contains(annotationName)) {
						ApiParam param = ApiParam.of().setField(paramName)
								.setType(this.processTypeNameForParam(simpleName)).setDesc(comment)
								.setJson(Json.class.getName().equals(annotationName));
						param.setValue(
								MockUtils.getValueByTypeAndName(parameter.getType().getValue(), parameter.getName()));
						paramList.add(param);
					}
				}
			}

			/**
			 * 简单类型: 参数不处理：list、map、[]
			 */
			else if (this.isSimpleProperty(simpleName) || this.isMap(simpleName) || this.isCollection(simpleName)
					|| this.isArray(simpleName)) {
				ApiParam param = ApiParam.of().setField(paramName).setType(this.processTypeNameForParam(simpleName))
						.setDesc(comment);
				param.setValue(MockUtils.getValueByTypeAndName(parameter.getType().getValue(), parameter.getName()));
				paramList.add(param);
			}

			/**
			 * 对象类型: 对象中处理：list、map
			 */
			else {
				paramList.addAll(buildParams(fullTypeName, paramName, 1));
			}
		}
		return apiMethod.setRequestParams(paramList);
	}

	/**
	 * 响应参数, 现在返回值都是 Result， 获取不到真实的反回值， 需要在 @return 中标注返回值类型 返回值的约定： <br>
	 * 1. String <br>
	 * 2. Xml <br>
	 * 3. Model <br>
	 * 4. PlainFile、 MultipartFile： 流式输出 <br>
	 * 5. Json : Result <br>
	 * 
	 * 等所有对象类型
	 * 
	 * @param method
	 * @return
	 */
	private ApiMethod buildReturnParams(ApiMethod apiMethod, JavaMethod method) {

		/**
		 * void 返回值
		 */
		if (VOID.equals(method.getReturnType().getFullyQualifiedName())) {
			return apiMethod;
		}

		/**
		 * 返回值列表
		 */
		List<ApiParam> paramList = Lists.newArrayList();

		/**
		 * 返回值
		 */
		ApiReturn apiReturn = this.processReturnType(method.getReturnType().getGenericCanonicalName());
		String typeName = apiReturn.getSimpleName();
		JavaClass apiReturnCls = this.getJavaClass(typeName);

		/**
		 * 基本类型
		 */
		if (this.isSimpleProperty(typeName)) {
			ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
			apiParam.setValue(MockUtils.getValueByTypeAndName(apiParam.getType(), apiParam.getField()));
			paramList.add(apiParam);
		}

		/**
		 * xml 类型
		 */
		else if (this.isResponseXml(apiReturnCls) != null) {
			paramList.addAll(this.buildReturns(typeName, StringUtils.EMPTY, 1));
		}

		/**
		 * Model 数据输出
		 */
		else if (MODEL.equals(apiReturnCls.getSimpleName())) {
			ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
			paramList.add(apiParam);
		}

		/**
		 * 流 数据输出
		 */
		else if (FILES.contains(apiReturnCls.getSimpleName())) {
			ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
			paramList.add(apiParam);
		}

		/**
		 * Json
		 */
		else if (Result.class.getName().equals(typeName)) {
			paramList.addAll(this.buildReturns(typeName, StringUtils.EMPTY, 1));

			/**
			 * 获取 return 类型的数据
			 */
			List<DocletTag> paramTags = method.getTagsByName(RETURN);
			if (paramTags != null && paramTags.size() > 0 && StringUtils.isNotBlank(paramTags.get(0).getValue())) {
				paramList.addAll(this.buildReturns(paramTags.get(0).getValue(), "obj", 1));
			}
		}

		/**
		 * Json 类型
		 */
		else {
			paramList.addAll(this.buildReturns(typeName, StringUtils.EMPTY, 1));
		}

		return apiMethod.setResponseParams(paramList);
	}

	/**
	 * 递归创建参数
	 * 
	 * @param parameter
	 * @return
	 */
	private List<ApiParam> buildReturns(String className, String pre, int deep) {

		/**
		 * 最多解析2层
		 */
		if (deep >= 3) {
			return null;
		}

		/**
		 * 解析字段
		 */
		List<ApiParam> paramList = new ArrayList<>();
		JavaClass cls = getJavaClass(className);
		List<JavaField> fields = getFields(cls, 0);
		for (JavaField field : fields) {

			/**
			 * 排除一些方法
			 */
			String fieldName = field.getName();
			if ("this$0".equals(fieldName) || "serialVersionUID".equals(fieldName)) {
				continue;
			}

			String typeSimpleName = field.getType().getSimpleName().toLowerCase();
			String subTypeName = field.getType().getFullyQualifiedName();
			String comment = field.getComment();

			/**
			 * 支持自定义的备注 @ApiDoc --- 这种方式只能获取源码中的注释
			 */
			List<JavaAnnotation> annotations = field.getAnnotations();
			if (annotations != null && annotations.size() > 0) {
				for (JavaAnnotation annotation : annotations) {
					if (ApiDoc.class.getName().equals(annotation.getType().getCanonicalName())) {
						comment = StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(VALUE_PROP)));
					}
				}
			}

			/**
			 * 获取编译之后的注释
			 */
			if (StringUtils.isBlank(comment)) {
				try {
					Class<?> clzz = Class.forName(className);
					Field filed = clzz.getDeclaredField(fieldName);
					ApiDoc apiDoc = filed.getAnnotation(ApiDoc.class);
					if (apiDoc != null) {
						comment = apiDoc.value();
					}
				} catch (Exception e) {
				}
			}

			/**
			 * 简单类型 : 简单类型可以使用验证器
			 */
			if (this.isSimpleProperty(subTypeName)) {
				ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
				param.setType(this.processTypeNameForParam(typeSimpleName));
				param.setDesc(comment);
				param.setValue(MockUtils.getValueByTypeAndName(field.getType().getSimpleName(), field.getName()));
				paramList.add(param);
			}

			/**
			 * 处理集合类型，且只处理第二层 <br>
			 * list 参数可以传递： p3 = 1, p3 = 1
			 */
			else if (this.isCollection(subTypeName)) {
				String gNameTemp = field.getType().getGenericCanonicalName();
				String gName = this.getSimpleGicName(gNameTemp)[0];

				/**
				 * 内部属性是基本类型
				 */
				if (this.isSimpleProperty(gName)) {
					ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
					param.setType(this.processTypeNameForParam(typeSimpleName));
					param.setDesc(comment);
					paramList.add(param);
				} else {

					/**
					 * 添加一个父参数
					 */
					ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
					param.setType(this.processTypeNameForParam(typeSimpleName));
					param.setDesc(comment);
					paramList.add(param);

					/**
					 * 解析子属性
					 */
					paramList.addAll(this.buildReturns(gName, (!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName,
							deep + 1));
				}
			}

			/**
			 * map、[] 类型需要配置 @json 才能做解析 <br>
			 * map 参数可以传递 ： p4[a]=11, p4[b]=12 <br>
			 */
			else if (this.isMap(subTypeName) || this.isArray(subTypeName)) {

				// map 不解析非简单模型
				ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
				param.setType(this.processTypeNameForParam(typeSimpleName));
				param.setDesc(comment);
				paramList.add(param);
			}

			/**
			 * 对象类型
			 */
			else {
				/**
				 * 添加一个父参数
				 */
				ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
				param.setType(this.processTypeNameForParam(typeSimpleName));
				param.setDesc(comment);
				paramList.add(param);

				/**
				 * 解析子属性
				 */
				paramList.addAll(this.buildReturns(subTypeName,
						(!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName, deep + 1));
			}
		}
		return paramList;
	}

	/**
	 * 递归创建参数
	 * 
	 * @param parameter
	 * @return
	 */
	private List<ApiParam> buildParams(String className, String pre, int deep) {

		/**
		 * 最多解析2层
		 */
		if (deep >= 3) {
			return null;
		}

		/**
		 * 解析字段
		 */
		List<ApiParam> paramList = new ArrayList<>();
		JavaClass cls = getJavaClass(className);
		List<JavaField> fields = getFields(cls, 0);
		for (JavaField field : fields) {

			/**
			 * 排除一些方法
			 */
			String fieldName = field.getName();
			if ("this$0".equals(fieldName) || "serialVersionUID".equals(fieldName)) {
				continue;
			}

			String typeSimpleName = field.getType().getSimpleName().toLowerCase();
			String subTypeName = field.getType().getFullyQualifiedName();
			String comment = field.getComment();

			/**
			 * 支持自定义的备注 @ApiDoc --- 这种方式只能获取源码中的注释
			 */
			List<JavaAnnotation> annotations = field.getAnnotations();
			if (annotations != null && annotations.size() > 0) {
				for (JavaAnnotation annotation : annotations) {
					if (ApiDoc.class.getName().equals(annotation.getType().getCanonicalName())) {
						comment = StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(VALUE_PROP)));
					}
				}
			}

			/**
			 * 获取编译之后的注释
			 */
			if (StringUtils.isBlank(comment)) {
				try {
					Class<?> clzz = Class.forName(className);
					Field filed = clzz.getDeclaredField(fieldName);
					ApiDoc apiDoc = filed.getAnnotation(ApiDoc.class);
					if (apiDoc != null) {
						comment = apiDoc.value();
					}
				} catch (Exception e) {
				}
			}

			/**
			 * 简单类型 : 简单类型可以使用验证器
			 */
			if (this.isSimpleProperty(subTypeName)) {
				ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]");
				param.setType(this.processTypeNameForParam(typeSimpleName));
				param.setDesc(comment);
				paramList.add(param);
				this.buildVaildAndJson(param, field);
			}

			/**
			 * 处理集合类型，且只处理第二层 <br>
			 * list 参数可以传递： p3 = 1, p3 = 1
			 */
			else if (this.isCollection(subTypeName)) {
				String gNameTemp = field.getType().getGenericCanonicalName();
				String gName = this.getSimpleGicName(gNameTemp)[0];

				/**
				 * 内部属性是基本类型
				 */
				if (this.isSimpleProperty(gName)) {
					ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]");
					param.setType(this.processTypeNameForParam(typeSimpleName));
					param.setDesc(comment);
					paramList.add(param);
				} else {
					paramList.addAll(this.buildParams(gName, pre + "[" + fieldName + "]", deep + 1));
				}
			}

			/**
			 * map、[] 类型需要配置 @json 才能做解析 <br>
			 * map 参数可以传递 ： p4[a]=11, p4[b]=12 <br>
			 */
			else if (this.isMap(subTypeName) || this.isArray(subTypeName)) {

				// map 不解析非简单模型
				ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]" + "[a]");
				param.setType(this.processTypeNameForParam(typeSimpleName));
				param.setDesc(comment);
				this.buildVaildAndJson(param, field);
				paramList.add(param);
			}

			/**
			 * 对象类型
			 */
			else {
				paramList.addAll(this.buildParams(subTypeName, pre + "[" + fieldName + "]", deep + 1));
			}
		}
		return paramList;
	}

	/**
	 * 字段验证
	 * 
	 * @param param
	 * @param field
	 */
	private void buildVaildAndJson(ApiParam param, JavaField field) {
		List<JavaAnnotation> annotations = field.getAnnotations();

		// Valid
		if (annotations != null && annotations.size() > 0) {
			for (JavaAnnotation annotation : annotations) {
				String annotationName = annotation.getType().getCanonicalName();
				if (VALID_PARAMS.contains(annotationName)) {
					StringBuilder valid = new StringBuilder();
					valid.append(annotation.getType().getName()).append("\t");
					valid.append(StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(VALUE_PROP))))
							.append("\t");
					valid.append(StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(MSG_PROP))));
					param.addValid(valid.toString());
				}
			}
		}

		// Json
		if (annotations != null && annotations.size() > 0) {
			for (JavaAnnotation annotation : annotations) {
				String annotationName = annotation.getType().getCanonicalName();
				if (Json.class.getName().equals(annotationName)) {
					param.setJson(Json.class.getName().equals(annotationName));
				}
			}
		}

		// 模拟参数值
		param.setValue(MockUtils.getValueByTypeAndName(field.getType().getSimpleName(), field.getName()));
	}

	/**
	 * 获得java 类
	 * 
	 * @param simpleName
	 * @return
	 */
	private JavaClass getJavaClass(String simpleName) {
		JavaClass cls = builder.getClassByName(simpleName);
		List<JavaField> fieldList = this.getFields(cls, 0);
		// handle inner class
		if (Objects.isNull(cls.getFields()) || fieldList.isEmpty()) {
			cls = javaFilesMap.get(simpleName);
		} else {
			List<JavaClass> classList = cls.getNestedClasses();
			for (JavaClass javaClass : classList) {
				javaFilesMap.put(javaClass.getFullyQualifiedName(), javaClass);
			}
		}

		// 不能返回空类ixng
		if (cls == null) {
			throw new RuntimeException(
					"ERROR: Unable to find java class \"" + simpleName + "\"");
		}
		return cls;
	}

	/**
	 * Get fields
	 *
	 * @param cls1 The JavaClass object
	 * @param i    Recursive counter
	 * @return list of JavaField
	 */
	private List<JavaField> getFields(JavaClass cls1, int i) {
		List<JavaField> fieldList = new ArrayList<>();
		if (null == cls1) {
			return fieldList;
		} else if ("Object".equals(cls1.getSimpleName()) || "Timestamp".equals(cls1.getSimpleName())
				|| "Date".equals(cls1.getSimpleName()) || "Locale".equals(cls1.getSimpleName())) {
			return fieldList;
		} else {
			JavaClass pcls = cls1.getSuperJavaClass();
			fieldList.addAll(getFields(pcls, i));
			fieldList.addAll(cls1.getFields());
		}
		return fieldList;
	}
}
