package com.tmt.shop.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.kotlin.MonosKt;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.entity.Shop;
import com.tmt.shop.entity.ShopXml;
import com.tmt.shop.service.ShopService;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * 测试的 demo
 * @author lifeng
 */
@RestController("/admin/hello")
public class HelloController {

	@Autowired
	private ShopService shopService;
	
	// 模拟异步api的调用
	public static void async_task(MonoSink<String> sink) {
		Thread nThread = new Thread() {
			@Override
			public void run() {
				try {
					System.out.println(Thread.currentThread().getName());
					Thread.sleep(200000L);
					sink.success("123");
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		};
		nThread.setName("我是用来阻塞的");
		nThread.setDaemon(true);
		nThread.start();
	}
	
	/**
	 * from async apis
	 * @return
	 */
	@GetMapping("/say/async_api")
	public Mono<String> sayAsync_api() {
		return Mono.create((sink) -> {
			async_task(sink);
		});
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
	 * 协程 -- 只能用来处理 io 的问题
	 * 如果仅仅是cpu 的事情，反而慢，所有只有一个场景可用，那就是 网络IO
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