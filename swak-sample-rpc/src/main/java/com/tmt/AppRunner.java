package com.tmt;

import org.springframework.context.annotation.ComponentScan;

import com.swak.Application;
import com.swak.ApplicationBoot;

@ComponentScan
@ApplicationBoot
public class AppRunner {

	public static void main(String[] args) {
		Application.run(AppRunner.class, args);
		
		System.out.println("开始停止服务");
		new Thread(() -> {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Application.stop();
		}).start();
	}
}