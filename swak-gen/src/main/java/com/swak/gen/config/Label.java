package com.swak.gen.config;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="dict")
public class Label implements Serializable{

	private static final long serialVersionUID = 5338892506958356310L;
	
	private String label;
	private String value;
	private String description;
	
	@XmlAttribute
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	@XmlAttribute
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	@XmlElement
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}
