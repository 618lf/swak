package com.tmt.disruptor;

import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.WorkHandler;

public class MsgDataHandler implements WorkHandler<MsgData> {

	private AtomicInteger count;
	public MsgDataHandler(AtomicInteger count) {
		this.count = count;
	}
	
	@Override
	public void onEvent(MsgData event) throws Exception {
        event.getMsg();  
        count.incrementAndGet();
        Thread.sleep(10);
        System.out.println(Thread.currentThread().getName());
	}  
}