package com.swak.vertx.config;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.function.Consumer;

import com.swak.utils.StringUtils;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.zookeeper.ZookeeperClusterManager;
import static com.swak.Application.APP_LOGGER;

/**
 * 集群化的 vertx bean
 * 
 * @author lifeng
 */
public class ClusterVertxBean extends VertxBean {

	public ClusterVertxBean(VertxProperties properties) {
		super(properties);
	}

	@Override
	public void apply(Consumer<Vertx> apply) {
		ClusterManager clusterManager = new ZookeeperClusterManager();
		String ipAddress = this.getHost();
		VertxOptions options = this.init();
		options.setClusterManager(clusterManager).setClusterHost(ipAddress);
		Vertx.clusteredVertx(options, res -> {
			if (res.succeeded()) {
				apply.accept(res.result());
				this.inited = true;
				this.vertx = res.result();
			} else {
				APP_LOGGER.error("Cluster failed: " + res.cause());
			}
		});
	}

	private String getHost() {
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