package com.swak.nio;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * 文件
 * 
 * @author lifeng
 * @date 2020年12月18日 下午2:35:53
 */
@SuppressWarnings("unused")
public class Files {

	@SuppressWarnings("resource")
	public static void main(String[] args) throws InterruptedException, IOException {
		File fd = new File("/root/ooxx.txt");
		FileInputStream fis = new FileInputStream(fd);
		
		fis.read();
		fis.read();
		fis.read();

		CountDownLatch latch = new CountDownLatch(1);
		latch.await();
	}
}
