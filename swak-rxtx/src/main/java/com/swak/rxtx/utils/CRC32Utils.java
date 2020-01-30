package com.swak.rxtx.utils;

/**
 * CRC-32
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
 * <td>&nbsp; CRC-32</td>
 * <td align="center">0x04C11DB7</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-32/B</td>
 * <td align="center">0x04C11DB7</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-32/C</td>
 * <td align="center">0x1EDC6F41</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-32/D</td>
 * <td align="center">0xA833982B</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">LSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-32/MPEG-2</td>
 * <td align="center">0x04C11DB7</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">0x00000000</td>
 * <td align="center">MSB First</td>
 * </tr>
 * <tr>
 * <td>&nbsp; CRC-32/POSIX</td>
 * <td align="center">0x04C11DB7</td>
 * <td align="center">0x00000000</td>
 * <td align="center">0xFFFFFFFF</td>
 * <td align="center">MSB First</td>
 * </tr>
 * </table>
 * 
 * @author unnamed
 *
 */
public class CRC32Utils {

	/**
	 * CRC-32
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x04C11DB7</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32(byte[] source, int offset, int length) {
		long wCRCin = 0xFFFFFFFFL;
		// Long.reverse(0x04C11DB7L) >>> 32
		long wCPoly = 0xEDB88320L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((long) source[i] & 0x000000FFL);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x00000001L) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFFFFFFL;
	}

	/**
	 * CRC-32/B
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x04C11DB7</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32_B(byte[] source, int offset, int length) {
		long wCRCin = 0xFFFFFFFFL;
		long wCPoly = 0x04C11DB7L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c31 = ((wCRCin >> 31 & 1) == 1);
				wCRCin <<= 1;
				if (c31 ^ bit) {
					wCRCin ^= wCPoly;
				}
			}
		}
		wCRCin &= 0xFFFFFFFFL;
		return wCRCin ^= 0xFFFFFFFFL;
	}

	/**
	 * CRC-32/C
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x1EDC6F41</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32_C(byte[] source, int offset, int length) {
		long wCRCin = 0xFFFFFFFFL;
		// Long.reverse(0x1EDC6F41L) >>> 32
		long wCPoly = 0x82F63B78L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((long) source[i] & 0x000000FFL);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x00000001L) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFFFFFFL;
	}

	/**
	 * CRC-32/D
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0xA833982B</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">LSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32_D(byte[] source, int offset, int length) {
		long wCRCin = 0xFFFFFFFFL;
		// Long.reverse(0xA833982BL) >>> 32
		long wCPoly = 0xD419CC15L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			wCRCin ^= ((long) source[i] & 0x000000FFL);
			for (int j = 0; j < 8; j++) {
				if ((wCRCin & 0x00000001L) != 0) {
					wCRCin >>= 1;
					wCRCin ^= wCPoly;
				} else {
					wCRCin >>= 1;
				}
			}
		}
		return wCRCin ^= 0xFFFFFFFFL;
	}

	/**
	 * CRC-32/MPEG-2
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x04C11DB7</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">0x00000000</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32_MPEG_2(byte[] source, int offset, int length) {
		long wCRCin = 0xFFFFFFFFL;
		long wCPoly = 0x04C11DB7L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c31 = ((wCRCin >> 31 & 1) == 1);
				wCRCin <<= 1;
				if (c31 ^ bit) {
					wCRCin ^= wCPoly;
				}
			}
		}
		wCRCin &= 0xFFFFFFFFL;
		return wCRCin ^= 0x00000000L;
	}

	/**
	 * CRC-32/POSIX
	 * 
	 * <table width="400px" border="1" cellpadding="0" cellspacing="0">
	 * <tr>
	 * <th>多项式</th>
	 * <th>初始值</th>
	 * <th>异或值</th>
	 * <th>Bit反转</th>
	 * </tr>
	 * <tr>
	 * <td align="center">0x04C11DB7</td>
	 * <td align="center">0x00000000</td>
	 * <td align="center">0xFFFFFFFF</td>
	 * <td align="center">MSB First</td>
	 * </tr>
	 * </table>
	 * 
	 * @param source
	 * @param offset
	 * @param length
	 * @return
	 */
	public static long CRC32_POSIX(byte[] source, int offset, int length) {
		long wCRCin = 0x00000000L;
		long wCPoly = 0x04C11DB7L;
		for (int i = offset, cnt = offset + length; i < cnt; i++) {
			for (int j = 0; j < 8; j++) {
				boolean bit = ((source[i] >> (7 - j) & 1) == 1);
				boolean c31 = ((wCRCin >> 31 & 1) == 1);
				wCRCin <<= 1;
				if (c31 ^ bit) {
					wCRCin ^= wCPoly;
				}
			}
		}
		wCRCin &= 0xFFFFFFFFL;
		return wCRCin ^= 0xFFFFFFFFL;
	}
}
