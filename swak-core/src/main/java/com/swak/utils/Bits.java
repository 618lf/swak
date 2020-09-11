package com.swak.utils;

/**
 * 二进制相关操作
 * 
 * @author lifeng
 * @date 2020年9月7日 下午4:47:53
 */
public class Bits {

	/**
	 * 得到第几位的值： 0 or 1
	 * 
	 * @param num    原始值
	 * @param offset 偏移量 从 0开始
	 * @return
	 */
	public static int getBit(int num, int offset) {
		if (!(offset >= 0 && offset <= 31)) {
			throw new IllegalArgumentException("Offset is Over!");
		}
		return (num >> offset & 1);
	}

	/**
	 * 设置bit值，只能设置 0 或 1
	 * 
	 * @param num    原始值
	 * @param offset 偏移量 从 0开始
	 * @param value  0 or 1
	 */
	public static int setBit(int num, int offset, int value) {
		if (!(offset >= 0 && offset <= 31)) {
			throw new IllegalArgumentException("Offset is Over!");
		}
		if (!(value == 0 || value == 1)) {
			throw new IllegalArgumentException("Value is only in (0, 1)!");
		}
		if (value == 0) {
			return num & ~(1 << offset);
		}
		return num | (1 << offset);
	}
}