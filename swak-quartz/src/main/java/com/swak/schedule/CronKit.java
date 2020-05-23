package com.swak.schedule;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.springframework.util.Assert;

import com.swak.utils.RegexUtil;
import com.swak.utils.time.DateTimes;

/**
 * Cron 表达式解析与时间获取<br>
 *
 * <pre>
 * CronKit cron = new CronKit("0 0 1 1 * ? *");
 * Date next = cron.next();
 * </pre>
 *
 *
 * @author Yuanqy https://my.oschina.net/jweb/blog/786444
 */
public class CronKit {
	private final String cron;
	private final Long[] crop;// 范围

	private Lock lock = new ReentrantLock(); // 辅助Calendar线程安全
	private final Calendar cal = Calendar.getInstance();// 实例化是线程安全的，但多个线程对同一Cal操作时是非线程安全的

	/**
	 * 支持6-7位长度cron表达式 不支持字符：L W C #
	 * 
	 * @param cron
	 */
	public CronKit(String cron) {
		Assert.notNull(cron, "error: cron must be not null");
		cron = cron.replaceAll("\\s+", " ").trim();
		this.cron = cron;
		this.crop = parse(cron);
	}

	public String getCron() {
		return cron;
	}

	public Long getCrop(int i) {
		return crop[i];
	}

	private Long getP(Bounds bound) {
		return crop[bound.index];
	}

	// Java(Spring)
	// * * * * * * *
	// - - - - - - - 秒 分 时 天 月 周 年
	// | | | | | | +--- year [optional(1970-2099)]
	// | | | | | +----- day of week (1 - 7) (Sunday=7) L C #
	// | | | | +------- month (1 - 12)
	// | | | +--------- day of month (1 - 31) L W C
	// | | +----------- hour (0 - 23)
	// | +------------- min (0 - 59)
	// +--------------- second (0 - 59)
	private enum Bounds {
		seconds(0, 0, 59, Calendar.SECOND), //
		minutes(1, 0, 59, Calendar.MINUTE), //
		hours(2, 0, 23, Calendar.HOUR_OF_DAY), //
		dom(3, 1, 31, Calendar.DAY_OF_MONTH), // Day1
		months(4, 1, 12, Calendar.MONTH), // Calendar.MONTH的范围是0-11
		dow(5, 1, 7, Calendar.DAY_OF_WEEK), // Day2
		year(6, 1970, 9999, Calendar.YEAR);

		public int index;
		public int min;
		public int max;
		public int calType;

		private Bounds(int index, int min, int max, int calType) {
			this.index = index;
			this.min = min;
			this.max = max;
			this.calType = calType;
		}

		public static Bounds getIndex(int index) {
			for (Bounds en : Bounds.values()) {
                if (en.index == index) {
                    return en;
                }
            }
			return null;
		}
	}

	private List<String> months = Arrays.asList(
			new String[] { "JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV", "DEC" });
	private List<String> dow = Arrays.asList(new String[] { "SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT" });
	private LocalDateTime lastTmp = LocalDateTime.now();// next缓存最后一次时间

	// == parse 解析
	private Long[] parse(String cron) {
		String[] items = cron.split(" ");
		Assert.isTrue(items.length == 6 || items.length == 7, "error： cron length must be 6 or 7 ");
		Long[] cronPosition = new Long[7];
		cronPosition[6] = ~0L; // 默认为年填充所有
		for (int i = 0; i < items.length; i++) {
            cronPosition[i] = getField(items[i], i);
        }
		return cronPosition;
	}

