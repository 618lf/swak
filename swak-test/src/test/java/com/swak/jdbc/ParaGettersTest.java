package com.swak.jdbc;

public class ParaGettersTest {

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void main(String[] args) {
		Type type = (Type) Enum.valueOf((Class) Type.class, "Name");
		System.out.println(type);
		System.out.println(type.toString());
	}
}

enum Type {
	Name(1);

	int i;

	private Type(int i) {
		this.i = i;
	}
}
