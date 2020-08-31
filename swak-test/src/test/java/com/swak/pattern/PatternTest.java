package com.swak.pattern;

import java.util.regex.Pattern;

import com.swak.utils.StringUtils;

public class PatternTest {

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile(StringUtils.EMPTY);
		System.out.println(pattern.matcher("/").matches());
	}
}
