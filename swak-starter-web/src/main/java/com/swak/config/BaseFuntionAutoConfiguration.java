package com.swak.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;

import com.swak.ApplicationProperties;
import com.swak.incrementer.IdGen;
import com.swak.serializer.JavaSerializer;
import com.swak.serializer.KryoPoolSerializer;
import com.swak.serializer.KryoSerializer;
import com.swak.serializer.SerializationUtils;
import com.swak.serializer.Serializer;
import com.swak.utils.SpringContextHolder;

/**
 * 基础组件
 * 
 * @author lifeng
 */
@Configuration
@EnableConfigurationProperties(ApplicationProperties.class)
public class BaseFuntionAutoConfiguration {

	/**
	 * 基础配置
	 * 
	 * @param context
	 */
	public BaseFuntionAutoConfiguration(ApplicationContext context, ApplicationProperties properties) {
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
}
