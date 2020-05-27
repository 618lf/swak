package com.swak.bytes;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class Test {

	// 10000000 10000000 10000000 10000000
	public static void main(String[] args) throws IOException {

		byte[] bytes = new byte[] { -128, -128, -128, -128 };
		DataInputStream dis = new DataInputStream(new ByteArrayInputStream(bytes));
		int i1 = dis.readInt();
		System.out.println(i1 + "-" + (byte) i1);

		int i2 = ((bytes[0] & 0xFF) << 8) ^ (bytes[1] & 0xFF);
		System.out.println(i2);
	}
}
