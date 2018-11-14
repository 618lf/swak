package com.swak.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * 基于 NIO 的 高性能文件操作
 * 
 * @author lifeng
 */
public class FileUtils {

	/**
	 * 将 字节写入文件
	 * 
	 * @param file
	 * @param datas
	 */
	public static void write(File file, byte[] datas) {
		FileChannel channel = null;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			channel = out.getChannel();
			channel.write(ByteBuffer.wrap(datas));
		} catch (Exception e) {
		} finally {
			IOUtils.closeQuietly(out);
			IOUtils.closeQuietly(channel);
		}
	}

	/**
	 * 打开这个文件
	 * 
	 * @param file
	 */
	public static byte[] read(File file) {
		FileInputStream fis = null;
		ByteArrayOutputStream out = null;
		try {
			fis = new FileInputStream(file);
			out = new ByteArrayOutputStream();
			byte[] buffer = new byte[512];
			int n = 0;
			while ((n = fis.read(buffer)) != -1) {
				out.write(buffer, 0, n);
			}
			return out.toByteArray();
		} catch (Exception e) {
			return null;
		} finally {
			IOUtils.closeQuietly(fis);
			IOUtils.closeQuietly(out);
		}
	}

	/**
	 * 打开这个文件
	 * 
	 * @param file
	 */
	public static FileInputStream open(File file) {
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
		} catch (Exception e) {
		}
		return fis;
	}

	/**
	 * 得到文件的名称，不包括扩展名
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileName(String fileUrl) {
		return StringUtils.removeStart(StringUtils.substringAfterLast(fileUrl, "/"), ".");
	}

	/**
	 * 得到文件的扩展名, 大写
	 * 
	 * @param file
	 * @return
	 */
	public static String getFileSuffix(String fileUrl) {
		return StringUtils.lowerCase(StringUtils.substringAfterLast(fileUrl, "."));
	}

	/**
	 * 异步写文件, 执行成功之后会调用 completed
	 * 
	 * @param src
	 * @param dist
	 * @param bytebuf
	 * @param completed
	 * @throws IOException
	 */
	public static void asyncWrite(ReadableByteChannel src, AsynchronousFileChannel dist, ByteBuffer bytebuf,
			Runnable completed) throws IOException {
		bytebuf.clear();
		int read = src.read(bytebuf);
		if (read >= 0) {
			bytebuf.flip();
			dist.write(bytebuf, 0, null, new CompletionHandler<Integer, Void>() {
				@Override
				public void completed(Integer result, Void attachment) {
					_continue();
				}
				@Override
				public void failed(Throwable exc, Void attachment) {
					_continue();
				}
				private void _continue() {
					try {
						asyncWrite(src, dist, bytebuf, completed);
					} catch (IOException e) {
					}
				}
			});
		} else if (completed != null) {
			completed.run();
		}
	}
}