package com.swak.utils;

import java.math.BigInteger;

/**
 * int 类型工具类
 *
 * @author: lifeng
 * @date: 2020/3/29 14:08
 */
public final class Ints {

    public static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private Ints() {
    }

    /**
     * 默认值
     *
     * @param v1 数据
     * @param v2 默认值
     * @return 值
     */
    public static int defaultInteger(Integer v1, Integer v2) {
        if (v1 != null) {
            return v1;
        } else if (v2 == null) {
            return 0;
        }
        return v2;
    }

    /**
     * Returns the {@code int} nearest in value to {@code value}.
     *
     * @param value any {@code long} value
     * @return the same value cast to {@code int} if it is in the range of the
     * {@code int} type, {@link Integer#MAX_VALUE} if it is too large, or
     * {@link Integer#MIN_VALUE} if it is too small
     */
    public static int saturatedCast(long value) {
        if (value > Integer.MAX_VALUE) {
            return Integer.MAX_VALUE;
        }
        if (value < Integer.MIN_VALUE) {
            return Integer.MIN_VALUE;
        }
        return (int) value;
    }

    /**
     * 整形的添加
     *
     * @param values 数据
     * @return 结果
     */
    public static int add(int... values) {
        int num = 0;
        if (values != null && values.length != 0) {
            for (int value : values) {
                num += value;
            }
        }
        return num;
    }

    /**
     * 在随机取一个0~最小的之前的整数
     *
     * @param num 种子
     * @return 随机整数
     */
    public static int random(int num) {
        return (int) (Math.random() * num);
    }

    /**
     * 先比较最小的，在随机取一个0~最小的之前的整数
     *
     * @param max 随机数的最大值
     * @param min 随机数的最小值
     * @return 随机整数
     */
    public static int random(int max, int min) {
        return (int) (Math.random() * (max - min + 1)) + min;
    }

    /**
     * 最小值
     *
     * @param one 数据1
     * @param two 数据2
     * @return 最小值
     */
    public static int min(int one, int two) {
        return Math.min(one, two);
    }

    /**
     * 最大值
     *
     * @param one 数据1
     * @param two 数据2
     * @return 最大值
     */
    public static int max(int one, int two) {
        return Math.max(one, two);
    }

    /**
     * nullToZero
     *
     * @param value 数据
     * @return 整数
     */
    public static int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 整形的添加
     *
     * @param values 多个数据
     * @return 结果
     */
    public static int addI(Integer... values) {
        int num = 0;
        if (values != null && values.length != 0) {
            for (Integer value : values) {
                num += nullToZero(value);
            }
        }
        return num;
    }

    /**
     * 整形的减法
     *
     * @param minuend 被减数
     * @param subs    减数
     * @return 结果
     */
    public static int subI(Integer minuend, Integer... subs) {
        if (subs != null && subs.length != 0) {
            for (Integer value : subs) {
                minuend = nullToZero(minuend) - nullToZero(value);
            }
        }
        return nullToZero(minuend);
    }

    /**
     * 平均值
     *
     * @param x 数1
     * @param y 数2
     * @return 结果
     */
    public static int average(int x, int y) {
        return (x & y) + ((x ^ y) >> 1);
    }

    /**
     * 计算绝对值
     *
     * @param x 数据
     * @return 绝对值
     */
    public static int abs(int x) {
        int y;
        y = x >> 31;
        return (x ^ y) - y;
    }

    /**
     * 转换为16进制字符
     *
     * @param numb 10进制
     * @return 16进制
     */
    public static String encodeHex(Integer numb) {
		return Integer.toHexString(numb);
    }

    /**
     * 16进制字符 转为 数字
     *
     * @param hexs 16进制字符
     * @return 数字
     */
    public static int decodeHex(String hexs) {
        BigInteger bigint = new BigInteger(hexs, 16);
        return bigint.intValue();
    }
}