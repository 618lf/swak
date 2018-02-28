package com.swak.common.boot;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.swak.common.utils.SpringContextHolder;

/**
 * 启动服务
 * @author lifeng
 */
public class BootStrap {

	protected static Logger logger = LoggerFactory.getLogger(BootStrap.class);
	
	/**
	 * 启动的具体方法
	 */
	@SuppressWarnings("resource")
	public void start() {
        try {
			
			// 启动spring 
			new ClassPathXmlApplicationContext("classpath:applicationContext.xml").start();
			
			// 启动项
			logger.debug("========= system startup loading ====================");
			Map<String, Boot> boots = SpringContextHolder.getBeans(Boot.class);
			if (boots != null && !boots.isEmpty()) {
		    	Set<String> keys = boots.keySet();
		    	Iterator<String> it = keys.iterator();
		    	while(it.hasNext()){
		    		try {
		    			Boot realm = boots.get(it.next());
		    			logger.debug("Async loading - {}", realm.describe());
		    			realm.start();
					} catch (Exception e) {}
		    	}
		    }
			logger.debug("========= system startup loaded ====================");
			
			// 子类接入点
			this.init();
			
		} catch (Exception e) {
			logger.error("== Main context start error:",e);
		}
		synchronized (BootStrap.class) {
			while (true) {
				try {
					BootStrap.class.wait();
				} catch (InterruptedException e) {
					logger.error("== synchronized error:",e);
				}
			}
		}
	}
	
	/**
	 * 初始化操作
	 */
	protected void init() {}
	
	/**
	 * Main 启动
	 * @param args
	 */
	public static void main(String[] args) {
		new BootStrap().start();
	}
}