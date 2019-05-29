package com.swak.utils;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.swak.exception.BaseRuntimeException;

/**
 * 正则表达式工具类
 * 
 * @author lifeng
 */
public final class RegexUtil {

	public static int REPLACE_FIRST = 0x00000001;
	public static int REPLACE_ALL = 0x00000010;
	public static int MATCH_DEFAULT = 0x00000000;
	public static int MATCH_CASE_INSENSITIVE = 0x00000100;
	public static int MATCH_MULTILINE = 0x00001000;
	public static int MATCH_SINGLELINE = 0x00010000;

	public static final String DATE_REGEX = "^[0-9]{4}\\-[0-9]{1,2}\\-[0-9]{1,2}$";
	public static final String DIGIT_REGEX = "^\\d+(\\.\\d+)?$";
	public static final String IP_REGEX = "\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b";
	public static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
	public static final String PHONE_NUM_REGEX = "^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

	/**
	 * 校验日期
	 * 
	 * @param date
	 * @return
	 */
	public static boolean isDate(String date) {
		Pattern p = Pattern.compile(DATE_REGEX);
		Matcher m = p.matcher(date);
		return m.matches();
	}

	/**
	 * 校验数字
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isNumber(String num) {
		Pattern p = Pattern.compile(DIGIT_REGEX);
		Matcher m = p.matcher(num);
		return m.matches();
	}

	/**
	 * 校验数字
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isIp(String num) {
		Pattern p = Pattern.compile(IP_REGEX);
		Matcher m = p.matcher(num);
		return m.matches();
	}

	/**
	 * 校验数字
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isEmail(String num) {
		Pattern p = Pattern.compile(EMAIL_REGEX);
		Matcher m = p.matcher(num);
		return m.matches();
	}

	/**
	 * 校验数字
	 * 
	 * @param num
	 * @return
	 */
	public static boolean isPhoneNum(String num) {
		Pattern p = Pattern.compile(PHONE_NUM_REGEX);
		Matcher m = p.matcher(num);
		return m.matches();
	}

	/**
	 * 正则的校验
	 * 
	 * @param regex
	 * @param value
	 * @return
	 */
	public static boolean checkRegex(String regex, String value) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(value);
		return m.matches();
	}

	// ----------------------------------------------
	public static int asOptions(String flags) {
		int options = MATCH_DEFAULT;
		if (flags != null) {
			options = asOptions(flags.indexOf('i') == -1, flags.indexOf('m') != -1, flags.indexOf('s') != -1);
			if (flags.indexOf('g') != -1) {
				options |= REPLACE_ALL;
			}
		}
		return options;
	}

	public static int asOptions(boolean caseSensitive, boolean multiLine, boolean singleLine) {
		int options = MATCH_DEFAULT;
		if (!caseSensitive) {
			options = options | MATCH_CASE_INSENSITIVE;
		}
		if (multiLine) {
			options = options | MATCH_MULTILINE;
		}
		if (singleLine) {
			options = options | MATCH_SINGLELINE;
		}
		return options;
	}

	public static boolean hasFlag(int options, int flag) {
		return ((options & flag) > 0);
	}

	/**
	 * 返回默认的正则表达式匹配器
	 * 
	 * @return
	 */
	public static RegexpMatcher newRegexpMatcher() {
		return new RegexpMatcher();
	}

	/**
	 * 返回默认的正则表达式匹配器
	 * 
	 * @return
	 */
	public static RegexpMatcher newRegexpMatcher(String pattern) {
		RegexpMatcher regexpMatcher = new RegexpMatcher();
		regexpMatcher.setPattern(pattern);
		return regexpMatcher;
	}

	/**
	 * 返回默认的正则表达式匹配器
	 * 
	 * @return
	 */
	public static RegexpMatcher newRegexpMatcher(String... patterns) {
		RegexpMatcher regexpMatcher = new RegexpMatcher();
		regexpMatcher.setPatterns(patterns);
		return regexpMatcher;
	}

	/**
	 * 正则表达式匹配
	 * 
	 * @author lifeng
	 */
	public static class RegexpMatcher {
		private String[] patterns;

		public void setPatterns(String... patterns) throws BaseRuntimeException {
			this.patterns = patterns;
		}

		public void setPattern(String pattern) throws BaseRuntimeException {
			this.patterns = new String[] { pattern };
		}

		public String getPattern() throws BaseRuntimeException {
			return this.patterns[0];
		}

		/**
		 * 校验是否匹配
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public boolean matches(String argument) throws BaseRuntimeException {
			return matches(argument, MATCH_DEFAULT);
		}

		/**
		 * 校验是否匹配
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public boolean matches(String input, int options) throws BaseRuntimeException {
			try {
				for (String pattern : this.patterns) {
					Pattern p = getCompiledPattern(pattern, options);
					Boolean b = p.matcher(input).find();
					if (b) {
						return b;
					}
				}
				return false;
			} catch (Exception e) {
				throw new BaseRuntimeException(e);
			}
		}

		/**
		 * 返回分组数据
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public String[] getArrayGroups(String argument) throws BaseRuntimeException {
			Vector<String> groups = getGroups(argument, MATCH_DEFAULT);
			if (groups != null && groups.size() != 0) {
				return groups.toArray(new String[groups.size()]);
			}
			return null;
		}

		/**
		 * 返回分组数据
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public String[] getArrayGroups(String argument, int options) throws BaseRuntimeException {
			Vector<String> groups = getGroups(argument, options);
			if (groups != null && groups.size() != 0) {
				return groups.toArray(new String[groups.size()]);
			}
			return null;
		}

		/**
		 * 返回分组数据
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public Vector<String> getGroups(String argument) throws BaseRuntimeException {
			return getGroups(argument, MATCH_DEFAULT);
		}

		/**
		 * 返回分组数据
		 * 
		 * @param input
		 * @param options
		 * @return
		 * @throws BaseRuntimeException
		 */
		public Vector<String> getGroups(String input, int options) throws BaseRuntimeException {
			for (String pattern : this.patterns) {
				Pattern p = getCompiledPattern(pattern, options);
				Matcher matcher = p.matcher(input);
				if (!matcher.find()) {
					continue;
				}
				Vector<String> v = new Vector<String>();
				int cnt = matcher.groupCount();
				for (int i = 0; i <= cnt; i++) {
					String match = matcher.group(i);
					if (match == null) {
						match = "";
					}
					v.addElement(match);
				}
				return v;
			}
			return null;
		}

		protected Pattern getCompiledPattern(String pattern, int options) throws BaseRuntimeException {
			int cOptions = getCompilerOptions(options);
			try {
				return Pattern.compile(pattern, cOptions);
			} catch (PatternSyntaxException e) {
				throw new BaseRuntimeException(e);
			}
		}

		protected int getCompilerOptions(int options) {
			int cOptions = Pattern.UNIX_LINES;
			if (RegexUtil.hasFlag(options, MATCH_CASE_INSENSITIVE)) {
				cOptions |= Pattern.CASE_INSENSITIVE;
			}
			if (RegexUtil.hasFlag(options, MATCH_MULTILINE)) {
				cOptions |= Pattern.MULTILINE;
			}
			if (RegexUtil.hasFlag(options, MATCH_SINGLELINE)) {
				cOptions |= Pattern.DOTALL;
			}
			return cOptions;
		}
	}
}