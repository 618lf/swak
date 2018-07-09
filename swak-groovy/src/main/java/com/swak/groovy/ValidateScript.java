package com.swak.groovy;

import groovy.lang.Script;

/**
 * 只是一个例子
 * @author lifeng
 */
@Deprecated
public class ValidateScript extends Script {

	@Override
	public Object run() {
		return null;
	}
	
	public static Object add(int i, int j) {
		return i + j;
	}
	
}