package com.swak.rpc.api;

import java.io.Serializable;

/**
 * 序列号
 * @author lifeng
 */
public interface Sequence extends Serializable {

	// =========== 生成key 需要的 属性==========
	String getServiceName();
	String getMethodName();
	String getVersion();
	String getGroup();
	String[] getParameterTypes();
	
	/**
	 * 唯一序号
	 * @return
	 */
	default String getSequence() {
		StringBuilder buf = new StringBuilder();
        if (getGroup() != null && getGroup().length() > 0) {
            buf.append(getGroup());
            buf.append("/");
        }
        buf.append(getServiceName()); 
        buf.append(".");
        buf.append(getMethodName());
        if (getParameterTypes() != null) {
        	buf.append("(");
        	for(String paramType: getParameterTypes()) {
        		buf.append(paramType).append(",");
        	}
        	buf.deleteCharAt(buf.length()-1);
        	buf.append(")");
        }
        if (getVersion() != null && getVersion().length() > 0 && !"0.0.0".equals(getVersion())) {
            buf.append(":");
            buf.append(getVersion());
        }
        return buf.toString();
	}
}