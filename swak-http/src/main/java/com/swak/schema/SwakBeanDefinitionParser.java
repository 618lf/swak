package com.swak.schema;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.ManagedSet;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.util.StringUtils;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import com.swak.mvc.converter.DateConverter;
import com.swak.mvc.converter.StringEscapeConverter;
import com.swak.mvc.method.DefaultExceptionHandler;
import com.swak.mvc.method.RequestMappingHandlerAdapter;
import com.swak.mvc.method.RequestMappingHandlerMapping;

/**
 * 服务器解析器 - 提供一些默认的配置
 * @author lifeng
 */
public class SwakBeanDefinitionParser implements BeanDefinitionParser{

	public static final String HANDLER_MAPPING_BEAN_NAME = RequestMappingHandlerMapping.class.getName();
	public static final String HANDLER_ADAPTER_BEAN_NAME = RequestMappingHandlerAdapter.class.getName();
	public static final String EXCEPTION_HANDLER_BEAN_NAME = DefaultExceptionHandler.class.getName();
	
	/**
	 * 基本的解析
	 */
	@Override
	public BeanDefinition parse(Element element, ParserContext parserContext) {
        
		// mapping
		this.parseMapping(element, parserContext);
		
		// adapter
		this.parseAdapter(element, parserContext);
		
		// exception
		this.parseException(element, parserContext);
		
		// servlet
		this.parseServer(element, parserContext);
		
		return null;
	}
	
