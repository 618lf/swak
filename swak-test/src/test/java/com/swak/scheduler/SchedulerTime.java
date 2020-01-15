package com.swak.scheduler;

import java.util.Date;

import org.springframework.scheduling.support.CronSequenceGenerator;

import com.swak.utils.time.DateUtils;

/**
 * 调度时间的测试
 * 
 * @author lifeng
 */
public class SchedulerTime {

	public static void main(String[] args) throws InterruptedException {
		CronSequenceGenerator cronSequenceGenerator = new CronSequenceGenerator("0 0/3 * * * *");
		for (int i = 0; i <= 10; i++) {
			Thread.sleep(1000);
			Date nextDate = cronSequenceGenerator.next(DateUtils.getTimeStampNow());
			System.out.println(
					DateUtils.getTodayStr("mm:ss") + ":" + DateUtils.getFormatDate(nextDate, "yyyy-MM-dd HH:mm:ss"));
		}
	}
}
