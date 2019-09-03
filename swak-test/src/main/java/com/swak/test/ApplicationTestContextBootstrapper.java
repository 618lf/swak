package com.swak.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.MapConfigurationPropertySource;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.test.context.ContextConfigurationAttributes;
import org.springframework.test.context.ContextLoader;
import org.springframework.test.context.MergedContextConfiguration;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.support.DefaultTestContextBootstrapper;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import com.swak.ApplicationBoot;

public class ApplicationTestContextBootstrapper extends DefaultTestContextBootstrapper {

	/**
	 * 是否是 WEB 环境
	 */
	private static final String REACTIVE_FLUX_ENVIRONMENT_CLASS = "com.swak.reactivex.transport.http.server.ReactiveServer";
	private static final String REACTIVE_VERT_ENVIRONMENT_CLASS = "com.swak.vertx.transport.server.ReactiveServer";
	private static final Log logger = LogFactory.getLog(ApplicationTestContextBootstrapper.class);

	@Override
	public TestContext buildTestContext() {
		TestContext context = super.buildTestContext();
		return context;
	}

	@Override
	protected Set<Class<? extends TestExecutionListener>> getDefaultTestExecutionListenerClasses() {
		Set<Class<? extends TestExecutionListener>> listeners = super.getDefaultTestExecutionListenerClasses();
		return listeners;
	}

	@Override
	protected ContextLoader resolveContextLoader(Class<?> testClass,
			List<ContextConfigurationAttributes> configAttributesList) {
		Class<?>[] classes = getClasses(testClass);
		if (!ObjectUtils.isEmpty(classes)) {
			for (ContextConfigurationAttributes configAttributes : configAttributesList) {
				addConfigAttributesClasses(configAttributes, classes);
			}
		}
		return super.resolveContextLoader(testClass, configAttributesList);
	}

	private void addConfigAttributesClasses(ContextConfigurationAttributes configAttributes, Class<?>[] classes) {
		List<Class<?>> combined = new ArrayList<>();
		combined.addAll(Arrays.asList(classes));
		if (configAttributes.getClasses() != null) {
			combined.addAll(Arrays.asList(configAttributes.getClasses()));
		}
		configAttributes.setClasses(ClassUtils.toClassArray(combined));
	}

	@Override
	protected Class<? extends ContextLoader> getDefaultContextLoaderClass(Class<?> testClass) {
		return ApplicationContextLoader.class;
	}

	@Override
	protected MergedContextConfiguration processMergedContextConfiguration(MergedContextConfiguration mergedConfig) {
		Class<?>[] classes = getOrFindConfigurationClasses(mergedConfig);
		List<String> propertySourceProperties = getAndProcessPropertySourceProperties(mergedConfig);
		mergedConfig = createModifiedConfig(mergedConfig, classes, StringUtils.toStringArray(propertySourceProperties));
		WebApplicationType webApplicationType = getWebApplicationType(mergedConfig);
		if (webApplicationType == WebApplicationType.REACTIVE) {
			return new ReactiveWebMergedContextConfiguration(mergedConfig);
		}
		return mergedConfig;
	}

	private WebApplicationType getWebApplicationType(MergedContextConfiguration configuration) {
		ConfigurationPropertySource source = new MapConfigurationPropertySource(
				TestPropertySourceUtils.convertInlinedPropertiesToMap(configuration.getPropertySourceProperties()));
		Binder binder = new Binder(source);
		return binder.bind("spring.main.web-application-type", Bindable.of(WebApplicationType.class))
				.orElseGet(this::deduceWebApplicationType);
	}

	private WebApplicationType deduceWebApplicationType() {
		if (ClassUtils.isPresent(REACTIVE_FLUX_ENVIRONMENT_CLASS, null)
				|| ClassUtils.isPresent(REACTIVE_VERT_ENVIRONMENT_CLASS, null)) {
			return WebApplicationType.REACTIVE;
		}
		return WebApplicationType.NONE;
	}

