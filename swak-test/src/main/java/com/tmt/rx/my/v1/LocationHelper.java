package com.tmt.rx.my.v1;

import com.tmt.rx.my.LocationBean;

/**
 * 这种方式明显是一种阻塞式的调用
 * 1. 提交地理位置 必须等待 获取地理位置 之后才能做其他的事情
 * 2. 当前线程一直等待上面两步做完才能做其他的事情。
 * @author lifeng
 */
public class LocationHelper {

	private Api api;
	private static LocationHelper helper = new LocationHelper();

	private LocationHelper() {
	}

	public static LocationHelper getHelper() {
		return helper;
	}

	/**
	 * 第一步： 获取地理位置
	 * 第二步： 提交请求
	 * 这种是一种明显的阻塞式的调用
	 * @param address
	 */
	void commit(String address) {
		try {
			LocationBean location = api.getLocation(address);
			api.submitLocation(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}