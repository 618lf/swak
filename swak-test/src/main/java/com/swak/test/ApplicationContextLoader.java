package com.swak.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.SpringVersion;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextCustomizer;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.support.AbstractContextLoader;
import org.springframework.test.context.support.AnnotationConfigContextLoaderUtils;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.swak.Application;
import com.swak.test.utils.TestPropertyValues;

public class ApplicationContextLoader extends AbstractContextLoader {

	@Override
	public ApplicationContext loadContext(MergedContextConfiguration config) throws Exception {
		Class<?>[] configClasses = config.getClasses();
		String[] configLocations = config.getLocations();
		Assert.state(!ObjectUtils.isEmpty(configClasses) || !ObjectUtils.isEmpty(configLocations),
				() -> "No configuration classes " + "or locations found in @SpringApplicationConfiguration. "
						+ "For default configuration detection to work you need " + "Spring 4.0.3 or better (found "
						+ SpringVersion.getVersion() + ").");
		SpringApplication application = getSpringApplication();
		application.setMainApplicationClass(config.getTestClass());
		application.addPrimarySources(Arrays.asList(configClasses));
		application.getSources().addAll(Arrays.asList(configLocations));
		ConfigurableEnvironment environment = getEnvironment();
		if (!ObjectUtils.isEmpty(config.getActiveProfiles())) {
			setActiveProfiles(environment, config.getActiveProfiles());
		}
		ResourceLoader resourceLoader = (application.getResourceLoader() != null) ? application.getResourceLoader()
				: new DefaultResourceLoader(getClass().getClassLoader());
		TestPropertySourceUtils.addPropertiesFilesToEnvironment(environment, resourceLoader,
				config.getPropertySourceLocations());
		TestPropertySourceUtils.addInlinedPropertiesToEnvironment(environment, getInlinedProperties(config));
		application.setEnvironment(environment);
		List<ApplicationContextInitializer<?>> initializers = getInitializers(config, application);
		if (config instanceof ReactiveWebMergedContextConfiguration) {
			application.setWebApplicationType(WebApplicationType.REACTIVE);
		} else {
			application.setWebApplicationType(WebApplicationType.NONE);
		}
		application.setInitializers(initializers);
		return application.run();
	}

	/**
	 * Builds new {@link org.springframework.boot.SpringApplication} instance. You
	 * can override this method to add custom behavior
	 * 
	 * @return {@link org.springframework.boot.SpringApplication} instance
	 */
	protected Application getSpringApplication() {
		return new Application();
	}

	/**
	 * Builds a new {@link ConfigurableEnvironment} instance. You can override this
	 * method to return something other than {@link StandardEnvironment} if
	 * necessary.
	 * 
	 * @return a {@link ConfigurableEnvironment} instance
	 */
	protected ConfigurableEnvironment getEnvironment() {
		return new StandardEnvironment();
	}

	private void setActiveProfiles(ConfigurableEnvironment environment, String[] profiles) {
		TestPropertyValues.of("spring.profiles.active=" + StringUtils.arrayToCommaDelimitedString(profiles))
				.applyTo(environment);
	}

	protected String[] getInlinedProperties(MergedContextConfiguration config) {
		ArrayList<String> properties = new ArrayList<>();
		// JMX bean names will clash if the same bean is used in multiple contexts
		disableJmx(properties);
		properties.addAll(Arrays.asList(config.getPropertySourceProperties()));
		if (!isEmbeddedWebEnvironment(config) && !hasCustomServerPort(properties)) {
			properties.add("server.port=-1");
		}
		return StringUtils.toStringArray(properties);
	}

	private void disableJmx(List<String> properties) {
		properties.add("spring.jmx.enabled=false");
	}

	private boolean hasCustomServerPort(List<String> properties) {
		Binder binder = new Binder(convertToConfigurationPropertySource(properties));
		return binder.bind("server.port", Bindable.of(String.class)).isBound();
	}

	private ConfigurationPropertySource convertToConfigurationPropertySource(List<String> properties) {
		return new MapConfigurationPropertySource(
				TestPropertySourceUtils.convertInlinedPropertiesToMap(StringUtils.toStringArray(properties)));
	}

