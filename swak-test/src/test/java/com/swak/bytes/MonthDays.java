package com.swak.bytes;

import com.swak.utils.Bits;
import com.swak.utils.time.DateUtils;

@SuppressWarnings("deprecation")
public class MonthDays {

	public static void main(String[] args) {
		// 设置本月的所有天数
		int days = DateUtils.getDayOfMonth(
				DateUtils.getMonthLastDate(DateUtils.getFormatDate("2020" + "-" + "09" + "-01", "yyyy-MM-dd")));

		// 标记每天都没有完成
		int opsDays = 0;
		for (int i = 1; i <= days; i++) {
			opsDays = Bits.setBit(opsDays, i - 1, 1);
		}

		System.out.println(days + ":" + opsDays);

		for (int i = 1; i <= days; i++) {
			opsDays = Bits.setBit(opsDays, i - 1, 0);
		}
		
		System.out.println(days + ":" + opsDays);
	}

}
