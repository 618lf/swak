package com.tmt.concurrent;

import java.util.Calendar;

public class DateTestImpl implements DateTest {

	private String _date = null;

	public DateTestImpl() {
		try {
			_date += Calendar.getInstance().getTime();
			// 设定五秒延迟
			Thread.sleep(5000);
		} catch (InterruptedException e) {
		}
	}

	@Override
	public String getDate() {
		return "date "+_date;
	}
}