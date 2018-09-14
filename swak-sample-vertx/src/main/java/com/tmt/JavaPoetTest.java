package com.tmt;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import javax.lang.model.element.Modifier;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.TypeVariableName;
import com.squareup.javapoet.WildcardTypeName;
import com.swak.entity.Result;

/**
 * http://android.walfud.com/javapoet-看这一篇就够了/
 */
public class JavaPoetTest {
	public static void main(String[] args) {
		TypeSpec clazz = clazz(field(), method(code()));
		JavaFile file = JavaFile.builder("com.walfud.howtojavapoet", clazz).build();
		System.out.println(file.toString());
	}

	/**
	 * `public abstract class Clazz<T> extends String implements Serializable,
	 * Comparable<String>, Comparable<? extends String> { ... }`
	 * 
	 * @param fieldSpec
	 * @param methodSpec
	 * @return
	 */
	public static TypeSpec clazz(FieldSpec fieldSpec, MethodSpec methodSpec) {
		return TypeSpec.classBuilder("Clazz").addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
				.addTypeVariable(TypeVariableName.get("T")).superclass(String.class)
				.addSuperinterface(Serializable.class)
				.addSuperinterface(ParameterizedTypeName.get(Comparable.class, String.class))
				.addSuperinterface(ParameterizedTypeName.get(ClassName.get(Comparable.class),
						WildcardTypeName.subtypeOf(String.class)))
				.addType(TypeSpec.classBuilder("InnerClass").build()).addStaticBlock(CodeBlock.builder().build())
				.addInitializerBlock(CodeBlock.builder().build()).addField(fieldSpec).addMethod(methodSpec).build();
	}

	/**
	 * `public static final Map<String, T> mFoo = new HashMap();`
	 * 
	 * @return
	 */
	public static FieldSpec field() {
		return FieldSpec
				.builder(ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(String.class),
						TypeVariableName.get("T")), "mFoo", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
				.initializer("new $T()", HashMap.class).build();
	}

	/**
	 * `@Override public <T> Integer method(String string, T t, Map<Integer, ?
	 * extends T> map) throws IOException, RuntimeException { ... }`
	 * 
	 * @param codeBlock
	 * @return
	 */
	public static MethodSpec method(CodeBlock codeBlock) {
		return MethodSpec.methodBuilder("method").addAnnotation(Override.class)
				.addTypeVariable(TypeVariableName.get("T")).addModifiers(Modifier.PUBLIC)
				.returns(ParameterizedTypeName.get(ClassName.get(CompletableFuture.class), ClassName.get(Result.class)))
				.addParameter(String.class, "string").addParameter(TypeVariableName.get("T"), "t")
				.addParameter(ParameterizedTypeName.get(ClassName.get(Map.class), ClassName.get(Integer.class),
						WildcardTypeName.subtypeOf(TypeVariableName.get("T"))), "map")
				.addException(IOException.class).addException(RuntimeException.class).addCode(codeBlock).build();
	}

	public static CodeBlock code() {
		return CodeBlock.builder().addStatement("int foo = 1").addStatement("$T bar = $S", String.class, "a string")
				// Object obj = new HashMap<Integer, ? extends T>(5);
				.addStatement("$T obj = new $T(5)", Object.class,
						ParameterizedTypeName.get(ClassName.get(HashMap.class), ClassName.get(Integer.class),
								WildcardTypeName.subtypeOf(TypeVariableName.get("T"))))
				// method(new Runnable(String param) {
				// @Override
				// void run() {
				// }
				// });
				.addStatement("baz($L)",
						TypeSpec.anonymousClassBuilder("$T param", String.class).superclass(Runnable.class)
								.addMethod(MethodSpec.methodBuilder("run").addAnnotation(Override.class)
										.returns(TypeName.VOID).build())
								.build())
				// for
				.beginControlFlow("for (int i = 0; i < 5; i++)").endControlFlow()
				// while
				.beginControlFlow("while (false)").endControlFlow()
				// do... while
				.beginControlFlow("do").endControlFlow("while (false)")
				// if... else if... else...
				.beginControlFlow("if (false)").nextControlFlow("else if (false)").nextControlFlow("else")
				.endControlFlow()
				// try... catch... finally
				.beginControlFlow("try").nextControlFlow("catch ($T e)", Exception.class)
				.addStatement("e.printStackTrace()").nextControlFlow("finally").endControlFlow()
				.addStatement("return 0").build();
	}
}