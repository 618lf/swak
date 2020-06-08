package com.sample.tools.plugin.plugins.codegen.gen;

/**
 * 代码生成方案
 * 
 * @author lifeng
 * @date 2020年6月7日 下午4:30:33
 */
public class Scheme {

	private String packageName;
	private String functionName;
	private Table table;

	public Table getTable() {
		return table;
	}

	public void setTable(Table table) {
		this.table = table;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public String getFunctionName() {
		return functionName;
	}

	public void setFunctionName(String functionName) {
		this.functionName = functionName;
	}
}
