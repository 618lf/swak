package com.swak.generate.flux;

import java.io.File;
import java.util.ArrayList;
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
import com.swak.utils.Lists;
import com.swak.utils.Maps;

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

	private void writeAsyncClass(Element elem) throws Exception {

		TypeElement interfaceClazz = (TypeElement) elem;
		String className = interfaceClazz.getSimpleName().toString();

		// 如果有接口则使用接口的名称
		if (elem.getKind().isClass() && interfaceClazz.getInterfaces() != null
				&& interfaceClazz.getInterfaces().size() != 0) {
			// Definition className with one interface
			TypeElement de = (TypeElement) ((DeclaredType) interfaceClazz.getInterfaces().get(0)).asElement();
			className = de.getSimpleName().toString();
		}

		TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(className + ASYNC).addModifiers(Modifier.PUBLIC);

		// add class generic type
		classBuilder.addTypeVariables(getTypeNames(interfaceClazz.getTypeParameters()));

		// is class and not interfaces
		if (elem.getKind().isClass()
				&& (interfaceClazz.getInterfaces() == null || interfaceClazz.getInterfaces().size() == 0)) {

			// add direct method
			addMethods(interfaceClazz, classBuilder);

			// add superClass methods
			addSuperClassMethods(interfaceClazz.getSuperclass(), classBuilder);
		}

		// is class and has interfaces
		else if (elem.getKind().isClass()) {

			// add method form super interfaces
			addSuperInterfaceMethods(interfaceClazz.getInterfaces(), classBuilder);
		}

		// is interface
		else {

			// add direct method
			addMethods(interfaceClazz, classBuilder);

			// add method form super interfaces
			addSuperInterfaceMethods(interfaceClazz.getInterfaces(), classBuilder);
		}

		// write class
		JavaFile javaFile = JavaFile
				.builder(processingEnv.getElementUtils().getPackageOf(interfaceClazz).getQualifiedName().toString(),
						classBuilder.build())
				.build();

		javaFile.writeTo(new File(System.getProperty("basedir"), TARGET_DIR));
	}

	/**
	 * 添加方法
	 * 
	 * @param interfaceClazz 接口或类
	 * @param classBuilder   类创建器
	 */
	private void addMethods(TypeElement interfaceClazz, TypeSpec.Builder classBuilder) {
		this.addMethods(interfaceClazz, null, classBuilder);
	}

	/**
	 * 添加方法
	 * 
	 * @param interfaceClazz 接口或类
	 * @param declaredType   接口或类的定义
	 * @param classBuilder   类 创建器
	 */
	private void addMethods(TypeElement interfaceClazz, DeclaredType declaredType, TypeSpec.Builder classBuilder) {
		Map<TypeMirror, TypeMirror> genericTypeMirrorMappers = this.genericTypeMirrorMappers(interfaceClazz,
				declaredType);
		Map<String, TypeName> genericTypeAttrMappers = this.genericTypeAttrMappers(genericTypeMirrorMappers);
		List<? extends Element> elements = interfaceClazz.getEnclosedElements();
		if (elements != null && !elements.isEmpty()) {
			for (Element e : elements) {
				if (ElementKind.METHOD.equals(e.getKind())) {
					ExecutableElement method = (ExecutableElement) e;

					// must public
					if (!method.getModifiers().contains(Modifier.PUBLIC)) {
						continue;
					}

					MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder(method.getSimpleName().toString())
							.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
							.returns(getReturnType(method.getReturnType(), genericTypeMirrorMappers,
									genericTypeAttrMappers))
							.addTypeVariables(getTypeNames(method.getTypeParameters()))
							.addAnnotations(getAnnotationSpec(method.getAnnotationMirrors()));

					// add method params
					List<? extends VariableElement> vars = method.getParameters();
					for (VariableElement var : vars) {
						TypeName paTypeName = getParamType(var.asType(), genericTypeMirrorMappers,
								genericTypeAttrMappers);
						methodBuilder.addParameter(
								ParameterSpec.builder(paTypeName, var.getSimpleName().toString()).build());
					}
					classBuilder.addMethod(methodBuilder.build());
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
	private Map<TypeMirror, TypeMirror> genericTypeMirrorMappers(TypeElement interfaceClazz,
			DeclaredType declaredType) {
		Map<TypeMirror, TypeMirror> genericTypeMirrorMappers = Maps.newHashMap();
		List<? extends TypeParameterElement> genericTypes = interfaceClazz.getTypeParameters();
		if (genericTypes.size() == 0) {
			return genericTypeMirrorMappers;
		}
		if (declaredType != null) {
			List<? extends TypeMirror> realTypes = declaredType.getTypeArguments();
			for (int i = 0; i < genericTypes.size(); i++) {
				genericTypeMirrorMappers.put(genericTypes.get(i).asType(), realTypes.get(i));
			}
		}
		return genericTypeMirrorMappers;
	}

	/**
	 * 泛型的实际类型 -- 泛型参数
	 * 
	 * @param interfaceClazz 类或接口
	 * @return 泛型的实际类型
	 */
	private Map<String, TypeName> genericTypeAttrMappers(Map<TypeMirror, TypeMirror> genericTypeMirrorMappers) {
		Map<String, TypeName> genericTypeAttrMappers = Maps.newHashMap();
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

	/**
	 * 方法参数
	 */
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
			List<TypeName> realTypes = Lists.newArrayList();
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

	/**
	 * 没判断集合类型
	 * 
	 * @param type
	 * @return
	 */
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

	private List<TypeVariableName> getTypeNames(List<? extends TypeParameterElement> types) {
		List<TypeVariableName> result = new ArrayList<>();
		if (types != null && !types.isEmpty()) {
			for (TypeParameterElement type : types) {
				result.add(TypeVariableName.get(type));
			}
		}
		return result;
	}

	/**
	 * 不支持注解的复制
	 *
	 * @param types 类型
	 * @return 注解
	 */
	private List<AnnotationSpec> getAnnotationSpec(List<? extends AnnotationMirror> types) {
		// if (types != null && !types.isEmpty()) {
//			for (AnnotationMirror type : types) {
//				AnnotationSpec.Builder builder = AnnotationSpec
//						.builder((ClassName) ClassName.get(type.getAnnotationType()));
//				Map<? extends ExecutableElement, ? extends AnnotationValue> values = type.getElementValues();
//				if (values != null && !values.isEmpty()) {
//					values.forEach((key, value) -> {
//
//					});
//				}
//				result.add(builder.build());
//			}
//		}
		return new ArrayList<>();
	}

	private void addSuperInterfaceMethods(List<? extends TypeMirror> superInterfaces, TypeSpec.Builder classBuilder) {
		if (superInterfaces != null && !superInterfaces.isEmpty()) {
			for (TypeMirror tm : superInterfaces) {
				try {
					if (tm.getKind().equals(TypeKind.DECLARED)) {
						TypeElement de = (TypeElement) ((DeclaredType) tm).asElement();
						addMethods(de, (DeclaredType) tm, classBuilder);
						addSuperInterfaceMethods(de.getInterfaces(), classBuilder);
					}
				} catch (Exception e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
							"FluxAsyncProcessor process superinterface " + tm.toString() + " fail. exception:"
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}

	private void addSuperClassMethods(TypeMirror superClass, TypeSpec.Builder classBuilder) {
		if (superClass != null && !ClassName.get(superClass).equals(ClassName.OBJECT)) {
			try {
				if (superClass.getKind().equals(TypeKind.DECLARED)) {
					TypeElement de = (TypeElement) ((DeclaredType) superClass).asElement();

					// is class and not interfaces
					if (de.getKind().isClass() && (de.getInterfaces() == null || de.getInterfaces().size() == 0)) {

						// add direct method
						addMethods(de, (DeclaredType) superClass, classBuilder);

						// add superClass methods
						addSuperClassMethods(de.getSuperclass(), classBuilder);
					}

					// is Interface or is class and has interfaces
					else {
						// add method form super interfaces
						addSuperInterfaceMethods(de.getInterfaces(), classBuilder);
					}
				}
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
						"FluxAsyncProcessor process superClass " + superClass.toString() + " fail. exception:"
								+ e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
