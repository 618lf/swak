package com.sample.tools.plugin.plugins.codegen.gen;

/**
 * 模版
 * 
 * @author lifeng
 */
public class Template {

	private String name;
	private String content;
	private String filePath;
	private String fileName;

	public String getName() {
		return name;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}