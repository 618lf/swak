package com.swak.actuator.endpoint;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.MethodIntrospector.MetadataLookup;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.invoke.OperationMethod;
import com.swak.actuator.endpoint.invoke.OperationParameterResoler;
import com.swak.actuator.endpoint.invoke.ReflectiveOperationInvoker;
import com.swak.utils.StringUtils;

/**
 * 获得 Endpoints
 * @author lifeng
 * @param <E>
 * @param <O>
 */
public abstract class EndpointDiscoverer<E extends ExposableEndpoint<O>, O extends Operation> implements EndpointsSupplier<E> {

	private final ApplicationContext applicationContext;
	private volatile Collection<E> endpoints;
	private final OperationParameterResoler operationParameterResoler;
	
	public EndpointDiscoverer(ApplicationContext applicationContext, OperationParameterResoler operationParameterResoler) {
		this.applicationContext  = applicationContext;
		this.operationParameterResoler = operationParameterResoler;
	}
	
	@Override
	public Collection<E> getEndpoints() {
		if (this.endpoints == null) {
			this.endpoints = discoverEndpoints();
		}
		return this.endpoints;
	}
	
	private Collection<E> discoverEndpoints() {
		Collection<EndpointBean> endpointBeans = createEndpointBeans();
		return convertToEndpoints(endpointBeans);
	}
	
	private Collection<EndpointBean> createEndpointBeans() {
		Map<String, EndpointBean> byId = new LinkedHashMap<>();
		String[] beanNames = BeanFactoryUtils.beanNamesForAnnotationIncludingAncestors(
				this.applicationContext, Endpoint.class);
		for (String beanName : beanNames) {
			EndpointBean endpointBean = createEndpointBean(beanName);
			EndpointBean previous = byId.putIfAbsent(endpointBean.getId(), endpointBean);
			Assert.state(previous == null,
					() -> "Found two endpoints with the id '" + endpointBean.getId()
							+ "': '" + endpointBean.getBeanName() + "' and '"
							+ previous.getBeanName() + "'");
		}
		return byId.values();
	}
	
	private EndpointBean createEndpointBean(String beanName) {
		Object bean = this.applicationContext.getBean(beanName);
		return new EndpointBean(beanName, bean);
	}
	
	private Collection<E> convertToEndpoints(Collection<EndpointBean> endpointBeans) {
		Set<E> endpoints = new LinkedHashSet<>();
		for (EndpointBean endpointBean : endpointBeans) {
			if (isEndpointExposed(endpointBean)) {
				endpoints.add(convertToEndpoint(endpointBean));
			}
		}
		return Collections.unmodifiableSet(endpoints);
	}
	
	private boolean isEndpointExposed(EndpointBean endpointBean) {
		return endpointBean.isEnabledByDefault();
	}
	
	private E convertToEndpoint(EndpointBean endpointBean) {
		MultiValueMap<OperationKey, O> indexed = new LinkedMultiValueMap<>();
		addOperations(indexed, endpointBean, false);
		List<O> operations = indexed.values().stream().map(this::getLast)
				.filter(Objects::nonNull).collect(Collectors.collectingAndThen(
						Collectors.toList(), Collections::unmodifiableList));
		return createEndpoint(endpointBean, operations);
	}
	
	private void addOperations(MultiValueMap<OperationKey, O> indexed, EndpointBean endpointBean, boolean replaceLast) {
		Set<OperationKey> replacedLast = new HashSet<>();
		Collection<O> operations = this.createOperations(endpointBean);
		for (O operation : operations) {
			OperationKey key = createOperationKey(operation);
			O last = getLast(indexed.get(key));
			if (replaceLast && replacedLast.add(key) && last != null) {
				indexed.get(key).remove(last);
			}
			indexed.add(key, operation);
		}
	}
	
	/**
	 * 创建 operations
	 * @param endpointBean
	 * @return
	 */
	private Collection<O> createOperations(EndpointBean endpointBean) {
		return MethodIntrospector.selectMethods(endpointBean.getBean().getClass(), (MetadataLookup<O>) (method) -> {
			AnnotationAttributes annotationAttributes = AnnotatedElementUtils.getMergedAnnotationAttributes(method,
					com.swak.actuator.endpoint.annotation.Operation.class);
			if (annotationAttributes == null) {
				return null;
			}
			OperationMethod operationMethod = new OperationMethod(endpointBean.getBean(), method);
			ReflectiveOperationInvoker invoker = new ReflectiveOperationInvoker(operationParameterResoler, operationMethod);
			return createOperation(endpointBean, invoker);
		}).values().stream().filter((i) -> i != null).collect(Collectors.toList());
	}
	
	/**
	 * 创建 Endpoint
	 * @param endpointBean
	 * @param id
	 * @param enabledByDefault
	 * @param operations
	 * @return
	 */
	protected abstract E createEndpoint(EndpointBean endpointBean, Collection<O> operations);

	/**
	 * 创建 Operation
	 * @param endpointBean
	 * @param method
	 * @return
	 */
	protected abstract O createOperation(EndpointBean endpointBean, ReflectiveOperationInvoker invoker);
	
	/**
	 * Create a {@link OperationKey} for the given operation.
	 * @param operation the source operation
	 * @return the operation key
	 */
	protected abstract OperationKey createOperationKey(O operation);
	
	
	private <T> T getLast(List<T> list) {
		return (CollectionUtils.isEmpty(list) ? null : list.get(list.size() - 1));
	}
	
	/**
	 * A key generated for an {@link Operation} based on specific criteria from the actual
	 * operation implementation.
	 */
	public static final class OperationKey {

		private final Object key;

		private final Supplier<String> description;

		/**
		 * Create a new {@link OperationKey} instance.
		 * @param key the underlying key for the operation
		 * @param description a human readable description of the key
		 */
		public OperationKey(Object key, Supplier<String> description) {
			Assert.notNull(key, "Key must not be null");
			Assert.notNull(description, "Description must not be null");
			this.key = key;
			this.description = description;
		}

		@Override
		public int hashCode() {
			return this.key.hashCode();
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj == null || getClass() != obj.getClass()) {
				return false;
			}
			return this.key.equals(((OperationKey) obj).key);
		}

		@Override
		public String toString() {
			return this.description.get();
		}

	}
	
	/**
	 * EndpointBean
	 * @author lifeng
	 */
	public static class EndpointBean {
		
		private final String beanName;

		private final Object bean;

		private final String id;

		private boolean enabledByDefault;
		
		EndpointBean(String beanName, Object bean) {
			AnnotationAttributes attributes = AnnotatedElementUtils
					.findMergedAnnotationAttributes(bean.getClass(), Endpoint.class, true,
							true);
			this.beanName = beanName;
			this.bean = bean;
			this.id = attributes.getString("id");
			this.enabledByDefault = (Boolean) attributes.get("enableByDefault");
			Assert.state(StringUtils.hasText(this.id),
					() -> "No @Endpoint id attribute specified for "
							+ bean.getClass().getName());
		}

		public boolean isEnabledByDefault() {
			return enabledByDefault;
		}

		public void setEnabledByDefault(boolean enabledByDefault) {
			this.enabledByDefault = enabledByDefault;
		}

		public String getBeanName() {
			return beanName;
		}

		public Object getBean() {
			return bean;
		}

		public String getId() {
			return id;
		}
	}
}
