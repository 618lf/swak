package org.apache.dubbo.config.spring;

import java.util.Map;

import org.apache.dubbo.common.constants.CommonConstants;

import com.swak.Constants;
import com.swak.dubbo.PrimaryReferenceBean;
import com.swak.utils.StringUtils;

/**
 * 解决 异步接口
 * 
 * @author lifeng
 * @date 2020年9月6日 上午11:16:27
 */
public class ReferenceBean<T> extends PrimaryReferenceBean<T> {

	private static final long serialVersionUID = 1L;

	/**
	 * 可以处理异步接口
	 */
	@Override
	protected T createProxy(Map<String, String> map) {
		this.changeAsyncInterface(map);
		return super.createProxy(map);
	}

	/**
	 * 将异步接口转换为同步接口
	 * 
	 * @param map
	 */
	protected void changeAsyncInterface(Map<String, String> map) {
		String interfaceName = null;
		if (map.containsKey(CommonConstants.INTERFACE_KEY)
				&& (interfaceName = map.get(CommonConstants.INTERFACE_KEY)) != null
				&& StringUtils.endsWith(interfaceName, Constants.ASYNC_SUFFIX)) {
			interfaceName = StringUtils.removeEnd(interfaceName, Constants.ASYNC_SUFFIX);
			map.put(CommonConstants.INTERFACE_KEY, interfaceName);
		}
	}
}
