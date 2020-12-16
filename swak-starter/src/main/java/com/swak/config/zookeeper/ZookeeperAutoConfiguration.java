package com.swak.config.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryNTimes;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.zookeeper.ZookeeperService;
import com.swak.zookeeper.curator.CuratorZookeeperService;

@Configuration
@ConditionalOnClass({CuratorFramework.class, ZookeeperService.class})
@EnableConfigurationProperties(ZookeeperProperties.class)
public class ZookeeperAutoConfiguration {

	ZookeeperProperties properties;

	public ZookeeperAutoConfiguration(ZookeeperProperties properties) {
		this.properties = properties;
	}

	/**
	 * 创建zookeeper 客户端
	 * 
	 * @return
	 */
	@Bean(destroyMethod = "close")
	public ZookeeperService zookeeperService() {
		CuratorFrameworkFactory.Builder builder = CuratorFrameworkFactory.builder()
				.connectString(properties.getAddress()).retryPolicy(new RetryNTimes(1, 1000))
				.connectionTimeoutMs(properties.getTimeout()).sessionTimeoutMs(properties.getSessionExpireMs());
		String authority = properties.getAuthority();
		if (authority != null && authority.length() > 0) {
			builder = builder.authorization("digest", authority.getBytes());
		}
		CuratorFramework client = builder.build();
		return new CuratorZookeeperService(client);
	}
}
