package com.swak.utils.buffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * ByteBuffer 工具方法
 * 
 * @author lifeng
 */
public class ByteBuffers {

	/**
	 * 获得字节数组
	 * 
	 * @param buf
	 * @param start
	 * @param length
	 * @return
	 */
	public static byte[] getBytes(ByteBuffer buf, int start, int length) {
		if (buf.hasArray()) {
			int baseOffset = buf.arrayOffset() + start;
			return Arrays.copyOfRange(buf.array(), baseOffset, baseOffset + length);
		}
		byte[] v = new byte[length];
		buf.get(v, start, length);
		return v;
	}
}