	/**
	 * Return the {@link ApplicationContextInitializer initializers} that will be
	 * applied to the context. By default this method will adapt
	 * {@link ContextCustomizer context customizers}, add
	 * {@link SpringApplication#getInitializers() application initializers} and add
	 * {@link MergedContextConfiguration#getContextInitializerClasses() initializers
	 * specified on the test}.
	 * 
	 * @param config
	 *            the source context configuration
	 * @param application
	 *            the application instance
	 * @return the initializers to apply
	 * @since 2.0.0
	 */
	protected List<ApplicationContextInitializer<?>> getInitializers(MergedContextConfiguration config,
			SpringApplication application) {
		List<ApplicationContextInitializer<?>> initializers = new ArrayList<>();
		for (ContextCustomizer contextCustomizer : config.getContextCustomizers()) {
			initializers.add(new ContextCustomizerAdapter(contextCustomizer, config));
		}
		initializers.addAll(application.getInitializers());
		for (Class<? extends ApplicationContextInitializer<?>> initializerClass : config
				.getContextInitializerClasses()) {
			initializers.add(BeanUtils.instantiateClass(initializerClass));
		}
		if (config.getParent() != null) {
			initializers.add(new ParentContextApplicationContextInitializer(config.getParentApplicationContext()));
		}
		return initializers;
	}

	private boolean isEmbeddedWebEnvironment(MergedContextConfiguration config) {
		return false;
	}

	@Override
	public void processContextConfiguration(ContextConfigurationAttributes configAttributes) {
		super.processContextConfiguration(configAttributes);
		if (!configAttributes.hasResources()) {
			Class<?>[] defaultConfigClasses = detectDefaultConfigurationClasses(configAttributes.getDeclaringClass());
			configAttributes.setClasses(defaultConfigClasses);
		}
	}

	/**
	 * Detect the default configuration classes for the supplied test class. By
	 * default simply delegates to
	 * {@link AnnotationConfigContextLoaderUtils#detectDefaultConfigurationClasses}.
	 * 
	 * @param declaringClass
	 *            the test class that declared {@code @ContextConfiguration}
	 * @return an array of default configuration classes, potentially empty but
	 *         never {@code null}
	 * @see AnnotationConfigContextLoaderUtils
	 */
	protected Class<?>[] detectDefaultConfigurationClasses(Class<?> declaringClass) {
		return AnnotationConfigContextLoaderUtils.detectDefaultConfigurationClasses(declaringClass);
	}

	@Override
	public ApplicationContext loadContext(String... locations) throws Exception {
		throw new UnsupportedOperationException(
				"SpringApplicationContextLoader " + "does not support the loadContext(String...) method");
	}

	@Override
	protected String[] getResourceSuffixes() {
		return new String[] { "-context.xml", "Context.groovy" };
	}

	@Override
	protected String getResourceSuffix() {
		throw new IllegalStateException();
	}

	/**
	 * Adapts a {@link ContextCustomizer} to a {@link ApplicationContextInitializer}
	 * so that it can be triggered via {@link SpringApplication}.
	 */
	private static class ContextCustomizerAdapter
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		private final ContextCustomizer contextCustomizer;

		private final MergedContextConfiguration config;

		ContextCustomizerAdapter(ContextCustomizer contextCustomizer, MergedContextConfiguration config) {
			this.contextCustomizer = contextCustomizer;
			this.config = config;
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			this.contextCustomizer.customizeContext(applicationContext, this.config);
		}

	}

	@Order(Ordered.HIGHEST_PRECEDENCE)
	private static class ParentContextApplicationContextInitializer
			implements ApplicationContextInitializer<ConfigurableApplicationContext> {

		private final ApplicationContext parent;

		ParentContextApplicationContextInitializer(ApplicationContext parent) {
			this.parent = parent;
		}

		@Override
		public void initialize(ConfigurableApplicationContext applicationContext) {
			applicationContext.setParent(this.parent);
		}

	}

}
