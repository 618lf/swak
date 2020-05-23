package com.swak.test.builder;

import java.io.File;

import org.junit.Test;

import com.thoughtworks.qdox.JavaProjectBuilder;
import com.thoughtworks.qdox.model.JavaClass;

public class TestBuilder {

	@Test
	public void test() {
		JavaProjectBuilder builder = new JavaProjectBuilder();
		builder.addSourceTree(new File("src/main/java"));
		
		JavaClass clazz = builder.getClassByName("com.swak.entity.Parameters");
		System.out.println(clazz);
	}
}
