package com.tmt.boot;

import com.swak.common.boot.BootStrap;
import com.swak.common.utils.SpringContextHolder;
import com.swak.http.Server;

/**
 * 启动服务
 * 
 * @author lifeng
 */
public class HttpBootStrap extends BootStrap{
	

	/**
	 * 系统 http 服务器
	 */
	@Override
	protected void init() {
		
		// 获得服务器对象
		Server server = SpringContextHolder.getBean(Server.class);
		
		// 启动服务器
		try {
			server.start();
		} catch (Exception e1) {}
	}
	
	/**
	 * Main 启动
	 * @param args
	 */
	public static void main(String[] args) {
		new HttpBootStrap().start();
	}
}