package com.swak.bytes;

import com.swak.utils.Bits;

public class BitOps {

	public static void main(String[] args) {
		int num = Bits.setBit(0, 30, 1);
		num = Bits.setBit(num, 28, 1);
		num = Bits.setBit(num, 27, 1);
		System.out.println(num);
		System.out.println(Bits.getBit(num, 28));
	}
}
