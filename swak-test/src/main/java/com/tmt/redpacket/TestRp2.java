package com.tmt.redpacket;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.swak.common.boot.Boot;
import com.swak.common.cache.collection.Reapacket;
import com.swak.common.utils.Ints;
import com.swak.common.utils.SpringContextHolder;

/**
 * 测试抢红包
 * @author lifeng
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/com/tmt/cache/applicationContext-test-redis.xml" }) // 加载配置文件
public class TestRp2 {

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
	}
	
	int honBaoCount = 1_0_0000;  
    int threadCount = 20; 
    volatile boolean overed = false;
	AtomicInteger count = new AtomicInteger();
	
	@Test
	public void testRp() {
		// 启动一个红包
		Reapacket.get().name("lifeng").size(1000).start();
		
		
		// 用于抢红包
		AtomicInteger count = new AtomicInteger();
		Reapacket rob = Reapacket.get().name("lifeng");
        final CountDownLatch latch = new CountDownLatch(threadCount + 1);  
		
		// 模拟抢红包
		long t1 = System.currentTimeMillis();
		System.out.println("开始抢红包");
		Stream.iterate(0, i -> i+1).limit(threadCount).forEach(i -> {
			Thread thread = new Thread(() -> {
				while(!overed) { 
					int j = Ints.random(honBaoCount);
					rob.userRob("" + j);
					
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
				if (rob.finish()) {
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
