package com.tmt.cache;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.swak.common.boot.Boot;
import com.swak.common.cache.Cache;
import com.swak.common.cache.redis.RedisUtils;
import com.swak.common.utils.SpringContextHolder;

/**
 * redis 的性能测试
 * 
 * 单线程下redis 的性能为 100000次约 1.8秒
 * 多线程下redis 的性能为 100000次约 2.2秒
 * 
 * 所以在请求中，尽量减少对redis 的使用，如果能使用本地缓存，
 * 可以使用本地缓存作为二级缓存，如果服务器不多，可以使用，
 * 如果服务器过多也会导致问题。
 * 
 * 使用二级缓存之后提升了多少个数量级： 100000次约 86毫秒
 * @author lifeng
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/com/tmt/cache/applicationContext-test-redis.xml" }) // 加载配置文件
public class TestRedis {
	
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

	/**
	 * 执行测试
	 */
	@Test
	public void test() {
		Cache cache = CacheUtils.sys().get();
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		for (int i = 0; i < 100000; i++) {
			cache.put("hanqian", "哈哈");
			cache.get("hanqian");
		}
		System.out.println("norma ,use=" + (System.currentTimeMillis() - t1));
	}
	
	/**
	 * 执行测试
	 * 多条命令非常的块，而且是原子的操作
	 */
	@Test
	public void testLua() {
		String lua = new StringBuilder().append("redis.call(\"SET\", KEYS[1], KEYS[2]); return redis.call(\"GET\", KEYS[1]);").toString();
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		String[] keys = new String[] {"sys#lifeng", "哈哈"};
		for (int i = 0; i < 100000; i++) {
			RedisUtils.getRedis().runAndGetOne(lua, keys);
		}
		System.out.println("lua ,use=" + (System.currentTimeMillis() - t1));
	}
}