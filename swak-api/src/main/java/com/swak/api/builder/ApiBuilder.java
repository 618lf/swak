package com.swak.api.builder;

import com.swak.annotation.*;
import com.swak.api.ApiConfig;
import com.swak.api.mock.MockUtils;
import com.swak.doc.*;
import com.swak.entity.Result;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;
import com.swak.utils.router.RouterUtils;
import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.*;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 构建 API
 *
 * @author: lifeng
 * @date: 2020/3/28 15:14
 */
public class ApiBuilder implements IBuilder {

    private JavaProjectBuilder builder;
    private Collection<JavaClass> javaClasses;
    private Map<String, JavaClass> javaFilesMap = new ConcurrentHashMap<>();
    private ApiConfig config;

    /**
     * 创建 api
     *
     * @param config 配置
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
     * @return apis
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
     * @param cls java文件
     * @return 一个APi
     * @author lifeng
     * @date 2020/3/28 15:15
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
     * @param apiMethod     api method
     * @param classMapping  类注解
     * @param methodMapping 方法注解
     * @return apiMethod
     * @author lifeng
     * @date 2020/3/28 15:32
     */
    private ApiMethod buildRouter(ApiMethod apiMethod, JavaAnnotation classMapping, JavaAnnotation methodMapping) {

        // urls
        List<String> ps1 = this.processMappingUrl(classMapping.getNamedParameter(PATH_PROP));
        List<String> ps2 = this.processMappingUrl(methodMapping.getNamedParameter(VALUE_PROP));

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
     * 处理 urls
     *
     * @param patterns mapping 中配置的模式
     * @return urls
     * @author lifeng
     * @date 2020/3/28 16:33
     */
    @SuppressWarnings("unchecked")
    private List<String> processMappingUrl(Object patterns) {
        List<String> ps1 = Lists.newArrayList();
        if (patterns instanceof String) {
            ps1.add(StringUtils.removeQuotes(String.valueOf(patterns)));
        } else {
            List<Object> patternsObject = (List<Object>) patterns;
            for (Object p : patternsObject) {
                ps1.add(StringUtils.removeQuotes(String.valueOf(p)));
            }
        }
        return ps1;
    }

    /**
     * 创建 请求头
     *
     * @param apiMethod api method
     * @param method    具体的方法
     * @return apiMethod
     * @author lifeng
     * @date 2020/3/28 15:35
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
     * 创建 请求参数
     *
     * @param apiMethod  api method
     * @param javaMethod 具体的方法
     * @return apiMethod
     * @author lifeng
     * @date 2020/3/28 15:35
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

            // 忽略一些系统内置的参数
            if (this.isIgnoreParams(parameter)) {
                continue;
            }

            // 必须有备注说明
            if (!paramTagMap.containsKey(paramName)) {
                throw new RuntimeException(
                        "ERROR: Unable to find javadoc @param for actual param \"" + paramName + "\" in method "
                                + javaMethod.getName() + " from " + javaMethod.getDeclaringClass().getCanonicalName());
            }
            String comment = paramTagMap.get(paramName);
            if (StringUtils.isEmpty(comment)) {
                comment = NO_COMMENTS_FOUND;
            }

            // 注解： Body, Json, Header
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

            // 简单类型: 参数不处理：list、map、[]
            else if (this.isSimpleProperty(simpleName) || this.isMap(simpleName) || this.isCollection(simpleName)
                    || this.isArray(simpleName)) {
                ApiParam param = ApiParam.of().setField(paramName).setType(this.processTypeNameForParam(simpleName))
                        .setDesc(comment);
                param.setValue(MockUtils.getValueByTypeAndName(parameter.getType().getValue(), parameter.getName()));
                paramList.add(param);
            }

            // 对象类型: 对象中处理：list、map
            else {
                List<ApiParam> params = buildParams(fullTypeName, paramName, 1);
                if (params != null) {
                    paramList.addAll(params);
                }
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
     * 等所有对象类型 <br>
     *
     * @param apiMethod api method
     * @param method    具体的方法
     * @return apiMethod
     * @author lifeng
     * @date 2020/3/28 15:36
     */
    private ApiMethod buildReturnParams(ApiMethod apiMethod, JavaMethod method) {

        // void 返回值
        if (VOID.equals(method.getReturnType().getFullyQualifiedName())) {
            return apiMethod;
        }

        // 返回值列表
        List<ApiParam> paramList = Lists.newArrayList();

        // 返回值
        ApiReturn apiReturn = this.processReturnType(method.getReturnType().getGenericCanonicalName());
        String typeName = apiReturn.getSimpleName();
        JavaClass apiReturnCls = this.getJavaClass(typeName);

        // 基本类型
        if (this.isSimpleProperty(typeName)) {
            ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
            apiParam.setValue(MockUtils.getValueByTypeAndName(apiParam.getType(), apiParam.getField()));
            paramList.add(apiParam);
        }

        // xml 类型
        else if (this.isResponseXml(apiReturnCls) != null) {
            List<ApiParam> params = this.buildReturns(typeName, StringUtils.EMPTY, 1);
            if (params != null) {
                paramList.addAll(params);
            }
        }

        // Model 数据输出
        else if (MODEL.equals(apiReturnCls.getSimpleName())) {
            ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
            paramList.add(apiParam);
        }

        // 流 数据输出
        else if (FILES.contains(apiReturnCls.getSimpleName())) {
            ApiParam apiParam = ApiParam.of().setField("no param name").setType(typeName);
            paramList.add(apiParam);
        }

        //  Json
        else if (Result.class.getName().equals(typeName)) {
            List<ApiParam> params = this.buildReturns(typeName, StringUtils.EMPTY, 1);
            if (params != null) {
                paramList.addAll(params);
            }

            // 获取 return 类型的数据
            List<DocletTag> paramTags = method.getTagsByName(RETURN);
            if (paramTags != null && paramTags.size() > 0 && StringUtils.isNotBlank(paramTags.get(0).getValue())) {
                params = this.buildReturns(paramTags.get(0).getValue(), "obj", 1);
                if (params != null) {
                    paramList.addAll(params);
                }
            }
        }

        // Json 类型
        else {
            List<ApiParam> params = this.buildReturns(typeName, StringUtils.EMPTY, 1);
            if (params != null) {
                paramList.addAll(params);
            }
        }

        return apiMethod.setResponseParams(paramList);
    }

    /**
     * 创建返回值
     *
     * @param className 类型名称
     * @param pre       前缀
     * @param deep      处理的深度
     * @return 参数集合
     * @author lifeng
     * @date 2020/3/28 15:35
     */
    private List<ApiParam> buildReturns(String className, String pre, int deep) {

        // 最多解析2层
        if (deep >= DEEP) {
            return null;
        }

        // 解析字段
        List<ApiParam> paramList = Lists.newArrayList();
        JavaClass cls = getJavaClass(className);
        List<JavaField> fields = getFields(cls, 0);
        for (JavaField field : fields) {

            // 排除一些方法
            String fieldName = field.getName();
            if ("this$0".equals(fieldName) || "serialVersionUID".equals(fieldName)) {
                continue;
            }
            String typeSimpleName = field.getType().getSimpleName().toLowerCase();
            String subTypeName = field.getType().getFullyQualifiedName();
            String comment = this.processFieldComment(className, field);

            // 简单类型 : 简单类型可以使用验证器
            if (this.isSimpleProperty(subTypeName)) {
                ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
                param.setType(this.processTypeNameForParam(typeSimpleName));
                param.setDesc(comment);
                param.setValue(MockUtils.getValueByTypeAndName(field.getType().getSimpleName(), field.getName()));
                paramList.add(param);
            }
            // 处理集合类型，且只处理第二层
            // list 参数可以传递： p3 = 1, p3 = 1
            else if (this.isCollection(subTypeName)) {
                String gNameTemp = field.getType().getGenericCanonicalName();
                String gName = this.getSimpleGicName(gNameTemp)[0];

                // 内部属性是基本类型
                if (this.isSimpleProperty(gName)) {
                    ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
                    param.setType(this.processTypeNameForParam(typeSimpleName));
                    param.setDesc(comment);
                    paramList.add(param);
                }
                // 其他类型
                else {
                    // 添加一个父参数
                    List<ApiParam> params = this.processObjectParams(pre, fieldName, typeSimpleName, comment, gName, deep);
                    paramList.addAll(params);
                }
            }

            // map、[] 类型需要配置 @json 才能做解析 <br>
            // map 参数可以传递 ： p4[a]=11, p4[b]=12 <br>
            else if (this.isMap(subTypeName) || this.isArray(subTypeName)) {
                // map 不解析非简单模型
                ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
                param.setType(this.processTypeNameForParam(typeSimpleName));
                param.setDesc(comment);
                paramList.add(param);
            }
            // 对象类型
            else {
                List<ApiParam> params = this.processObjectParams(pre, fieldName, typeSimpleName, comment, subTypeName, deep);
                paramList.addAll(params);
            }
        }
        return paramList;
    }

    /**
     * 处理类型参数
     *
     * @param pre            前缀
     * @param fieldName      字段名称
     * @param typeSimpleName 类型名称
     * @param comment        注释
     * @param subTypeName    类型
     * @param deep           深度
     * @return 参数
     * @author lifeng
     * @date 2020/3/28 17:02
     */
    private List<ApiParam> processObjectParams(String pre, String fieldName, String typeSimpleName, String comment,
                                               String subTypeName, int deep) {

        List<ApiParam> paramList = Lists.newArrayList();
        ApiParam param = ApiParam.of().setField((!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName);
        param.setType(this.processTypeNameForParam(typeSimpleName));
        param.setDesc(comment);
        paramList.add(param);

        // 解析子属性
        List<ApiParam> params = this.buildReturns(subTypeName,
                (!StringUtils.isBlank(pre) ? pre + "." : pre) + fieldName, deep + 1);
        if (params != null) {
            paramList.addAll(params);
        }
        return paramList;
    }

    /**
     * 创建 请求参数
     *
     * @param className 类型名称
     * @param pre       前缀
     * @param deep      处理的深度
     * @return 参数集合
     * @author lifeng
     * @date 2020/3/28 15:35
     */
    private List<ApiParam> buildParams(String className, String pre, int deep) {

        // 最多解析2层
        if (deep >= DEEP) {
            return null;
        }

        // 解析字段
        List<ApiParam> paramList = new ArrayList<>();
        JavaClass cls = getJavaClass(className);
        List<JavaField> fields = getFields(cls, 0);
        for (JavaField field : fields) {

            // 排除一些方法
            String fieldName = field.getName();
            if ("this$0".equals(fieldName) || "serialVersionUID".equals(fieldName)) {
                continue;
            }

            String typeSimpleName = field.getType().getSimpleName().toLowerCase();
            String subTypeName = field.getType().getFullyQualifiedName();

            // 获取源码中的注释
            String comment = this.processSourceFieldComment(field);

            // 获取编译之后的注释
            comment = this.processClassFieldComment(comment, className, fieldName);

            // 简单类型 : 简单类型可以使用验证器
            if (this.isSimpleProperty(subTypeName)) {
                ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]");
                param.setType(this.processTypeNameForParam(typeSimpleName));
                param.setDesc(comment);
                paramList.add(param);
                this.buildVaildAndJson(param, field);
            } else if (this.isCollection(subTypeName)) {
                // 处理集合类型，且只处理第二层 <br>
                // list 参数可以传递： p3 = 1, p3 = 1
                String gNameTemp = field.getType().getGenericCanonicalName();
                String gName = this.getSimpleGicName(gNameTemp)[0];

                // 内部属性是基本类型
                if (this.isSimpleProperty(gName)) {
                    ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]");
                    param.setType(this.processTypeNameForParam(typeSimpleName));
                    param.setDesc(comment);
                    paramList.add(param);
                } else {
                    List<ApiParam> params = this.buildParams(gName, pre + "[" + fieldName + "]", deep + 1);
                    if (params != null) {
                        paramList.addAll(params);
                    }
                }
            } else if (this.isMap(subTypeName) || this.isArray(subTypeName)) {
                // map、[] 类型需要配置 @json 才能做解析 <br>
                // map 参数可以传递 ： p4[a]=11, p4[b]=12 <br>
                // map 不解析非简单模型
                ApiParam param = ApiParam.of().setField(pre + "[" + fieldName + "]" + "[a]");
                param.setType(this.processTypeNameForParam(typeSimpleName));
                param.setDesc(comment);
                this.buildVaildAndJson(param, field);
                paramList.add(param);
            } else {
                List<ApiParam> params = this.buildParams(subTypeName, pre + "[" + fieldName + "]", deep + 1);
                if (params != null) {
                    paramList.addAll(params);
                }
            }
        }
        return paramList;
    }

