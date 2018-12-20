package com.swak.config.vertx;

import java.net.Inet4Address;
import java.net.UnknownHostException;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.swak.config.zookeeper.ZookeeperClusterManager;
import com.swak.utils.StringUtils;
import com.swak.vertx.config.ClusterVertxBean;
import com.swak.vertx.config.VertxBean;
import com.swak.vertx.config.VertxProperties;
import com.swak.vertx.handler.VertxHandler;

import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.spi.cluster.ClusterManager;

/**
 * 配置集群的vertx
 * 
 * @author lifeng
 */
@ConditionalOnClass(io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager.class)
@ConditionalOnMissingBean(VertxBean.class)
@EnableConfigurationProperties(VertxProperties.class)
public class ClusterVertxAutoConfiguration {

	/**
	 * 配置一个集群的 VertxBean
	 * 
	 * @return
	 */
	@Bean
	public VertxHandler vertxBean(VertxOptions vertxOptions, DeliveryOptions deliveryOptions, VertxProperties properties) {

		// 配置在zookeeper
		RetryPolicy retryPolicy = new ExponentialBackoffRetry(properties.getRetryInitialSleepTime(),
				properties.getRetryMaxTimes(), properties.getRetryIntervalTimes());

		// Read the zookeeper hosts from a system variable
		String hosts = properties.getZookeeperHosts();

		CuratorFramework curator = CuratorFrameworkFactory.builder().connectString(hosts)
				.namespace(properties.getRootPath()).sessionTimeoutMs(properties.getSessionTimeout())
				.connectionTimeoutMs(properties.getConnectTimeout()).retryPolicy(retryPolicy).build();

		// cluster config
		ClusterManager clusterManager = new ZookeeperClusterManager(retryPolicy, curator);
		String ipAddress = this.getHost(properties);
		vertxOptions.setClustered(properties.isClusterable());
		vertxOptions.setClusterManager(clusterManager).setClusterHost(ipAddress);
		vertxOptions.setClusterPort(properties.getClusterPort());
		vertxOptions.setClusterPingInterval(properties.getClusterPingInterval());
		vertxOptions.setClusterPingReplyInterval(properties.getClusterPingIntervalReply());
		vertxOptions.setHAEnabled(properties.isHaEnabled());
		vertxOptions.setHAGroup(properties.getHaGroup());
		vertxOptions.setQuorumSize(properties.getQuorumSize());
		return new ClusterVertxBean(vertxOptions, deliveryOptions);
	}

	private String getHost(VertxProperties properties) {
		String ipAddress = properties.getClusterHost();
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