package com.swak.vertx.transport.async;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
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

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.swak.entity.Result;
import com.swak.vertx.Constants;

/**
 * @VertxAsync
 * 
 * 自动身成异步处理的接口类
 * 
 * @author lifeng
 */
public class VertxAsyncProcessor extends AbstractProcessor {

	protected static String ASYNC = Constants.ASYNC_SUFFIX;
	protected static String GENERATE_PATH_KEY = "motanGeneratePath";
	protected static String TARGET_DIR;

	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		String path = processingEnv.getOptions().get(GENERATE_PATH_KEY);// use javac complie options
																		// -AmotanGeneratePath=xxx
		if (path != null) {
			TARGET_DIR = path;
		} else { // use jvm option -DmotanGeneratePath=xxx
			TARGET_DIR = System.getProperty(GENERATE_PATH_KEY, "target/generated-sources/annotations/");
		}

	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latestSupported();
	}

	@Override
	public Set<String> getSupportedAnnotationTypes() {
		HashSet<String> types = new HashSet<>();
		types.add(VertxAsync.class.getName());
		return types;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (roundEnv.processingOver()) {
			return true;
		}
		for (Element elem : roundEnv.getElementsAnnotatedWith(VertxAsync.class)) {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					"MotanAsyncProcessor will process " + elem.toString() + ", generate class path:" + TARGET_DIR);
			try {
				writeAsyncClass(elem);
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
						"MotanAsyncProcessor done for " + elem.toString());
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
						"MotanAsyncProcessor process " + elem.toString() + " fail. exception:" + e.getMessage());
				e.printStackTrace();
			}
		}
		return true;
	}

	private void writeAsyncClass(Element elem) throws ClassNotFoundException, IOException, Exception {

		if (elem.getKind().isInterface()) {
			TypeElement interfaceClazz = (TypeElement) elem;
			String className = interfaceClazz.getSimpleName().toString();
			TypeSpec.Builder classBuilder = TypeSpec.interfaceBuilder(className + ASYNC).addModifiers(Modifier.PUBLIC);

			// add class generic type
			classBuilder.addTypeVariables(getTypeNames(interfaceClazz.getTypeParameters()));

			// add direct method
			addMethods(interfaceClazz, classBuilder);

			// add method form superinterface
			addSuperInterfaceMethods(interfaceClazz.getInterfaces(), classBuilder);

			// write class
			JavaFile javaFile = JavaFile
					.builder(processingEnv.getElementUtils().getPackageOf(interfaceClazz).getQualifiedName().toString(),
							classBuilder.build())
					.build();

			javaFile.writeTo(new File(System.getProperty("basedir"), TARGET_DIR));

		} else {
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE,
					"MotanAsyncProcessor not process, because " + elem.toString() + " not a interface.");
		}
	}

	private void addMethods(TypeElement interfaceClazz, TypeSpec.Builder classBuilder) {
		List<? extends Element> elements = interfaceClazz.getEnclosedElements();
		if (elements != null && !elements.isEmpty()) {
			for (Element e : elements) {
				if (ElementKind.METHOD.equals(e.getKind())) {
					ExecutableElement method = (ExecutableElement) e;
					MethodSpec.Builder methodBuilder = MethodSpec
							.methodBuilder(method.getSimpleName().toString())
							.addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT).returns(ParameterizedTypeName.get(ClassName.get(CompletableFuture.class), ClassName.get(Result.class)))
							.addTypeVariables(getTypeNames(method.getTypeParameters()));
					// add method params
					List<? extends VariableElement> vars = method.getParameters();
					for (VariableElement var : vars) {
						methodBuilder.addParameter(ParameterSpec
								.builder(TypeName.get(var.asType()), var.getSimpleName().toString()).build());
					}
					classBuilder.addMethod(methodBuilder.build());
				}
			}
		}
	}

	private List<TypeVariableName> getTypeNames(List<? extends TypeParameterElement> types) {
		List<TypeVariableName> result = new ArrayList<TypeVariableName>();
		if (types != null && !types.isEmpty()) {
			for (TypeParameterElement type : types) {
				result.add(TypeVariableName.get(type));
			}
		}
		return result;
	}

	private void addSuperInterfaceMethods(List<? extends TypeMirror> superInterfaces, TypeSpec.Builder classBuilder) {
		if (superInterfaces != null && !superInterfaces.isEmpty()) {
			for (TypeMirror tm : superInterfaces) {
				try {
					if (tm.getKind().equals(TypeKind.DECLARED)) {
						TypeElement de = (TypeElement) ((DeclaredType) tm).asElement();
						addMethods(de, classBuilder);
						addSuperInterfaceMethods(de.getInterfaces(), classBuilder);
					}
				} catch (Exception e) {
					processingEnv.getMessager().printMessage(Diagnostic.Kind.WARNING,
							"MotanAsyncProcessor process superinterface " + tm.toString() + " fail. exception:"
									+ e.getMessage());
					e.printStackTrace();
				}
			}
		}
	}
}
