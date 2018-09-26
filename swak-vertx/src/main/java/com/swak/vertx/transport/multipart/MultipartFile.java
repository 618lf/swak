package com.swak.vertx.transport.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * 上传文件
 * 
 * @author lifeng
 */
public class MultipartFile {

	private String name;
	private String fileName;
	private CharSequence contentType;
	private long length;
	private byte[] data;
	private File file;
	private Consumer<Void> accept;

	public MultipartFile(String name, String fileName, File file) {
		this.name = name;
		this.fileName = fileName;
		this.file = file;
	}
	
	public MultipartFile(String name, String fileName, byte[] data) {
		this.name = name;
		this.fileName = fileName;
		this.data = data;
		this.length = data.length;
	}
	
	public Consumer<Void> getAccept() {
		return accept;
	}

	public void setAccept(Consumer<Void> accept) {
		this.accept = accept;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public CharSequence getContentType() {
		return contentType;
	}

	public void setContentType(CharSequence contentType) {
		this.contentType = contentType;
	}

	public long getLength() {
		return length;
	}

	public void setLength(long length) {
		this.length = length;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}
	
	/**
	 * 返回文件输出流
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream() throws IOException {
		if (data != null) {
			return new ByteArrayInputStream(data);
		}
		if (file != null) {
			return new FileInputStream(file);
		}
		return null;
	}

	@Override
	public String toString() {
		long kb = length / 1024;
		return "FileItem(" + "name='" + name + '\'' + ", fileName='" + fileName + '\'' + ", contentType='" + contentType
				+ '\'' + ", size=" + (kb < 1 ? 1 : kb) + "KB)";
	}
}
