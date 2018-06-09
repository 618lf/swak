package com.swak.rpc.invoker;

import java.io.Serializable;

/**
 * 定义每一个 mapping
 * @author lifeng
 */
public interface MappingInfo {

	/**
	 * 序号
	 * @return
	 */
	Serializable getSequence();
}
