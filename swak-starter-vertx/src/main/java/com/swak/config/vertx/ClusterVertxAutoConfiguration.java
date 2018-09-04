package com.swak.config.vertx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.swak.vertx.config.ClusterVertxBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;

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
	public VertxHandler vertxBean(VertxProperties properties) {
		return new ClusterVertxBean(properties);
	}
}