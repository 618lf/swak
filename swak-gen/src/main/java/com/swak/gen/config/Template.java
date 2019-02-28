package com.swak.gen.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.swak.entity.BaseEntity;

/**
 * 模版
 * @author lifeng
 */
@XmlRootElement(name="template")
public class Template extends BaseEntity<Long> implements Serializable{
    
	private static final long serialVersionUID = 8538971826003974983L;
	
	private String name;
    private String category;
    private String filePath;
    private String fileName;
    private String templatePath;
    private String content;
    
	@Override
	public Long getId() {
		return this.id;
	}
	@Override
	public void setId(Long id) {
		this.id = id;
	}
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@XmlElement(name = "filePath")
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	@XmlElement(name = "fileName")
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	@XmlElement(name = "templatePath")
	public String getTemplatePath() {
		return templatePath;
	}
	public void setTemplatePath(String templatePath) {
		this.templatePath = templatePath;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
}