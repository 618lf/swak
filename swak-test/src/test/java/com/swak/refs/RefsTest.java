package com.swak.refs;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class RefsTest {
	public static void main(String[] args) throws InterruptedException, IOException {

		// 测试强引用被回收
		// M m = new M();
//		m.hashCode();
//		m = null;
//		System.gc();
//		System.in.read();

		// 测试软引用被回收
		// -Xmx20M
//		SoftReference<M> mReference = new SoftReference<M>(new M());
//		System.out.println(mReference.get());
//		System.gc(); // 多线程运行的
//
//		TimeUnit.SECONDS.sleep(1);
//		System.out.println(mReference.get());
//
//		new Thread(() -> {
//			for (;;) {
//				new M();
//				try {
//					TimeUnit.MILLISECONDS.sleep(10);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				if (mReference.get() == null) {
//					System.out.println("被回收了");
//					break;
//				}
//			}
//		}).start();

		// 测试软引用被回收 -- 重要
//		WeakReference<M> weakReference = new WeakReference<M>(new M());
//		System.out.println(weakReference.get());
//		System.gc();
//		TimeUnit.SECONDS.sleep(1);
//		System.out.println(weakReference.get());
//		new WeakThread().start();
//		TimeUnit.SECONDS.sleep(1);
//		System.gc();
//		TimeUnit.SECONDS.sleep(2);
//		System.gc();

		// 虚引用
//		ReferenceQueue<M> queue = new ReferenceQueue<>();
//		PhantomReference<M> phantomReference = new PhantomReference<>(new M(), queue);
	}
}

class M {
	byte[] data = new byte[10 * 1024 * 1024];

	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("被回收");
	}
}

class WeakThread extends Thread {

	WeakThreadLocal m;

	public WeakThread() {
		m = new WeakThreadLocal();
	}

	@Override
	public void run() {
		m.set(new M());
		m = null;
		for (;;) {
			try {
				TimeUnit.MILLISECONDS.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}

class WeakThreadLocal extends ThreadLocal<M> {
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		System.out.println("ThreadLocal 被回收");
	}
}
