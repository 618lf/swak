package com.swak.shop.web;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin/hello")
public class HelloController {

	@GetMapping("/say")
	public Mono<String> say() {
		return Mono.just("hello Lifeng");
	}
}