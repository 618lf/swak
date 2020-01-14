package com.swak.rabbit;

/**
 * 系统默认的消息队列
 * 
 * @author lifeng
 */
public interface Constants {

	// retry queue
	String retry1s_channel = "swak.x.retry1s";
	String retry5s_channel = "swak.x.retry5s";
	String retry10s_channel = "swak.x.retry10s";
	String retry30s_channel = "swak.x.retry30s";
	String retry60s_channel = "swak.x.retry60s";
	String retry120s_channel = "swak.x.retry120s";
	String retry180s_channel = "swak.x.retry180s";
	String retry240s_channel = "swak.x.retry240s";
	String retry300s_channel = "swak.x.retry300s";
	String retry360s_channel = "swak.x.retry360s";
	String retry420s_channel = "swak.x.retry420s";
	String retry480s_channel = "swak.x.retry480s";
	String retry540s_channel = "swak.x.retry540s";
	String retry600s_channel = "swak.x.retry600s";
	String retry1200s_channel = "swak.x.retry1200s";
	String retry1800s_channel = "swak.x.retry1800s";
	String retry3600s_channel = "swak.x.retry3600s";
	String retry7200s_channel = "swak.x.retry7200s";
	String dead_channel = "swak.x.dead";
	String retry_channel = "swak.x.retry";
	String fail_channel = "swak.x.fail";
	
	// retry - consumer queue
	String retry1s_channel_cus = "swak.x.retry1s_cus";
	String retry5s_channel_cus = "swak.x.retry5s_cus";
	String retry10s_channel_cus = "swak.x.retry10s_cus";
	String retry30s_channel_cus = "swak.x.retry30s_cus";
	String retry60s_channel_cus = "swak.x.retry60s_cus";
	String retry120s_channel_cus = "swak.x.retry120s_cus";
	String retry180s_channel_cus = "swak.x.retry180s_cus";
	String retry240s_channel_cus = "swak.x.retry240s_cus";
	String retry300s_channel_cus = "swak.x.retry300s_cus";
	String retry360s_channel_cus = "swak.x.retry360s_cus";
	String retry420s_channel_cus = "swak.x.retry420s_cus";
	String retry480s_channel_cus = "swak.x.retry480s_cus";
	String retry540s_channel_cus = "swak.x.retry540s_cus";
	String retry600s_channel_cus = "swak.x.retry600s_cus";
	String retry1200s_channel_cus = "swak.x.retry1200s_cus";
	String retry1800s_channel_cus = "swak.x.retry1800s_cus";
	String retry3600s_channel_cus = "swak.x.retry3600s_cus";
	String retry7200s_channel_cus = "swak.x.retry7200s_cus";
		
	// retry count
	String x_retry = "x-retry";
	
	// dead times
	int dead = 10000;
	
	// retry times
	int[] retrys = new int[] { 1 * 1000, 5 * 1000, 10 * 1000, 30 * 1000, 60 * 1000, 120 * 1000, 180 * 1000, 240 * 1000,
			300 * 1000, 360 * 1000, 420 * 1000, 480 * 1000, 540 * 1000, 600 * 1000, 1200 * 1000, 1800 * 1000,
			3600 * 1000, 7200 * 1000 };
}
