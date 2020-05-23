package com.swak.utils.buffer;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * ByteBuffer 工具方法
 *
 * @author: lifeng
 * @date: 2020/3/29 13:36
 */
public class ByteBuffers {

    /**
     * 获得字节数组
     *
     * @param buf    ByteBuffer
     * @param start  开始位置
     * @param length 长度
     * @return 获得字节数组
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