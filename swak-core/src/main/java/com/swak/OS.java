package com.swak;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 操作系统
 *
 * @author: lifeng
 * @date: 2020/3/29 15:45
 */
public enum OS {

	/**
	 *
	 */
	linux,

	/**
	 *
	 */
	mac,

	/**
	 *
	 */
	windows,

	/**
	 *
	 */
	Others;

	/**
	 * 本机系统类型
	 *
	 * @return OS
	 */
	public static OS me() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains(OS.linux.name())) {
			return OS.linux;
		} else if (os.contains(OS.mac.name())) {
			return OS.mac;
		} else if (os.contains(OS.windows.name())) {
			return OS.windows;
		}
		return OS.Others;
	}

	/**
	 * 本机IP
	 *
	 * @return ip
	 */
	public static String ip() {
		try {
			String ip = null;
			if (OS.me() != OS.linux) {
				ip = InetAddress.getLocalHost().getHostAddress();
			} else {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface inf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String idrefs = inetAddress.getHostAddress();
							if (!idrefs.contains("::") && !idrefs.contains("0:0:") && !idrefs.contains("fe80")) {
								ip = idrefs;
							}
						}
					}
				}
			}
			return ip;
		} catch (Exception e) {
			return Constants.LOCALHOST;
		}
	}

	/**
	 * 当前用户的home
	 *
	 * @return user.home
	 */
	public static String home() {
		return System.getProperty("user.home");
	}

	/**
	 * 当前用户临时目录
	 *
	 * @return java.io.tmpdir
	 */
	public static String temp() {
		return System.getProperty("java.io.tmpdir");
	}

	/**
	 * 获取java 的版本
	 *
	 * @return user.home
	 */
	public static String java() {
		return System.getProperty("java.version");
	}
}