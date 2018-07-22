package com.swak.motan.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * BasicReferer Config
 * 
 * @author alanwei
 * @since 2016-09-13
 */
@ConfigurationProperties(prefix = "spring.motan.annotationBean")
public class AnnotationBeanConfigProperties {

	private String scanPackage = "com.tmt";

	public String getScanPackage() {
		return scanPackage;
	}

	public void setScanPackage(String scanPackage) {
		this.scanPackage = scanPackage;
	}
}