    /**
     * 处理字段的注释
     *
     * @param className 类名称
     * @param field     字段
     * @return 注释
     * @author lifeng
     * @date 2020/3/28 16:54
     */
    private String processFieldComment(String className, JavaField field) {
        String fieldName = field.getName();
        if (FIELD_this$0.equals(fieldName) || FIELD_serialVersionUID.equals(fieldName)) {
            return null;
        }

        // 获取源码中的注释
        String comment = this.processSourceFieldComment(field);

        // 获取编译之后的注释
        return this.processClassFieldComment(comment, className, fieldName);
    }

    /**
     * 处理字段的注释
     *
     * @param field 字段
     * @return comment
     * @author lifeng
     * @date 2020/3/28 16:43
     */
    private String processSourceFieldComment(JavaField field) {

        // 默认的注释
        String comment = field.getComment();

        // 支持自定义的备注 @ApiDoc --- 这种方式只能获取源码中的注释
        List<JavaAnnotation> annotations = field.getAnnotations();
        if (annotations != null && annotations.size() > 0) {
            for (JavaAnnotation annotation : annotations) {
                if (ApiDoc.class.getName().equals(annotation.getType().getCanonicalName())) {
                    comment = StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(VALUE_PROP)));
                }
            }
        }
        return comment;
    }

    /**
     * 获取 ApiDoc 中的注释
     *
     * @param comment   注释
     * @param className 类
     * @param fieldName 字段
     * @return ApiDoc 中的注释
     * @author lifeng
     * @date 2020/3/28 16:25
     */
    private String processClassFieldComment(String comment, String className, String fieldName) {
        if (StringUtils.isBlank(comment)) {
            try {
                Class<?> clzz = Class.forName(className);
                Field filed = clzz.getDeclaredField(fieldName);
                ApiDoc apiDoc = filed.getAnnotation(ApiDoc.class);
                if (apiDoc != null) {
                    return apiDoc.value();
                }
            } catch (Exception ignored) {
            }
        }
        return comment;
    }

    /**
     * 创建 验证信息
     *
     * @param param api method
     * @param field 具体的方法
     * @author lifeng
     * @date 2020/3/28 15:35
     */
    private void buildVaildAndJson(ApiParam param, JavaField field) {
        List<JavaAnnotation> annotations = field.getAnnotations();

        // Valid
        if (annotations != null && annotations.size() > 0) {
            for (JavaAnnotation annotation : annotations) {
                String annotationName = annotation.getType().getCanonicalName();
                if (VALID_PARAMS.contains(annotationName)) {
                    String valid = annotation.getType().getName() + "\t" +
                            StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(VALUE_PROP))) +
                            "\t" +
                            StringUtils.removeQuotes(String.valueOf(annotation.getNamedParameter(MSG_PROP)));
                    param.addValid(valid);
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
     * @param simpleName 类的字符串
     * @return java类
     * @author lifeng
     * @date 2020/3/28 15:38
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
     * 类的字段
     *
     * @param cls1 java 类
     * @param i    第一个参数
     * @return 所有符合条件的字段
     * @author lifeng
     * @date 2020/3/28 15:39
     */
    private List<JavaField> getFields(JavaClass cls1, int i) {
        List<JavaField> fieldList = Lists.newArrayList();
        if (!(null == cls1 || CLASS_Object.equals(cls1.getSimpleName()) || CLASS_Timestamp.equals(cls1.getSimpleName())
                || CLASS_Date.equals(cls1.getSimpleName()) || CLASS_Locale.equals(cls1.getSimpleName()))) {
            JavaClass pcls = cls1.getSuperJavaClass();
            fieldList.addAll(getFields(pcls, i));
            fieldList.addAll(cls1.getFields());
        }
        return fieldList;
    }
}
