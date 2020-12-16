package com.swak.config.vertx;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;

import com.swak.annotation.FluxReferer;
import com.swak.config.AbstractAnnotationBeanPostProcessor;
import com.swak.utils.AnnotationUtils;
import com.swak.vertx.config.ReferenceBean;

/**
 * 处理 @FluxReference 的自动注入
 * 
 * @author lifeng
 * @date 2020年8月23日 下午2:29:17
 */
@SuppressWarnings("unchecked")
public class FluxReferenceAnnotationBeanPostProcessor extends AbstractAnnotationBeanPostProcessor {

	public static final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";

	private static final int CACHE_SIZE = Integer.getInteger(BEAN_NAME + ".cache.size", 32);

	private final ConcurrentMap<String, ReferenceBean> referenceBeanCache = new ConcurrentHashMap<>(CACHE_SIZE);
	private final ConcurrentMap<InjectionMetadata.InjectedElement, ReferenceBean> injectedFieldReferenceBeanCache = new ConcurrentHashMap<>(
			CACHE_SIZE);

	private final ConcurrentMap<InjectionMetadata.InjectedElement, ReferenceBean> injectedMethodReferenceBeanCache = new ConcurrentHashMap<>(
			CACHE_SIZE);

	public FluxReferenceAnnotationBeanPostProcessor() {
		super(FluxReferer.class);
	}

	@Override
	protected Object doGetInjectedBean(AnnotationAttributes attributes, Object bean, String beanName,
			Class<?> injectedType, InjectedElement injectedElement) throws Exception {

		String referenceBeanName = generateReferenceBeanName(attributes, injectedType);

		ReferenceBean referenceBean = buildReferenceBeanIfAbsent(referenceBeanName, attributes, injectedType);

		registerReferenceBean(referenceBean, referenceBeanName);

		cacheInjectedReferenceBean(referenceBean, injectedElement);

		return referenceBean.get();
	}

	private ReferenceBean buildReferenceBeanIfAbsent(String referenceBeanName, AnnotationAttributes attributes,
			Class<?> referencedType) throws Exception {

		ReferenceBean referenceBean = referenceBeanCache.get(referenceBeanName);

		if (referenceBean == null) {
			referenceBean = this.buildReferenceBean(attributes, referencedType);
			referenceBeanCache.put(referenceBeanName, referenceBean);
		} else if (!referencedType.isAssignableFrom(referenceBean.getType())) {
			throw new IllegalArgumentException(
					"reference bean name " + referenceBeanName + " has been duplicated, but interfaceClass "
							+ referenceBean.getType().getName() + " cannot be assigned to " + referencedType.getName());
		}
		return referenceBean;
	}

	private String generateReferenceBeanName(AnnotationAttributes attributes, Class<?> interfaceClass) {
		StringBuilder beanNameBuilder = new StringBuilder("@Reference");

		if (!attributes.isEmpty()) {
			beanNameBuilder.append('(');
			for (Map.Entry<String, Object> entry : attributes.entrySet()) {
				beanNameBuilder.append(entry.getKey()).append('=').append(entry.getValue()).append(',');
			}
			// replace the latest "," to be ")"
			beanNameBuilder.setCharAt(beanNameBuilder.lastIndexOf(","), ')');
		}

		beanNameBuilder.append(" ").append(interfaceClass.getName());

		return beanNameBuilder.toString();
	}

	private ReferenceBean buildReferenceBean(AnnotationAttributes attributes, Class<?> referencedType) {
		ReferenceBean referenceBean = new ReferenceBean();
		referenceBean.setType(referencedType);
		return referenceBean;
	}

	private void registerReferenceBean(ReferenceBean referenceBean, String beanName) {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (!beanFactory.containsBean(beanName)) {
			beanFactory.autowireBean(referenceBean);
			beanFactory.registerSingleton(beanName, referenceBean);
		}
	}

	private void cacheInjectedReferenceBean(ReferenceBean referenceBean,
			InjectionMetadata.InjectedElement injectedElement) {
		if (injectedElement.getMember() instanceof Field) {
			injectedFieldReferenceBeanCache.put(injectedElement, referenceBean);
		} else if (injectedElement.getMember() instanceof Method) {
			injectedMethodReferenceBeanCache.put(injectedElement, referenceBean);
		}
	}

	@Override
	protected String buildInjectedObjectCacheKey(AnnotationAttributes attributes, Object bean, String beanName,
			Class<?> injectedType, InjectedElement injectedElement) {
		return generateReferenceBeanName(attributes, injectedType) + "#source=" + (injectedElement.getMember())
				+ "#attributes=" + AnnotationUtils.getAttributes(attributes, getEnvironment());
	}
}
