package com.swak.api.builder;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.swak.api.DocAnnotationConstants;
import com.swak.api.DocGlobalConstants;
import com.swak.api.DocTags;
import com.swak.api.model.Api;
import com.swak.api.model.ApiConfig;
import com.swak.api.model.ApiHeader;
import com.swak.api.model.ApiMethod;
import com.swak.api.model.ApiParam;
import com.swak.api.model.ApiReturn;
import com.swak.api.model.CustomRespField;
import com.swak.api.utils.DocClassUtil;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.DocletTag;
import com.thoughtworks.qdox.model.JavaAnnotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.expression.AnnotationValue;

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
			if (isController(cls) && this.config.isPackageMatch(cls.getCanonicalName())) {
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
		return new Api().setName(cls.getName()).setDesc(cls.getComment()).setMethods(buildApiMethods(cls));
	}

	/**
	 * 创建 API 方法
	 * 
	 * @param cls
	 * @return
	 */
	private List<ApiMethod> buildApiMethods(final JavaClass cls) {
		List<JavaMethod> methods = cls.getMethods();
		List<ApiMethod> methodDocList = Lists.newArrayList();
		for (JavaMethod method : methods) {

			// 需要是 requestMapping
			if (!this.isRequestMethod(method)) {
				continue;
			}

			// 必须需要配置注释
			if (StringUtils.isEmpty(method.getComment())) {
				throw new RuntimeException(
						"Unable to find comment for method " + method.getName() + " in " + cls.getCanonicalName());
			}

			// 请求 Method
			ApiMethod apiMethod = new ApiMethod().setDesc(method.getComment()).setName(method.getName())
					.setDetail(method.getComment()).setUrl(this.getUrl(method)).setMethod(this.getMethod(method));

			// 请求头
			apiMethod.setRequestHeaders(this.buildRequestHeaders(method));

			// 请求参数
			List<ApiParam> requestParams = buildRequestParams(method, cls.getCanonicalName());
			apiMethod.setRequestParams(requestParams);

			// 响应参数
			List<ApiParam> responseParams = buildReturnApiParams(method, cls.getGenericFullyQualifiedName());
			apiMethod.setResponseParams(responseParams);

			methodDocList.add(apiMethod);
		}
		return methodDocList;
	}

	/**
	 * 创建 请求头
	 * 
	 * @param cls
	 * @return
	 */
	private List<ApiHeader> buildRequestHeaders(final JavaMethod method) {

		// 方法的param
		Map<String, String> paramMap = this.getParamsComments(method, PARAM);
		List<ApiHeader> apiReqHeaders = Lists.newArrayList();

		for (JavaParameter javaParameter : method.getParameters()) {
			String paramName = javaParameter.getName();
			List<JavaAnnotation> javaAnnotations = javaParameter.getAnnotations();
			for (JavaAnnotation annotation : javaAnnotations) {
				String annotationName = annotation.getType().getName();
				if (REQUEST_HERDER.equals(annotationName)) {
					ApiHeader apiReqHeader = new ApiHeader();
					Map<String, Object> requestHeaderMap = annotation.getNamedParameterMap();
					if (requestHeaderMap.get(VALUE_PROP) != null) {
						apiReqHeader.setName(StringUtils.removeQuotes((String) requestHeaderMap.get(VALUE_PROP)));
					} else {
						apiReqHeader.setName(paramName);
					}
					StringBuilder desc = new StringBuilder();
					String comments = paramMap.get(paramName);
					desc.append(comments);

					if (requestHeaderMap.get(DEFAULT_VALUE_PROP) != null) {
						desc.append("(" + DEFAULT_VALUE_PROP + ": ")
								.append(StringUtils.removeQuotes((String) requestHeaderMap.get(DEFAULT_VALUE_PROP)))
								.append(")");
					}
					apiReqHeader.setDesc(desc.toString());
					if (requestHeaderMap.get(REQUIRED_PROP) != null) {
						apiReqHeader.setRequired(!"false".equals(requestHeaderMap.get(REQUIRED_PROP)));
					} else {
						apiReqHeader.setRequired(true);
					}
					String typeName = javaParameter.getType().getValue().toLowerCase();
					apiReqHeader.setType(this.processTypeNameForParam(typeName));
					apiReqHeaders.add(apiReqHeader);
					break;
				}
			}
		}
		return apiReqHeaders;
	}

	/**
	 * 创建请求参数
	 * 
	 * @param javaMethod
	 * @param className
	 * @return
	 */
	private List<ApiParam> buildRequestParams(final JavaMethod javaMethod, final String className) {
		Map<String, CustomRespField> responseFieldMap = new HashMap<>();
		Map<String, String> paramTagMap = this.getParamsComments(javaMethod, PARAM);
		List<JavaParameter> parameterList = javaMethod.getParameters();
		if (parameterList.size() < 1) {
			return null;
		}
		List<ApiParam> paramList = new ArrayList<>();
		int requestBodyCounter = 0;
		List<ApiParam> reqBodyParamsList = new ArrayList<>();
		out: for (JavaParameter parameter : parameterList) {
			String paramName = parameter.getName();
			String typeName = parameter.getType().getGenericCanonicalName();
			String simpleName = parameter.getType().getValue().toLowerCase();
			String fullTypeName = parameter.getType().getFullyQualifiedName();
			if (!this.isIgnoreParams(parameter)) {
				if (!paramTagMap.containsKey(paramName)) {
					throw new RuntimeException("ERROR: Unable to find javadoc @param for actual param \"" + paramName
							+ "\" in method " + javaMethod.getName() + " from " + className);
				}
				String comment = paramTagMap.get(paramName);
				if (StringUtils.isEmpty(comment)) {
					comment = NO_COMMENTS_FOUND;
				}
				List<JavaAnnotation> annotations = parameter.getAnnotations();
				if (annotations.size() == 0) {
					// default set required is true
					if (DocClassUtil.isCollection(fullTypeName) || DocClassUtil.isArray(fullTypeName)) {
						String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
						String gicName = gicNameArr[0];
						if (DocClassUtil.isArray(gicName)) {
							gicName = gicName.substring(0, gicName.indexOf("["));
						}
						String typeTemp = "";
						if (DocClassUtil.isPrimitive(gicName)) {
							typeTemp = " of " + DocClassUtil.processTypeNameForParams(gicName);
							ApiParam param = ApiParam.of().setField(paramName)
									.setType(DocClassUtil.processTypeNameForParams(simpleName) + typeTemp)
									.setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
							paramList.add(param);
						} else {
							ApiParam param = ApiParam.of().setField(paramName)
									.setType(DocClassUtil.processTypeNameForParams(simpleName) + typeTemp)
									.setDesc(comment).setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
							paramList.add(param);
							paramList.addAll(buildParams(gicNameArr[0], "└─", 1, "true", responseFieldMap, false,
									new HashMap<>()));
						}

					} else if (DocClassUtil.isPrimitive(simpleName)) {
						ApiParam param = ApiParam.of().setField(paramName)
								.setType(DocClassUtil.processTypeNameForParams(simpleName)).setDesc(comment)
								.setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
						paramList.add(param);
					} else if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
						ApiParam param = ApiParam.of().setField(paramName).setType("map").setDesc(comment)
								.setRequired(true).setVersion(DocGlobalConstants.DEFAULT_VERSION);
						paramList.add(param);
					} else {
						paramList.addAll(
								buildParams(fullTypeName, "", 0, "true", responseFieldMap, false, new HashMap<>()));
					}
				}
				for (JavaAnnotation annotation : annotations) {
					String required = "true";
					AnnotationValue annotationRequired = (AnnotationValue) annotation
							.getProperty(DocAnnotationConstants.REQUIRED_PROP);
					if (null != annotationRequired) {
						required = annotationRequired.toString();
					}
					String annotationName = annotation.getType().getName();
					if (REQUEST_BODY.equals(annotationName)
							|| (VALID.equals(annotationName) && annotations.size() == 1)) {
						if (requestBodyCounter > 0) {
							throw new RuntimeException(
									"You have use @RequestBody Passing multiple variables  for method "
											+ javaMethod.getName() + " in " + className
											+ ",@RequestBody annotation could only bind one variables.");
						}
						if (DocClassUtil.isPrimitive(fullTypeName)) {
							ApiParam bodyParam = ApiParam.of().setField(paramName)
									.setType(DocClassUtil.processTypeNameForParams(simpleName)).setDesc(comment)
									.setRequired(Boolean.valueOf(required));
							reqBodyParamsList.add(bodyParam);
						} else {
							if (DocClassUtil.isCollection(fullTypeName) || DocClassUtil.isArray(fullTypeName)) {
								String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
								String gicName = gicNameArr[0];
								if (DocClassUtil.isArray(gicName)) {
									gicName = gicName.substring(0, gicName.indexOf("["));
								}
								if (DocClassUtil.isPrimitive(gicName)) {
									ApiParam bodyParam = ApiParam.of().setField(paramName)
											.setType(DocClassUtil.processTypeNameForParams(simpleName)).setDesc(comment)
											.setRequired(Boolean.valueOf(required))
											.setVersion(DocGlobalConstants.DEFAULT_VERSION);
									reqBodyParamsList.add(bodyParam);
								} else {
									reqBodyParamsList.addAll(buildParams(gicNameArr[0], "", 0, "true", responseFieldMap,
											false, new HashMap<>()));
								}

							} else if (DocClassUtil.isMap(fullTypeName)) {
								if (DocGlobalConstants.JAVA_MAP_FULLY.equals(typeName)) {
									ApiParam apiParam = ApiParam.of().setField(paramName).setType("map")
											.setDesc(comment).setRequired(Boolean.valueOf(required))
											.setVersion(DocGlobalConstants.DEFAULT_VERSION);
									paramList.add(apiParam);
									continue out;
								}
								String[] gicNameArr = DocClassUtil.getSimpleGicName(typeName);
								reqBodyParamsList.addAll(buildParams(gicNameArr[1], "", 0, "true", responseFieldMap,
										false, new HashMap<>()));
							} else {
								reqBodyParamsList.addAll(
										buildParams(typeName, "", 0, "true", responseFieldMap, false, new HashMap<>()));
							}
						}
						requestBodyCounter++;
					} else {
						if (REQUEST_PARAM.equals(annotationName)
								|| DocAnnotationConstants.SHORT_PATH_VARIABLE.equals(annotationName)) {
							AnnotationValue annotationValue = (AnnotationValue) annotation
									.getProperty(DocAnnotationConstants.VALUE_PROP);
							if (null != annotationValue) {
								paramName = StringUtils.removeQuotes(annotationValue.toString());
							}
							AnnotationValue annotationOfName = (AnnotationValue) annotation
									.getProperty(DocAnnotationConstants.NAME_PROP);
							if (null != annotationOfName) {
								paramName = StringUtils.removeQuotes(annotationOfName.toString());
							}

							ApiParam param = ApiParam.of().setField(paramName)
									.setType(DocClassUtil.processTypeNameForParams(simpleName)).setDesc(comment)
									.setRequired(Boolean.valueOf(required))
									.setVersion(DocGlobalConstants.DEFAULT_VERSION);
							paramList.add(param);
						} else {
							continue;
						}
					}
				}
			}
		}
		if (requestBodyCounter > 0) {
			paramList.addAll(reqBodyParamsList);
			return paramList;
		}
		return paramList;
	}

	/**
	 * 响应参数
	 * 
	 * @param method
	 * @param controllerName
	 * @return
	 */
	private List<ApiParam> buildReturnApiParams(JavaMethod method, String controllerName) {
		if ("void".equals(method.getReturnType().getFullyQualifiedName())) {
			return null;
		}
		Map<String, CustomRespField> responseFieldMap = new HashMap<>();
		ApiReturn apiReturn = DocClassUtil.processReturnType(method.getReturnType().getGenericCanonicalName());
		String returnType = apiReturn.getGenericCanonicalName();
		String typeName = apiReturn.getSimpleName();
		if (DocClassUtil.isMvcIgnoreParams(typeName)) {
			if (DocGlobalConstants.MODE_AND_VIEW_FULLY.equals(typeName)) {
				return null;
			} else {
				throw new RuntimeException(
						"smart-doc can't support " + typeName + " as method return in " + controllerName);
			}
		}
		if (DocClassUtil.isPrimitive(typeName)) {
			return primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(typeName));
		}
		if (DocClassUtil.isCollection(typeName)) {
			if (returnType.contains("<")) {
				String gicName = returnType.substring(returnType.indexOf("<") + 1, returnType.lastIndexOf(">"));
				if (DocClassUtil.isPrimitive(gicName)) {
					return primitiveReturnRespComment("array of " + DocClassUtil.processTypeNameForParams(gicName));
				}
				return buildParams(gicName, "", 0, null, responseFieldMap, true, new HashMap<>());
			} else {
				return null;
			}
		}
		if (DocClassUtil.isMap(typeName)) {
			String[] keyValue = DocClassUtil.getMapKeyValueType(returnType);
			if (keyValue.length == 0) {
				return null;
			}
			if (DocClassUtil.isPrimitive(keyValue[1])) {
				return primitiveReturnRespComment("key value");
			}
			return buildParams(keyValue[1], "", 0, null, responseFieldMap, true, new HashMap<>());
		}
		if (StringUtils.isNotEmpty(returnType)) {
			return buildParams(returnType, "", 0, null, responseFieldMap, true, new HashMap<>());
		}
		return null;
	}

	/**
	 * build request params list or response fields list
	 */
	private List<ApiParam> buildParams(String className, String pre, int i, String isRequired,
			Map<String, CustomRespField> responseFieldMap, boolean isResp, Map<String, String> registryClasses) {
		if (StringUtils.isEmpty(className)) {
			throw new RuntimeException("Class name can't be null or empty.");
		}
		List<ApiParam> paramList = new ArrayList<>();
		if (registryClasses.containsKey(className) && i > registryClasses.size()) {
			return paramList;
		}
		registryClasses.put(className, className);
		String simpleName = DocClassUtil.getSimpleName(className);
		String[] globGicName = DocClassUtil.getSimpleGicName(className);
		JavaClass cls = getJavaClass(simpleName);
		// clsss.isEnum()
		List<JavaField> fields = getFields(cls, 0);
		int n = 0;
		if (DocClassUtil.isPrimitive(simpleName)) {
			paramList.addAll(primitiveReturnRespComment(DocClassUtil.processTypeNameForParams(simpleName)));
		} else if (DocClassUtil.isCollection(simpleName) || DocClassUtil.isArray(simpleName)) {
			if (!DocClassUtil.isCollection(globGicName[0])) {
				String gicName = globGicName[0];
				if (DocClassUtil.isArray(gicName)) {
					gicName = gicName.substring(0, gicName.indexOf("["));
				}
				paramList.addAll(
						buildParams(gicName, pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses));
			}
		} else if (DocClassUtil.isMap(simpleName)) {
			if (globGicName.length == 2) {
				paramList.addAll(
						buildParams(globGicName[1], pre, i + 1, isRequired, responseFieldMap, isResp, registryClasses));
			}
		} else if (DocGlobalConstants.JAVA_OBJECT_FULLY.equals(className)) {
			ApiParam param = ApiParam.of().setField(pre + "any object").setType("object");
			if (StringUtils.isEmpty(isRequired)) {
				param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setVersion(DocGlobalConstants.DEFAULT_VERSION);
			} else {
				param.setDesc(DocGlobalConstants.ANY_OBJECT_MSG).setRequired(false)
						.setVersion(DocGlobalConstants.DEFAULT_VERSION);

			}
			paramList.add(param);
		} else {
			out: for (JavaField field : fields) {
				String fieldName = field.getName();
				if ("this$0".equals(fieldName) || "serialVersionUID".equals(fieldName)) {
					continue;
				}
				String typeSimpleName = field.getType().getSimpleName();
				String subTypeName = field.getType().getFullyQualifiedName();
				String fieldGicName = field.getType().getGenericCanonicalName();
				List<JavaAnnotation> javaAnnotations = field.getAnnotations();

				List<DocletTag> paramTags = field.getTags();
				String since = DocGlobalConstants.DEFAULT_VERSION;// since tag value
				if (!isResp) {
					for (DocletTag docletTag : paramTags) {
						if (DocClassUtil.isIgnoreTag(docletTag.getName())) {
							continue out;
						} else if (DocTags.SINCE.equals(docletTag.getName())) {
							since = docletTag.getValue();
						}
					}
				} else {
					for (DocletTag docletTag : paramTags) {
						if (DocTags.SINCE.equals(docletTag.getName())) {
							since = docletTag.getValue();
						}
					}
				}

				boolean strRequired = false;
				int annotationCounter = 0;
				an: for (JavaAnnotation annotation : javaAnnotations) {
					String annotationName = annotation.getType().getSimpleName();
					if (DocAnnotationConstants.SHORT_JSON_IGNORE.equals(annotationName) && isResp) {
						continue out;
					} else if (DocAnnotationConstants.SHORT_JSON_FIELD.equals(annotationName) && isResp) {
						if (null != annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP)) {
							if ("false"
									.equals(annotation.getProperty(DocAnnotationConstants.SERIALIZE_PROP).toString())) {
								continue out;
							}
						} else if (null != annotation.getProperty(DocAnnotationConstants.NAME_PROP)) {
							fieldName = StringUtils
									.removeQuotes(annotation.getProperty(DocAnnotationConstants.NAME_PROP).toString());
						}
					} else if (DocAnnotationConstants.SHORT_JSON_PROPERTY.equals(annotationName) && isResp) {
						if (null != annotation.getProperty(DocAnnotationConstants.VALUE_PROP)) {
							fieldName = StringUtils
									.removeQuotes(annotation.getProperty(DocAnnotationConstants.VALUE_PROP).toString());
						}
					} else if (DocClassUtil.isJSR303Required(annotationName)) {
						strRequired = true;
						annotationCounter++;
						break an;
					}
				}
				if (annotationCounter < 1) {
					doc: for (DocletTag docletTag : paramTags) {
						if (DocClassUtil.isRequiredTag(docletTag.getName())) {
							strRequired = true;
							break doc;
						}
					}
				}
				// cover comment
				CustomRespField customResponseField = responseFieldMap.get(field.getName());
				String comment;
				if (null != customResponseField && StringUtils.isNotEmpty(customResponseField.getDesc())) {
					comment = customResponseField.getDesc();
				} else {
					comment = field.getComment();
				}
				if (StringUtils.isNotEmpty(comment)) {
					comment = comment.replace("\r\n", "<br>");
					comment = comment.replace("\n", "<br>");
				}
				if (DocClassUtil.isPrimitive(subTypeName)) {
					ApiParam param = ApiParam.of().setField(pre + fieldName);
					String processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
					param.setType(processedType);
					if (StringUtils.isNotEmpty(comment)) {
						commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
					} else {
						commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
					}
				} else {
					ApiParam param = ApiParam.of().setField(pre + fieldName);
					String processedType = DocClassUtil.processTypeNameForParams(typeSimpleName.toLowerCase());
					param.setType(processedType);
					if (StringUtils.isNotEmpty(comment)) {
						commonHandleParam(paramList, param, isRequired, comment, since, strRequired);
					} else {
						commonHandleParam(paramList, param, isRequired, NO_COMMENTS_FOUND, since, strRequired);
					}
					StringBuilder preBuilder = new StringBuilder();
					for (int j = 0; j < i; j++) {
						preBuilder.append(DocGlobalConstants.FIELD_SPACE);
					}
					preBuilder.append("└─");
					if (DocClassUtil.isMap(subTypeName)) {
						String gNameTemp = field.getType().getGenericCanonicalName();
						if (DocGlobalConstants.JAVA_MAP_FULLY.equals(gNameTemp)) {
							ApiParam param1 = ApiParam.of().setField(preBuilder.toString() + "any object")
									.setType("object").setDesc(DocGlobalConstants.ANY_OBJECT_MSG)
									.setVersion(DocGlobalConstants.DEFAULT_VERSION);
							paramList.add(param1);
							continue;
						}
						String valType = DocClassUtil.getMapKeyValueType(gNameTemp)[1];
						if (!DocClassUtil.isPrimitive(valType)) {
							if (valType.length() == 1) {
								String gicName = (n < globGicName.length) ? globGicName[n]
										: globGicName[globGicName.length - 1];
								if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
									paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired,
											responseFieldMap, isResp, registryClasses));
								}
							} else {
								paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1, isRequired,
										responseFieldMap, isResp, registryClasses));
							}
						}
					} else if (DocClassUtil.isCollection(subTypeName)) {
						String gNameTemp = field.getType().getGenericCanonicalName();
						String[] gNameArr = DocClassUtil.getSimpleGicName(gNameTemp);
						if (gNameArr.length == 0) {
							continue out;
						}
						String gName = DocClassUtil.getSimpleGicName(gNameTemp)[0];
						if (!DocClassUtil.isPrimitive(gName)) {
							if (!simpleName.equals(gName) && !gName.equals(simpleName)) {
								if (gName.length() == 1) {
									int len = globGicName.length;
									if (len > 0) {
										String gicName = (n < len) ? globGicName[n] : globGicName[len - 1];
										if (!DocClassUtil.isPrimitive(gicName) && !simpleName.equals(gicName)) {
											paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1,
													isRequired, responseFieldMap, isResp, registryClasses));
										}
									}
								} else {
									paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1, isRequired,
											responseFieldMap, isResp, registryClasses));
								}
							}
						}
					} else if (subTypeName.length() == 1 || DocGlobalConstants.JAVA_OBJECT_FULLY.equals(subTypeName)) {
						if (!simpleName.equals(className)) {
							if (n < globGicName.length) {
								String gicName = globGicName[n];
								String simple = DocClassUtil.getSimpleName(gicName);
								if (DocClassUtil.isPrimitive(simple)) {
									// do nothing
								} else if (gicName.contains("<")) {
									if (DocClassUtil.isCollection(simple)) {
										String gName = DocClassUtil.getSimpleGicName(gicName)[0];
										if (!DocClassUtil.isPrimitive(gName)) {
											paramList.addAll(buildParams(gName, preBuilder.toString(), i + 1,
													isRequired, responseFieldMap, isResp, registryClasses));
										}
									} else if (DocClassUtil.isMap(simple)) {
										String valType = DocClassUtil.getMapKeyValueType(gicName)[1];
										if (!DocClassUtil.isPrimitive(valType)) {
											paramList.addAll(buildParams(valType, preBuilder.toString(), i + 1,
													isRequired, responseFieldMap, isResp, registryClasses));
										}
									} else {
										paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired,
												responseFieldMap, isResp, registryClasses));
									}
								} else {
									paramList.addAll(buildParams(gicName, preBuilder.toString(), i + 1, isRequired,
											responseFieldMap, isResp, registryClasses));
								}
							} else {
								paramList.addAll(buildParams(subTypeName, preBuilder.toString(), i + 1, isRequired,
										responseFieldMap, isResp, registryClasses));
							}
						}
						n++;
					} else if (DocClassUtil.isArray(subTypeName)) {
						fieldGicName = fieldGicName.substring(0, fieldGicName.indexOf("["));
						if (className.equals(fieldGicName)) {
							// do nothing
						} else if (!DocClassUtil.isPrimitive(fieldGicName)) {
							paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired,
									responseFieldMap, isResp, registryClasses));
						}
					} else if (simpleName.equals(subTypeName)) {
						// do nothing
					} else {
						paramList.addAll(buildParams(fieldGicName, preBuilder.toString(), i + 1, isRequired,
								responseFieldMap, isResp, registryClasses));
					}
				}
			}
		}
		return paramList;
	}

	private List<ApiParam> primitiveReturnRespComment(String typeName) {
		StringBuilder comments = new StringBuilder();
		comments.append("The api directly returns the ").append(typeName).append(" type value.");
		ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName).setDesc(comments.toString())
				.setVersion(DocGlobalConstants.DEFAULT_VERSION);
		List<ApiParam> paramList = new ArrayList<>();
		paramList.add(apiParam);
		return paramList;
	}

	private void commonHandleParam(List<ApiParam> paramList, ApiParam param, String isRequired, String comment,
			String since, boolean strRequired) {
		if (StringUtils.isEmpty(isRequired)) {
			param.setDesc(comment).setVersion(since);
			paramList.add(param);
		} else {
			param.setDesc(comment).setVersion(since).setRequired(strRequired);
			paramList.add(param);
		}
	}

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
		return cls;
	}

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
