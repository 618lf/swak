package com.swak.reactivex.transport.http.multipart;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

import org.springframework.core.io.Resource;

import io.netty.buffer.ByteBuf;

/**
 * 
 * 文件的描述
 * 
 * @author lifeng
 */
public class FileProps {

	private final long lastModifiedTime;
	private final long size;
	private final String name;
	private final Resource resource;

	private FileProps(Resource resource) throws IOException {
		this.resource = resource;
		this.lastModifiedTime = resource.lastModified();
		this.size = resource.contentLength();
		this.name = resource.getFilename();
	}

	/**
	 * 返回当前文件
	 * 
	 * @return
	 */
	public Resource resource() {
		return resource;
	}

	/**
	 * 是否文件 （jar 中的只能流输出）
	 * 
	 * @return
	 */
	public boolean isFile() {
		return resource.isFile();
	}

	/**
	 * The name of the file
	 */
	public String name() {
		return this.name;
	}

	/**
	 * The date the file was last modified
	 */
	public long lastModifiedTime() {
		return this.lastModifiedTime;
	}

	/**
	 * The size of the file, in bytes
	 */
	public long size() {
		return this.size;
	}

	/**
	 * 文件系统可以直接输出文件
	 * 
	 * @return
	 */
	public FileChannel channel() throws IOException {
		Path path = Paths.get(this.resource.getURI());
		return FileChannel.open(path, StandardOpenOption.READ);
	}

	/**
	 * jar 中执行二进制输出 研究下 nio 怎么读取流
	 * 
	 * @return
	 */
	public ByteBuf bytes() throws IOException {
		ReadableByteChannel channel = this.resource.readableChannel();
		ByteBuffer bytebuf = ByteBuffer.allocate(1024);
		int read;
		if ((read = channel.read(bytebuf)) >= 0) {

		}
		return null;
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static FileProps props(Resource resource) throws IOException {
		return new FileProps(resource);
	}
}
