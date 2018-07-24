package com.swak.actuator.config.motan;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.motan.CommandEndpoint;
import com.swak.actuator.motan.ServerEndpoint;
import com.swak.motan.manager.CommandService;
import com.swak.motan.manager.RegistryService;
import com.swak.motan.manager.ZkClientWrapper;
import com.swak.motan.manager.impl.ZookeeperCommandService;
import com.swak.motan.manager.impl.ZookeeperRegistryService;

/**
 * motan 监控 自动化配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({RegistryService.class, CommandService.class, ZooKeeper.class})
public class MotanEndpointAutoConfiguration {

	/**
	 * 配置  zk 客户端
	 * @return
	 */
	@Bean
	public ZkClientWrapper zkClientWrapper() {
		return new ZkClientWrapper();
	}
	
	/**
	 * 注册服务
	 * @return
	 */
	@Bean
	public RegistryService registryService() {
		return new ZookeeperRegistryService();
	}
	
	/**
	 * 命令服务
	 * @return
	 */
	@Bean
	public CommandService commandService() {
		return new ZookeeperCommandService();
	}
	
	/**
	 * 命令 api
	 * @param commandService
	 * @return
	 */
	@Bean
	public CommandEndpoint commandEndpoint(CommandService commandService) {
		return new CommandEndpoint(commandService);
	}
	
	/**
	 * 命令 api
	 * @param commandService
	 * @return
	 */
	@Bean
	public ServerEndpoint serverEndpoint(RegistryService registryService) {
		return new ServerEndpoint(registryService);
	}
}