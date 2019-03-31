package com.swak.flux.transport.http.multipart;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

import org.springframework.core.io.Resource;

import com.swak.exception.BaseRuntimeException;
import com.swak.utils.FileUtils;
import com.swak.utils.IOUtils;

import io.netty.handler.stream.ChunkedFile;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

/**
 * 
 * 文件的描述
 * 
 * @author lifeng
 */
public class FileProps implements Closeable {

	private final long lastModifiedTime;
	private final long size;
	private final String name;
	private final Resource resource;
	private FileChannel channel;
	private File deleteFile;

	/**
	 * 创建 FileProps
	 */
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
	public FileChannel channel() {
		return this.channel;
	}

	/**
	 * 以文件流的型式输出
	 * 
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public ChunkedFile chunked() {
		try {
			RandomAccessFile raf = null;
			if (this.resource.isFile()) {
				raf = new RandomAccessFile(this.resource.getFile(), "r");
			} else {
				raf = new RandomAccessFile(this.deleteFile, "r");
			}
			return new ChunkedFile(raf, 0, this.size(), 8192);
		} catch (Exception e) {
			throw new BaseRuntimeException(e);
		}
	}

	/**
	 * 释放资源
	 * 
	 * @throws IOException
	 */
	@Override
	public void close() throws IOException {
		if (this.channel != null) {
			this.channel.close();
		}
		if (this.deleteFile != null) {
			this.deleteFile.delete();
		}
	}

	/**
	 * 创建文件流 如果是相对目录getURL, getURI 都获取不到路径
	 * 
	 * @param sink
	 */
	private void resourceSink(MonoSink<FileProps> sink) {
		try {
			if (resource.isFile()) {
				Path path = Paths.get(resource.getFile().getPath());
				this.channel = FileChannel.open(path, StandardOpenOption.READ);
				sink.success(this);
			} else {
				File out = new File(System.getProperty("java.io.tmpdir"), UUID.randomUUID().toString() + ".tmp");
				out.createNewFile();
				ReadableByteChannel src = resource.readableChannel();
				AsynchronousFileChannel dist = AsynchronousFileChannel.open(out.toPath(), StandardOpenOption.WRITE);
				ByteBuffer bytebuf = ByteBuffer.allocate(1024);
				FileUtils.asyncWrite(src, dist, bytebuf, () -> {
					try {
						Path path = out.toPath();
						this.channel = FileChannel.open(path, StandardOpenOption.READ);
						this.deleteFile = out;
						sink.success(this);
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
	 * 打开链接
	 * 
	 * @return
	 */
	public Mono<FileProps> open() {
		return Mono.create((sink) -> {
			this.resourceSink(sink);
		});
	}

	/**
	 * 创建 FileProps
	 * 
	 * @param resource
	 * @return
	 * @throws IOException
	 */
	public static FileProps props(Resource resource) throws IOException {
		return new FileProps(resource);
	}
}
