package com.swak.config.dubbo;

import static com.alibaba.spring.util.AnnotationUtils.getAttribute;
import static org.springframework.beans.factory.support.BeanDefinitionBuilder.rootBeanDefinition;
import static org.springframework.util.StringUtils.hasText;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.spring.beans.factory.annotation.ReferenceAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.annotation.InjectionMetadata.InjectedElement;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;

import com.swak.vertx.config.ReferenceBean;
import com.swak.vertx.config.ServiceBean;

/**
 * dubbo 的桥接服务
 * 
 * @author lifeng
 * @date 2021年1月14日 下午5:25:45
 */
public class ReferenceBridgeAnnotationBeanPostProcessor extends ReferenceAnnotationBeanPostProcessor {

	private Environment environment;
	private OriginalReferenceRegistry originalRegistry;
	private BridgeReferenceRegistry referenceBridgeRegistry;
	private BridgeServiceRegistry serviceBridgeRegistry;

	public ReferenceBridgeAnnotationBeanPostProcessor() {
		originalRegistry = new OriginalReferenceRegistry();
		referenceBridgeRegistry = new BridgeReferenceRegistry();
		serviceBridgeRegistry = new BridgeServiceRegistry();
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	protected Object doGetInjectedBean(AnnotationAttributes attributes, Object bean, String beanName,
			Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) throws Exception {

		// 服务名称
		String referenceBeanName = this.originalRegistry.getReferenceBeanName(attributes, injectedType);

		// 注册原生服务
		Object originalReference = this.originalRegistry.doGetInjectedBean(attributes, beanName, referenceBeanName,
				injectedType, injectedElement);

		// 注册桥接引用
		Object bridgeReference = this.referenceBridgeRegistry.doGetInjectedBean(attributes, beanName, referenceBeanName,
				injectedType, injectedElement);
		
		// 注册桥接服务
		this.serviceBridgeRegistry.registryServiceBean();

		return bridgeReference;
	}

	/**
	 * 原服务注册
	 * 
	 * @author lifeng
	 * @date 2021年1月14日 下午5:56:42
	 */
	class OriginalReferenceRegistry {

		protected Object doGetInjectedBean(AnnotationAttributes attributes, Object bean, String beanName,
				Class<?> injectedType, InjectionMetadata.InjectedElement injectedElement) throws Exception {
			return ReferenceBridgeAnnotationBeanPostProcessor.super.doGetInjectedBean(attributes, bean, beanName,
					injectedType, injectedElement);
		}

		/**
		 * Get the bean name of {@link ReferenceBean} if {@link Reference#id() id
		 * attribute} is present, or
		 * {@link #generateReferenceBeanName(AnnotationAttributes, Class) generate}.
		 *
		 * @param attributes     the {@link AnnotationAttributes attributes} of
		 *                       {@link Reference @Reference}
		 * @param interfaceClass the {@link Class class} of Service interface
		 * @return non-null
		 * @since 2.7.3
		 */
		private String getReferenceBeanName(AnnotationAttributes attributes, Class<?> interfaceClass) {
			// id attribute appears since 2.7.3
			String beanName = getAttribute(attributes, "id");
			if (!hasText(beanName)) {
				beanName = generateReferenceBeanName(attributes, interfaceClass);
			}
			return beanName;
		}

		/**
		 * Build the bean name of {@link ReferenceBean}
		 *
		 * @param attributes     the {@link AnnotationAttributes attributes} of
		 *                       {@link Reference @Reference}
		 * @param interfaceClass the {@link Class class} of Service interface
		 * @return
		 * @since 2.7.3
		 */
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
	}

	/**
	 * 桥接引用注册
	 * 
	 * @author lifeng
	 * @date 2021年1月14日 下午5:33:34
	 */
	class BridgeReferenceRegistry {

		public final String BEAN_NAME = "referenceAnnotationBeanPostProcessor";

		private final int CACHE_SIZE = Integer.getInteger(BEAN_NAME + ".cache.size", 32);

		private final ConcurrentMap<String, ReferenceBean> referenceBeanCache = new ConcurrentHashMap<>(CACHE_SIZE);
		private final ConcurrentMap<InjectionMetadata.InjectedElement, ReferenceBean> injectedFieldReferenceBeanCache = new ConcurrentHashMap<>(
				CACHE_SIZE);

		private final ConcurrentMap<InjectionMetadata.InjectedElement, ReferenceBean> injectedMethodReferenceBeanCache = new ConcurrentHashMap<>(
				CACHE_SIZE);

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
				throw new IllegalArgumentException("reference bean name " + referenceBeanName
						+ " has been duplicated, but interfaceClass " + referenceBean.getType().getName()
						+ " cannot be assigned to " + referencedType.getName());
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
	}

	/**
	 * 桥接服务注册
	 * 
	 * @author lifeng
	 * @date 2021年1月14日 下午5:33:34
	 */
	class BridgeServiceRegistry {
		
		
		public void registryServiceBean() {
			
		}

		private String generateServiceBeanName(String beanName, Class<?> interfaceClass) {
			return new StringBuilder("@FluxService").append(" ").append(interfaceClass.getName()).toString();
		}

		private AbstractBeanDefinition registryBeans(Class<?> beanClass, Class<?> interClass, String beanName) {
			BeanDefinitionBuilder builder = rootBeanDefinition(ServiceBean.class);
			String resolvedBeanName = environment.resolvePlaceholders(beanName);
			builder.addPropertyReference("ref", resolvedBeanName);
			builder.addPropertyValue("beanClass", beanClass);
			builder.addPropertyValue("interClass", interClass);
			return builder.getBeanDefinition();
		}
	}
}