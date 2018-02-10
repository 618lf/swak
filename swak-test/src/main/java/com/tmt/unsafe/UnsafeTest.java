package com.tmt.unsafe;

import java.lang.reflect.Field;
import java.nio.ByteBuffer;

import sun.misc.Unsafe;

/**
 * 测试unsafe
 * 
 * @author lifeng
 */
@SuppressWarnings("restriction")
public class UnsafeTest {

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
	
	/**
	 * 可以直接分配内存
	 * -- 分配内存方法还有重分配内存方法都是分配的“堆外内存”
	 * -- 一定要注意是堆外内存，突破了java内容的限制
	 * 
	 * -- 有如下的两种方式来分配 “堆外内存”
	 * Unsafe和NIO包下ByteBuffer
	 * 
	 * ByteBuffer.allocateDirect分配的堆外内存不需要我们手动释放，而且ByteBuffer中也没有提供手动释放的API
	 */
	static void testAllocateMem() {
		long allocatedAddress = unsafe.allocateMemory(1L);
		unsafe.putByte(allocatedAddress, (byte) 100);
		byte shortValue = unsafe.getByte(allocatedAddress);
		System.out.println(new StringBuilder().append("Address:").append(allocatedAddress).append(" Value:").append(shortValue));
		
		/**
		 * Free掉,这个数据可能脏掉
		 */
		unsafe.freeMemory(allocatedAddress);
		shortValue = unsafe.getByte(allocatedAddress);
		System.out.println(new StringBuilder().append("Address:").append(allocatedAddress).append(" Value:").append(shortValue));
	
	    /**
	     * ByteBuffer 的用法
	     * -- 不需要手动释放堆外内存
	     */
		ByteBuffer.allocateDirect(10 * 1024 * 1024);
	}
	
	/**
	 * 直接修改某个对象的字段的值
	 * @param clazz
	 * @param fieldName
	 */
	static void modifyField(SampleClass sample, String fieldName) {
		try {
			
			// 对应的字段
			Field field = SampleClass.class.getDeclaredField(fieldName);
			
			// 字段所在类的偏移量
			long iFiledAddressShift = unsafe.objectFieldOffset(field);
			
			//获取对象的偏移地址，需要将目标对象设为辅助数组的第一个元素（也是唯一的元素）。由于这是一个复杂类型元素（不是基本数据类型），它的地址存储在数组的第一个元素。然后，获取辅助数组的基本偏移量。数组的基本偏移量是指数组对象的起始地址与数组第一个元素之间的偏移量。
			Object helperArray[]  = new Object[1];
			helperArray[0] = sample;
			
			// Object的偏移量
			long baseOffset = unsafe.arrayBaseOffset(Object[].class);
			
			// SampleClass的偏移量
			long addressOfSampleClass = unsafe.getLong(helperArray, baseOffset);
			
			// 字段的值
			int i = unsafe.getInt(addressOfSampleClass + iFiledAddressShift);
			
			System.out.println(new StringBuilder().append(" Field I Address:").append(addressOfSampleClass).append("+").append(iFiledAddressShift).append(" Value:").append(i));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * CAS操作
	 * 最重要的cas 操作，也很简单，这里就不演示了
	 */
	static void cas() {}

	public static void main(String[] args) {
		testAllocateMem();
		
		modifyField(new SampleClass(), "name");
	}
}