package com.swak.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

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
	 * @param file
	 * @return
	 */
	public static String getFileName(String fileUrl) {
		return StringUtils.removeStart(StringUtils.substringAfterLast(fileUrl, "/"), ".");
	}
	
	/**
	 * 得到文件的扩展名, 大写
	 * @param file
	 * @return
	 */
	public static String getFileSuffix(String fileUrl) {
		return StringUtils.lowerCase(StringUtils.substringAfterLast(fileUrl, "."));
	}
}