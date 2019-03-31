package com.swak.flux.transport.http.multipart;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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

	public MultipartFile(String name, String fileName, CharSequence contentType, long length) {
		this.name = name;
		this.fileName = fileName;
		this.contentType = contentType;
		this.length = length;
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