	/**
	 * 获取区间范围
	 */
	private long getField(String field, int index) {
		long bits = 0;
		if (field.contains(",")) {// 判断一个节点多个条件,递归
			String[] items = field.split(",");
			for (String chars : items) {
                bits |= getField(chars, index);
            }
		} else {
			int start = 0, end = 0, step = 1;
			Bounds bound = Bounds.getIndex(index);
			if (field.equals("*") || field.equals("?")) { // 是否仅有一个字符是 * 或者 ?。
				start = bound.min;
				end = bound.max;
			} else if (field.indexOf("-") > 0) {// 是否可以"-"分解为俩数字
				String[] items = field.split("-");
				start = parseIntOrName(items[0], bound);
				end = parseIntOrName(items[1], bound);
			} else if (field.indexOf("/") > 0) {// 是否可以"/"分解为俩数字
				String[] items = field.split("/");
				end = bound.max;
				start = bound.min;
				if (!items[0].equals("*")) {
                    start = parseIntOrName(items[0], bound);
                }
				step = parseIntOrName(items[1], bound);
			} else {// 默认 单个
				start = parseIntOrName(field, bound);
				end = start;
			}
			Assert.isTrue(start >= bound.min,
					String.format("Start of range (%d) must above the minimum (%d):%s", start, bound.min, field));
			Assert.isTrue(end <= bound.max,
					String.format("End of range (%d) must below  the maximum (%d):%s", end, bound.max, field));
			Assert.isTrue(start <= end,
					String.format("Start of range (%d) must below the end of range (%d):%s", start, end, field));
			bits |= getBits(start % 100, end % 100, step);// %100主要为了解决year的问题
		}
		return bits;
	}

	/**
	 * [核心1]获取区间<br>
	 * 原理： long类型二进制64位，cron 表达式所有的范围在0~59之间(除去第七位外)，这样可以把每个可用的点映射到二进制相应的位上。1代表有权限
	 */
	private Long getBits(int start, int end, int step) {
		Long bits = 0L;
		if (step == 1) // 当step=1时，可以直接反位并异或获取范围值。 这个if其实可以不要，都用下方的for循环匹配也可以
        {
            return ~(Long.MAX_VALUE << (end + 1)) & (Long.MAX_VALUE << start);
        }
		for (int i = start; i <= end; i += step) {
            bits |= 1L << i;
        }
		return bits;
	}

	private int parseIntOrName(String field, Bounds bound) {
		if (RegexUtil.isNumber(field)) {
            return Integer.parseInt(field);
        } else {
			field = field.toUpperCase();
			if (bound == Bounds.months) {
                return months.indexOf(field) + 1;
            } else if (bound == Bounds.dow) {
                return dow.indexOf(field) + 1;
            } else {
                throw new RuntimeException("parseIntOrName No match found:" + field + "[" + bound.index + "]");
            }
		}
	}

	/**
	 * 获取下一次的执行时间
	 */
	public LocalDateTime next() {
		lastTmp = next(lastTmp);
		return lastTmp;
	}

	public LocalDateTime next(LocalDateTime date) {
		lock.lock();// 保证线程安全
		try {
			boolean refresh = true;
			cal.setTime(DateTimes.convertLDTToDate(date));
			cal.set(Calendar.MILLISECOND, 0);// 毫秒清零
			cal.add(Calendar.SECOND, 1);// +1秒
			while (refresh) {
				boolean again = false;
				for (int i = 0; i < crop.length; i++) // 自小而大
                {
                    again |= checkField(cal, Bounds.getIndex(i));
                }
				refresh = again;
			}
			return DateTimes.convertDateToLDT(cal.getTime());
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			lock.unlock();
		}
	}

	/**
	 * [核心2]匹配时间 原理：通过位移后(位移方式和getBits方法一致) 判断 二进制范围内是否有匹配上的权限。
	 */
	private boolean checkField(Calendar cal, Bounds bound) {
		while (((1L << (calGet(cal, bound) % 100)) & getP(bound)) == 0) {// &运算，当匹配上即不为0；
			cal.add(bound.calType, 1);
			if (calGet(cal, bound) == bound.min) {
				clearChild(cal, bound);// 子集清零，当父级轮回，子集也得轮回
				return true;// 已轮回，需要重新计算
			}
		}
		return false;// 表示匹配完成
	}

	private int calGet(Calendar cal, Bounds bound) {
		return cal.get(bound.calType) + (bound == Bounds.months ? 1 : 0);// 如果是months得+1
	}

	private void clearChild(Calendar cal, Bounds bound) {
		if (bound == Bounds.dow)// dow与dom 只要清空秒,分,时就行了，并不想清空月份
        {
            bound = Bounds.dom;
        }
		for (int i = bound.index - 1; i >= 0; i--) { // 自大而小
			Bounds tmp = Bounds.getIndex(i);
			cal.set(tmp.calType, tmp.min);
		}
	}

}