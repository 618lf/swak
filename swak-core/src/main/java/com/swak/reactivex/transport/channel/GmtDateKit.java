package com.swak.reactivex.transport.channel;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * 用于格式化 GMT 日期
 * 
 * @author lifeng
 */
public class GmtDateKit {

	/**
	 * GMT Format Style
	 */
	public static final String HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";

	/**
	 * GMT Format
	 */
	public static final DateTimeFormatter GMT_FMT = DateTimeFormatter.ofPattern(HTTP_DATE_FORMAT, Locale.US);

	/**
	 * GMT ZoneId
	 */
	public static final ZoneId GMT_ZONE_ID = ZoneId.of("GMT");

	/**
	 * 当前事件的 GMT 表示
	 */
	public static String format() {
		return GMT_FMT.format(LocalDateTime.now().atZone(GMT_ZONE_ID));
	}

	/**
	 * Date 装为 GMT 表示
	 */
	public static String format(Date date) {
		return GMT_FMT.format(LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault()).atZone(GMT_ZONE_ID));
	}

	/**
	 * DateTime 装为 GMT 表示
	 */
	public static String format(long date) {
		return format(new Date(date));
	}

	/**
	 * GMT Date 转为 Date
	 */
	public static Date format(String date) {
		DateTimeFormatter fmt = DateTimeFormatter.ofPattern(HTTP_DATE_FORMAT, Locale.US);
		LocalDateTime formatted = LocalDateTime.parse(date, fmt);
		Instant instant = formatted.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}
}