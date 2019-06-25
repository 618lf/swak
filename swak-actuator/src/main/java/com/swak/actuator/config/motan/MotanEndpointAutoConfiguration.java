package com.swak.actuator.config.motan;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.actuator.motan.CommandEndpoint;
import com.swak.actuator.motan.ServerEndpoint;
import com.swak.motan.manager.CommandService;
import com.swak.motan.manager.RegistryService;
import com.swak.motan.manager.ZkClientWrapper;
import com.swak.motan.manager.impl.ZookeeperCommandService;
import com.swak.motan.manager.impl.ZookeeperRegistryService;
import com.swak.motan.properties.RegistryConfigProperties;

/**
 * motan 监控 自动化配置
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({RegistryService.class, CommandService.class})
@EnableConfigurationProperties({ RegistryConfigProperties.class })
@ConditionalOnProperty(prefix = "spring.motan.registry", name = "regProtocol", havingValue="zookeeper")
public class MotanEndpointAutoConfiguration {

	/**
	 * 配置  zk 客户端
	 * @return
	 */
	@Bean
	public ZkClientWrapper zkClientWrapper(RegistryConfigProperties properties) {
		String address = new StringBuilder(properties.getAddress()).append(":").append(properties.getPort()).toString();
		return new ZkClientWrapper(address);
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