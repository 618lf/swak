package com.swak.reactivex.transport.http.multipart;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.springframework.core.io.Resource;

import com.swak.utils.FileUtils;
import com.swak.utils.IOUtils;

import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

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
	private final FileChannel channel;
	private File deleteFile;

	/**
	 * 创建 FileProps
	 */
	private FileProps(Resource resource, FileChannel channel) throws IOException {
		this(resource, channel, null);
	}

	/**
	 * 创建 FileProps
	 */
	private FileProps(Resource resource, FileChannel channel, File deleteFile) throws IOException {
		this.resource = resource;
		this.channel = channel;
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
		return this.channel;
	}

	/**
	 * 释放资源
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (this.channel != null) {
			this.channel.close();
		}
		if (this.deleteFile != null) {
			this.deleteFile.delete();
		}
	}

	// /**
	// * jar 中执行二进制输出 研究下 nio 怎么读取流
	// *
	// * @return
	// */
	// public ByteBuf bytes() throws IOException {
	//
	// // 分配的缓冲区
	// ByteBuf byteBuf = Unpooled.buffer();
	//
	// // 读取数据
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// ReadableByteChannel channel = this.resource.readableChannel();
	// ByteBuffer bytebuf = ByteBuffer.allocate(1024);
	// while (channel.read(bytebuf) >= 0) {
	// bytebuf.flip();
	// baos.write(bytebuf.array(), 0, bytebuf.limit());
	// bytebuf.clear();
	// }
	//
	// // 设置缓冲数据
	// byteBuf.writeBytes(baos.toByteArray());
	//
	// // 关闭资源
	// baos.close();
	// bytebuf.clear();
	//
	// // 返回数据
	// return byteBuf;
	// }

	/**
	 * 创建文件流
	 * 
	 * @param sink
	 */
	private static void resourceSink(Resource resource, MonoSink<FileProps> sink) {
		try {
			if (resource.isFile()) {
				Path path = Paths.get(resource.getURI());
				FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
				sink.success(new FileProps(resource, channel));
			} else {
				File out = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString());
				ReadableByteChannel src = resource.readableChannel();
				AsynchronousFileChannel dist = AsynchronousFileChannel.open(out.toPath(), StandardOpenOption.WRITE);
				ByteBuffer bytebuf = ByteBuffer.allocate(1024);
				FileUtils.asyncWrite(src, dist, bytebuf, () -> {
					try {
						Path path = out.toPath();
						FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
						sink.success(new FileProps(resource, channel, out));
					} catch (Exception e) {
						out.delete();
						sink.error(e);
					} finally {
						IOUtils.closeQuietly(src);
						IOUtils.closeQuietly(dist);
						bytebuf.clear();
					}
				});
			}
		} catch (Exception e) {
			sink.error(e);
		}
	}

	/**
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static Mono<FileProps> props(Resource resource) {
		return Mono.create((sink) -> {
			resourceSink(resource, sink);
		});
	}
}
