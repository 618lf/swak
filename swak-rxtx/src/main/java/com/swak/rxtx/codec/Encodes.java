package com.swak.rxtx.codec;

import com.swak.codec.Hex;

/**
 * 编码
 * 
 * @author lifeng
 */
public interface Encodes {

	/**
	 * 字节转 16进制
	 * 
	 * @param input
	 * @return
	 */
	default String encodeHex(byte... input) {
		return new String(Hex.encodeHex(input, false));
	}

	/**
	 * 16进制转 字节
	 * 
	 * @param input
	 * @return
	 */
	default byte[] decodeHex(String input) {
		return Hex.decodeHex(input.toCharArray());
	}

	/**
	 * 计算 crc16
	 * 
	 * @param bytes
	 * @return
	 */
	default String crc16(String data) {
		return crc16(this.decodeHex(data));
	}

	/**
	 * 计算 crc16
	 * 
	 * @param bytes
	 * @return
	 */
	default String crc16(byte[] bytes) {
		int CRC = 0x0000ffff;
		int POLYNOMIAL = 0x0000a001;

		int i, j;
		for (i = 0; i < bytes.length; i++) {
			CRC ^= (int) bytes[i];
			for (j = 0; j < 8; j++) {
				if ((CRC & 0x00000001) == 1) {
					CRC >>= 1;
					CRC ^= POLYNOMIAL;
				} else {
					CRC >>= 1;
				}
			}
		}
		// 高低位转换，看情况使用（譬如本人这次对led彩屏的通讯开发就规定校验码高位在前低位在后，也就不需要转换高低位)
		// CRC = ( (CRC & 0x0000FF00) >> 8) | ( (CRC & 0x000000FF ) << 8);
		return Integer.toHexString(CRC);
	}
}
