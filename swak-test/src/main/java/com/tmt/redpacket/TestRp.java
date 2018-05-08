package com.tmt.redpacket;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.swak.common.boot.Boot;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.utils.Ints;
import com.swak.common.utils.JsonMapper;
import com.swak.common.utils.Maps;
import com.swak.common.utils.SpringContextHolder;

import redis.clients.util.SafeEncoder;

/**
 * 测试抢红包
 * @author lifeng
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/com/tmt/cache/applicationContext-test-redis.xml" }) // 加载配置文件
public class TestRp {

	int honBaoCount = 1_0_0000;  
    int threadCount = 20; 
    
	String list_rp = "rps";
	String list_rp_robd = "rps_rpbd";
	String map_rp_user = "rps_user";
	
	String script = "";
	
	volatile boolean overed = false;
	
	AtomicInteger count = new AtomicInteger();
	
	/**
	 * 需要启动
	 */
	@Before
	public void _before() {
		
		// 启动项
		Map<String, Boot> boots = SpringContextHolder.getBeans(Boot.class);
		if (boots != null && !boots.isEmpty()) {
	    	Set<String> keys = boots.keySet();
	    	Iterator<String> it = keys.iterator();
	    	while(it.hasNext()){
	    		try {
	    			Boot realm = boots.get(it.next());
	    			realm.start();
				} catch (Exception e) {}
	    	}
	    }
		
		// 读取脚本
		try {
			List<String> lines = IOUtils.readLines(TestRp.class.getResourceAsStream("getrp.lua"));
			StringBuilder _sc = new StringBuilder();
			lines.stream().forEach(s -> _sc.append(s).append("\n"));
			script = _sc.toString();
			
			// 输出脚本
			System.out.println("取红包的脚本：");
			System.out.println(script);
		}catch(Exception e) {}
		
		// 初始化红包
		this.loadRp();
	}
	
	private void loadRp() {
		
		// 清空数据
		RedisUtils.del(list_rp);
		RedisUtils.del(list_rp_robd);
		RedisUtils.del(map_rp_user);
		
		// 加载红包
		final CountDownLatch latch = new CountDownLatch(threadCount);  
		Stream.iterate(0, i -> i+1).limit(threadCount).forEach(i -> {
			Thread thread = new Thread() {
				@Override
				public void run() {
					int per = honBaoCount/threadCount;
					Map<String, Object> object = Maps.newHashMap();
					for(int j = i * per; j < (i+1) * per; j++) {  
						object.put("id", j);
						object.put("money", j);
						RedisUtils.lPush(list_rp, SafeEncoder.encode(JsonMapper.toJson(object)));
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
	
	@Test
	public void test() {
		final CountDownLatch latch = new CountDownLatch(threadCount + 1);  
		
		// 模拟抢红包
		long t1 = System.currentTimeMillis();
		System.out.println("开始抢红包");
		Stream.iterate(0, i -> i+1).limit(threadCount).forEach(i -> {
			Thread thread = new Thread(() -> {
				while(!overed) { 
					int j = Ints.random(honBaoCount);
					byte[][] values = new byte[][] {
						SafeEncoder.encode(list_rp),
						SafeEncoder.encode(list_rp_robd),
						SafeEncoder.encode(map_rp_user),
						SafeEncoder.encode("" + j)
					};
					RedisUtils.runScript(script, null, values);
					
					// 报表监听
					count.incrementAndGet();
				}
				latch.countDown();
			}) ;
			thread.start();
		});
		
		// 红包检查线程
		Thread thread = new Thread(() -> {
			while(!overed) {
				if (RedisUtils.lLen(list_rp) == 0) {
					overed = true;
					break;
				}
			}
			latch.countDown();
		}) ;
		thread.start();
		
		try {
			latch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		long t2 = System.currentTimeMillis() - t1;
		System.out.println("抢红包结束, use=" + t2 + ", count=" + count + ", qps=" + (count.get()/(t2/1000.0)));
	}
}