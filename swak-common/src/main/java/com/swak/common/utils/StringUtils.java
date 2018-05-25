package com.swak.common.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

/**
 * 方法来自 shiro
 * 
 * @author lifeng
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

	public static final String EMPTY_STRING = "";
	public static final char DEFAULT_DELIMITER_CHAR = ',';
	public static final char DEFAULT_QUOTE_CHAR = '"';

	public static String[] split(String line) {
		return split(line, DEFAULT_DELIMITER_CHAR);
	}

	public static String[] split(String line, char delimiter) {
		return split(line, delimiter, DEFAULT_QUOTE_CHAR);
	}

	public static String[] split(String line, char delimiter, char quoteChar) {
		return split(line, delimiter, quoteChar, quoteChar);
	}

	public static String[] split(String line, char delimiter, char beginQuoteChar, char endQuoteChar) {
		return split(line, delimiter, beginQuoteChar, endQuoteChar, false, true);
	}

	public static String[] split(String aLine, char delimiter, char beginQuoteChar, char endQuoteChar,
			boolean retainQuotes, boolean trimTokens) {
		String line = clean(aLine);
		if (line == null) {
			return null;
		}

		List<String> tokens = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		boolean inQuotes = false;

		for (int i = 0; i < line.length(); i++) {

			char c = line.charAt(i);
			if (c == beginQuoteChar) {
				if (inQuotes && line.length() > (i + 1) && line.charAt(i + 1) == beginQuoteChar) {
					sb.append(line.charAt(i + 1));
					i++;
				} else {
					inQuotes = !inQuotes;
					if (retainQuotes) {
						sb.append(c);
					}
				}
			} else if (c == endQuoteChar) {
				inQuotes = !inQuotes;
				if (retainQuotes) {
					sb.append(c);
				}
			} else if (c == delimiter && !inQuotes) {
				String s = sb.toString();
				if (trimTokens) {
					s = s.trim();
				}
				tokens.add(s);
				sb = new StringBuilder();
			} else {
				sb.append(c);
			}
		}
		String s = sb.toString();
		if (trimTokens) {
			s = s.trim();
		}
		tokens.add(s);
		return tokens.toArray(new String[tokens.size()]);
	}

	public static boolean hasText(String str) {
		if (!hasLength(str)) {
			return false;
		}
		int strLen = str.length();
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	public static boolean startsWithIgnoreCase(String str, String prefix) {
		if (str == null || prefix == null) {
			return false;
		}
		if (str.startsWith(prefix)) {
			return true;
		}
		if (str.length() < prefix.length()) {
			return false;
		}
		String lcStr = str.substring(0, prefix.length()).toLowerCase();
		String lcPrefix = prefix.toLowerCase();
		return lcStr.equals(lcPrefix);
	}

	public static String clean(String in) {
		String out = in;

		if (in != null) {
			out = in.trim();
			if (out.equals(EMPTY_STRING)) {
				out = null;
			}
		}

		return out;
	}

	public static String[] tokenizeToStringArray(String str, String delimiters) {
		return tokenizeToStringArray(str, delimiters, true, true);
	}

	public static String[] tokenizeToStringArray(String str, String delimiters, boolean trimTokens,
			boolean ignoreEmptyTokens) {

		if (str == null) {
			return null;
		}
		StringTokenizer st = new StringTokenizer(str, delimiters);
		List<String> tokens = Lists.newArrayList();
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			if (trimTokens) {
				token = token.trim();
			}
			if (!ignoreEmptyTokens || token.length() > 0) {
				tokens.add(token);
			}
		}
		return toStringArray(tokens);
	}

	public static String[] toStringArray(Collection<String> collection) {
		if (collection == null) {
			return null;
		}
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	/**
	 * 信息格式化
	 * 
	 * @param template
	 * @param args
	 * @return
	 */
	public static String format(String template, Object... args) {
		template = String.valueOf(template); // null -> "null"
		StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
		int templateStart = 0;
		int i = 0;
		while (i < args.length) {
			int placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}
			builder.append(template.substring(templateStart, placeholderStart));
			builder.append(args[i++]);
			templateStart = placeholderStart + 2;
		}
		builder.append(template.substring(templateStart));

		if (i < args.length) {
			builder.append(" [");
			builder.append(args[i++]);
			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}
			builder.append(']');
		}
		return builder.toString();
	}

	/**
	 * 删除指定的字符
	 * 
	 * @param str
	 * @param remove
	 * @return
	 */
	public static String removeStart(String str, String remove) {
		if (str.startsWith(remove)) {
			return str.substring(remove.length());
		}
		return str;
	}
	
    public static String defaultString(String str, String defaultStr) {
        return !StringUtils.hasText(str) ? defaultStr : str;
    }
    
	/**
	 * 将属性转换为默认字段(约定大于配置)  userName --> USER_NAME
	 * @param property
	 * @return
	 */
	public static String convertProperty2Column(String property){
		StringBuilder column = new StringBuilder();
		for(int i=0;i<property.length();i++){
			char c = property.charAt(i);
			if(Character.isUpperCase(c)){
				column.append("_");
			}
			column.append(Character.toUpperCase(c));
		}
		return column.toString();
	}
	
	/**
	 * 转义数据库的特殊字符 --- 数据库的转义
	 * @param value
	 * @return
	 */
	public static Object escapeDb(String value) {
		if (value != null && value instanceof String
				&& StringUtils.containsAny((String)value, '\\', '\'')) {
			return StringUtils.replaceEach(value, new String[]{"\\", "\'"}, new String[]{"\\\\\\\\", "\\'"});
		}
		return value;
	}
	
	/**
	 * 删除： utf-8 无法显示的字符(有问题)，直接将数据库的相关字段改为
	 * NAME VARCHAR(100) CHARACTER SET UTF8MB4 COLLATE UTF8MB4_GENERAL_CI DEFAULT NULL COMMENT '昵称';
	 * 貌似不能解决所有问题，很多地方需要改，还不如把不能插入的字符过滤掉
	 * @param src
	 * @param replace
	 * @return
	 */
	public static String mb4Replace(String src, String replace) {
		replace = replace==null?"":replace;
		if(StringUtils.isNotBlank(src)) {
			return src.replaceAll("[\\x{10000}-\\x{10FFFF}]", replace);
		}
		return src;
	}
}
