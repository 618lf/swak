package com.swak.netty;

import java.nio.ByteBuffer;

/**
 * ByteBuffer 测试
 * 
 * @author lifeng
 * @date 2020年7月21日 下午4:37:58
 */
public class ByteBufferTest {

	public static void main(String[] args) {
		ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
		byteBuffer.put((byte) 1);
		System.out.println(byteBuffer.position());
	}
}