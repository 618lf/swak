package com.swak.actuator.motan;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.swak.actuator.endpoint.annotation.Endpoint;
import com.swak.actuator.endpoint.annotation.Operation;
import com.swak.actuator.endpoint.annotation.Selector;
import com.swak.motan.manager.RegistryService;
import com.swak.utils.Lists;
import com.swak.utils.StringUtils;

/**
 * {@link Endpoint} to expose details of motan server.
 * 
 * @author lifeng
 */
@Endpoint(id = "motanServer")
public class ServerEndpoint {

	private final RegistryService registryService;
	
	public ServerEndpoint(RegistryService registryService) {
		this.registryService = registryService;
	}
	
	/**
	 * 所有的 分组
	 * @return
	 */
	@Operation
	public List<String> groups() {
		return registryService.getGroups();
	}
	
	/**
	 * 分组下的服务
	 * @return
	 */
	@Operation
	public List<String> services(@Selector String group) {
		if (StringUtils.isBlank(group)) {
			return Lists.newArrayList();
		}
		return registryService.getServicesByGroup(group);
	}
	
	/**
	 * 节点
	 * @param group
	 * @param service
	 * @return
	 */
	@Operation
	public List<JSONObject> nodes(@Selector String group) {
		if (StringUtils.isEmpty(group)) {
			return Lists.newArrayList();
        }
		return registryService.getAllNodes(group);
	}
	
	/**
	 * 节点
	 * @param group
	 * @param service
	 * @return
	 */
	@Operation
	public List<JSONObject> nodes(@Selector String group, @Selector String service, @Selector String nodeType) {
		if (StringUtils.isEmpty(group) || StringUtils.isEmpty(service)) {
			return Lists.newArrayList();
        }
		return registryService.getNodes(group, service, nodeType);
	}
}
