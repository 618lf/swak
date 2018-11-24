package com.swak;

/**
 * 操作系统
 * 
 * @author lifeng
 */
public enum OS {

	linux, mac, windows, Others;

	/**
	 * 是否是
	 * 
	 * @return
	 */
	public static OS me() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf(OS.linux.name()) > 0) {
			return OS.linux;
		} else if (os.indexOf(OS.mac.name()) > 0) {
			return OS.mac;
		} else if (os.indexOf(OS.windows.name()) > 0) {
			return OS.windows;
		}
		return OS.Others;
	}
}
