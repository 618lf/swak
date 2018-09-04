package com.swak.config.vertx;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.utils.StringUtils;
import com.swak.vertx.config.ClusterVertxBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;

import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;

/**
 * 配置集群的vertx
 * @author lifeng
 */
@ConditionalOnClass(ZookeeperClusterManager.class)
@ConditionalOnMissingBean(ClusterVertxBean.class)
@EnableConfigurationProperties(VertxProperties.class)
public class ClusterVertxAutoConfiguration {

	/**
	 * 配置一个集群的 VertxBean
	 * @return
	 */
	@Bean
	public VertxHandler vertxBean(VertxOptions vertxOptions, VertxProperties properties) {	
		// cluster config
		ClusterManager clusterManager = new ZookeeperClusterManager();
		String ipAddress = this.getHost(properties);
		vertxOptions.setClusterManager(clusterManager).setClusterHost(ipAddress);
		return new ClusterVertxBean(vertxOptions);
	}
	
	private String getHost(VertxProperties properties) {
		String ipAddress = properties.getHost();
		if (StringUtils.isBlank(ipAddress)) {
			try {
				ipAddress = Inet4Address.getLocalHost().getHostAddress();
			} catch (UnknownHostException e) {
				return "127.0.0.1";
			}
		}
		return ipAddress;
	}
}