package com.tmt.shop.web;

import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.cache.Cache;
import com.swak.cache.CacheManager;
import com.swak.entity.Result;
import com.swak.executor.Workers;
import com.swak.kotlin.MonosKt;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.entity.Shop;
import com.tmt.shop.entity.ShopXml;
import com.tmt.shop.service.ShopService;

import reactor.core.publisher.Mono;

/**
 * 测试的 demo
 * @author lifeng
 */
@RestController("/admin/hello")
public class HelloController {

	@Autowired
	private ShopService shopService;
	
	@Autowired
	private CacheManager cacheManager;
	
	/**
	 * from async apis
	 * @return
	 */
	@GetMapping("/say/cache_put")
	public void sayPut() {
		Cache<Object> cache = cacheManager.getCache("sys");
		cache.putObject("shop-1", new Shop());
		cache.putObject("shop-2", "shop");
	}
	
	/**
	 * from async apis
	 * @return
	 */
	@GetMapping("/say/cache_get")
	public void sayCache() {
		Cache<String> cache = cacheManager.getCache("sys");
		System.out.println(cache.getObject("shop-1"));
		Cache<Shop> cache2 = cacheManager.getCache("sys");
		System.out.println(cache2.getObject("shop-2"));
	}
	
	/**
	 * from async apis
	 * @return
	 */
	@GetMapping("/say/async_api")
	public Mono<Result> sayAsync_api() {
		return Workers.sink(() ->{
			return "123";
		}).map(s -> Result.success(s));
	}
	
	/**
	 * 返回 null 的问题
	 * @return
	 */
	@GetMapping("/say/void")
	public void sayVoid() {
		try {
			Thread.sleep(10000L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 返回 null 的问题
	 * @return
	 */
	@GetMapping("/say/mono-void")
	public Mono<Void> sayMonoVoid() {
		return Mono.empty();
	}
	
	/**
	 * 抛出异常
	 * @return
	 */
	@GetMapping("/say/error")
	public String sayError() {
		int i = 1/ 0;
		return "lifeng" + i;
	}
	
	/**
	 * 输出string 类型
	 * @return
	 */
	@GetMapping("/say/string")
	public String sayString() {
		return "lifeng";
	}
	
	/**
	 * 输出其他对象
	 * @return
	 */
	@GetMapping("/say/object")
	public Shop sayObject() {
		return new Shop();
	}
	
	/**
	 * 输出 Xml
	 * @return
	 */
	@GetMapping("/say/xml")
	public ShopXml sayXml() {
		ShopXml xml = new ShopXml();
		xml.setName("lifeng");
		return xml;
	}

	/**
	 * 返回 mono 对象
	 * @return
	 */
	@GetMapping("/say/mono")
	public Mono<String> sayMono() {
		return Mono.fromSupplier(() -> {
			return shopService.say();
		});
	}
	
	/**
	 * 返回 mono 对象
	 * @return
	 */
	@GetMapping("/say/future")
	public Mono<Result> sayFuture(String name) {
		Shop shop = new Shop(); shop.setName(name);
		return Mono.fromFuture(shopService.saveAndGet(shop)).map(s -> Result.success(s));
	}
	
	/**
	 * 返回 mono 对象
	 * @return
	 */
	@GetMapping("/say/stream")
	public Mono<Result> sayStream(String name) {
		Stream<CompletableFuture<Shop>> optional = Stream.of(name).map(s -> {
			Shop shop = new Shop(); shop.setName(name);
			return shop;
		}).map(s -> {
			return shopService.saveAndGet(s);
		});
		return Workers.stream(optional).map(s -> Result.success(s));
	}

	/**
	 * 协程 -- 只能用来处理 io 的问题
	 * 如果仅仅是cpu 的事情，反而慢，所有只有一个场景可用，那就是 网络IO
	 * 而且必须是异步IO，不知道是否会自动切协程，同步IO不会自动切协程
	 * 但如果是异步IO，那协程仅仅将异步代码变为同步代码。
	 * @param id
	 * @return
	 */
	@GetMapping("/say/xc")
	public Mono<Shop> sayXc() {
		return MonosKt.create(() -> {
			shopService.say();
			return new Shop();
		});
	}
}