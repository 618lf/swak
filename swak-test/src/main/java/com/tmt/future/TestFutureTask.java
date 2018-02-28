package com.tmt.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

/**
 * 兩個知識點：
 * 1. interrupt()方法只是设置线程的中断标记，当对处于阻塞状态的线程调用interrupt方法时（处于阻塞状态的线程是调用sleep, wait, join 的线程)，
 * 会抛出InterruptException异常，而这个异常会清除中断标记。
 * 
 * 2. FutureTask 的使用
 * @author lifeng
 *
 */
public class TestFutureTask {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
        FutureTask<String> task = new FutureTask<String>(() -> {
        	try {
        		while(!Thread.interrupted()) {
            		Thread.sleep(1000); // 有这句会抛出异常， 没有这句话会判断异常
            		System.out.println("interrupt 可以中断我的执行");
            	}
        	}catch(Exception e){
                System.out.println("我收到interrupt 所以我退出了");
            }
        	return "123";
        });
        Thread thread = new Thread(task);
        thread.start(); 
        TimeUnit.MILLISECONDS.sleep(10);
        thread.interrupt();
        System.out.println(task.get());
 	}
}