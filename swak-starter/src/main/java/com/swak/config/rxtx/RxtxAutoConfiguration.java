package com.swak.config.rxtx;

import static com.swak.Application.APP_LOGGER;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;

import com.swak.rxtx.Channels;
import com.swak.rxtx.channel.Channel;

/**
 * 系统配置 - 启动和关闭
 * 
 * @author lifeng
 */
@Configuration
@ConditionalOnClass({ Channels.class })
@EnableConfigurationProperties(RxtxProperties.class)
public class RxtxAutoConfiguration implements ApplicationListener<ContextRefreshedEvent> {

	@Autowired
	private RxtxProperties properties;

	public RxtxAutoConfiguration() {
		APP_LOGGER.debug("Loading Rxtx");
	}

	/**
	 * 创建 Channels
	 * 
	 * @param channelInit
	 * @return
	 */
	@Bean(destroyMethod = "")
	public Channels channels(Consumer<Channel> channelInit) {
		return Channels.builder().setWorks(properties.getWorks()).setHeartbeatSeconds(properties.getHeartbeatSeconds())
				.setChannelInit(channelInit).build();
	}

	/**
	 * 系统启动之后才开启：Rxtx
	 */
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		Channels.me().start();
	}
}