package com.swak.vertx.config;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.aop.support.AopUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.util.ClassUtils;

import com.swak.annotation.FluxReferer;
import com.swak.annotation.FluxService;
import com.swak.annotation.RequestMapping;
import com.swak.annotation.RequestMethod;
import com.swak.annotation.RestApi;
import com.swak.annotation.RestPage;
import com.swak.annotation.RestService;
import com.swak.annotation.RouterSupplier;
import com.swak.exception.BaseRuntimeException;
import com.swak.utils.Lists;
import com.swak.utils.Maps;
import com.swak.utils.Sets;
import com.swak.utils.StringUtils;
import com.swak.utils.router.RouterUtils;
import com.swak.vertx.transport.VertxProxy;

/**
 * 自动化配置bean
 * 
 * @author lifeng
 */
public class AnnotationBean implements BeanPostProcessor, BeanFactoryAware, Ordered {

	private final Set<ServiceBean> services = Sets.newOrderSet();
	private final Set<RouterBean> routers = Sets.newOrderSet();
	private final Map<String, ReferenceBean> references = Maps.newOrderMap();
	private final Set<IRouterSupplier> routerSuppliers = Sets.newOrderSet();
	private final Set<IRouterConfig> routerConfigs = Sets.newOrderSet();
	private final VertxProxy vertx;
	private BeanFactory beanFactory;

	public AnnotationBean(VertxProxy vertx) {
		this.vertx = vertx;
	}

	public VertxProxy getVertx() {
		return vertx;
	}

	public Set<ServiceBean> getServices() {
		return services;
	}

	public Set<RouterBean> getRouters() {
		return routers;
	}

	public Set<IRouterSupplier> getRouterSuppliers() {
		return routerSuppliers;
	}

	public Set<IRouterConfig> getRouterConfigs() {
		return routerConfigs;
	}

	@Override
	public int getOrder() {
		return 0;
	}

