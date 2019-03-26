package com.swak.config;

import static com.swak.Application.APP_LOGGER;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.swak.ApplicationProperties;
import com.swak.booter.AppBooter;
import com.swak.booter.AppShuter;
import com.swak.incrementer.IdGen;
import com.swak.serializer.JavaSerializer;
import com.swak.serializer.KryoPoolSerializer;
import com.swak.serializer.KryoSerializer;
import com.swak.serializer.SerializationUtils;
import com.swak.serializer.Serializer;
import com.swak.utils.SpringContextHolder;

/**
 * 系统配置 - 启动和关闭
 * 
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class ApplicationAutoConfiguration {
	
	public ApplicationAutoConfiguration(ApplicationContext context, ApplicationProperties properties) {
		APP_LOGGER.debug("Loading AppBooter");
		this.springContextHolder(context);
		this.serializer(properties);
		this.idGenerator(properties);
	}
	
	/**
	 * SpringContextHolder
	 * 
	 * @param context
	 */
	public void springContextHolder(ApplicationContext context) {
		SpringContextHolder.setApplicationContext(context);
	}

	/**
	 * IdGenerator
	 */
	public void idGenerator(ApplicationProperties properties) {
		IdGen.setServerSn(properties.getServerSn());
	}

	/**
	 * 序列化
	 * 
	 * @return
	 */
	public void serializer(ApplicationProperties properties) {
		String ser = properties.getSerialization();
		Serializer g_ser = null;
		if (ser.equals("java")) {
			g_ser = new JavaSerializer();
		} else if (ser.equals("kryo")) {
			g_ser = new KryoSerializer();
		} else if (ser.equals("kryo_pool")) {
			g_ser = new KryoPoolSerializer();
		} else {
			g_ser = new JavaSerializer();
		}

		// 公共引用
		SerializationUtils.g_ser = g_ser;
	}

	/**
	 * 启动
	 * 
	 * @return
	 */
	@Bean
	public AppBooter appBooter() {
		return new AppBooter();
	}
	
	/**
	 * 关闭
	 * 
	 * @return
	 */
	@Bean
	public AppShuter appShuter() {
		return new AppShuter();
	}
}