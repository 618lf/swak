package com.swak.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

public class SwakNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("server", new SwakServerBeanDefinitionParser());
		registerBeanDefinitionParser("interceptors", new SwakInterceptorsBeanDefinitionParser());
	}
}