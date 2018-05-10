package com.tmt.shop.web;

import org.springframework.beans.factory.annotation.Autowired;

import com.swak.common.Constants;
import com.swak.common.cache.Cache;
import com.swak.common.cache.redis.RedisCacheManager;
import com.swak.common.eventbus.EventProducer;
import com.swak.kotlin.MonosKt;
import com.swak.reactivex.web.annotation.GetMapping;
import com.swak.reactivex.web.annotation.PathVariable;
import com.swak.reactivex.web.annotation.RestController;
import com.tmt.shop.entity.Shop;
import com.tmt.shop.service.ShopService;

import reactor.core.publisher.Mono;

@RestController("/admin/hello")
public class HelloController {

	@Autowired
	private RedisCacheManager cacheManager;
	@Autowired
	private EventProducer eventProducer;
	@Autowired
	private ShopService shopService;

	@GetMapping("/say")
	public Mono<String> say() {
		return Mono.fromSupplier(() -> {
			Cache<String> cache = cacheManager.getCache("user2", 1000);
			cache.getString("name");
			
			eventProducer.publish(Constants.UPDATE_EVENT_TOPIC, 123);
			return shopService.say();
		});
	}

	@GetMapping("say/{id}")
	public Mono<Shop> say(@PathVariable String id) {
		return MonosKt.create(() -> {
			Shop shop = new Shop();
			return shop;
		});
	}
}