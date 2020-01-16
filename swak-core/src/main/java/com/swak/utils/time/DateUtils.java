package com.swak.utils.time;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

import com.swak.utils.StringUtils;

/**
 * 
 * @author TMT
 *
 */
@Deprecated
public class DateUtils {
    
	private static String[] parsePatterns = { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
		"yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" };
	
	/**
	 * 日期型字符串转化为日期 格式
	 * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", 
	 *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm" }
	 */
	public static Date parseDate(String str) {
		if (str == null){
			return null;
		}
		try {
			return parseDateWithLeniency(str, null, parsePatterns, true);
		} catch (ParseException e) {
			return null;
		}
	}
	
	   /**
     * <p>Parses a string representing a date by trying a variety of different parsers.</p>
     *
     * <p>The parse will try each parse pattern in turn.
     * A parse is only deemed successful if it parses the whole of the input string.
     * If no parse patterns match, a ParseException is thrown.</p>
     *
     * @param str  the date to parse, not null
     * @param locale the locale to use when interpretting the pattern, can be null in which
     * case the default system locale is used
     * @param parsePatterns  the date format patterns to use, see SimpleDateFormat, not null
     * @param lenient Specify whether or not date/time parsing is to be lenient.
     * @return the parsed date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws ParseException if none of the date patterns were suitable
     * @see java.util.Calendar#isLenient()
     */
    private static Date parseDateWithLeniency(
            final String str, final Locale locale, final String[] parsePatterns, final boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        final TimeZone tz = TimeZone.getDefault();
        final Locale lcl = locale==null ?Locale.getDefault() : locale;
        final ParsePosition pos = new ParsePosition(0);
        final Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);

