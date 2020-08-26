package com.swak.vertx.formatter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期格式化
 *
 * @author: lifeng
 * @date: 2020/3/29 19:33
 */
public class DateFormatter implements Converter<String, Date> {

    public static final String[] DATE_PATTERNS = {"yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy", "yyyyMM", "yyyy/MM", "yyyyMMddHHmmss", "yyyyMMdd"};
    private static final String DEFAULT_DATE_FPRMAT = "yyyy-MM-dd HH:mm:ss";
    private String dateFormat = DEFAULT_DATE_FPRMAT;
    private String[] dateFormats = null;

    public DateFormatter() {
        initDateFormats();
    }

    @Override
    public Date convert(String source) {
        if (dateFormats == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        if (!StringUtils.isEmpty(source)) {
            String str = source.trim();
            try {
                SimpleDateFormat parser = new SimpleDateFormat();
                parser.setLenient(true);
                final ParsePosition pos = new ParsePosition(0);
                for (final String parsePattern : dateFormats) {

                    String pattern = parsePattern;

                    // LANG-530 - need to make sure 'ZZ' output doesn't get passed to SimpleDateFormat
                    if (parsePattern.endsWith("ZZ")) {
                        pattern = pattern.substring(0, pattern.length() - 1);
                    }

                    parser.applyPattern(pattern);
                    pos.setIndex(0);

                    String str2 = str;
                    // LANG-530 - need to make sure 'ZZ' output doesn't hit SimpleDateFormat as it will ParseException
                    if (parsePattern.endsWith("ZZ")) {
                        str2 = str.replaceAll("([-+][0-9][0-9]):([0-9][0-9])$", "$1$2");
                    }

                    final Date date = parser.parse(str2, pos);
                    if (date != null && pos.getIndex() == str2.length()) {
                        return date;
                    }
                }
            } catch (Exception localParseException) {
                return null;
            }
        }
        return null;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
        initDateFormats();
    }

    private void initDateFormats() {
        dateFormats = new String[DATE_PATTERNS.length + 1];
        dateFormats[0] = this.dateFormat;
        System.arraycopy(DATE_PATTERNS, 0, dateFormats, 1, DATE_PATTERNS.length);
    }
}