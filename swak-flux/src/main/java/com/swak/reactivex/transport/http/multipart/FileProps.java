package com.swak.reactivex.transport.http.multipart;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * 
 * 文件的描述
 * 
 * @author lifeng
 */
public class FileProps {

	private long creationTime;
	private long lastModifiedTime;
	private long size;
	private Path path;

	private FileProps(Path path) throws IOException {
		this.path = path;
		BasicFileAttributes basicAttribs = Files.getFileAttributeView(path, BasicFileAttributeView.class).readAttributes();
		this.creationTime = basicAttribs.creationTime().toMillis();
		this.lastModifiedTime = basicAttribs.lastModifiedTime().toMillis();
		this.size = basicAttribs.size();
	}
	
	/**
	 * 返回当前文件
	 * 
	 * @return
	 */
	public Path file() {
		return path;
	}

	/**
	 * The date the file was created
	 */
	public long creationTime() {
		return this.creationTime;
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
	 * 
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static FileProps props(Path file) throws IOException {
		return new FileProps(file);
	}
}
