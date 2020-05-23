package com.swak.rxtx.utils;

/**
 * CRC-16
 * 
 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
 * <tr>
 * <th>名称</th>
 * <th>多项式</th>
 * <th>初始值</th>
 * <th>异或值</th>
 * <th>Bit反转</th>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/IBM</td>
 * <td align="center">0x8005</td>
 * <td align="center">0x0000</td>
 * <td align="center">0x0000</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/CCITT</td>
 * <td align="center">0x1021</td>
 * <td align="center">0x0000</td>
 * <td align="center">0x0000</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/CCITT-FALSE</td>
 * <td align="center">0x1021</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">0x0000</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/DECT R</td>
 * <td align="center">0x0589</td>
 * <td align="center">0x0000</td>
 * <td align="center">0x0001</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/DECT X</td>
 * <td align="center">0x0589</td>
 * <td align="center">0x0000</td>
 * <td align="center">0x0000</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/DNP</td>
 * <td align="center">0x3D65</td>
 * <td align="center">0x0000</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/GENIBUS</td>
 * <td align="center">0x1021</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/MAXIM</td>
 * <td align="center">0x8005</td>
 * <td align="center">0x0000</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/MODBUS</td>
 * <td align="center">0x8005</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">0x0000</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/USB</td>
 * <td align="center">0x8005</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/X25</td>
 * <td align="center">0x1021</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">0xFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-16/XMODEM</td>
 * <td align="center">0x1021</td>
 * <td align="center">0x0000</td>
 * <td align="center">0x0000</td>
 * <td align="center">MSB First</td>
 * </tr>
 * </table>
 * 
 * @author unnamed
 *
 */
public class CRC16Utils {

	/**
	 * CRC-16/IBM
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x8005</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_IBM(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		// Integer.reverse(0x8005) >>> 16
		int wCPoly = 0xA001;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0x0000;
	}

	/**
	 * CRC-16/CCITT
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1021</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_CCITT(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		// Integer.reverse(0x1021) >>> 16
		int wCPoly = 0x8408;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0x0000;
	}

	/**
	 * CRC-16/CCITT-FALSE
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1021</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_CCITT_FALSE(byte[] source, int offset, int length) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0x1021;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
			}
		}
		wCRCin &= 0xFFFF;
		return wCRCin ^= 0x0000;
	}

	/**
	 * CRC-16/DECT R
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x0589</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0x0001</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_DECT_R(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		int wCPoly = 0x0589;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
			}
		}
		wCRCin &= 0xFFFF;
		return wCRCin ^= 0x0001;
	}

	/**
	 * CRC-16/DECT X
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x0589</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_DECT_X(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		int wCPoly = 0x0589;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
			}
		}
		wCRCin &= 0xFFFF;
		return wCRCin ^= 0x0000;
	}

	/**
	 * CRC-16/DNP
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x3D65</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_DNP(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		// Integer.reverse(0x3D65) >>> 16
		int wCPoly = 0xA6BC;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFF;
	}

	/**
	 * CRC-16/GENIBUS
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1021</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_GENIBUS(byte[] source, int offset, int length) {
		int wCRCin = 0xFFFF;
		int wCPoly = 0x1021;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
			}
		}
		wCRCin &= 0xFFFF;
		return wCRCin ^= 0xFFFF;
	}

	/**
	 * CRC-16/MAXIM
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x8005</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_MAXIM(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		// Integer.reverse(0x8005) >>> 16
		int wCPoly = 0xA001;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFF;
	}

	/**
	 * CRC-16/MODBUS
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x8005</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_MODBUS(byte[] source, int offset, int length) {
		int wCRCin = 0xFFFF;
		// Integer.reverse(0x8005) >>> 16
		int wCPoly = 0xA001;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0x0000;
	}

	/**
	 * CRC-16/USB
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x8005</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_USB(byte[] source, int offset, int length) {
		int wCRCin = 0xFFFF;
		// Integer.reverse(0x8005) >>> 16
		int wCPoly = 0xA001;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFF;
	}

	/**
	 * CRC-16/X25
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1021</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">0xFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_X25(byte[] source, int offset, int length) {
		int wCRCin = 0xFFFF;
		// Integer.reverse(0x1021) >>> 16
		int wCPoly = 0x8408;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((int) source[i] & 0x00FF);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x0001) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFF;
	}

	/**
	 * CRC-16/XMODEM
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1021</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">0x0000</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static int CRC16_XMODEM(byte[] source, int offset, int length) {
		int wCRCin = 0x0000;
		int wCPoly = 0x1021;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c15 = ((wCRCin >> 15 & 1) == 1);
				wCRCin <<= 1;
				if (c15 ^ bit) {
                    wCRCin ^= wCPoly;
                }
			}
		}
		wCRCin &= 0xFFFF;
		return wCRCin ^= 0x0000;
	}
}
