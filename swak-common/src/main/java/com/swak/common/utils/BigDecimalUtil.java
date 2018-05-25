package com.swak.common.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {

	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	// 这个类不能实例化
	private BigDecimalUtil() {}

	/**
	 * 提供精确的加法运算。
	 * @param v1  被加数
	 * @param v2  加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}
	
	
	
	
	/** 判断值是否相等
	 * @param v1
	 * @param v2
	 * @return
	 */
	public static boolean valueEqual(BigDecimal v1, BigDecimal v2){
		if(v1 == null || v2 == null){
			return false;
		}
		return v1.compareTo(v2) ==0;
	}
	/**
	 * 判断是否为空
	 * @param v1  被加数
	 * @param v2  加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add(BigDecimal v1, BigDecimal v2) {
		if(v1 == null){
			v1 = BigDecimal.ZERO;
		}
		if(v2 == null){
			v2 = BigDecimal.ZERO;
		}
		return v1.add(v2);
	}
	/**
	 * 判断是否为空
	 * @param v1  被加数
	 * @param v2  加数
	 * @return 两个参数的和
	 */
	public static BigDecimal add( BigDecimal ... values ) {
		BigDecimal total = BigDecimal.ZERO;
		for( BigDecimal temp : values ) {
			if( temp == null) {
				temp = BigDecimal.ZERO;
			}
			total = total.add(temp);
		}
		return total;
	}

	/**
	 * 判断是否为空
	 * @param v1  被加数
	 * @param v2  加数
	 * @return 两个参数的和
	 */
	public static BigDecimal sub(BigDecimal v1, BigDecimal v2) {
		if(v1 == null){
			v1 = BigDecimal.ZERO;
		}
		if(v2 == null){
			v2 = BigDecimal.ZERO;
		}
		return v1.subtract(v2);
	}
	
	/**
	 * 提供非负数的减法
	 * @param v1  被加数
	 * @param v2  加数
	 * @return 两个参数的和
	 */
	public static BigDecimal subNonnegative(BigDecimal v1, BigDecimal v2) {
		if(v1 == null){
			v1 = BigDecimal.ZERO;
		}
		if(v2 == null){
			v2 = BigDecimal.ZERO;
		}
		BigDecimal result = v1.subtract(v2);
		return result.compareTo(BigDecimal.ZERO)>0?result:BigDecimal.ZERO;
	}
	
	/** 
	 * @Title: 两数相除 
	 * @param v1: 被除数
	 * @param v2: 除数
	 * @return BigDecimal    返回类型 
	 */
	public static BigDecimal div(BigDecimal v1, BigDecimal v2){
		if(v1 == null){
			v1 = BigDecimal.ZERO;
		}
		if(v2 == null){
			v2 = BigDecimal.ZERO;
		}
		if(v1 == BigDecimal.ZERO){
			return BigDecimal.ZERO;
		}
		return div(v1, v2, DEF_DIV_SCALE);
	}
	
	/**
	 * 提供精确的减法运算。
	 * @param v1  被减数
	 * @param v2  减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}
	
	/**
	 * 提供精确的乘法运算。
	 * @param v1  被乘数
	 * @param v2  乘数
	 * @return 两个参数的积
	 */
	public static BigDecimal mul(BigDecimal v1, BigDecimal v2) {
		if(v1 == null || v2 == null){
			return BigDecimal.ZERO;
		}
		return v1.multiply(v2);
	}
	
	/**
	 * 提供精确的乘法运算。
	 * @param v1  被乘数
	 * @param v2  乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
	 * 小数点以后10位，以后的数字四舍五入。
	 * @param v1  被除数
	 * @param v2  除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 * @param v1  被除数
	 * @param v2  除数
	 * @param scale  表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
			"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指
	 * 定精度，以后的数字四舍五入。
	 * @param v1  被除数
	 * @param v2  除数
	 * @param scale  表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */

	public static BigDecimal div(BigDecimal v1, BigDecimal v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
			"The scale must be a positive integer or zero");
		}
		return v1.divide(v2, scale, BigDecimal.ROUND_HALF_UP);
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * @param v  需要四舍五入的数字
	 * @param scale  小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
			"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 提供精确的小数位四舍五入处理。
	 * @param v  需要四舍五入的数字
	 * @param scale  小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double floor(double v, int scale) {
		if (scale < 0) {throw new IllegalArgumentException( "The scale must be a positive integer or zero");}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_DOWN).doubleValue();
	}
	
	/** 万变成分
	 * @param money
	 * @return
	 */
	public static long tenThoundsToCent(String money){
		//去掉逗号
		if(money.indexOf(',') > 0){
			money = StringUtils.remove(money, ',');
		}
		BigDecimal moneyd = BigDecimal.valueOf(Double.valueOf(money));
		moneyd = moneyd.multiply(BigDecimal.valueOf(1000000));
		return moneyd.longValue();
	}
	
	
	/**
	 * @param money
	 * @return
	 */
	public static double centTotenThounds(String money){
		Double moneyd = Double.valueOf(money);
		return BigDecimalUtil.div(moneyd, 1000000d,6);
	}
	
	/**
	 * @param money
	 * @return
	 */
	public static boolean equalZERO (BigDecimal money){
		if(null == money){
			return false;
		}
		return money.compareTo(BigDecimal.ZERO) == 0;
	}
	
	/**
	 * @param money
	 * @return
	 */
	public static boolean biggerThenZERO (BigDecimal money){
		if(null == money){
			return false;
		}
		return money.compareTo(BigDecimal.ZERO) > 0;
	}
	
	/**
	 * @param money
	 * @return
	 */
	public static boolean smallerThenZERO (BigDecimal money){
		if(null == money){
			return false;
		}
		return money.compareTo(BigDecimal.ZERO) < 0;
	}
	
	/**
	 * @param money
	 * @return
	 */
	public static boolean notEqualZERO (BigDecimal money){
		if(null == money){
			return false;
		}
		return money.compareTo(BigDecimal.ZERO) != 0;
	}
	
	/**
	 * String -> BigDecimal
	 * @param value
	 * @return
	 */
	public static BigDecimal valueOf(String value){
		return StringUtils.isEmpty(value)?BigDecimal.ZERO:new BigDecimal(value);
	}
	
	/**
	 * Object -> BigDecimal
	 * @param value
	 * @return
	 */
	public static BigDecimal valueOf(Object value){
		return value == null ?BigDecimal.ZERO:(valueOf(String.valueOf(value)));
	}
	
	/**
	 * 比较两个BigDecimal中的d1是否比d2大
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static boolean biggerThen (BigDecimal d1,BigDecimal d2){
		if(null == d1 || d2 == null){
			return false;
		}
		return d1.compareTo(d2) > 0;
	}
	
	/**
	 * 格式化未string，并保留两位小数
	 * @param d1
	 * @param scale
	 * @return
	 */
	public static String toString(BigDecimal d1, int scale) {
		BigDecimal b = d1;
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).toString();
	}
}
