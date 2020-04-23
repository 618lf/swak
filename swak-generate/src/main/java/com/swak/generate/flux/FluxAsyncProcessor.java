package com.swak.generate.flux;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.TypeParameterElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.swak.Constants;
import com.swak.annotation.FluxAsync;

/**
 * 自动身成异步处理的接口类 --- 目前不支持注解
 *
 * @author lifeng
 */
public class FluxAsyncProcessor extends AbstractProcessor {

	protected static String ASYNC = Constants.ASYNC_SUFFIX;
	protected static String GENERATE_PATH_KEY = "fluxGeneratePath";
	protected static String TARGET_DIR;

	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		String path = processingEnv.getOptions().get(GENERATE_PATH_KEY);// use javac complie options
		// -AfluxGeneratePath=xxx
		if (path != null) {
			TARGET_DIR = path;
		} else { // use jvm option -DfluxGeneratePath=xxx
			TARGET_DIR = System.getProperty(GENERATE_PATH_KEY, "src/main/generated-sources/");
		}
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		HashSet<String> types = new HashSet<>();
		types.add(FluxAsync.class.getName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		Set<? extends Element> elems = roundEnv.getElementsAnnotatedWith(FluxAsync.class);
		if (elems == null || elems.isEmpty()) {
			return true;
		}
		for (Element elem : roundEnv.getElementsAnnotatedWith(FluxAsync.class)) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					"FluxAsyncProcessor will process " + elem.toString() + ", generate class path:" + TARGET_DIR);
			try {
				writeAsyncClass(elem);
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						"FluxAsyncProcessor done for " + elem.toString());
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
						"FluxAsyncProcessor process " + elem.toString() + " fail. exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return true;
	}

	// 写入异步接口
	private void writeAsyncClass(Element elem) throws Exception {

		// 处理的类类型
		TypeElement type = (TypeElement) elem;
		String className = type.getSimpleName().toString();

		// 如果有接口则根据接口来生成
		if (elem.getKind().isClass() && type.getInterfaces() != null && type.getInterfaces().size() != 0) {
			type = (TypeElement) ((DeclaredType) type.getInterfaces().get(0)).asElement();
			className = type.getSimpleName().toString();
		}

		// 类定义器
		TypeSpec.Builder builder = TypeSpec.interfaceBuilder(className + ASYNC).addModifiers(Modifier.PUBLIC);

		// 级联创建
		this.cascadeBuildIn(builder, type, new HashMap<>());

		// 将类写入文件
		JavaFile javaFile = JavaFile
				.builder(processingEnv.getElementUtils().getPackageOf(type).getQualifiedName().toString(),
						builder.build())
				.build();

		javaFile.writeTo(new File(System.getProperty("basedir"), TARGET_DIR));
	}

	// 级联的创建
	@SuppressWarnings("unchecked")
	private void cascadeBuildIn(TypeSpec.Builder builder, TypeElement type,
			Map<TypeMirror, TypeMirror> genericTypeMirrorMappers) {

		// 添加本类中的方法
		this.buildIn(builder, type, genericTypeMirrorMappers);

		// 如果是接口类型，则查找所有的接口体系
		List<TypeMirror> extendsTypes = null;
		if (type.getKind().isInterface()) {
			extendsTypes = (List<TypeMirror>) type.getInterfaces();
		}
		// 如果是类类型， 则查找类体系
		else if (!ClassName.get(type.getSuperclass()).equals(ClassName.OBJECT)) {
			extendsTypes = new ArrayList<>();
			extendsTypes.add(type.getSuperclass());
		}

		// 处理父类或接口
		if (extendsTypes != null) {
			for (TypeMirror sTypeMirror : extendsTypes) {
				if (sTypeMirror.getKind().equals(TypeKind.DECLARED)) {

					// 类型
					TypeElement de = (TypeElement) ((DeclaredType) sTypeMirror).asElement();

					// 类型参数的转换
					Map<TypeMirror, TypeMirror> actualMappers = this.genericTypeMirrorMappers(
							((DeclaredType) sTypeMirror).getTypeArguments(), de.getTypeParameters(),
							genericTypeMirrorMappers);

					// 级联添加
					cascadeBuildIn(builder, de, actualMappers);
				}
			}
		}
	}

	/**
	 * 泛型的实际类型 -- 泛型类型
	 * 
	 * @param interfaceClazz 类或接口
	 * @return 泛型的实际类型
	 */
	private Map<TypeMirror, TypeMirror> genericTypeMirrorMappers(List<? extends TypeMirror> typeArguments,
			List<? extends TypeParameterElement> typeParameters, Map<TypeMirror, TypeMirror> actualMappers) {
		Map<TypeMirror, TypeMirror> genericTypeMirrorMappers = new HashMap<>();
		for (int i = 0; i < typeParameters.size(); i++) {

			// 实际的类型
			TypeMirror actualType = typeArguments.get(i);

			// 类型转换
			actualType = this.getActualParameterizedType(actualType, actualMappers);

			// 这一次处理中的类型
			genericTypeMirrorMappers.put(typeParameters.get(i).asType(), actualType);
		}
		return genericTypeMirrorMappers;
	}

	// 封装之后的类型
	private TypeMirror getActualParameterizedType(TypeMirror type, Map<TypeMirror, TypeMirror> actualMappers) {
		if (actualMappers == null || actualMappers.isEmpty()) {
			return type;
		}
		return actualMappers.get(type);
	}

	// 处理本类中方法
	private void buildIn(TypeSpec.Builder builder, TypeElement type,
			Map<TypeMirror, TypeMirror> genericTypeMirrorMappers) {

		Map<String, TypeName> genericTypeAttrMappers = this.genericTypeAttrMappers(genericTypeMirrorMappers);

		// 声明的元素
		List<? extends Element> elements = type.getEnclosedElements();

		if (elements != null && !elements.isEmpty()) {
			for (Element e : elements) {
				if (ElementKind.METHOD.equals(e.getKind())) {

					// 方法
					ExecutableElement method = (ExecutableElement) e;

					// 只处理公共方法
					if (!method.getModifiers().contains(Modifier.PUBLIC)) {
						continue;
					}

					// 构建方法
					MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
							.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
							.returns(getReturnType(method.getReturnType(), genericTypeMirrorMappers,
									genericTypeAttrMappers))
							.addTypeVariables(getTypeNames(method.getTypeParameters()))
							.addAnnotations(getAnnotationSpec(method.getAnnotationMirrors()));

					// 方法的参数
					List<? extends VariableElement> vars = method.getParameters();
					for (VariableElement var : vars) {
						TypeName paTypeName = getParamType(var.asType(), genericTypeMirrorMappers,
								genericTypeAttrMappers);
						methodBuilder.addParameter(
								ParameterSpec.builder(paTypeName, var.getSimpleName().toString()).build());
					}
					builder.addMethod(methodBuilder.build());
				}
			}
		}
	}

	// 类型变量名称和类型名称对应
	private Map<String, TypeName> genericTypeAttrMappers(Map<TypeMirror, TypeMirror> genericTypeMirrorMappers) {
		Map<String, TypeName> genericTypeAttrMappers = new HashMap<>();
		genericTypeMirrorMappers.keySet().forEach(key -> {
			TypeName typeName = TypeName.get(key);
			if (typeName instanceof TypeVariableName) {
				genericTypeAttrMappers.put(((TypeVariableName) typeName).name,
						TypeName.get(genericTypeMirrorMappers.get(key)));
			}
		});
		return genericTypeAttrMappers;
	}

	/**
	 * 返回类型
	 */
	private TypeName getReturnType(TypeMirror type, Map<TypeMirror, TypeMirror> genericTypeMirrorMappers,
			Map<String, TypeName> genericTypeAttrMappers) {

		// 实际的参数类型
		TypeName realType = this.getParamType(type, genericTypeMirrorMappers, genericTypeAttrMappers);

		// 判断是否异步接口
		if (realType instanceof ParameterizedTypeName) {

			// 转换为泛型类型
			ParameterizedTypeName pTypeName = (ParameterizedTypeName) realType;

			// 如果已经是异步接口则不用再次封装
			if (pTypeName.rawType.canonicalName().equals("java.util.concurrent.CompletableFuture")
					|| pTypeName.rawType.canonicalName().equals("java.util.concurrent.CompletionStage")) {
				return realType;
			}
		}

		// 异步返回
		return ParameterizedTypeName.get(ClassName.get(CompletableFuture.class), realType);
	}

	// 方法参数
	private TypeName getParamType(TypeMirror type, Map<TypeMirror, TypeMirror> genericTypeMirrorMappers,
			Map<String, TypeName> genericTypeAttrMappers) {

		// 第一次转换 直接使用的泛型
		type = genericTypeMirrorMappers.getOrDefault(type, type);

		// 获取非集合类型
		TypeName realType = this.getNonCollectionTypeName(type);

		// 如果是泛型类型
		if (realType instanceof ParameterizedTypeName) {

			// 转换为泛型类型
			ParameterizedTypeName pTypeName = (ParameterizedTypeName) realType;

			// 如果的泛型类型
			List<TypeName> realTypes = new ArrayList<>();
			for (TypeName attr : pTypeName.typeArguments) {
				if (attr instanceof TypeVariableName) {
					realTypes.add(genericTypeAttrMappers.getOrDefault(((TypeVariableName) attr).name, attr));
				} else {
					realTypes.add(attr);
				}
			}

			// 转换实际的类型
			realType = ParameterizedTypeName.get(pTypeName.rawType, realTypes.toArray(new TypeName[0]));

		}

		// 返回实际的类型
		return realType;
	}

	// 没判断集合类型
	private TypeName getNonCollectionTypeName(TypeMirror type) {
		TypeName realType;
		if (type == null || type.getKind().equals(TypeKind.VOID)) {
			realType = ClassName.get(Void.class);
		} else if (type.getKind().isPrimitive()) {
			realType = ClassName.get(type).box();
		} else {
			realType = ClassName.get(type);
		}
		return realType;
	}

	// 类型变量名称
	private List<TypeVariableName> getTypeNames(List<? extends TypeParameterElement> types) {
		List<TypeVariableName> result = new ArrayList<>();
		if (types != null && !types.isEmpty()) {
			for (TypeParameterElement type : types) {
				result.add(TypeVariableName.get(type));
			}
		}
		return result;
	}

	// 注解的复制
	private List<AnnotationSpec> getAnnotationSpec(List<? extends AnnotationMirror> types) {
		return new ArrayList<>();
	}
}