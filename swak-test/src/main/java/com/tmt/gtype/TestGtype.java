package com.tmt.gtype;

import org.springframework.core.ResolvableType;

/**
 * 获得范型
 * 
 * 只能获得固定下来的范型
 * 
 * @author lifeng
 */
public class TestGtype<T> implements Gtype<T>{

	public void test() {
	    ResolvableType resolvableType = ResolvableType.forClass(this.getClass());
	    System.out.println(resolvableType);
	    Class<?> resolve = resolvableType.getInterfaces()[0].getGeneric(0).resolve();
	    System.out.println(resolve);
	}
	
	public static void main(String[] args) {
		TestGtype<String> gtype = new TestGtype<String>();
		gtype.test();
	}
}

interface Gtype<T> {
	
}
