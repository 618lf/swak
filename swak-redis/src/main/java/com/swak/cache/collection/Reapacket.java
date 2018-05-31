package com.swak.cache.collection;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Stream;

import com.swak.cache.SafeEncoder;
import com.swak.cache.redis.operations.SyncOperations;
import com.swak.utils.IOUtils;
import com.swak.utils.JsonMapper;
import com.swak.utils.Maps;

import io.lettuce.core.ScriptOutputType;

/**
 * 红包
 * @author lifeng
 */
public class Reapacket {

	// 脚本
	static String script = null;
	static {
		try {
			List<String> lines = IOUtils.readLines(Reapacket.class.getResourceAsStream("getrp.lua"));
			StringBuilder _sc = new StringBuilder();
			lines.stream().forEach(s -> _sc.append(s).append("\n"));
			script = _sc.toString();
		}catch(Exception e) {}
	}
	
	// 基本的队列或散列表名称
	static String BASE_LIST_RP = "-RPS";
	static String BASE_LIST_RP_ROBD = "-RPS_RPBD";
	static String BASE_MAP_RP_USER = "-RPS_USER";
	
	private int size;
	
	String list_rp = null;
	String list_rp_robd = null;
	String map_rp_user = null;
	
	public Reapacket name(String name) {
		this.list_rp = new StringBuilder(name).append(BASE_LIST_RP).toString();
		this.list_rp_robd = new StringBuilder(name).append(BASE_LIST_RP_ROBD).toString();
		this.map_rp_user = new StringBuilder(name).append(BASE_MAP_RP_USER).toString();
		return this;
	}
	public Reapacket size(int size) {
		this.size = size;
		return this;
	}
	public Reapacket start() {
		this.init();
		return this;
	}
	
	/**
	 * 用户抢红包
	 * @param user
	 * @return
	 */
	public String userRob(String user) {
		byte[][] values = new byte[][] {
			SafeEncoder.encode(list_rp),
			SafeEncoder.encode(list_rp_robd),
			SafeEncoder.encode(map_rp_user),
			SafeEncoder.encode(user)
		};
		byte[] result = SyncOperations.runScript(script, ScriptOutputType.VALUE, values);
		return result != null? SafeEncoder.encode(result) : null;
	}
	
	/**
	 * 是否抢完
	 * @return
	 */
	public boolean finish() {
		return SyncOperations.lLen(list_rp) == 0;
	}
	
	private void init() {
		
		// 多线程来初始化数据
		int threadCount = 2;
		
		// 加载红包
		final CountDownLatch latch = new CountDownLatch(threadCount);  
		Stream.iterate(0, i -> i+1).limit(threadCount).forEach(i -> {
			Thread thread = new Thread() {
				@Override
				public void run() {
					int per = size / threadCount;
					Map<String, Object> object = Maps.newHashMap();
					for(int j = i * per; j < (i+1) * per; j++) {  
						object.put("id", j);
						object.put("men", j);
						SyncOperations.lPush(list_rp, SafeEncoder.encode(JsonMapper.toJson(object)));
					}
					latch.countDown();
				} 
			};
			thread.start();
		});
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static Reapacket get() {
		return new Reapacket();
	}
}