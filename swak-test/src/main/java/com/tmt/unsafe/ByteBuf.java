package com.tmt.unsafe;

import java.lang.reflect.Field;

import sun.misc.Unsafe;

/**
 * 类似 netty 的 ByteBuf，
 * 由于unsafe 分配的内容需要手动释放，jvm不会自动释放这部分的内容
 * 所以需要在释放 ByteBuf 时（ByteBuf会再堆中分配内容，gc会回收）
 * 
 * 貌似netty 有更好的释放方式，以后我们在研究
 * @author lifeng
 */
@SuppressWarnings("restriction")
public class ByteBuf {
	
	private long address = 0;  
	
	// 让对象占用堆内存,触发[Full GC
    private byte[] bytes = null;  
    
	static final Unsafe unsafe;

	static {
		try {
			Field theUnsafe = Unsafe.class.getDeclaredField("theUnsafe");
			theUnsafe.setAccessible(true);
			unsafe = (Unsafe) theUnsafe.get(null);
		} catch (Exception e) {
			throw new AssertionError(e);
		}
	}
	
	private ByteBuf(int size) {
		address = unsafe.allocateMemory(size * 1L);
		bytes = new byte[size];
	}
	
	/**
	 * 创建一个堆外内容
	 * @param size
	 * @return
	 */
	public static ByteBuf allocateMemory(int size) {
		return new ByteBuf(size);
	}
	
	/**
	 * 主要是这个方法：
	 * 当堆中的对象即将被垃圾回收器释放的时候，会调用该对象的finalize。
	 * 由于JVM只会帮助我们管理内存资源，不会帮助我们管理数据库连接，文件句柄等资源，
	 * 所以我们需要在finalize自己释放资源
	 */
	@Override  
    protected void finalize() throws Throwable {  
        super.finalize();  
        System.out.println("finalize." + bytes.length);  
        unsafe.freeMemory(address);  
    }  
}
