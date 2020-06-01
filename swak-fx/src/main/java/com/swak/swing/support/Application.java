package com.swak.swing.support;

/**
 * 程序启动
 * 
 * @author lifeng
 * @date 2020年5月21日 上午10:33:08
 */
public class Application {

	/**
	 * 唯一的应用实例
	 */
	private static Application CONTEXT = null;

	/**
	 * 初始化
	 * 
	 * @throws Exception
	 */
	public void init() throws Exception {
	}

	/**
	 * 启动
	 * 
	 * @throws Exception
	 */
	public void start() throws Exception {

	}

	/**
	 * Launch App
	 * 
	 * @param appClass
	 * @param args
	 */
	public static void launch(final Class<? extends Application> appClass, final String[] args) {
		try {
			CONTEXT = appClass.getDeclaredConstructor().newInstance();
			CONTEXT.init();
			CONTEXT.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}