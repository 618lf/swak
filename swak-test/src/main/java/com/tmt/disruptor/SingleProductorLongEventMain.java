package com.tmt.disruptor;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class SingleProductorLongEventMain {

	private static final Translator TRANSLATOR = new Translator();

	public static void main(String[] args) throws Exception {
		// Specify the size of the ring buffer, must be power of 2.
		int bufferSize = 1024;
		
		ThreadFactory threadFactory = new ThreadFactory() {
		    private final AtomicInteger index = new AtomicInteger(1);
		    @Override
		    public Thread newThread(Runnable r) {
		        return new Thread((ThreadGroup) null, r, "disruptor-thread-" + index.getAndIncrement());
		    }
		};

		// Construct the Disruptor
		Disruptor<MsgData> disruptor = new Disruptor<MsgData>(MsgData::new, bufferSize, threadFactory,
				ProducerType.SINGLE, new BlockingWaitStrategy());

		// Connect the handler
		disruptor.handleEventsWith(new MsgDataHandler2(null));

		// Start the Disruptor, starts all threads running
		disruptor.start();

		// Get the ring buffer from the Disruptor to be used for publishing.
		RingBuffer<MsgData> ringBuffer = disruptor.getRingBuffer();

		ByteBuffer bb = ByteBuffer.allocate(8);
		long t1 = System.currentTimeMillis();
		System.out.println("begin=" + t1);
		for (long l = 0; l < 1200; l++) {
			bb.putLong(0, l);
			ringBuffer.publishEvent(TRANSLATOR, bb);
		}
		System.out.println("over ,use=" + (System.currentTimeMillis() - t1));
	}
}
