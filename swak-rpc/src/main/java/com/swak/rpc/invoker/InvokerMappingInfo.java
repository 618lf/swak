package com.swak.rpc.invoker;

import java.io.Serializable;

import com.swak.rpc.annotation.RpcService;
import com.swak.utils.StringUtils;

/**
 * 查找 method 的 条件
 * 
 * @author lifeng
 */
public class InvokerMappingInfo implements MappingInfo{

	// 唯一序号
	private Serializable sequence;
	
	// 查找
	private String interfaceName;
	private String methodName;
	private Class<?>[] parameterTypes;
	private String version = null;
	
	// 控制
	private long timeout = -1;
	private boolean ignore = false;
	
	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public boolean isIgnore() {
		return ignore;
	}

	public void setIgnore(boolean ignore) {
		this.ignore = ignore;
	}
	public Class<?>[] getParameterTypes() {
		return parameterTypes;
	}

	public void setParameterTypes(Class<?>[] parameterTypes) {
		this.parameterTypes = parameterTypes;
	}

	/**
	 * 合并
	 */
	public InvokerMappingInfo combine(InvokerMappingInfo other) {
		String version = other.getVersion() == null ? this.getVersion() : other.getVersion();
		long timeout = other.getTimeout() == -1 ? this.getTimeout() : other.getTimeout();
		boolean ignore = other.isIgnore() ? other.isIgnore() : this.isIgnore();
		return InvokerMappingInfo.build(version, timeout, ignore);
	}
	
	/**
	 * 固化，之后不能在改变
	 * @return
	 */
	public InvokerMappingInfo fixed(String interfaceName, String methodName, Class<?>[] parameterTypes) {
		this.interfaceName = interfaceName;
		this.methodName = methodName;
		this.parameterTypes = parameterTypes;
		if (!StringUtils.hasText(version)) {
			version = RpcService.DEFAULT_VERSION;
		}
		if (timeout  == -1) {
			timeout = RpcService.DEFAULT_TIME_OUT;
		}
		
		// 生成序号，之后数据不能在改变
		this.getSequence();
		
		// 返回
		return this;
	}
	
	/**
	 * 生成的唯一序号
	 * @return
	 */
	@Override
	public Serializable getSequence() {
		if (this.sequence == null) {
			StringBuilder sequence = new StringBuilder();
			sequence.append(this.interfaceName).append("#")
			.append(this.methodName).append("#")
			.append(this.version).append("#");
			if (this.parameterTypes != null) {
				for(Class<?> c: parameterTypes) {
					sequence.append(c.getName()).append("|");
				}
			}
			this.sequence = sequence.toString();
		}
		return this.sequence;
	}
	
	@Override
	public int hashCode() {
		return this.getSequence().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof MappingInfo)) {
			return false;
		}
		return this.getSequence().equals(((MappingInfo)obj).getSequence());
	}

	@Override
	public String toString() {
		return this.getSequence().toString();
	}

	// ----------- 创建 Info ----------------
	public static InvokerMappingInfo build(String version) {
		InvokerMappingInfo info = new InvokerMappingInfo();
		info.setVersion(version);
		return info;
	}
	public static InvokerMappingInfo build(String version, long timeout, boolean ignore) {
		InvokerMappingInfo info = new InvokerMappingInfo();
		info.setVersion(version);
		info.setTimeout(timeout);
		info.setIgnore(ignore);
		return info;
	}
}