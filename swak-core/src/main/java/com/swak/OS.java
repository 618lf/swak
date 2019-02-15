package com.swak;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

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
	 * 本机IP
	 * 
	 * @return
	 */
	public static String ip() {
		try {
			String ip = null;
			if (OS.me() != OS.linux) {
				ip = InetAddress.getLocalHost().getHostAddress().toString();
			} else {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
						.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							String ipaddress = inetAddress.getHostAddress().toString();
							if (!ipaddress.contains("::") && !ipaddress.contains("0:0:")
									&& !ipaddress.contains("fe80")) {
								ip = ipaddress;
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
	 * @return
	 */
	public static String home() {
		return System.getProperty("user.home");
	}
}
