package com.swak.rabbit.handler;

import java.lang.reflect.Proxy;

import com.swak.rabbit.annotation.Publisher;

/**
 * 依赖配置
 * 
 * @author lifeng
 */
public class ReferenceBean {

	private final Class<?> type;
	private final Publisher reference;

	public ReferenceBean(Publisher reference, Class<?> type) {
		this.reference = reference;
		this.type = type;
	}

	/**
	 * 获得代理对象,可以切换为其他代理实现
	 * 
	 * @return
	 */
	public Object newRefer() {
		return Proxy.newProxyInstance(this.type.getClassLoader(), new Class[] { this.type },
				new InvokerHandler(reference, type));
	}

	@Override
	public int hashCode() {
		int result = type.hashCode();
		result = 31 * result + reference.queue().hashCode();
		result = 31 * result + reference.routing().hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ReferenceBean)) {
			return false;
		}
		ReferenceBean other = (ReferenceBean) obj;
		return type == other.type && reference.queue().equals(other.reference.queue())
				&& reference.routing().equals(other.reference.routing());
	}
}