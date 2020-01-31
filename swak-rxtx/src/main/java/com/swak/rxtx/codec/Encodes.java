package com.swak.rxtx.codec;

import com.swak.codec.Hex;
import com.swak.rxtx.utils.CRC16Utils;

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
	 * 计算 crc16 -- MODBUS
	 * 
	 * @param bytes
	 * @return
	 */
	default String crc16(String data) {
		return crc16(this.decodeHex(data));
	}

	/**
	 * 计算 crc16 -- MODBUS
	 * 
	 * @param bytes
	 * @return
	 */
	default String crc16(byte[] bytes) {
		int CRC = CRC16Utils.CRC16_MODBUS(bytes, 0, bytes.length);
		return "SD" + Integer.toHexString(CRC);
	}
}
