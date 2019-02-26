package com.swak.reactivex.web.formatter;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

public class DateFormatterConverter implements Converter<String, Date>{

	public static final String[] DATE_PATTERNS = {"yyyy-MM-dd",  "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH", "yyyy-MM", "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy", "yyyyMM", "yyyy/MM", "yyyyMMddHHmmss", "yyyyMMdd"};
	private static final String DEFAULT_DATE_FPRMAT = "yyyy-MM-dd HH:mm:ss";
	private String dateFormat = DEFAULT_DATE_FPRMAT;
	private String[] dateFormats = null;
	
	public DateFormatterConverter() {
		initDateFormats();
	}
	
	@Override
	public Date convert(String source) {
		if (source == null || StringUtils.isEmpty(source)) {
			return null;
		} else {
			String str = source.trim();
			try {
				return parseDateWithLeniency(str, null, dateFormats, true);
			} catch (ParseException localParseException) {
				return null;
			}
		}
	}
	
	private Date parseDateWithLeniency(
            final String str, final Locale locale, final String[] parsePatterns, final boolean lenient) throws ParseException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }
        
        SimpleDateFormat parser;
        if (locale == null) {
            parser = new SimpleDateFormat();
        } else {
            parser = new SimpleDateFormat("", locale);
        }
        
        parser.setLenient(lenient);
        final ParsePosition pos = new ParsePosition(0);
        for (final String parsePattern : parsePatterns) {

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
        throw new ParseException("Unable to parse the date: " + str, -1);
    }
	
	public String getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		initDateFormats();
	}
	
	private void initDateFormats() {
		dateFormats = new String[DATE_PATTERNS.length+1];
		dateFormats[0] = this.dateFormat;
		for(int i =0;i<DATE_PATTERNS.length; i++) {
			dateFormats[i+1] = DATE_PATTERNS[i];
		}
	}
}