        for (final String parsePattern : parsePatterns) {
            final FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (fdp.parse(str, pos, calendar) && pos.getIndex()==str.length()) {
                    return calendar.getTime();
                }
            } catch(final IllegalArgumentException ignore) {
                // leniency is preventing calendar from being set
            }
            pos.setIndex(0);
        }
        throw new ParseException("Unable to parse the date: " + str, -1);
    }

    /**
     * 根据传入的时间得到当前月第一天
     * @param date yyyy-MM-dd格式的日期
     * @return
     */
    public static Date getMonthFirstDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int minDate = cal.getActualMinimum(Calendar.DATE);   
        cal.set(Calendar.DAY_OF_MONTH, minDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    /**
     * 根据传入的时间得到当前月最后一天最后时刻
     * @param date yyyy-MM-dd格式的日期
     * @return
     */
    public static Date getMonthLastDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int maxDate = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DAY_OF_MONTH, maxDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 得到月份
     * @param dtStart
     * @return
     */
    public static int getMonth(Date dtStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtStart);
        return cal.get(Calendar.MONTH);
    }
    
    /**
     * 得到年份
     * @param dtStart
     * @return
     */
    public static int getYear(Date dtStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtStart);
        return cal.get(Calendar.YEAR);
    }
    
    /**
     * 根据时间以及对应的格式得到Date
     * @param strDate
     * @param pattern
     * @return
     */
    public static Date getFormatDate(String strDate, String pattern) {
        DateFormat format = new SimpleDateFormat(pattern);
        Date dResult = null;
        try {
            dResult = format.parse(strDate);
        } catch (ParseException e) {            
            e.printStackTrace();
        }
        return dResult;
    }
    
    /**
     * 根据传入的Date对象及格式返回字符型日期
     * @param occurTime
     * @param pattern
     * @return
     */
    public static String getFormatDate(Date occurTime, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return df.format(occurTime);
    }
    
    /**
     * 得到相差offset天的时间
     * @param date
     * @param offset
     * @return
     */
    public static Date getDateByOffset(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, offset);
        return cal.getTime();
    }
    
    /**
     * 得到相差offset周的日期
     * @param date
     * @param offset
     * @return
     */
    public static Date getWeekByOffset(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.WEEK_OF_YEAR, offset);
        return cal.getTime();
    }
    
    /**
     * 得到相差offset月的时间
     * @param date
     * @param offset
     * @return
     */
    public static Date getMonthByOffset(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, offset);
        return cal.getTime();
    }
    
    /**
     * 得到相差offset年的时间
     * @param date
     * @param offset
     * @return
     */
    public static Date getYearByOffset(Date date, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, offset);
        return cal.getTime();
    }
    
    /**
     * 得到当天的第一时刻
     * @param dtStart
     * @return
     */
    public static Date getDayFirstTime(Date dtStart) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtStart);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 得到当天的最后时刻
     * @param dtEnd
     * @return
     */
    public static Date getDayLastTime(Date dtEnd) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(dtEnd);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    
    /**
     * 得到所在周的第一天
     * @param year
     * @param week
     * @return
     */
    public static Date getWeekFirstDate(int year, int week) {
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);      
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week + 1);
        return DateUtils.getWeekFirstDate(cal.getTime());
    }
    
    /**
     * 得到所在周的第一天
     * @param year
     * @param week
     * @return
     */
    public static Date getWeekLastDate(int year, int week) {
    	Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);      
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week + 1);
        return DateUtils.getWeekLastDate(cal.getTime());
    }
    
    /**
     * 得到本周的第一天
     * @param date
     * @return
     */
    public static Date getWeekFirstDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 0 - (cal.get(Calendar.DAY_OF_WEEK) - 1));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }
    
    /**
     * 得到本周的最后一天
     * @param date
     * @return
     */
    public static Date getWeekLastDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, 7 - cal.get(Calendar.DAY_OF_WEEK));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }

    /**
     * 得到当前年的第一天
     * @param dtTmp
     * @return
     */
    public static Date getYearFirstDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        int minDate = cal.getActualMinimum(Calendar.DATE);
        cal.set(Calendar.DATE, minDate);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        return cal.getTime();
    }

    /**
     * 得到当前年的最后一天
     * @param dtTmp
     * @return
     */
    public static Date getYearLastDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, Calendar.DECEMBER);
        int maxDate = cal.getActualMaximum(Calendar.DATE);
        cal.set(Calendar.DATE, maxDate);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        return cal.getTime();
    }
    
    /**
     * 计算某天是当年的第几天
     * @param date
     * @return
     */
    public static int getDayOfYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_YEAR);
    }
    
    /**
     * 将传入日期的时分秒清零
     * @param date
     * @return
     */
    public static Date clearTime(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date cDate = null;
        try {
            cDate = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cDate;
    }
    
    /**
     * 计算某天是当周的第几天
     * @param date
     * @return
     */
    public static int getDayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_WEEK);
    }
    
    /**
     * 计算某天是当月的第几天
     * @param date
     * @return
     */
    public static int getDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * 计算某天是当年的第几周
     * @param date
     * @return
     */
    public static int getWeekOfYear(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.WEEK_OF_YEAR);
    }
    
    /**
     * 得到传入日期的小时
     * @param date
     * @return
     */
    public static int getHourOfDay(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.HOUR_OF_DAY);
    }
    
    /**
     * 得到传入日期的小时
     * @param date
     * @return
     */
    public static int getMinuteOfHour(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal.get(Calendar.MINUTE);
    }
    
    public static String getWeekName(Date date) {
        String weekName = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch(calendar.get(Calendar.DAY_OF_WEEK)) {
        case 2:
            weekName = "星期一";
            break;
        case 3:
            weekName = "星期二";
            break;
        case 4:
            weekName = "星期三";
            break;
        case 5:
            weekName = "星期四";
            break;
        case 6:
            weekName = "星期五";
            break;
        case 7:
            weekName = "星期六";
            break;
        case 1:
            weekName = "星期日";
            break;
        }
        return weekName;
    }
    
    /**
     * 得到指定日期是当年的第几周
     * @param date
     * @return
     */
    public static int getWeek(Date date) {
    	GregorianCalendar g = new GregorianCalendar();g.setTime(date);
    	return g.get(Calendar.WEEK_OF_YEAR);
    }
    
    /**
     * 得到指定日期是当年的第几周
     * @param date
     * @return
     */
    public static int getWeek() {
    	GregorianCalendar g = new GregorianCalendar();g.setTime(new Date());
    	return g.get(Calendar.WEEK_OF_YEAR);
    }
    
    public static String getTodayStr(){
    	return getFormatDate(new Date(), "yyyy-MM-dd");
    }
    
    public static String getTodayStr(String format){
    	return getFormatDate(new Date(), format);
    }
    
    /**
     * 得到相差offset天的时间
     * @param date
     * @param offset
     * @return
     */
    public static Date getTodayOffsetDate(int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DAY_OF_YEAR, offset);
        return cal.getTime();
    }
    
    /**
     * 获取当前的时间戳 yyyy-MM-dd HH:mm:ss:ssss
     * @return
     */
    public static Timestamp getTimeStampNow(){
    	return new Timestamp(new Date().getTime());
    }
    
    /**
     * 获取当前的日期 yyyy-MM-dd
     * @return
     */
    public static Date getTodayDate() {
    	return DateUtils.clearTime(new Date());
    }
    
    /**
     * 获取当前的日期 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getTodayTime() {
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date cDate = null;
        try {
            cDate = sdf.parse(sdf.format(new Date()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cDate;
    }
    
    /**
     * 获取当前的日期 yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static Date getFormateDate(Date date, String pattern) {
    	pattern = pattern == null?"yyyy-MM-dd HH:mm:ss":pattern;
    	SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date cDate = null;
        try {
            cDate = sdf.parse(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return cDate;
    }
    
    /**
     * 判断date1是否在date2的前面
     * @param date1
     * @param date2
     * @return
     */
    public static boolean before(Date date1,Date date2){
    	if(date1==null || date2==null){
    		return false;
    	}
    	return date1.before(date2);
    }
    
    /**
     * 判断date1是否在date2的后面
     * @param date1
     * @param date2
     * @return
     */
    public static boolean after(Date date1,Date date2){
    	if(date1==null || date2==null){
    		return false;
    	}
    	return date1.after(date2);
    }
    
    /**
     * 得到 : 早,中,晚
     * @return
     */
	public static String getDateSx() {
		Calendar cal = Calendar.getInstance();
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		if (hour >= 6 && hour < 8) {
			return "早上";
		} else if (hour >= 8 && hour < 11) {
			return "上午";
		} else if (hour >= 11 && hour < 13) {
			return "中午";
		} else if (hour >= 13 && hour < 18) {
			return "下午";
		} else {
			return "晚上";
		}
	}
	
	//------------- 秒 美化----------------------------
	/**
	 * 显示秒值为**年**月**天 **时**分**秒 如1年2个月3天 10小时
	 *
	 * @return
	 */
    public static final String prettySeconds(int totalSeconds) {
        StringBuilder s = new StringBuilder();
        int second = totalSeconds % 60;
        if (totalSeconds > 0) {
            s.append("秒");
            s.append(StringUtils.reverse(String.valueOf(second)));
        }

        totalSeconds = totalSeconds / 60;
        int minute = totalSeconds % 60;
        if (totalSeconds > 0) {
            s.append("分");
            s.append(StringUtils.reverse(String.valueOf(minute)));
        }

        totalSeconds = totalSeconds / 60;
        int hour = totalSeconds % 24;
        if (totalSeconds > 0) {
            s.append(StringUtils.reverse("小时"));
            s.append(StringUtils.reverse(String.valueOf(hour)));
        }

        totalSeconds = totalSeconds / 24;
        int day = totalSeconds % 31;
        if (totalSeconds > 0) {
            s.append("天");
            s.append(StringUtils.reverse(String.valueOf(day)));
        }

        totalSeconds = totalSeconds / 31;
        int month = totalSeconds % 12;
        if (totalSeconds > 0) {
            s.append("月");
            s.append(StringUtils.reverse(String.valueOf(month)));
        }

        totalSeconds = totalSeconds / 12;
        int year = totalSeconds;
        if (totalSeconds > 0) {
            s.append("年");
            s.append(StringUtils.reverse(String.valueOf(year)));
        }
        return s.reverse().toString();
    }
    
    /**
	 * 将毫秒的差额 格式化时间格式
	 * @param ms
	 * @param pattern dd:hh:mm:ss:ms(默认)
	 * @return
	 */
	public static String formatLongTime(long ms, String pattern) {
		String _pattern = pattern==null?"dd:hh:mm:ss:ms":pattern;
		Boolean ddShow = _pattern.contains("dd");
		Boolean hhShow = _pattern.contains("hh");
		Boolean mmShow = _pattern.contains("mm");
		Boolean ssShow = _pattern.contains("ss");
		Boolean msShow = _pattern.contains("ms");
		
		Integer ss = 1000;  
	    Integer mi = ss * 60;  
	    Integer hh = mi * 60;  
	    Integer dd = hh * 24;  
	  
	    Long day = ms / dd;  
	    Long hour = (ms - day * dd) / hh;  
	    Long minute = (ms - day * dd - hour * hh) / mi;  
	    Long second = (ms - day * dd - hour * hh - minute * mi) / ss;  
	    Long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;  
	      
	    StringBuffer sb = new StringBuffer();
	    if(day > 0 && ddShow) {sb.append(day).append("天");}  
	    if(hour > 0 && hhShow) {sb.append(hour).append("小时");}  
	    if(minute > 0 && mmShow) {sb.append(minute).append("分");}  
	    if(second > 0 && ssShow) {sb.append(second).append("秒");}
	    if(milliSecond > 0 && msShow) {sb.append(milliSecond).append("毫秒");} 
	    return sb.toString();
	}
}