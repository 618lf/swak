package com.swak.vertx.config;

import static com.swak.Application.APP_LOGGER;

import java.util.function.Consumer;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.DeliveryOptions;

/**
 * 集群化的 vertx bean
 * 
 * @author lifeng
 */
public class ClusterVertxBean extends VertxBean {

	public ClusterVertxBean(VertxOptions vertxOptions, DeliveryOptions deliveryOptions) {
		super(vertxOptions, deliveryOptions);
	}

	@Override
	public void apply(Consumer<Vertx> apply) {
		Vertx.clusteredVertx(vertxOptions, res -> {
			if (res.succeeded()) {
				apply.accept(res.result());
				this.inited = true;
				this.vertx = res.result();
			} else {
				APP_LOGGER.error("Cluster failed: " + res.cause());
			}
		});
	}
}