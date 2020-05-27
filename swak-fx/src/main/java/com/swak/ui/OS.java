package com.swak.ui;

/**
 * 操作系统
 * 
 * @author lifeng
 */
public enum OS {

	linux, mac, windows, Others;

	/**
	 * 本机系统类型
	 * 
	 * @return
	 */
	public static OS me() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf(OS.linux.name()) >= 0) {
			return OS.linux;
		} else if (os.indexOf(OS.mac.name()) >= 0) {
			return OS.mac;
		} else if (os.indexOf(OS.windows.name()) >= 0) {
			return OS.windows;
		}
		return OS.Others;
	}

	/**
	 * 当前用户的home
	 * 
	 * @return
	 */
	public static String home() {
		return System.getProperty("user.home");
	}

	/**
	 * java的版本
	 * 
	 * @return
	 */
	public static String java() {
		return System.getProperty("java.version");
	}

	/**
	 * 执行外部命令
	 * 
	 * @param strcmd
	 * @return
	 */
	public static boolean run(String strcmd) {
		// 执行命令
		Runtime rt = Runtime.getRuntime();
		Process ps = null;
		try {
			ps = rt.exec(strcmd);
			ps.waitFor();
		} catch (Exception e) {
			if (ps != null) {
				ps.destroy();
			}
			return false;
		}

		// 执行命令 -- 成功
		int i = ps.exitValue(); // 接收执行完毕的返回值
		ps.destroy(); // 销毁子进程
		ps = null;
		return i == 0;
	}
}
