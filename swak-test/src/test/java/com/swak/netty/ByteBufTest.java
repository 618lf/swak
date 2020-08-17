package com.swak.netty;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * ByteBuf 测试
 * 
 * @author lifeng
 * @date 2020年7月21日 下午5:23:02
 */
public class ByteBufTest {

	public static void main(String[] args) {
		ByteBuf byteBuf = Unpooled.buffer(1024);
		byteBuf.writeByte(1);
		System.out.println(byteBuf);
	}
}