	protected Class<?>[] getOrFindConfigurationClasses(MergedContextConfiguration mergedConfig) {
		Class<?>[] classes = mergedConfig.getClasses();
		if (mergedConfig.hasLocations()) {
			return classes;
		}
		Class<?> found = new AnnotatedClassFinder(ApplicationBoot.class)
				.findFromClass(mergedConfig.getTestClass());
		Assert.state(found != null, "Unable to find a @SpringBootConfiguration, you need to use "
				+ "@ContextConfiguration or @SpringBootTest(classes=...) " + "with your test");
		logger.info("Found @SpringBootConfiguration " + found.getName() + " for test " + mergedConfig.getTestClass());
		return merge(found, classes);
	}

	private Class<?>[] merge(Class<?> head, Class<?>[] existing) {
		Class<?>[] result = new Class<?>[existing.length + 1];
		result[0] = head;
		System.arraycopy(existing, 0, result, 1, existing.length);
		return result;
	}

	private List<String> getAndProcessPropertySourceProperties(MergedContextConfiguration mergedConfig) {
		List<String> propertySourceProperties = new ArrayList<>(
				Arrays.asList(mergedConfig.getPropertySourceProperties()));
		String differentiator = getDifferentiatorPropertySourceProperty();
		if (differentiator != null) {
			propertySourceProperties.add(differentiator);
		}
		processPropertySourceProperties(mergedConfig, propertySourceProperties);
		return propertySourceProperties;
	}

	/**
	 * Return a "differentiator" property to ensure that there is something to
	 * differentiate regular tests and bootstrapped tests. Without this property a
	 * cached context could be returned that wasn't created by this bootstrapper. By
	 * default uses the bootstrapper class as a property.
	 * 
	 * @return the differentiator or {@code null}
	 */
	protected String getDifferentiatorPropertySourceProperty() {
		return getClass().getName() + "=true";
	}

	/**
	 * Post process the property source properties, adding or removing elements as
	 * required.
	 * 
	 * @param mergedConfig
	 *            the merged context configuration
	 * @param propertySourceProperties
	 *            the property source properties to process
	 */
	protected void processPropertySourceProperties(MergedContextConfiguration mergedConfig,
			List<String> propertySourceProperties) {
		Class<?> testClass = mergedConfig.getTestClass();
		String[] properties = getProperties(testClass);
		if (!ObjectUtils.isEmpty(properties)) {
			propertySourceProperties.addAll(0, Arrays.asList(properties));
		}
	}

	protected Class<?>[] getClasses(Class<?> testClass) {
		ApplicationTest annotation = getAnnotation(testClass);
		return (annotation != null) ? annotation.classes() : null;
	}

	protected String[] getProperties(Class<?> testClass) {
		ApplicationTest annotation = getAnnotation(testClass);
		return (annotation != null) ? annotation.properties() : null;
	}

	protected ApplicationTest getAnnotation(Class<?> testClass) {
		return AnnotatedElementUtils.getMergedAnnotation(testClass, ApplicationTest.class);
	}

	/**
	 * Create a new {@link MergedContextConfiguration} with different classes.
	 * 
	 * @param mergedConfig
	 *            the source config
	 * @param classes
	 *            the replacement classes
	 * @return a new {@link MergedContextConfiguration}
	 */
	protected final MergedContextConfiguration createModifiedConfig(MergedContextConfiguration mergedConfig,
			Class<?>[] classes) {
		return createModifiedConfig(mergedConfig, classes, mergedConfig.getPropertySourceProperties());
	}

	/**
	 * Create a new {@link MergedContextConfiguration} with different classes and
	 * properties.
	 * 
	 * @param mergedConfig
	 *            the source config
	 * @param classes
	 *            the replacement classes
	 * @param propertySourceProperties
	 *            the replacement properties
	 * @return a new {@link MergedContextConfiguration}
	 */
	protected final MergedContextConfiguration createModifiedConfig(MergedContextConfiguration mergedConfig,
			Class<?>[] classes, String[] propertySourceProperties) {
		return new MergedContextConfiguration(mergedConfig.getTestClass(), mergedConfig.getLocations(), classes,
				mergedConfig.getContextInitializerClasses(), mergedConfig.getActiveProfiles(),
				mergedConfig.getPropertySourceLocations(), propertySourceProperties,
				mergedConfig.getContextCustomizers(), mergedConfig.getContextLoader(),
				getCacheAwareContextLoaderDelegate(), mergedConfig.getParent());
	}
}
