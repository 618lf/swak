package com.swak.rpc.boot;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;

import com.google.common.collect.Sets;
import com.swak.rpc.annotation.RpcService;
import com.swak.rpc.remote.RemoteServiceFactory;

/**
 * 客户端启动需要执行的类
 * 
 * @author lifeng
 */
public class ClientBooter implements BeanFactoryPostProcessor {

	private ConfigurableListableBeanFactory beanFactory;
    private RemoteServiceFactory serviceFactory = new RemoteServiceFactory();
    
	/**
	 * 自动注册服务
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		this.beanFactory = beanFactory;
		Arrays.stream(beanFactory.getBeanDefinitionNames())
		.map(s -> this.beanFactory.getBeanDefinition(s).getBeanClassName())
		.filter(s -> this.filter(s))
		.flatMap(beanClassName -> {
			try {
				Class<?> beanClass = Class.forName(beanClassName, false, beanFactory.getBeanClassLoader());
				return this.getFieldDependRpcServices(beanClass).stream();
			}catch (Exception e) {
				return null;
			}
		}).forEach(clazz -> this.registerRpcService(clazz));
	}
	
	// 过滤扫描的类
	private boolean filter(String s) {
		return s != null && s.startsWith("com.tmt.");
	}
	
	// 获取 class 中方法所依赖的接口
	private Collection<Class<?>> getFieldDependRpcServices(Class<?> target) {
		Set<Class<?>> set = null;
		while (target != Object.class) {
			Field[] fields = target.getDeclaredFields();
			for (int j = 0; j < fields.length; j++) {
				Field field = fields[j];
				Class<?> clazz = field.getType();
				
				// 必须是接口且添加了RpcService注解
				if (!clazz.isInterface() || !clazz.isAnnotationPresent(RpcService.class)) {
					continue;
				}

				if (set == null) {
					set = new HashSet<>();
				}

				set.add(clazz);
			}
			target = target.getSuperclass();
		}
		return set == null ? Sets.newHashSet() : set;
	}
	
	// 注册成为服务
	private void registerRpcService(Class<?> rpc) {
		
		// 查看beanFactory 中是否存在
		String[] names = beanFactory.getBeanNamesForType(rpc);
		if (names != null && names.length > 0) {
            System.out.println("己经存在!");
			return;
		}
		String beanName = rpc.getSimpleName();
		Object serviceBean = serviceFactory.register(rpc);
		beanFactory.registerSingleton(beanName, serviceBean);
	}
}