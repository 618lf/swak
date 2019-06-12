package com.swak.rabbit;

/**
 * 系统默认的消息队列
 * 
 * @author lifeng
 */
public interface Constants {

	// retry queue
	String retry1s_channel = "swak.retry1s";
	String retry5s_channel = "swak.retry5s";
	String retry10s_channel = "swak.retry10s";
	String retry30s_channel = "swak.retry30s";
	String retry60s_channel = "swak.retry60s";
	String retry120s_channel = "swak.retry120s";
	String retry180s_channel = "swak.retry180s";
	String retry240s_channel = "swak.retry240s";
	String retry300s_channel = "swak.retry300s";
	String retry360s_channel = "swak.retry360s";
	String retry420s_channel = "swak.retry420s";
	String retry480s_channel = "swak.retry480s";
	String retry540s_channel = "swak.retry540s";
	String retry600s_channel = "swak.retry600s";
	String retry1200s_channel = "swak.retry1200s";
	String retry1800s_channel = "swak.retry1800s";
	String retry3600s_channel = "swak.retry3600s";
	String retry7200s_channel = "swak.retry7200s";
	String dead_channel = "swak.dead";
	String retry_channel = "swak.retry";
	String fail_channel = "swak.fail";
	
	// retry count
	String x_retry = "x-retry";
	String x_death_queue = "x-death-queue";
	
	// dead times
	int dead = 10000;
	
	// retry times
	int[] retrys = new int[] { 1 * 1000, 5 * 1000, 10 * 1000, 30 * 1000, 60 * 1000, 120 * 1000, 180 * 1000, 240 * 1000,
			300 * 1000, 360 * 1000, 420 * 1000, 480 * 1000, 540 * 1000, 600 * 1000, 1200 * 1000, 1800 * 1000,
			3600 * 1000, 7200 * 1000 };
}