	/**
	 * 设置工厂类
	 */
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
	}

	/**
	 * 获得代理类
	 * 
	 * @param type
	 * @return
	 */
	public Object getProxy(Object bean) {
		if (AopUtils.isAopProxy(bean)) {
			return bean;
		}
		return this.beanFactory.getBean(bean.getClass());
	}

	/**
	 * init reference field
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (AopUtils.isAopProxy(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}

		// registry router
		if (isController(clazz)) {

			// 定义错误
			if (StringUtils.contains(beanName, "/")) {
				throw new BaseRuntimeException(
						"Use @RestController like this: @RestController(path='/api/goods', value='goodsApi') or @RestController(path='/api/goods')");
			}

			// 直接可以将请求映射到service上
			FluxService fluxService = AnnotatedElementUtils.findMergedAnnotation(clazz, FluxService.class);
			RequestMapping classMapping = AnnotatedElementUtils.findMergedAnnotation(clazz, RequestMapping.class);
			Method[] methods = clazz.getDeclaredMethods();
			for (Method method : methods) {
				RequestMapping methodMapping = AnnotatedElementUtils.findMergedAnnotation(method, RequestMapping.class);
				if (methodMapping == null) {
					continue;
				}

				RouterBean routerBean = this.router(classMapping, methodMapping, bean, method, fluxService != null);
				if (routerBean != null) {
					routers.add(routerBean);
				}
			}
		}

		// registry sub routers
		RouterSupplier routerSupplier = clazz.getAnnotation(RouterSupplier.class);
		if (routerSupplier != null && bean instanceof IRouterSupplier) {
			IRouterSupplier rs = (IRouterSupplier) bean;
			routerSuppliers.add(rs);
		}

		// registry config routers
		if (bean instanceof IRouterConfig) {
			IRouterConfig rs = (IRouterConfig) bean;
			routerConfigs.add(rs);
		}

		// fill the reference
		this.cascadeFillReference(clazz, bean);

		// return
		return bean;
	}

	/**
	 * 级联处理自动依赖
	 * 
	 * @param clazz
	 */
	private void cascadeFillReference(Class<?> clazz, Object bean) {
		// fill reference
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			try {
				if (!field.isAccessible()) {
					field.setAccessible(true);
				}
				FluxReferer reference = field.getAnnotation(FluxReferer.class);
				if (reference != null) {
					Object value = refer(reference, field.getType());
					if (value != null) {
						field.set(bean, value);
					}
				}
			} catch (Exception e) {
				throw new BeanInitializationException("Failed to init service reference at filed " + field.getName()
						+ " in class " + bean.getClass().getName(), e);
			}
		}

		// super class
		if (clazz.getSuperclass() != null) {
			this.cascadeFillReference(clazz.getSuperclass(), bean);
		}
	}

	/**
	 * 创建路由
	 * 
	 * @param classMapping
	 * @param methodMapping
	 * @param bean
	 * @param method
	 * @param mergeService
	 * @return
	 */
	protected RouterBean router(RequestMapping classMapping, RequestMapping methodMapping, Object bean, Method method,
			boolean mergeService) {
		String[] patterns1 = classMapping.value();
		String[] patterns2 = methodMapping.value();
		List<String> result = Lists.newArrayList();
		if (patterns1.length != 0 && patterns2.length != 0) {
			for (String pattern1 : patterns1) {
				for (String pattern2 : patterns2) {
					result.add(RouterUtils.combine(pattern1, pattern2));
				}
			}
		} else if (patterns1.length != 0) {
			result = Lists.newArrayList(patterns1);
		} else if (patterns2.length != 0) {
			result = Lists.newArrayList(patterns2);
		} else {
			result.add(StringUtils.EMPTY);
		}

		// method
		RequestMethod requestMethod = classMapping.method() == RequestMethod.ALL ? methodMapping.method()
				: classMapping.method();
		requestMethod = requestMethod == RequestMethod.ALL ? null : requestMethod;
		return new RouterBean(vertx, bean, method, result, requestMethod, mergeService);
	}

	/**
	 * 创建并缓存依赖
	 * 
	 * @param reference
	 * @param interfaceType
	 * @return
	 */
	protected Object refer(FluxReferer reference, Class<?> interfaceType) {
		ReferenceBean referenceBean = references.get(interfaceType.getName());
		if (referenceBean == null) {
			referenceBean = new ReferenceBean(interfaceType);
			references.put(interfaceType.getName(), referenceBean);
		}
		return referenceBean.getRefer(vertx);
	}

	/**
	 * init service config
	 *
	 * @param bean
	 * @param beanName
	 * @return
	 * @throws BeansException
	 */
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		if (AopUtils.isAopProxy(bean)) {
			clazz = AopUtils.getTargetClass(bean);
		}

		// 判断是否服务
		if (this.isServie(clazz)) {

			// 获取服务配置
			FluxService fluxService = AnnotatedElementUtils.findMergedAnnotation(clazz, FluxService.class);

			if (fluxService != null) {
				Class<?>[] classes = ClassUtils.getAllInterfacesForClass(clazz);
				if (classes == null || classes.length == 0) {
					classes = new Class<?>[] { clazz };
				}
				for (Class<?> inter : classes) {
					if (inter.getName().startsWith("org.springframework.") || !fitWith(fluxService, inter)) {
						continue;
					}
					ServiceBean serviceBean = new ServiceBean(inter, bean, fluxService);
					services.add(serviceBean);
				}
			}
		}
		return bean;
	}

	/**
	 * 如果继承了多个接口，可以指定需要实现的服务
	 * 
	 * @param mapping
	 * @param inter
	 * @return
	 */
	private boolean fitWith(FluxService mapping, Class<?> inter) {
		if (mapping.service() == null || mapping.service() == void.class) {
			return true;
		}
		return mapping.service() == inter;
	}

	/**
	 * 是否是 Api
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean isController(Class<?> clazz) {
		return (clazz.isAnnotationPresent(RestApi.class) || clazz.isAnnotationPresent(RestPage.class)
				|| clazz.isAnnotationPresent(RestService.class));
	}

	/**
	 * 是否是 Service
	 * 
	 * @param clazz
	 * @return
	 */
	protected boolean isServie(Class<?> clazz) {
		return (clazz.isAnnotationPresent(FluxService.class) || clazz.isAnnotationPresent(RestService.class));
	}
}