	/**
	 * 解析Mapping
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseMapping(Element element, ParserContext parserContext) {
		RootBeanDefinition handlerMappingDef = new RootBeanDefinition(RequestMappingHandlerMapping.class);
		handlerMappingDef.setSource(parserContext.extractSource(element));
		handlerMappingDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(HANDLER_MAPPING_BEAN_NAME , handlerMappingDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerMappingDef, HANDLER_MAPPING_BEAN_NAME));
		return new RuntimeBeanReference(HANDLER_MAPPING_BEAN_NAME);
	}
	
	/**
	 * 解析Conversion
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseConversion(Element element, ParserContext parserContext) {
		RuntimeBeanReference conversionServiceRef;
		if (element.hasAttribute("conversion-service")) {
			conversionServiceRef = new RuntimeBeanReference(element.getAttribute("conversion-service"));
		}
		else {
			RootBeanDefinition conversionDef = new RootBeanDefinition(FormattingConversionServiceFactoryBean.class);
			conversionDef.setSource(parserContext.extractSource(element));
			conversionDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
			
			// 默认的转化器
			ManagedSet<?> converters = this.getPropertyConverters(element, parserContext.extractSource(element), parserContext);
			conversionDef.getPropertyValues().add("converters", converters);
			
			String conversionName = parserContext.getReaderContext().registerWithGeneratedName(conversionDef);
			parserContext.registerComponent(new BeanComponentDefinition(conversionDef, conversionName));
			conversionServiceRef = new RuntimeBeanReference(conversionName);
		}
		return conversionServiceRef;
	}
	
	private ManagedSet<?> getPropertyConverters(Element element, Object source, ParserContext parserContext) {
		ManagedSet<? super Object> propertyConverters = new ManagedSet<Object>();
		propertyConverters.setSource(source);
		propertyConverters.add(createConverterDefinition(DateConverter.class, source));
		propertyConverters.add(createConverterDefinition(StringEscapeConverter.class, source));
		return propertyConverters;
	}
	
	private RootBeanDefinition createConverterDefinition(Class<?> converterClass, Object source) {
		RootBeanDefinition beanDefinition = new RootBeanDefinition(converterClass);
		beanDefinition.setSource(source);
		beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		return beanDefinition;
	}
	
	/**
	 * 解析Adapter
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseAdapter(Element element, ParserContext parserContext) {
		RootBeanDefinition handlerAdapterDef = new RootBeanDefinition(RequestMappingHandlerAdapter.class);
		handlerAdapterDef.setSource(parserContext.extractSource(element));
		handlerAdapterDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		handlerAdapterDef.getPropertyValues().add("conversionService", parseConversion(element, parserContext));
		parserContext.getRegistry().registerBeanDefinition(HANDLER_ADAPTER_BEAN_NAME , handlerAdapterDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerAdapterDef, HANDLER_ADAPTER_BEAN_NAME));
		return new RuntimeBeanReference(HANDLER_ADAPTER_BEAN_NAME);
	}
	
	/**
	 * 解析Mapping
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseException(Element element, ParserContext parserContext) {
		RootBeanDefinition handlerMappingDef = new RootBeanDefinition(DefaultExceptionHandler.class);
		handlerMappingDef.setSource(parserContext.extractSource(element));
		handlerMappingDef.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		parserContext.getRegistry().registerBeanDefinition(EXCEPTION_HANDLER_BEAN_NAME , handlerMappingDef);
		parserContext.registerComponent(new BeanComponentDefinition(handlerMappingDef, EXCEPTION_HANDLER_BEAN_NAME));
		return new RuntimeBeanReference(EXCEPTION_HANDLER_BEAN_NAME);
	}
	
	/**
	 * 解析filter
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseFilter(Element element, ParserContext parserContext) {
		Element servletElement = DomUtils.getChildElementByTagName(element, "filter");
		if (servletElement != null) {
			String filterName = servletElement.getAttribute("filter-name");
			return new RuntimeBeanReference(filterName);
		}
		return null;
	}
	
	/**
	 * 解析Servlet
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseServlet(Element element, ParserContext parserContext) {
		Element servletElement = DomUtils.getChildElementByTagName(element, "servlet");
		if (servletElement != null) {
			String servletName = servletElement.getAttribute("servlet-name");
			return new RuntimeBeanReference(servletName);
		}
		return null;
	}
	
	/**
	 * 解析pool
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parsePool(Element element, ParserContext parserContext) {
		Element servletElement = DomUtils.getChildElementByTagName(element, "pool");
		if (servletElement != null) {
			String filterName = servletElement.getAttribute("pool-name");
			return new RuntimeBeanReference(filterName);
		}
		return null;
	}
	
	/**
	 * 解析Server
	 * @param element
	 * @param parserContext
	 * @return
	 */
	private RuntimeBeanReference parseServer(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(ServerBean.class);
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		builder.setLazyInit(false);
		String host = element.getAttribute("host");  
        String port = element.getAttribute("port");
        String timeout = element.getAttribute("timeout");
        String tcpNoDelay = element.getAttribute("tcpNoDelay");
        String soKeepAlive = element.getAttribute("soKeepAlive");
        String startReport = element.getAttribute("startReport");
        String enableGzip = element.getAttribute("enableGzip");
        String enableCors = element.getAttribute("enableCors");
        String sslOn = element.getAttribute("sslOn");
        String certFilePath = element.getAttribute("certFilePath");
        String privateKeyPath = element.getAttribute("privateKeyPath");
        String privateKeyPassword = element.getAttribute("privateKeyPassword");
        builder.addPropertyValue("host", host);
        builder.addPropertyValue("port", port);
        if (!StringUtils.isEmpty(timeout)) {
        	builder.addPropertyValue("timeout", timeout);
        }
        if (!StringUtils.isEmpty(tcpNoDelay)) {
        	builder.addPropertyValue("tcpNoDelay", tcpNoDelay);
        }
        if (!StringUtils.isEmpty(soKeepAlive)) {
        	builder.addPropertyValue("soKeepAlive", soKeepAlive);
        }
        if (!StringUtils.isEmpty(startReport)) {
        	builder.addPropertyValue("startReport", startReport);
        }
        if (!StringUtils.isEmpty(enableGzip)) {
        	builder.addPropertyValue("enableGzip", enableGzip);
        }
        if (!StringUtils.isEmpty(enableCors)) {
        	builder.addPropertyValue("enableCors", enableCors);
        }
        if (!StringUtils.isEmpty(sslOn)) {
        	builder.addPropertyValue("sslOn", sslOn);
        }
        if (!StringUtils.isEmpty(certFilePath)) {
        	builder.addPropertyValue("certFilePath", certFilePath);
        }
        if (!StringUtils.isEmpty(privateKeyPath)) {
        	builder.addPropertyValue("privateKeyPath", privateKeyPath);
        }
        if (!StringUtils.isEmpty(privateKeyPassword)) {
        	builder.addPropertyValue("privateKeyPassword", privateKeyPassword);
        }
        RootBeanDefinition serverBeanDefinition = (RootBeanDefinition)builder.getBeanDefinition();
        RuntimeBeanReference filterRef = this.parseFilter(element, parserContext);
        if (filterRef != null) {
        	serverBeanDefinition.getPropertyValues().add("filter", filterRef);
        }
        serverBeanDefinition.getPropertyValues().add("servlet", this.parseServlet(element, parserContext));
        RuntimeBeanReference poolRef = this.parsePool(element, parserContext);
        if (filterRef != null) {
        	serverBeanDefinition.getPropertyValues().add("pool", poolRef);
        }
        String name = parserContext.getReaderContext().registerWithGeneratedName(serverBeanDefinition);
        parserContext.getReaderContext().getRegistry().registerBeanDefinition(name , serverBeanDefinition);
        return new RuntimeBeanReference(name);
	}
}